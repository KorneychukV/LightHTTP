package ru.vkorneychuk.lightHTTP.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import ru.vkorneychuk.lightHTTP.defaultResponseTypes.HTTPResponse;
import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;
import ru.vkorneychuk.lightHTTP.exceptions.HTTPExceptionResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

@Slf4j
public class ResponseHandler {

    public static HTTPResponse prepareHttpResponse(Object responseBody) throws HTTPExceptionResponse {
        HTTPResponse httpResponse = new HTTPResponse();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            httpResponse.setResponseBody(objectMapper.writeValueAsString(responseBody));
        } catch (IOException e) {
            throw new HTTPExceptionResponse(HTTPStatus.SERVER_INTERNAL_ERROR, "Неверный тип возвращаемого значения");
        }

        return httpResponse;
    }

    public static void sendResponse(HTTPResponse httpResponse, HttpExchange exchange){

        prepareHeaders(exchange.getResponseHeaders(), httpResponse.getHeaders());

        byte[] body;

        try {
            body = prepareResponseBody(httpResponse.getResponseBody());
            exchange.sendResponseHeaders(httpResponse.getStatus().code, body.length);
        } catch (IOException e) {
            String message = String.format("Preparing response error: %s", e);
            body = message.getBytes();
            log.error("Preparing response error: %s", e);
        }

        try(OutputStream os = exchange.getResponseBody()){
            os.write(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void prepareHeaders(Headers responseHeaders, Headers requiredHeaders){
        responseHeaders.putAll(requiredHeaders);
    }

    public static byte[] prepareResponseBody(Object responseBody) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(responseBody);
        oos.flush();
        return bos.toByteArray();
    }
}
