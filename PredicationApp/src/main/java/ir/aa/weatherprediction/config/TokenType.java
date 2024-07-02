package ir.aa.weatherprediction.config;

public enum TokenType {

    BEARER("Bearer"), API_TOKEN("ApiToken");

    String name;
    TokenType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
