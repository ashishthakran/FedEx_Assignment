package com.test.fedex.common.utils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class contains logging utility methods.
 */
@Slf4j
public class LogUtils {

    private LogUtils() {}

    /**
     * This method will record API timeouts if any.
     * @param apiName
     * @param stopWatch
     * @param threshold
     */
    public static void recordTimeOuts(@Valid @NotNull final String apiName, @Valid @NotNull StopWatch stopWatch, final double threshold) {
        double seconds = stopWatch.getTotalTimeSeconds();
        if(seconds >= threshold) {
            log.warn("\n========================================================" +
                    "=================== Time Out {} seconds recorded for {}" +
                    "=======================================================\n", seconds, apiName);
        }
    }
}
