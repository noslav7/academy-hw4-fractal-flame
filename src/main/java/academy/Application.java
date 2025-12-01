package academy;

import academy.config.ConfigBuilder;
import academy.config.ConfigBuilder.CliOverrides;
import academy.config.ConfigLoader;
import academy.config.FractalConfig;
import academy.config.JsonFractalConfig;
import academy.render.FractalRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "fractal-flame", mixinStandardHelpOptions = true, version = "1.0")
public class Application implements Runnable {
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

    @Option(names = {"-g", "--gamma"}, description = "Gamma value for correction")
    private Double gamma;

    @Option(names = {"-br", "--brightness"}, description = "Exposure multiplier")
    private Double brightness;

    @Option(
            names = {"-gc", "--gamma-correction"},
            description = "Enable gamma correction",
            arity = "0..1",
            fallbackValue = "true")
    private Boolean gammaCorrection;

    @Option(
            names = {"-s", "--symmetry-level"},
            description = "Rotational symmetry level (>=1)")
    private Integer symmetryLevel;

    @Option(
            names = {"-c", "--config"},
            description = "Path to JSON config file")
    private Path configPath;

    private final ConfigLoader configLoader =
            new ConfigLoader(new ObjectMapper().findAndRegisterModules());
    private final FractalRenderer renderer = new FractalRenderer();

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            FractalConfig config = buildConfig();
            renderer.render(config);
        } catch (Exception e) {
            LOGGER.error("Application failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    private FractalConfig buildConfig() {
        JsonFractalConfig jsonConfig = configLoader.load(configPath);
        CliOverrides overrides = buildOverrides();
        return new ConfigBuilder().apply(jsonConfig).apply(overrides).build();
    }

    private CliOverrides buildOverrides() {
        CliOverrides overrides = new CliOverrides();
        overrides.setWidth(width);
        overrides.setHeight(height);
        overrides.setIterations(iterationCount);
        overrides.setSeed(seed);
        overrides.setOutputPath(outputPath != null ? Path.of(outputPath) : null);
        overrides.setThreads(threads);
        overrides.setBurnIn(burnIn);
        overrides.setGamma(gamma);
        overrides.setBrightness(brightness);
        overrides.setGammaCorrection(gammaCorrection);
        overrides.setSymmetryLevel(symmetryLevel);
        overrides.setAffineParams(ConfigBuilder.parseAffine(affine));
        overrides.setVariations(parseFunctionsSafe());
        return overrides;
    }

    private List<academy.variation.VariationDefinition> parseFunctionsSafe() {
        try {
            return ConfigBuilder.parseFunctions(functions);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), e.getMessage());
        }
    }
}
