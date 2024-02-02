package com.redhat;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

@QuarkusTest
public class TpsResultCollectorTest {

    @Inject
    Factory factory;

    @Test
    public void testTpsResultCollector() throws Exception {
        factory.setFormat(ResultCollector.FORMAT_TPS);
        TpsResultCollector tpsResultCollector = (TpsResultCollector) factory.getResultCollector();
        tpsResultCollector.init();

        assertEquals(0, tpsResultCollector.size);
        assertEquals(0, tpsResultCollector.lastTPS);
        assertEquals(0, tpsResultCollector.currentBucketTPS);

        tpsResultCollector.onResponseReceived(0, null);

        assertEquals(1, tpsResultCollector.size);
        assertEquals(0, tpsResultCollector.lastTPS);
        assertEquals(1, tpsResultCollector.currentBucketTPS);

        Thread.sleep(1100);

        assertEquals(1, tpsResultCollector.size);
        assertEquals(1, tpsResultCollector.lastTPS);
        assertEquals(0, tpsResultCollector.currentBucketTPS);
    }
}
