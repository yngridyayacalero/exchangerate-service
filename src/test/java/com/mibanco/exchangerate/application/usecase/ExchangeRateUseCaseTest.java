package com.mibanco.exchangerate.application.usecase;

import com.mibanco.exchangerate.application.exception.ExchangeRateNotSupportedException;
import com.mibanco.exchangerate.domain.model.ExchangeRateOperation;
import com.mibanco.exchangerate.domain.service.ExchangeRateService;
import com.mibanco.exchangerate.domain.service.ExchangeRateStrategy;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateUseCaseTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ExchangeRateStrategy supportedStrategy;

    @Mock
    private ExchangeRateStrategy unsupportedStrategy;

    @InjectMocks
    private ExchangeRateUseCase useCase;

    @BeforeEach
    void init() {
        useCase = new ExchangeRateUseCase(exchangeRateService, List.of(supportedStrategy, unsupportedStrategy));
    }

    @Test
    void testGetRateSuccess() {
        ExchangeRateOperation operation = new ExchangeRateOperation(100.0, "USD", "PEN");

        when(supportedStrategy.support("USD", "PEN")).thenReturn(true);
        when(supportedStrategy.get(operation)).thenReturn(Single.just(3.75));
        Double result = useCase.getRate(operation).blockingGet();
        assertEquals(3.75, result);
    }

    @Test
    void testGetRateUnsupported() {
        ExchangeRateOperation operation = new ExchangeRateOperation(100.0, "USD", "CLP");
        when(unsupportedStrategy.support("USD", "CLP")).thenReturn(false);
        Exception exception = assertThrows(ExchangeRateNotSupportedException.class, () -> {
            useCase.getRate(operation).blockingGet();
        });

        String expected = "Tipo de cambio no soportado: USD -> CLP";
        assertEquals(expected, exception.getMessage());
    }
}
