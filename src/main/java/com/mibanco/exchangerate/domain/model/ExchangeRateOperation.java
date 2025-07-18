package com.mibanco.exchangerate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExchangeRateOperation {
    private double amount;
    private String originCurrency;
    private String fateCurrency;
}

