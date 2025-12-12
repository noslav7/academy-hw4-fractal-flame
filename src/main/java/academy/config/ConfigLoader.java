package academy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class ConfigLoader {

    private final ObjectMapper mapper;

    public ConfigLoader(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    public JsonFractalConfig load(Path path) {
        if (path == null) {
            return null;
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
            return mapper.readValue(inputStream, JsonFractalConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read config file: " + path, e);
        }
    }
}
