package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sun.net.httpserver.HttpExchange;
import ru.vkorneychuk.lightHTTP.annotations.RequestBody;
import ru.vkorneychuk.lightHTTP.containers.ConfigContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointArgument;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointMetaData;
import ru.vkorneychuk.lightHTTP.exceptions.ManyBodyExpected;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        EndpointMetaData currentEndpoint = getEndpointMetaData();
        Class<?> currentEndpointType = currentEndpoint.getMethod().getDeclaringClass();

        List<Object> arguments = new ArrayList<>();

        currentEndpoint.getArguments().forEach(argument -> {
           if (argument.getAnnotation() == RequestBody.class){
               arguments.add(this.extractRequestBody(exchange.getResponseBody(), argument.getType()));
           } else {
               arguments.add(null);
           }
        });

        try {
            // Arguments order is important
            currentEndpoint.getMethod()
                    .invoke(currentEndpointType.getDeclaredConstructor().newInstance(), arguments.toArray());
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
    public EndpointMetaData getEndpointMetaData() {
        return endpointContainer.getEndpoint(this.currentURI);
    }

    @Override
    public Object extractRequestBody(OutputStream requestBodyStream, Class<?> argumentType) {

        Object body;
        try {
            body = objectMapper.readValue(exchange.getRequestBody(), argumentType);
            return body;
        } catch (InvalidFormatException e) {
            System.err.println("Parse error.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void extractGetParameters() {
    }
}
