package com.test.fedex.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

public class LogUtilsTest {

    @Test
    public void testRecordTimeOuts_whenNullInput() {
        Assertions.assertThrows(NullPointerException.class, () -> LogUtils.recordTimeOuts(null, null, 0.0d));
    }

    @Test
    public void testRecordTimeOuts() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Thread.sleep(5000);
        stopWatch.stop();

        LogUtils.recordTimeOuts("SHIPMENT", stopWatch, 5.0d);
    }
}
