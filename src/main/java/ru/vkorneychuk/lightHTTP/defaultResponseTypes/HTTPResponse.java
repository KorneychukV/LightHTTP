package ru.vkorneychuk.lightHTTP.defaultResponseTypes;

import com.sun.net.httpserver.Headers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HTTPResponse {

    Object responseBody = null;
    Headers headers = new Headers();
    HTTPStatus status = HTTPStatus.OK;
}
