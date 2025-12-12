package academy.color;

import java.util.ArrayList;
import java.util.List;

public final class Palette {
    private final List<RgbColor> colors;

    public Palette(List<RgbColor> colors) {
        if (colors == null || colors.isEmpty()) {
            throw new IllegalArgumentException("Palette must contain at least one color");
        }
        this.colors = List.copyOf(colors);
    }

    public static Palette defaultPalette() {
        List<RgbColor> defaults = new ArrayList<>();
        defaults.add(new RgbColor(0.2, 0.2, 0.35));
        defaults.add(new RgbColor(0.3, 0.6, 0.9));
        defaults.add(new RgbColor(0.9, 0.8, 0.3));
        defaults.add(new RgbColor(0.95, 0.4, 0.2));
        defaults.add(new RgbColor(0.5, 0.2, 0.6));
        return new Palette(defaults);
    }

    public RgbColor sample(double t) {
        double normalized = normalize(t);
        if (colors.size() == 1) {
            return colors.getFirst();
        }
        double scaled = normalized * (colors.size() - 1);
        int index = (int) Math.floor(scaled);
        double fraction = scaled - index;
        RgbColor start = colors.get(index);
        RgbColor end = colors.get(Math.min(index + 1, colors.size() - 1));
        return new RgbColor(
                start.r() * (1 - fraction) + end.r() * fraction,
                start.g() * (1 - fraction) + end.g() * fraction,
                start.b() * (1 - fraction) + end.b() * fraction);
    }

    private static double normalize(double value) {
        double v = value % 1.0;
        if (v < 0) v += 1.0;
        return v;
    }
}
