package com.igse.exception;

import static com.igse.util.ErrorCode.CORRELATION_ID_NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Value("${services.kafka.regisTopics}")
    private String topic;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        log.error("Validation error header {} statusCode {} request {} ",headers.getHost(),status.value(),request.getContextPath());
        List<Object> validationErrors = getObjects(ex);
        IgseValidationError<Object> validationFailed = IgseValidationError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation Failed")
                .errors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationFailed);
    }

    private static List<Object> getObjects(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<Object> validationErrors = new ArrayList<>(fieldErrors.size());
        fieldErrors.forEach(fieldError ->
                {
                    ValidationError validationError = ValidationError.builder()
                            .field(fieldError.getField())
                            .message(fieldError.getDefaultMessage()).build();
                    validationErrors.add(validationError);
                }
        );
        return validationErrors;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MicroserviceError> validateCustom(Exception ex) {
        log.error("General Exception {}", ex.getMessage());
        MicroserviceError microserviceError = MicroserviceError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorDetails(List.of(ErrorDetails.builder()
                        .code("0000")
                        .message(INTERNAL_SERVER_ERROR.reasonPhrase()).build()))
                .build();
        MDC.clear();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(microserviceError);
    }


    @ExceptionHandler(UserException.class)
    public ResponseEntity<MicroserviceError> microserviceErrorResponseEntity(UserException userException) {
        MicroserviceError microserviceError = MicroserviceError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorDetails(List.of(ErrorDetails.builder()
                        .code(userException.getErrorCode())
                        .message(userException.getMessage()).build()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(microserviceError);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<MicroserviceError> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        MicroserviceError microserviceError = MicroserviceError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorDetails(List.of(ErrorDetails.builder()
                        .code(CORRELATION_ID_NOT_FOUND.getCode())
                        .message(CORRELATION_ID_NOT_FOUND.getMessage()).build()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(microserviceError);
    }
}
