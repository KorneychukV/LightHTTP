package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import java.io.OutputStream;
import java.lang.reflect.Method;

public interface RequestMethod extends Runnable {

    Object extractRequestBody(OutputStream requestBodyStream);

    void extractGetParameters();

//    void getRequestHeaders();

    Method getEndpointMethod();

    void getMethodParameters(Method method);


}
