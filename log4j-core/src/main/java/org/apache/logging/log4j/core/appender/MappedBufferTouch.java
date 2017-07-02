package org.apache.logging.log4j.core.appender;

import java.nio.MappedByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.core.util.Log4jThreadFactory;

final class MappedBufferTouch {
    private static final int PAGE_SIZE = 4096;
    private static int dummy;
    private static final ExecutorService toucher = Executors.newSingleThreadExecutor(
            Log4jThreadFactory.createDaemonThreadFactory("MappedBufferToucher"));

    static void touch(final MappedByteBuffer mappedBuffer) {
        toucher.submit(new Runnable() {
            @Override
            public void run() {
                dummy ^= mappedBuffer.get(mappedBuffer.capacity() - 1);
                for (int offset = 0; offset < mappedBuffer.capacity(); offset += PAGE_SIZE) {
                    dummy ^= mappedBuffer.get(offset);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private MappedBufferTouch() {
    }
}
