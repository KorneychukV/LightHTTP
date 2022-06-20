package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import ru.vkorneychuk.lightHTTP.annotations.RequestBody;
import ru.vkorneychuk.lightHTTP.annotations.RequestHeader;
import ru.vkorneychuk.lightHTTP.containers.ConfigContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointArgument;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointMetaData;
import ru.vkorneychuk.lightHTTP.defaultResponseTypes.HTTPResponse;
import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;
import ru.vkorneychuk.lightHTTP.exceptions.HTTPExceptionResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PostHandler extends RequestMethodHandler implements RequestMethod {

    private final HttpExchange exchange;
    private final EndpointContainer endpointContainer = EndpointContainer.getInstance();
    private final String currentURI;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PostHandler(HttpExchange exchange){
        ConfigContainer configContainer = ConfigContainer.getInstance();
        this.currentURI = exchange.getRequestURI().getPath().replace(configContainer.getDefaultApiPath(), "");
        this.exchange = exchange;
    }

    @Override
    public void run() {
        try {
            applyEndpointMethod();
        } catch (HTTPExceptionResponse e) {
            processHTTPException(e);
        }
    }

    @Override
    public void applyEndpointMethod() throws HTTPExceptionResponse{

        EndpointMetaData currentEndpoint = getEndpointMetaData();
        List<Object> arguments = fillArguments(currentEndpoint);
        Object response = callMethod(currentEndpoint, arguments);

        HTTPResponse httpResponse = prepareHTTPResponse(response);
        sendResponse(httpResponse);

    }

    public HTTPResponse prepareHTTPResponse(Object responseBody){
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.setResponseBody(responseBody);
        return httpResponse;
    }

    public Object callMethod(EndpointMetaData currentEndpoint, List<Object> arguments) throws HTTPExceptionResponse {
        try {
            return currentEndpoint.getMethod()
                    .invoke(currentEndpoint.getMethod().getDeclaringClass().getDeclaredConstructor().newInstance(),
                            arguments.toArray());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new HTTPExceptionResponse(
                    HTTPStatus.SERVER_INTERNAL_ERROR,
                    String.format("Calling method error: %s", e));
        }
    }

    public void sendResponse(HTTPResponse httpResponse){

        prepareHeaders(this.exchange.getResponseHeaders(), httpResponse.getHeaders());

        byte[] body;

        try {
            body = prepareResponseBody(httpResponse.getResponseBody());
            this.exchange.sendResponseHeaders(httpResponse.getStatus().code, body.length);
        } catch (IOException e) {
            String message = String.format("Preparing response error: %s", e);
            body = message.getBytes();
            System.err.printf("Preparing response error: %s", e);
        }

        try(OutputStream os = this.exchange.getResponseBody()){
            os.write(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public byte[] prepareResponseBody(Object responseBody) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(responseBody);
        oos.flush();
        return bos.toByteArray();
    }

    public void prepareHeaders(Headers responseHeaders, Headers requiredHeaders){
        responseHeaders.putAll(requiredHeaders);
    }

    public List<Object> fillArguments(EndpointMetaData currentEndpoint) throws HTTPExceptionResponse {
        List<Object> arguments = new ArrayList<>();
        for (EndpointArgument argument: currentEndpoint.getArguments()){
            // Arguments order is important
            if (argument.getAnnotation() == RequestBody.class){
                arguments.add(this.extractRequestBody(exchange.getResponseBody(), argument.getType()));
            } else if (argument.getAnnotation() == RequestHeader.class){
                arguments.add(this.extractRequestHeaders(
                        exchange.getRequestHeaders(),
                        argument.getType(),
                        argument.getRequestParameterName()));
            } else {
                arguments.add(null);
            }
        }
        return arguments;
    }

    @Override
    public EndpointMetaData getEndpointMetaData() {
        return endpointContainer.getEndpoint(this.currentURI);
    }

    @Override
    public Object extractRequestBody(OutputStream requestBodyStream, Class<?> argumentType) throws HTTPExceptionResponse {

        Object body;
        try {
            body = objectMapper.readValue(exchange.getRequestBody(), argumentType);
            return body;
        } catch (IOException e) {
            throw new HTTPExceptionResponse(HTTPStatus.BAD_REQUEST, "Bad request body");
        }
    }

    @Override
    public void extractGetParameters() {
    }

    @Override
    public void processHTTPException(HTTPExceptionResponse e) {

        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.setResponseBody(e.getMessage());
        httpResponse.setStatus(e.status);
        sendResponse(httpResponse);

        if (is5xxStatus(e.status)) {
            throw new RuntimeException(e);
        }
    }

    public boolean is1xxStatus(HTTPStatus status){
        return status.code / 100 == 1;
    }

    public boolean is2xxStatus(HTTPStatus status){
        return status.code / 100 == 2;
    }

    public boolean is3xxStatus(HTTPStatus status){
        return status.code / 100 == 3;
    }

    public boolean is4xxStatus(HTTPStatus status){
        return status.code / 100 == 4;
    }

    public boolean is5xxStatus(HTTPStatus status){
        return status.code / 100 == 5;
    }

    @Override
    public Object extractRequestHeaders(Headers headers, Class<?> argumentType, String headerName) throws HTTPExceptionResponse {
        String header = headers.getFirst(headerName);
        try {
            return argumentType.cast(header);
        } catch (ClassCastException e){
            throw new HTTPExceptionResponse(HTTPStatus.SERVER_INTERNAL_ERROR, "Wrong header type");
        }

    }
}
