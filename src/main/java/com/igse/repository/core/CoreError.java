package com.igse.repository.core;

import com.igse.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreError {


    public Mono<Throwable> handleCoreError(ClientResponse clientResponse) {
        var exception = switch (clientResponse.statusCode().value()) {
            case 400 -> createError("4001", HttpStatus.BAD_REQUEST.getReasonPhrase());
            case 401, 403 -> createError("4002", "auth error");
            case 404 -> createError("4003", "Wallet " + HttpStatus.NOT_FOUND.getReasonPhrase());
            case 500 -> createError("4004", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            default -> createError("4005", "something went wrong");
        };
        return Mono.error(exception);
    }

    private UserException createError(String code, String msg) {
        return UserException.builder()
                .errorCode(code)
                .message(msg).build();
    }
}
