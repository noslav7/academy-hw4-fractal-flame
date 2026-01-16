package academy.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * Сохраняет изображение на диск.
 */
final class ImageWriter {
    private ImageWriter() {}

    /**
     * Записывает PNG-файл по указанному пути.
     *
     * @param target путь к файлу
     * @param image изображение
     */
    static void write(Path target, BufferedImage image) {
        try {
            Path parent = target.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            ImageIO.write(image, "PNG", target.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write image: " + target, e);
        }
    }
}
