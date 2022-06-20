package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import com.sun.net.httpserver.Headers;
import ru.vkorneychuk.lightHTTP.containers.EndpointArgument;
import ru.vkorneychuk.lightHTTP.containers.EndpointMetaData;
import ru.vkorneychuk.lightHTTP.exceptions.HTTPExceptionResponse;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

public interface RequestMethod extends Runnable {

    void applyEndpointMethod() throws HTTPExceptionResponse;

    List<Object> fillArguments(EndpointMetaData currentEndpoint) throws HTTPExceptionResponse;

    Object extractRequestBody(OutputStream requestBodyStream, Class<?> argumentType) throws HTTPExceptionResponse;

    void extractGetParameters();

    Object extractRequestHeaders(Headers headers, Class<?> headerType, String headerName) throws HTTPExceptionResponse;

    EndpointMetaData getEndpointMetaData();

    void processHTTPException(HTTPExceptionResponse e);

    Object callMethod(EndpointMetaData currentEndpoint, List<Object> arguments) throws HTTPExceptionResponse;

}
