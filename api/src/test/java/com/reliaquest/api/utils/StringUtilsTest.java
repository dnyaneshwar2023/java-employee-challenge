package com.reliaquest.api.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringUtilsTest {
    @Test
    void itShouldReturnTrueWhenSearchStringIsSubstringOfSourceIgnoringCase() {
        String sourceString = "I workout daily at gym";
        assertTrue(StringUtils.containsString(sourceString, "Daily"));
        assertTrue(StringUtils.containsString(sourceString, "GYM"));
    }

    @Test
    void itShouldReturnFalseWhenSearchStringIsNotSubstringOfSourceIgnoringCase() {
        String sourceString = "I workout daily at gym";
        assertFalse(StringUtils.containsString(sourceString, "everyday"));
        assertFalse(StringUtils.containsString(sourceString, "home"));
    }
}
