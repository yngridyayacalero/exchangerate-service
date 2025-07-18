package com.mibanco.exchangerate.domain.service;

import com.mibanco.exchangerate.domain.model.ExchangeRateOperation;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final List<ExchangeRateStrategy> strategies;

    public Single<Double> getRate(ExchangeRateOperation operation) {
        return Flowable.fromIterable(strategies)
                .filter(s -> s.support(operation.getOriginCurrency(), operation.getFateCurrency()))
                .firstOrError()
                .flatMap(s -> s.get(operation));
    }

}
