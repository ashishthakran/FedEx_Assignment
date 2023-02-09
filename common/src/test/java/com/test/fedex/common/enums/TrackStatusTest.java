package com.test.fedex.common.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TrackStatusTest {

    @DisplayName("Should pass non-null enum values as method parameters")
    @ParameterizedTest(name = "{index} => status=''{0}''")
    @EnumSource(TrackStatus.class)
    public void shouldPassNonNullEnumValuesAsMethodParameter(TrackStatus trackStatus) {
        assertNotNull(trackStatus);
    }

    @DisplayName("Should pass a non-null message to our test method")
    @ParameterizedTest(name = "{index} => message=''{0}''")
    @ValueSource(strings = {"NEW", "DELIVERED"})
    public void testGetStatus(String status) {
        TrackStatus trackStatus = TrackStatus.getStatus(status);
        assertNotEquals(TrackStatus.UNKNOWN, trackStatus);
    }

    @Test
    public void testGetStatus_whenNullInput() {
        TrackStatus trackStatus = TrackStatus.getStatus(null);
        assertEquals(TrackStatus.UNKNOWN, trackStatus);
    }
}
