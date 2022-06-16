package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import ru.vkorneychuk.lightHTTP.containers.ConfigContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.exceptions.ManyBodyExpected;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PostHandler extends RequestMethodHandler implements RequestMethod {

    private final HttpExchange exchange;
    private final EndpointContainer endpointContainer = EndpointContainer.getInstance();
    private final String currentURI;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Method currentMethod;
    private Class<?> currentMethodType;

    public PostHandler(HttpExchange exchange){
        ConfigContainer configContainer = ConfigContainer.getInstance();
        this.currentURI = exchange.getRequestURI().getPath().replace(configContainer.getDefaultApiPath(), "");
        this.exchange = exchange;
    }

    @Override
    public void run() {

        this.currentMethod = getEndpointMethod();
        this.currentMethodType = this.currentMethod.getDeclaringClass();

        Object body = this.extractRequestBody(exchange.getResponseBody());

        try {
            // Arguments order is important
            this.currentMethod.invoke(this.currentMethodType.getDeclaredConstructor().newInstance(), body);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            System.err.printf("Calling method error: %s", e);
        }

        byte[] bytes = ("Done").getBytes();
        try {
            this.exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = this.exchange.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (IOException e) {
            System.err.printf("Preparing response error: %s", e);
        }
    }

    @Override
    public Object extractRequestBody(OutputStream requestBodyStream) {
        Class<?> requestBodyType;
        try {
            requestBodyType = super.getRequestBodyType(this.currentMethod);
        } catch (ManyBodyExpected e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

        if (requestBodyType == null){
            return null;
        }

        Object body;
        try {
            System.out.println(exchange.getRequestBody());
            body = objectMapper.readValue(exchange.getRequestBody(), requestBodyType);
            System.out.println(requestBodyType);
            return body;
        } catch (IOException e) {
            System.err.println("Parse error.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void extractGetParameters() {
    }

    @Override
    public void getMethodParameters(Method method) {

    }

    @Override
    public Method getEndpointMethod() {
        return endpointContainer.getEndpoint(this.currentURI);
    }
}
