package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import ru.vkorneychuk.lightHTTP.containers.EndpointMetaData;

import java.io.OutputStream;
import java.lang.reflect.Method;

public interface RequestMethod extends Runnable {

    Object extractRequestBody(OutputStream requestBodyStream, Class<?> argumentType);

    void extractGetParameters();

//    void getRequestHeaders();

    EndpointMetaData getEndpointMetaData();


}
