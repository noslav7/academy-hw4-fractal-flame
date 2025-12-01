package academy.config;

import academy.variation.VariationDefinition;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfigBuilderTest {

    @Test
    void parseFunctions_shouldCreateDefinitions() {
        List<VariationDefinition> definitions =
                ConfigBuilder.parseFunctions("swirl:1.0,linear:0.5");
        Assertions.assertThat(definitions).hasSize(2);
        Assertions.assertThat(definitions.get(0).type().name()).isEqualTo("SWIRL");
        Assertions.assertThat(definitions.get(1).weight()).isEqualTo(0.5);
    }

    @Test
    void parseAffine_shouldCreateParams() {
        AffineParams params = ConfigBuilder.parseAffine("1,0,0,0,1,0");
        Assertions.assertThat(params.a()).isEqualTo(1.0);
        Assertions.assertThat(params.e()).isEqualTo(1.0);
    }
}

