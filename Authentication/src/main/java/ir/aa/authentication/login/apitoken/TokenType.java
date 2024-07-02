package ir.aa.authentication.login.apitoken;

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
