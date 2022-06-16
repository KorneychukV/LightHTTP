package ru.vkorneychuk.lightHTTP.containers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointArgument {

    Class<?> type = null;
    String argumentName = null;
    String requestParameterName = null;
    Class<?> annotation = null;

}
