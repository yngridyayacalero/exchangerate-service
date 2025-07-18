package com.mibanco.exchangerate.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExchangeRateResponse {
    private double amount;
    private double exchangedAmount;
    private String originCurrency;
    private String fateCurrency;
    private double rate;
}
