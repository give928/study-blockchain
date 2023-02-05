package com.give928.blockchain.common.util;

import java.math.BigInteger;

public final class FeeUtil {
    private FeeUtil() {
    }

    public static BigInteger calculate(BigInteger gasUsed, BigInteger effectiveGasPrice) {
        if (gasUsed == null || effectiveGasPrice == null) {
            return null;
        }
        return gasUsed.multiply(effectiveGasPrice);
    }

    public static BigInteger calculate(String gasUsed, String effectiveGasPrice) {
        if (gasUsed == null || effectiveGasPrice == null) {
            return null;
        }
        return calculate(HexadecimalUtil.toBigInteger(gasUsed), HexadecimalUtil.toBigInteger(effectiveGasPrice));
    }

    public static BigInteger calculate(BigInteger gasUsed, String effectiveGasPrice) {
        if (gasUsed == null || effectiveGasPrice == null) {
            return null;
        }
        return calculate(gasUsed, HexadecimalUtil.toBigInteger(effectiveGasPrice));
    }
}
