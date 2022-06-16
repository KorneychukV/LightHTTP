package ru.vkorneychuk.lightHTTP.containers;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.List;

@Getter
@Setter
public class EndpointMetaData {

    String endpointPath;
    Method method;
    List<EndpointArgument> arguments;
    Class<?> responseType;

}
