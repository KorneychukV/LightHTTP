package ru.vkorneychuk.lightHTTP.defaultResponseTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    Timestamp timestamp;
    int status;
    String error;
    String message;
    String path;

}
