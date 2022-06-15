package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PostHandler implements MethodHandler{

    private final HttpExchange exchange;
    private EndpointContainer endpointContainer = EndpointContainer.getInstance();

    public PostHandler(HttpExchange exchange){
        this.exchange = exchange;
    }

    @Override
    public void run() {

        Method method = endpointContainer.getMethod(this.exchange.getRequestURI().getPath());

        try {
            method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance());
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

}
