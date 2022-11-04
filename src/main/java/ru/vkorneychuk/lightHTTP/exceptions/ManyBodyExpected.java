package ru.vkorneychuk.lightHTTP.exceptions;

public class ManyBodyExpected extends RuntimeException {

    public ManyBodyExpected() {
        super("Method cannot contain more than one request body");
    }
}
