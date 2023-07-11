package http;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    public boolean isPost(){
        return this == POST;
    }
}
