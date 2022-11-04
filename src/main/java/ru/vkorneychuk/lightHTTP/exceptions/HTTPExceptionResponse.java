package ru.vkorneychuk.lightHTTP.exceptions;

import ru.vkorneychuk.lightHTTP.defaultResponseTypes.ExceptionResponse;
import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;

import java.sql.Timestamp;

public class HTTPExceptionResponse extends Exception {

    public HTTPStatus status;

    public HTTPExceptionResponse(HTTPStatus status, String message) {
        // TODO get raise exception path
        super(new ExceptionResponse(
                new Timestamp(System.currentTimeMillis()),
                status,
                message,
                "")
                .toString());
        this.status = status;
    }
}
