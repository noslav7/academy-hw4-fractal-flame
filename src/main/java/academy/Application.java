package academy;

import academy.config.ConfigBuilder;
import academy.config.ConfigBuilder.CliOverrides;
import academy.config.ConfigLoader;
import academy.config.FractalConfig;
import academy.config.JsonFractalConfig;
import academy.render.FractalRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;

@Command(name = "fractal-flame", mixinStandardHelpOptions = true, version = "1.0")
public class Application implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Option(
            names = {"-w", "--width"},
            description = "Image width in pixels")
    private Integer width;

    @Option(
            names = {"-h", "--height"},
            description = "Image height in pixels")
    private Integer height;

    @Option(
            names = {"-i", "--iteration-count"},
            description = "Number of iterations")
    private Long iterationCount;

    @Option(names = "--seed", description = "Seed for the random generator")
    private Double seed;

    @Option(
            names = {"-o", "--output-path"},
            description = "Output PNG path")
    private String outputPath;

    @Option(
            names = {"-t", "--threads"},
            description = "Number of worker threads")
    private Integer threads;

    @Option(
            names = {"-ap", "--affine-params"},
            description = "Affine params in format a,b,c,d,e,f")
    private String affine;

    @Option(
            names = {"-f", "--functions"},
            description = "Comma-separated list of variation:weight")
    private String functions;

    @Option(
            names = {"-b", "--burn-in"},
            description = "Number of iterations to skip before plotting")
    private Long burnIn;

    @Option(names = {"--gamma"}, description = "Gamma value for correction")
    private Double gamma;

    @Option(names = {"-br", "--brightness"}, description = "Exposure multiplier")
    private Double brightness;

    @Option(
            names = {"-g", "-gc", "--gamma-correction"},
            description = "Enable gamma correction",
            arity = "0..1",
            fallbackValue = "true")
    private Boolean gammaCorrection;

    @Option(
            names = {"-lgc", "--log-gamma-correction"},
            description = "Use logarithmic gamma correction (per Draves paper)",
            arity = "0..1",
            fallbackValue = "true")
    private Boolean logGammaCorrection;

    @Option(
            names = {"-s", "--symmetry-level"},
            description = "Rotational symmetry level (>=1)")
    private Integer symmetryLevel;

    @Option(
            names = {"-c", "--config"},
            description = "Path to JSON config file")
    private Path configPath;
    @CommandLine.Unmatched
    private List<String> unmatched = new ArrayList<>();

    private final ConfigLoader configLoader =
            new ConfigLoader(new ObjectMapper().findAndRegisterModules());
    private final FractalRenderer renderer = new FractalRenderer();

    public static void main(String[] args) {
        new CommandLine(new Application()).setUnmatchedArgumentsAllowed(true).execute(args);
    }

    @Override
    public Integer call() {
        try {
            applyUnmatchedSystemProperties();
            FractalConfig config = buildConfig();
            renderer.render(config);
            return ExitCode.OK;
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid configuration: {}", e.getMessage());
            return ExitCode.USAGE;
        } catch (Exception e) {
            LOGGER.error("Application failed: {}", e.getMessage(), e);
            return ExitCode.SOFTWARE;
        }
    }

    private FractalConfig buildConfig() {
        JsonFractalConfig jsonConfig = configLoader.load(configPath);
        CliOverrides overrides = buildOverrides();
        return new ConfigBuilder().apply(jsonConfig).apply(overrides).build();
    }

    private void applyUnmatchedSystemProperties() {
        if (unmatched == null || unmatched.isEmpty()) {
            return;
        }
        for (String argument : unmatched) {
            if (argument == null) continue;
            if (argument.startsWith("-D") && argument.length() > 2) {
                String assignment = argument.substring(2);
                int idx = assignment.indexOf('=');
                String key;
                String value;
                if (idx >= 0) {
                    key = assignment.substring(0, idx);
                    value = assignment.substring(idx + 1);
                } else {
                    key = assignment;
                    value = "true";
                }
                if (!key.isBlank()) {
                    System.setProperty(key, value);
                    LOGGER.debug("Applied system property: {}={}", key, value);
                }
            } else {
                LOGGER.warn("Ignoring unsupported argument: {}", argument);
            }
        }
    }

    private CliOverrides buildOverrides() {
        CliOverrides overrides = new CliOverrides();
        overrides.setWidth(width);
        overrides.setHeight(height);
        overrides.setIterations(iterationCount);
        overrides.setSeed(seed);
        String normalizedOutput = normalizeOption(outputPath, "--output-path", 1);
        overrides.setOutputPath(normalizedOutput != null ? Path.of(normalizedOutput) : null);
        overrides.setThreads(threads);
        overrides.setBurnIn(burnIn);
        overrides.setGamma(gamma);
        overrides.setBrightness(brightness);
        overrides.setGammaCorrection(gammaCorrection);
        overrides.setLogGammaCorrection(logGammaCorrection);
        overrides.setSymmetryLevel(symmetryLevel);
        String normalizedAffine = normalizeOption(affine, "--affine-params", 5);
        overrides.setAffineParams(
                normalizedAffine != null ? ConfigBuilder.parseAffine(normalizedAffine) : null);
        String normalizedFunctions = normalizeOption(functions, "--functions", 3);
        overrides.setVariations(
                normalizedFunctions != null ? ConfigBuilder.parseFunctions(normalizedFunctions) : null);
        return overrides;
    }

    /**
     * Normalizes CLI string options by trimming and validating their length.
     *
     * @param value raw option value
     * @param optionName human-readable option identifier used in error messages
     * @param minLength minimum allowed length for the trimmed value
     * @return trimmed value or {@code null} when the option was not provided
     */
    private String normalizeOption(String value, String optionName, int minLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() < minLength) {
            throw new IllegalArgumentException(
                    optionName + " must contain at least " + minLength + " characters");
        }
        return trimmed;
    }
}
