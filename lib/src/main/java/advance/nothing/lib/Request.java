package advance.nothing.lib;

import android.util.Log;

import java.util.List;

public class Request {
    private String method;
    private String url;
    private List<Header> headers;
    private TypedOutput body;

    private final String tag = this.getClass().getSimpleName();

    public Request(String method, String url, List<Header> headers, TypedOutput body) {
        this.method = method;
        this.url = url;
        Log.e(tag,"url in req:" + url);
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public TypedOutput getBody() {
        return body;
    }
}
