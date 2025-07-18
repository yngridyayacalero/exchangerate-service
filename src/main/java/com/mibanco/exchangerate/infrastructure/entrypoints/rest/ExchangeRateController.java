package com.mibanco.exchangerate.infrastructure.entrypoints.rest;

import com.mibanco.exchangerate.application.dto.ApiError;
import com.mibanco.exchangerate.application.dto.ExchangeRateRequest;
import com.mibanco.exchangerate.application.dto.ExchangeRateResponse;
import com.mibanco.exchangerate.application.exception.ExchangeRateNotSupportedException;
import com.mibanco.exchangerate.application.usecase.ExchangeRateUseCase;
import com.mibanco.exchangerate.domain.model.ExchangeRateOperation;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateController.class);
    private final ExchangeRateUseCase exchangeRateUseCase;

    @PostMapping
    @Operation(
            summary = "Calcula el tipo de cambio entre monedas",
            description = "Se requiere el header 'Authorization: Bearer 123456' para acceder a este recurso.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Token de autorización Bearer",
                            required = true,
                            example = "Bearer 123456"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tipo de cambio calculado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExchangeRateResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Tipo de cambio no soportado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Error inesperado del servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)))
            })
    public Single<ResponseEntity<Object>> calculate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para calcular el tipo de cambio",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ExchangeRateRequest.class))
            )
            @RequestBody ExchangeRateRequest request) {

        ExchangeRateOperation operation = new ExchangeRateOperation(
                request.getAmount(),
                request.getOriginCurrency(),
                request.getFateCurrency()
        );

        return exchangeRateUseCase.getRate(operation)
                .map(rate -> {
                    double exchangedAmount = request.getAmount() * rate;
                    ExchangeRateResponse response = ExchangeRateResponse.builder()
                            .amount(request.getAmount())
                            .exchangedAmount(exchangedAmount)
                            .originCurrency(request.getOriginCurrency())
                            .fateCurrency(request.getFateCurrency())
                            .rate(rate)
                            .build();
                    return ResponseEntity.ok((Object) response);
                })
                .onErrorReturn(error -> {
                    if (error instanceof ExchangeRateNotSupportedException) {
                        log.error("Tipo de cambio no soportado", error);
                        return ResponseEntity.unprocessableEntity()
                                .body(new ApiError(error.getMessage()));
                    }
                    log.error("Sucedió un error inesperado", error);
                    return ResponseEntity.internalServerError()
                            .body(new ApiError("Error inesperado del servidor"));
                });
    }
}