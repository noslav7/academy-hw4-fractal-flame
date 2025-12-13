package academy.render;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ProgressTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressTracker.class);

    private final long total;
    private final AtomicLong processed = new AtomicLong();
    private final AtomicInteger lastLoggedPercent = new AtomicInteger();

    ProgressTracker(long total) {
        this.total = total;
    }

    void increment(long delta) {
        if (delta <= 0) return;
        long current = processed.addAndGet(delta);
        if (total <= 0) {
            return;
        }
        int percent = (int) Math.round(current * 100.0 / total);
        int previous = lastLoggedPercent.get();
        if (percent - previous >= 5 && lastLoggedPercent.compareAndSet(previous, percent)) {
            LOGGER.atInfo()
                    .addKeyValue("progressPercent", Math.min(100, percent))
                    .log("Render progress");
        }
    }
}
