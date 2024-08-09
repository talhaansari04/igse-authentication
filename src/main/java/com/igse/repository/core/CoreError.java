package com.igse.repository.core;

import com.igse.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreError {


    public Mono<Throwable> handleCoreError(ClientResponse clientResponse) {
        HttpStatusCode status = clientResponse.statusCode();
        throw UserException.builder().status(status.value()).message(HttpStatus.INTERNAL_SERVER_ERROR.toString()).build();
    }
}
