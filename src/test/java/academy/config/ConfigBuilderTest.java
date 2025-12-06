package academy.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import academy.variation.VariationDefinition;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfigBuilderTest {

    private static final double EPSILON = 1.0e-12;

    @Test
    void givenValidFunctionListWhenParseFunctionsThenDefinitionsMatchInput() {
        List<VariationDefinition> definitions = ConfigBuilder.parseFunctions("swirl:1.0,linear:0.5");

        assertAll(
                () -> assertEquals(2, definitions.size()),
                () -> assertEquals("SWIRL", definitions.get(0).type().name()),
                () -> assertEquals(1.0, definitions.get(0).weight(), EPSILON),
                () -> assertEquals("LINEAR", definitions.get(1).type().name()),
                () -> assertEquals(0.5, definitions.get(1).weight(), EPSILON));
    }

    @Test
    void givenBlankFunctionListWhenParseFunctionsThenReturnsNull() {
        assertNull(ConfigBuilder.parseFunctions("   "));
    }

    @Test
    void givenSixNumbersWhenParseAffineThenCreatesMatchingParams() {
        AffineParams params = ConfigBuilder.parseAffine("1,0.25,-0.5,0.75,1,0.1");

        assertAll(
                () -> assertEquals(1.0, params.a(), EPSILON),
                () -> assertEquals(0.25, params.b(), EPSILON),
                () -> assertEquals(-0.5, params.c(), EPSILON),
                () -> assertEquals(0.75, params.d(), EPSILON),
                () -> assertEquals(1.0, params.e(), EPSILON),
                () -> assertEquals(0.1, params.f(), EPSILON));
    }
}

