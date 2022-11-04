package ru.vkorneychuk.lightHTTP.handlers.methodHandlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import ru.vkorneychuk.lightHTTP.annotations.arguments.GetParameter;
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

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class GetHandler implements RequestMethod {

    private final HttpExchange exchange;
    private final String currentURI;
    private final HashMap<String, String> getParameters = new HashMap<>();

    public GetHandler(HttpExchange exchange){
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

    public List<Object> fillArguments(EndpointMetaData currentEndpoint) throws HTTPExceptionResponse {
        List<Object> arguments = new ArrayList<>();

        extractGetParameters();

        for (EndpointArgument argument: currentEndpoint.getArguments()){
            // Arguments order is important
            if (argument.getAnnotation() == RequestBody.class){
                log.debug("Тело цикла не используется при GET-запросах");
                arguments.add(null);
            } else if (argument.getAnnotation() == GetParameter.class){
                arguments.add(fillGetParameter(argument.getType(), argument.getRequestParameterName()));
            }else if (argument.getAnnotation() == RequestHeader.class){
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
    public EndpointMetaData getEndpointMetaData() throws HTTPExceptionResponse {
        EndpointMetaData endpointMetaData = EndpointContainer.getInstance().getEndpoint(this.currentURI);
        if (endpointMetaData.getRequestMethod().equals("GET")){
            return endpointMetaData;
        } else {
            throw new HTTPExceptionResponse(HTTPStatus.METHOD_NOT_ALLOWED, "Отсутствует вызываемый метод");
        }
    }

    @Override
    public Object extractRequestBody(OutputStream requestBodyStream, Class<?> argumentType) {
        return null;
    }

    private Object fillGetParameter(Class<?> parameterType, String parameterName) throws HTTPExceptionResponse {
        String parameter = getParameters.get(parameterName);
        if (parameter == null){
            return null;
        }
        try {
            return castString(parameterType.getSimpleName(), parameter);
        } catch (ClassCastException e){
            throw new HTTPExceptionResponse(HTTPStatus.SERVER_INTERNAL_ERROR,
                    String.format("Wrong parameter %s type", parameterName));
        }
    }

    private Object castString(String requiredType, String value){
        return switch (requiredType){
            case "Integer", "int" -> Integer.valueOf(value);
            case "Long", "long" -> Long.valueOf(value);
            case "Byte", "byte" -> Byte.valueOf(value);
            case "Short", "short" -> Short.valueOf(value);
            case "Float", "float" -> Float.valueOf(value);
            case "Double", "double" -> Double.valueOf(value);
            case "String" -> value;
            default -> throw new ClassCastException("Не удалось извлечь параметр");
        };
    }

    @Override
    public void extractGetParameters(){
        // Todo добавить парсинг массива
        String query = exchange.getRequestURI().getQuery();

        if(query == null) return;

        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                this.getParameters.put(entry[0], entry[1]);
            } else{
                this.getParameters.put(entry[0], "");
            }
        }
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
