package advance.nothing.lib;

import java.util.List;

public class Response<T> {

    private int status;
    private String reason;
    private List<Header> headers;
    private TypedInput body;

    public Response(int status, String reason, List<Header> headers, TypedInput body) {
        this.status = status;
        this.reason = reason;
        this.headers = headers;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public TypedInput getBody() {
        return body;
    }
}
