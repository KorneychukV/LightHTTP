package ru.vkorneychuk.lightHTTP.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;
import ru.vkorneychuk.lightHTTP.exceptions.HTTPExceptionResponse;
import ru.vkorneychuk.lightHTTP.handlers.methodHandlers.GetHandler;
import ru.vkorneychuk.lightHTTP.handlers.methodHandlers.PostHandler;
import ru.vkorneychuk.lightHTTP.handlers.methodHandlers.RequestMethod;

import java.io.IOException;

@Slf4j
public class RequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestMethodType = exchange.getRequestMethod();
        exchange.getRequestURI();

        RequestMethod requestMethod = switch (requestMethodType) {
            case "POST" -> new PostHandler(exchange);
            case "GET" -> new GetHandler(exchange);
            default -> null;
        };

        if (requestMethod == null) {
            log.warn("Request method doesn't support. Sorry:(");
            ExceptionHandler.sendException(HTTPStatus.CONFLICT, "Request method doesn't support. Sorry:(", exchange);
        }

        Thread getThread = new Thread(requestMethod);
        getThread.start();

    }
}
