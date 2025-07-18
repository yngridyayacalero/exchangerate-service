package com.mibanco.exchangerate.domain.service.impl;

import com.mibanco.exchangerate.domain.model.ExchangeRateOperation;
import com.mibanco.exchangerate.domain.service.ExchangeRateStrategy;
import io.reactivex.rxjava3.core.Single;
import org.springframework.stereotype.Component;

@Component
public class PenToUsd implements ExchangeRateStrategy {

    @Override
    public boolean support(String origin, String fate) {
        return origin.equalsIgnoreCase("PEN") && fate.equalsIgnoreCase("USD");
    }

    @Override
    public Single<Double> get(ExchangeRateOperation operation) {
        return Single.just(0.26);
    }
}
