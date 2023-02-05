package com.give928.blockchain.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public final class HexadecimalUtil {
    private static final String HEX_PREFIX = "0x";
    private static final String HEX_PADDING = "0x000000000000000000000000";
    private static final String EMPTY = "";
    private static final int RADIX = 16;

    private HexadecimalUtil() {
    }

    public static String removeHexadecimalPadding(String hexadecimal) {
        if (hexadecimal == null) {
            return null;
        }
        return hexadecimal.replace(HEX_PADDING, HEX_PREFIX);
    }

    public static BigInteger toBigInteger(String hexadecimal) {
        if (hexadecimal == null) {
            return null;
        }
        return new BigInteger(hexadecimal.replace(HEX_PREFIX, EMPTY), RADIX);
    }

    public static Long toLong(String hexadecimal) {
        if (hexadecimal == null) {
            return null;
        }
        return Long.decode(hexadecimal);
    }

    public static LocalDateTime toLocalDateTime(String hexadecimal) {
        if (hexadecimal == null) {
            return null;
        }
        return toLocalDateTime(HexadecimalUtil.toLong(hexadecimal));
    }

    public static LocalDateTime toLocalDateTime(BigInteger timestamp) {
        if (timestamp == null) {
            return null;
        }
        return toLocalDateTime(timestamp.longValueExact());
    }

    private static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp * 1_000),
                                       TimeZone.getDefault()
                                               .toZoneId());
    }

    public static String objectToString(Object value) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        ObjectWriter ow = objectMapper.writer()
                .withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
