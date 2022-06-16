package ru.vkorneychuk.lightHTTP.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.vkorneychuk.lightHTTP.handlers.methodHandlers.PostHandler;

import java.io.IOException;

public class RequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestMethod = exchange.getRequestMethod();
        exchange.getRequestURI();

        switch (requestMethod) {
            case "POST" -> {
                PostHandler postHandler = new PostHandler(exchange);
                Thread getThread = new Thread(postHandler);
                getThread.start();
            }
            default -> System.out.println("Don't support. Sorry:(");
        }

    }
}
