package com.igse.exception;

import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @Value("${services.kafka.regisTopics}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public CustomErrorResponse validateCustom(Exception ex) {
        CustomErrorResponse errors = new CustomErrorResponse();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException cause = (MethodArgumentNotValidException) ex;
            cause.getBindingResult().getFieldErrors().forEach(e ->
                    {
                        errors.setMessage(e.getDefaultMessage());
                        errors.setStatus(HttpStatus.BAD_REQUEST.value());
                    }
            );
        } else if (ex instanceof UserException) {
            UserException userException = (UserException) ex;
            errors.setMessage(userException.getMessage());
            errors.setStatus(userException.getStatus());
        } else if (ex instanceof WebClientRequestException) {
            WebClientRequestException userException = (WebClientRequestException) ex;
            errors.setMessage(userException.getMessage());
            errors.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } else {
            errors.setMessage(GlobalConstant.GENERAL_EXCEPTION);
            errors.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        log.error("{}", ex.getMessage());

        return errors;
    }
}
