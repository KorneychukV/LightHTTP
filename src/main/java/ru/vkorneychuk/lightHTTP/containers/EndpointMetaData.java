package ru.vkorneychuk.lightHTTP.containers;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.Map;

@Getter
@Setter
public class EndpointMetaData {

    String endpointPath;
    Method method;
    Map<String, EndpointArguments> arguments;
    Class<?> responseType;

}
