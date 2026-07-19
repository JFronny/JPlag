package de.jplag;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for formating runtime values.
 */
public final class TimeUtil {

    private TimeUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Convert a duration in milliseconds to a human-readable representation.
     * @param durationInMilliseconds Number of milliseconds to convert.
     * @return Readable representation of the time interval.
     */
    public static String formatDuration(long durationInMilliseconds) {
        Duration duration = Duration.ofMillis(durationInMilliseconds);
        return String.format("%dh %02dmin %02ds %03dms", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart(),
                duration.toMillisPart());
    }

    /**
     * Measures the duration of a task by aggregating the durations of subtasks.
     */
    public static class DurationAggregator {
        private final AtomicLong aggregate = new AtomicLong(0);

        /**
         * Returns the aggregate duration of all completed subtasks in milliseconds.
         * @return the duration in milliseconds
         */
        public long getAggregateMilliseconds() {
            return aggregate.get() / 1_000_000;
        }

        /**
         * Begins measurement for a new subtask. {@link Subtask#close} the resulting object to mark the subtask as completed and
         * add the measured time to the aggregate.
         * @return a {@link AutoCloseable} object representing the open subtask
         */
        public Subtask measureSubtask() {
            return new Subtask(aggregate, System.nanoTime());
        }

        /**
         * Measures the duration of a single subtask.<br>
         * For use in try-with-resources statements.
         */
        public static class Subtask implements AutoCloseable {
            private final AtomicLong aggregate;
            private final long start;

            private Subtask(AtomicLong aggregate, long start) {
                this.aggregate = aggregate;
                this.start = start;
            }

            @Override
            public void close() {
                aggregate.addAndGet(System.nanoTime() - start);
            }
        }
    }
}
