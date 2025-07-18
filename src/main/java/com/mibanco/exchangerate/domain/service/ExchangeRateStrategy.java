package com.mibanco.exchangerate.domain.service;

import com.mibanco.exchangerate.domain.model.ExchangeRateOperation;
import io.reactivex.rxjava3.core.Single;

public interface ExchangeRateStrategy {
    boolean support(String origin, String fate);
    Single<Double> get(ExchangeRateOperation operation);
}
