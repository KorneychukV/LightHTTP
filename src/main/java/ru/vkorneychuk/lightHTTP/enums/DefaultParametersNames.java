package ru.vkorneychuk.lightHTTP.enums;

public enum DefaultParametersNames {
    PORT("port"),
    DEFAULT_API_PATH("default-api-path");

    private final String name;

    DefaultParametersNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
