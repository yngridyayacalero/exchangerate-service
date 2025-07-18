package com.mibanco.exchangerate.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRateRequest {
    private double amount;
    private String originCurrency;
    private String fateCurrency;
}
