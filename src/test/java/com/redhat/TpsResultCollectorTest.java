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

        assertEquals(0, tpsResultCollector.size.get());
        assertEquals(0, tpsResultCollector.lastTPS.get());
        assertEquals(0, tpsResultCollector.currentBucketTPS.get());

        tpsResultCollector.onResponseReceived(0, null);

        assertEquals(1, tpsResultCollector.size.get());
        assertEquals(0, tpsResultCollector.lastTPS.get());
        assertEquals(1, tpsResultCollector.currentBucketTPS.get());

        Thread.sleep(1100);

        assertEquals(1, tpsResultCollector.size.get());
        assertEquals(1, tpsResultCollector.lastTPS.get());
        assertEquals(0, tpsResultCollector.currentBucketTPS.get());
    }
}
