package com.mibanco.exchangerate.application.usecase;

import com.mibanco.exchangerate.application.dto.ExchangeRateRequest;
import com.mibanco.exchangerate.application.dto.ExchangeRateResponse;
import com.mibanco.exchangerate.domain.model.ExchangeRateOperation;
import com.mibanco.exchangerate.domain.service.ExchangeRateService;
import com.mibanco.exchangerate.application.exception.ExchangeRateNotSupportedException;
import com.mibanco.exchangerate.domain.service.ExchangeRateStrategy;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ExchangeRateUseCase {

    private final ExchangeRateService exchangeRateService;

    private final List<ExchangeRateStrategy> strategies;

    @CircuitBreaker(name = "exchangeRateService", fallbackMethod = "fallbackRate")
    @Retry(name = "exchangeRateService")
    //@Transactional si se quiere implementar con base de datos y guardar las operaciones, tambien se debe añadir la dependencia
    public Single<Double> getRate(ExchangeRateOperation operation) {
        return Flowable.fromIterable(strategies)
                .filter(s -> s.support(operation.getOriginCurrency(), operation.getFateCurrency()))
                .firstOrError()
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof NoSuchElementException) {
                        return Single.error(new ExchangeRateNotSupportedException(operation.getOriginCurrency(),operation.getFateCurrency()));
                    }
                    return Single.error(throwable);
                })
                .flatMap(s -> s.get(operation));
    }

    public Single<Double> fallbackRate(ExchangeRateOperation operation, Throwable throwable) {
        return Single.just(1.0);
    }

}
