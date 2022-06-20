package ru.vkorneychuk.lightHTTP.test.DTO;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
// TODO Class must be serializable
public class Person implements Serializable {

    String name;
    Integer age;

}
