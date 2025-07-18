package com.mibanco.exchangerate.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@Getter
@AllArgsConstructor
public class ExchangeRateNotSupportedException extends RuntimeException {
    private final String originCurrency;
    private final String fateCurrency;

    @Override
    public String getMessage() {
        return String.format("Tipo de cambio no soportado: %s -> %s", originCurrency, fateCurrency);
    }


}
