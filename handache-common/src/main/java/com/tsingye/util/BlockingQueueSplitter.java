package com.tsingye.util;

import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * a custom splitter, split element via Queue.take().
 * It would keep thread blocking while none available elements.
 * So the stream should always be sequential.
 * <p>Example:<code>
 * BlockingQueueSplitter.stream(queue)
 * </code></p>
 *
 * @param <T>
 */
public class BlockingQueueSplitter<T> implements Spliterator<T> {

    private final BlockingQueue<T> queue;

    private BlockingQueueSplitter(final BlockingQueue<T> queue) {
        this.queue = queue;
    }

    /**
     * build a stream to get element by thread blocking queue.take()
     *
     * @param queue target BlockingQueue
     * @param <T>   the element's type in blockingQueue
     * @return a sequential stream
     */
    public static <T> Stream<T> stream(BlockingQueue<T> queue) {
        return StreamSupport.stream(new BlockingQueueSplitter<>(queue), false);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        try {
            final T next = this.queue.take();
            if (next == null) {
                return false;
            }
            action.accept(next);
            return true;
        } catch (final InterruptedException e) {
            throw new RuntimeException("interrupted", e);
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.CONCURRENT | Spliterator.NONNULL | Spliterator.ORDERED;
    }
}
