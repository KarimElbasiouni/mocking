package com.karim;

import java.math.BigDecimal;

public interface PaymentProcessor {
    boolean charge(BigDecimal amount);
}
