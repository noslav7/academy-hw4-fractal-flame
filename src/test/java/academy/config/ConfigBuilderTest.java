package academy.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.color.RgbColor;
import academy.variation.VariationDefinition;
import academy.variation.VariationParameters;
import academy.variation.VariationType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigBuilderTest {

    private static final double EPSILON = 1.0e-12;

    @Test
    void givenValidFunctionListWhenParseFunctionsThenDefinitionsMatchInput() {
        List<VariationDefinition> definitions = CliParsers.parseFunctions("swirl:1.0,linear:0.5");

        assertAll(
                () -> assertEquals(2, definitions.size()),
                () -> assertEquals("SWIRL", definitions.get(0).type().name()),
                () -> assertEquals(1.0, definitions.get(0).weight(), EPSILON),
                () -> assertEquals("LINEAR", definitions.get(1).type().name()),
                () -> assertEquals(0.5, definitions.get(1).weight(), EPSILON));
    }

    @Test
    void givenBlankFunctionListWhenParseFunctionsThenReturnsNull() {
        assertNull(CliParsers.parseFunctions("   "));
    }

    @Test
    void givenSixNumbersWhenParseAffineThenCreatesMatchingParams() {
        AffineParams params = CliParsers.parseAffine("1,0.25,-0.5,0.75,1,0.1");

        assertAll(
                () -> assertEquals(1.0, params.a(), EPSILON),
                () -> assertEquals(0.25, params.b(), EPSILON),
                () -> assertEquals(-0.5, params.c(), EPSILON),
                () -> assertEquals(0.75, params.d(), EPSILON),
                () -> assertEquals(1.0, params.e(), EPSILON),
                () -> assertEquals(0.1, params.f(), EPSILON));
    }

    @Test
    void givenJsonConfigWithAllFieldsWhenApplyThenBuildsExpectedConfig(@TempDir Path tempDir) {
        JsonFractalConfig json = new JsonFractalConfig();
        json.size = new JsonFractalConfig.Size(100, 80);
        json.iterationCount = 123L;
        json.output_path = tempDir.resolve("json.png").toString();
        json.threads = 2;
        json.seed = 7.5;
        json.burnIn = 10L;
        json.gamma = 1.6;
        json.gammaCorrection = true;
        json.logGammaCorrection = false;
        json.symmetryLevel = 3;
        json.brightness = 1.1;

        JsonFractalConfig.JsonAffineParams globalAffine = new JsonFractalConfig.JsonAffineParams();
        globalAffine.a = 0.5;
        globalAffine.b = 0.1;
        globalAffine.c = -0.2;
        globalAffine.d = 0.3;
        globalAffine.e = 0.4;
        globalAffine.f = 0.0;
        json.affineParams = globalAffine;

        JsonFractalConfig.JsonColor color = new JsonFractalConfig.JsonColor(0.1, 0.2, 0.3);
        JsonFractalConfig.JsonFunction function = new JsonFractalConfig.JsonFunction();
        function.name = "sinusoidal";
        function.weight = 0.75;
        function.color = color;
        function.colorIndex = 0.4;
        JsonFractalConfig.JsonAffineParams localAffine = new JsonFractalConfig.JsonAffineParams();
        localAffine.a = 1.0;
        localAffine.d = 1.0;
        localAffine.e = 0.0;
        localAffine.f = 0.0;
        function.affine = localAffine;
        function.params = Map.of("a", 2.0);
        json.functions = List.of(function);

        JsonFractalConfig.JsonPalette palette = new JsonFractalConfig.JsonPalette();
        palette.colors =
                List.of(new JsonFractalConfig.JsonColor(1.0, 0.0, 0.0), new JsonFractalConfig.JsonColor(0.0, 1.0, 0.0));
        json.palette = palette;

        JsonFractalConfig.JsonCamera camera = new JsonFractalConfig.JsonCamera();
        camera.centerX = 0.1;
        camera.centerY = -0.2;
        camera.scale = 1.5;
        camera.rotation = 0.5;
        camera.autoFit = false;
        camera.fitMargin = 0.2;
        camera.fitSamples = 10_000L;
        json.camera = camera;

        FractalConfig config = new ConfigBuilder().apply(json).build();

        assertAll(
                () -> assertEquals(100, config.width()),
                () -> assertEquals(80, config.height()),
                () -> assertEquals(123L, config.iterationCount()),
                () -> assertEquals(Path.of(json.output_path), config.outputPath()),
                () -> assertEquals(2, config.threads()),
                () -> assertEquals(7.5, config.seed(), EPSILON),
                () -> assertEquals(10L, config.burnInIterations()),
                () -> assertEquals(1.6, config.gamma(), EPSILON),
                () -> assertEquals(1.1, config.brightness(), EPSILON),
                () -> assertTrue(config.gammaCorrection()),
                () -> assertEquals(3, config.symmetryLevel()),
                () -> assertEquals(
                        VariationType.SINUSOIDAL, config.variations().get(0).type()),
                () -> assertEquals(0.75, config.variations().get(0).weight(), EPSILON),
                () -> assertEquals(0.1, config.variations().get(0).color().r(), EPSILON),
                () -> assertEquals(0.4, config.variations().get(0).colorIndex(), EPSILON),
                () -> assertEquals(2.0, config.variations().get(0).parameters().get("a", -1.0), EPSILON),
                () -> assertEquals(0.5, config.affineParams().a(), EPSILON),
                () -> assertEquals(0.1, config.affineParams().b(), EPSILON),
                () -> assertEquals(1.0, config.palette().sample(0.0).r(), EPSILON),
                () -> assertTrue(config.palette().sample(0.99).g() > 0.9),
                () -> assertEquals(0.1, config.camera().centerX(), EPSILON),
                () -> assertEquals(-0.2, config.camera().centerY(), EPSILON),
                () -> assertEquals(1.5, config.camera().scale(), EPSILON),
                () -> assertEquals(0.5, config.camera().rotationDegrees(), EPSILON),
                () -> assertTrue(!config.camera().autoFit()),
                () -> assertEquals(0.2, config.camera().fitMargin(), EPSILON),
                () -> assertEquals(10_000L, config.camera().fitSamples()));
    }

    @Test
    void givenJsonAndCliOverridesWhenApplyThenCliWins(@TempDir Path tempDir) {
        JsonFractalConfig json = new JsonFractalConfig();
        json.size = new JsonFractalConfig.Size(50, 50);
        json.iterationCount = 10_000L;
        json.output_path = tempDir.resolve("json-only.png").toString();
        json.threads = 1;

        ConfigBuilder.CliOverrides overrides = new ConfigBuilder.CliOverrides();
        overrides.setWidth(200);
        overrides.setHeight(150);
        overrides.setIterations(500L);
        overrides.setOutputPath(tempDir.resolve("cli.png"));
        overrides.setThreads(4);
        overrides.setAffineParams(new AffineParams(1, 0, 0, 0, 1, 0));
        List<VariationDefinition> variations = new ArrayList<>();
        variations.add(new VariationDefinition(
                VariationType.LINEAR,
                1.0,
                RgbColor.of(1.0, 0.0, 0.0),
                0.0,
                AffineParams.IDENTITY,
                VariationParameters.empty()));
        overrides.setVariations(variations);
        overrides.setBurnIn(5L);
        overrides.setPalette(new Palette(List.of(RgbColor.of(1.0, 1.0, 0.0))));
        overrides.setCamera(new CameraSettings(0, 0, 1.0, 0.0, false, 0.2, 20_000L));
        overrides.setBrightness(2.0);
        overrides.setGamma(1.8);
        overrides.setGammaCorrection(true);
        overrides.setLogGammaCorrection(false);
        overrides.setSymmetryLevel(2);

        FractalConfig config = new ConfigBuilder().apply(json).apply(overrides).build();

        assertAll(
                () -> assertEquals(200, config.width()),
                () -> assertEquals(150, config.height()),
                () -> assertEquals(500L, config.iterationCount()),
                () -> assertEquals(tempDir.resolve("cli.png"), config.outputPath()),
                () -> assertEquals(4, config.threads()),
                () -> assertEquals(5L, config.burnInIterations()),
                () -> assertEquals(2.0, config.brightness(), EPSILON),
                () -> assertEquals(1.8, config.gamma(), EPSILON),
                () -> assertTrue(config.gammaCorrection()),
                () -> assertEquals(2, config.symmetryLevel()),
                () -> assertEquals(1, config.variations().size()),
                () -> assertEquals(0.2, config.camera().fitMargin(), EPSILON));
    }
}
