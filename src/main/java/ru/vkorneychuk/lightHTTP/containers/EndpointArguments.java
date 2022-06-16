package ru.vkorneychuk.lightHTTP.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;

@Getter
@Setter
@AllArgsConstructor
public class EndpointArguments {

    Class<?> type;
    String name;
    Class<?> annotation;

}
