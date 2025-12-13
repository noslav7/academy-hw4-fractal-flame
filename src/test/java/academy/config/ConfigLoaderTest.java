package academy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigLoaderTest {

    @Test
    void givenJsonFileWhenLoadThenParsesFields(@TempDir Path tempDir) throws Exception {
        Path jsonFile = tempDir.resolve("config.json");
        String json =
                """
                {
                  "size": {"width": 64, "height": 32},
                  "iteration_count": 111,
                  "output_path": "out.png",
                  "threads": 3,
                  "gamma_correction": true
                }
                """;
        Files.writeString(jsonFile, json);

        ConfigLoader loader = new ConfigLoader(new ObjectMapper().findAndRegisterModules());
        JsonFractalConfig config = loader.load(jsonFile);

        assertNotNull(config);
        assertEquals(64, config.size.width());
        assertEquals(32, config.size.height());
        assertEquals(111L, config.iterationCount);
        assertEquals("out.png", config.output_path);
        assertEquals(3, config.threads);
        assertEquals(Boolean.TRUE, config.gammaCorrection);
    }
}
