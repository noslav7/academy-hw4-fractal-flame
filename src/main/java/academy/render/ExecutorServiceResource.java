package academy.render;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Обёртка для ExecutorService, чтобы использовать try-with-resources. */
public final class ExecutorServiceResource implements AutoCloseable {
    private final ExecutorService executor;

    public ExecutorServiceResource(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public ExecutorService get() {
        return executor;
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
