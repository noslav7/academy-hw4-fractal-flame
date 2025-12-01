package academy.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.file.Path;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonFractalConfig {

    public Size size;

    @JsonProperty("iteration_count")
    public Long iterationCount;

    public String output_path;
    public Integer threads;
    public Double seed;

    public List<JsonFunction> functions;

    @JsonProperty("affine_params")
    public JsonAffineParams affineParams;

    @JsonProperty("burn_in")
    public Long burnIn;

    @JsonProperty("gamma")
    public Double gamma;

    @JsonProperty("gamma_correction")
    public Boolean gammaCorrection;

    @JsonProperty("symmetry_level")
    public Integer symmetryLevel;

    public Double brightness;

    public JsonPalette palette;

    public JsonCamera camera;

    public record Size(Integer width, Integer height) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JsonFunction {
        public String name;
        public Double weight;
        public JsonColor color;
        @JsonProperty("color_index")
        public Double colorIndex;
        @JsonProperty("affine")
        public JsonAffineParams affine;
        public java.util.Map<String, Double> params;
    }

    public record JsonColor(Double r, Double g, Double b) {}

    public static class JsonAffineParams {
        public Double a;
        public Double b;
        public Double c;
        public Double d;
        public Double e;
        public Double f;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JsonPalette {
        public List<JsonColor> colors;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JsonCamera {
        @JsonProperty("center_x")
        public Double centerX;

        @JsonProperty("center_y")
        public Double centerY;

        public Double scale;
        public Double rotation;

        @JsonProperty("auto_fit")
        public Boolean autoFit;

        @JsonProperty("fit_margin")
        public Double fitMargin;

        @JsonProperty("fit_samples")
        public Long fitSamples;
    }

    Path outputPath() {
        return output_path != null ? Path.of(output_path) : null;
    }
}

