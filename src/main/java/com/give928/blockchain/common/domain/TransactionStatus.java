package com.give928.blockchain.common.domain;

import java.util.Arrays;
import java.util.Objects;

public enum TransactionStatus {
    PENDING(null, null), SUCCESS("0x1", Boolean.TRUE), FAIL("0x0", Boolean.FALSE);

    private final String value;
    private final Boolean ok;

    TransactionStatus(String value, Boolean ok) {
        this.value = value;
        this.ok = ok;
    }

    public static TransactionStatus find(String status) {
        return Arrays.stream(values())
                .filter(transactionStatus -> Objects.equals(transactionStatus.value, status))
                .findAny()
                .orElse(PENDING);
    }

    public static TransactionStatus find(Boolean ok) {
        return Arrays.stream(values())
                .filter(transactionStatus -> Objects.equals(transactionStatus.ok, ok))
                .findAny()
                .orElse(PENDING);
    }
}
