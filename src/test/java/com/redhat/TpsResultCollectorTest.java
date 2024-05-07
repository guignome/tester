package com.redhat;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TpsResultCollectorTest {

    @Inject
    Vertx vertx;

    @Inject
    Factory factory;

    @Test
    public void testTpsResultCollector() throws Exception {
        System.out.println("Starting testTPS");
        //factory.registerResultCollector(ResultCollector.FORMAT_TPS, "/tmp/test.tps", null);

        TpsResultCollector tpsResultCollector = new TpsResultCollector(vertx);
        tpsResultCollector.init("/tmp/results.tps", null);
        System.out.println("TPSResultCollector created. ");
        printStats(tpsResultCollector);
        assertEquals(0, tpsResultCollector.size.get());
        assertEquals(0, tpsResultCollector.lastTPS.get());
        assertEquals(0, tpsResultCollector.currentBucketTPS.get());

        tpsResultCollector.afterStep(null, null);
        System.out.println("Called AfterStep on TPSResultcollector");
        printStats(tpsResultCollector);

        assertEquals(1, tpsResultCollector.size.get());
        assertEquals(0, tpsResultCollector.lastTPS.get());
        assertEquals(1, tpsResultCollector.currentBucketTPS.get());

        Thread.sleep(1100);
        System.out.println("TPSResultCollector done sleeping");
        printStats(tpsResultCollector);

        assertEquals(1, tpsResultCollector.size.get());
        assertEquals(1, tpsResultCollector.lastTPS.get());
        assertEquals(0, tpsResultCollector.currentBucketTPS.get());
    }

    private void printStats( TpsResultCollector col) {
        System.out.println("TPSTest stats: " + col.lastTPS + " " + col.currentBucketTPS + " " + col.size + " " + col.requestCounter);
    }
}
