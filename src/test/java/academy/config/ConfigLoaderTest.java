package academy.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Тесты загрузчика JSON-конфига. */
class ConfigLoaderTest {

    /** Проверяет, что JSON-файл читается корректно. */
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

        assertAll(
                () -> assertNotNull(config),
                () -> assertEquals(Integer.valueOf(64), config.size.width()),
                () -> assertEquals(Integer.valueOf(32), config.size.height()),
                () -> assertEquals(Long.valueOf(111L), config.iterationCount),
                () -> assertEquals("out.png", config.output_path),
                () -> assertEquals(Integer.valueOf(3), config.threads),
                () -> assertEquals(Boolean.TRUE, config.gammaCorrection));
    }
}
