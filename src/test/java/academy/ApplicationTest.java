package academy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

/**
 * Проверяет работу CLI приложения.
 */
class ApplicationTest {

    /**
     * Проверяет, что CLI параметры применяются и PNG сохраняется.
     */
    @Test
    void givenCliOptionsWhenExecuteThenRendersAndAppliesSystemProps(@TempDir Path tempDir) {
        Path output = tempDir.resolve("cli.png");

        int exitCode = new CommandLine(new Application())
                .execute(
                        "--width",
                        "32",
                        "--height",
                        "32",
                        "--iteration-count",
                        "200",
                        "--threads",
                        "1",
                        "--seed",
                        "1.5",
                        "--output-path",
                        output.toString(),
                        "--affine-params",
                        "1,0,0,0,1,0",
                        "--functions",
                        "linear:1.0",
                        "--burn-in",
                        "0",
                        "--gamma",
                        "2.2",
                        "--brightness",
                        "1.0",
                        "--symmetry-level",
                        "1",
                        "--gamma-correction",
                        "--log-gamma-correction",
                        "-Dapp.custom=on");

        assertEquals(0, exitCode);
        assertTrue(Files.exists(output));
        assertEquals("on", System.getProperty("app.custom"));
    }
}
