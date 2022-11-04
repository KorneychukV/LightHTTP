package ru.vkorneychuk.lightHTTP.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.vkorneychuk.lightHTTP.defaultResponseTypes.HTTPResponse;
import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;
import ru.vkorneychuk.lightHTTP.exceptions.HTTPExceptionResponse;

public class ExceptionHandler {

    public static void sendException(HTTPStatus httpStatus, String message, HttpExchange exchange){
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.setResponseBody(message);
        httpResponse.setStatus(httpStatus);
        ResponseHandler.sendResponse(httpResponse, exchange);
        throw new RuntimeException(message);
    }

    public static void sendException(HTTPExceptionResponse e, HttpExchange exchange){
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.setResponseBody(e.getMessage());
        httpResponse.setStatus(e.status);
        ResponseHandler.sendResponse(httpResponse, exchange);

        if (HttpStatusHandler.is5xxStatus(e.status)) {
            throw new RuntimeException(e);
        }
    }

}
