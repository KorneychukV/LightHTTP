package ru.vkorneychuk.lightHTTP.defaultResponseTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    Timestamp timestamp;
    HTTPStatus status;
    String message;
    String path;

    @Override
    public String toString() {
        return "{" +
                "\"timestamp\":"  +   "\""    +   timestamp + "\"" +
                ", \"code\":"                 +   status.code +
                ", \"error\":"    +   "\""    +   status.message + "\"" +
                ", \"message\":"  +   "\""    +   message + '\"' +
                ", \"path\":"     +   "\""    +   path + '\"' +
                '}';
    }
}
