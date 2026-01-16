package academy.config;

import academy.variation.VariationDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;

final class VariationFactory {

    private VariationFactory() {}

    private static final Path DEFAULT_PRESET_PATH = Path.of("config", "presets", "flame.json");

    static List<VariationDefinition> defaultVariations() {
        JsonFractalConfig config = loadDefaultConfig();
        List<VariationDefinition> variations = JsonConfigMapper.toVariations(config.functions);
        if (variations == null || variations.isEmpty()) {
            throw new IllegalStateException("Default variations are not configured: " + DEFAULT_PRESET_PATH);
        }
        return variations;
    }

    private static JsonFractalConfig loadDefaultConfig() {
        ConfigLoader loader = new ConfigLoader(new ObjectMapper().findAndRegisterModules());
        JsonFractalConfig config = loader.load(DEFAULT_PRESET_PATH);
        if (config == null) {
            throw new IllegalStateException("Unable to load default preset: " + DEFAULT_PRESET_PATH);
        }
        return config;
    }
}
