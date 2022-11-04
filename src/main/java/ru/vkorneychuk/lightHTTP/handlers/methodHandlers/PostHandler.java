package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import ru.vkorneychuk.lightHTTP.annotations.arguments.RequestBody;
import ru.vkorneychuk.lightHTTP.annotations.arguments.RequestHeader;
import ru.vkorneychuk.lightHTTP.containers.ConfigContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointArgument;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointMetaData;
import ru.vkorneychuk.lightHTTP.defaultResponseTypes.HTTPResponse;
import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;
import ru.vkorneychuk.lightHTTP.exceptions.HTTPExceptionResponse;
import ru.vkorneychuk.lightHTTP.handlers.ExceptionHandler;
import ru.vkorneychuk.lightHTTP.handlers.ResponseHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PostHandler implements RequestMethod {

    private final HttpExchange exchange;
    private final String currentURI;

    public PostHandler(HttpExchange exchange){
        ConfigContainer configContainer = ConfigContainer.getInstance();
        this.currentURI = exchange.getRequestURI().getPath().replace(configContainer.getDefaultApiPath(), "");
        this.exchange = exchange;
    }

    @Override
    public void run() {
        try {
            Object response = applyEndpointMethod();
            HTTPResponse httpResponse = ResponseHandler.prepareHttpResponse(response);
            ResponseHandler.sendResponse(httpResponse, exchange);
        } catch (HTTPExceptionResponse e) {
            ExceptionHandler.sendException(e, exchange);
        }
    }

    @Override
    public Object applyEndpointMethod() throws HTTPExceptionResponse{
        EndpointMetaData currentEndpoint = getEndpointMetaData();
        List<Object> arguments = fillArguments(currentEndpoint);
        return callMethod(currentEndpoint, arguments);
    }

    @Override
    public EndpointMetaData getEndpointMetaData() throws HTTPExceptionResponse {
        EndpointMetaData endpointMetaData = EndpointContainer.getInstance().getEndpoint(this.currentURI);
        if (endpointMetaData.getRequestMethod().equals("POST")){
            return endpointMetaData;
        } else {
            throw new HTTPExceptionResponse(HTTPStatus.METHOD_NOT_ALLOWED, "Отсутствует вызываемый метод");
        }
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

    @Override
    public Object extractRequestBody(OutputStream requestBodyStream, Class<?> argumentType) throws HTTPExceptionResponse {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(exchange.getRequestBody(), argumentType);
        } catch (IOException e) {
            throw new HTTPExceptionResponse(HTTPStatus.BAD_REQUEST, "Bad request body");
        }
    }

    @Override
    public void extractGetParameters() {}

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
