package advance.nothing.lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlConnectionClient implements Client {
    @Override
    public Response execute(Request request) throws IOException {
        HttpURLConnection connection = openConnection(request);
        prepareRequest(connection,request);
        return readResponse(connection);
    }

    protected HttpURLConnection openConnection(Request request) throws IOException{
        return (HttpURLConnection) new URL(request.getUrl()).openConnection();
    }

    void prepareRequest(HttpURLConnection connection,Request request) throws IOException{
        connection.setRequestMethod(request.getMethod());
        connection.setDoInput(true);

        if (request.getHeaders() != null){
            for (Header header : request.getHeaders()) {
                connection.addRequestProperty(header.getName(),header.getValue());
            }
        }


        TypedOutput body = request.getBody();
        if (body != null){
            connection.setDoOutput(true);
            connection.addRequestProperty("Content-Type",body.mimeType());
            long length = body.length();
            if (length != -1){
                connection.addRequestProperty("Content-Length",String.valueOf(length));
            }
            body.writeTo(connection.getOutputStream());
        }
    }


    Response readResponse(HttpURLConnection connection) throws IOException{
        int status = connection.getResponseCode();
        String reason = connection.getResponseMessage();

        List<Header> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
            String name = field.getKey();
            for (String value : field.getValue()) {
                headers.add(new Header(name,value));
            }
        }

        String mineType = connection.getContentType();
        int length = connection.getContentLength();
        InputStream stream;
        if (status >= 400){
            stream = connection.getErrorStream();
        }else {
            stream = connection.getInputStream();
        }
        TypedInputStream responseBody = new TypedInputStream(mineType, length, stream);
        return new Response(status,reason,headers,responseBody);
    }

    class TypedInputStream implements TypedInput{

        private String mimeType;
        private long length;
        private InputStream stream;

        public TypedInputStream(String mimeType, long length, InputStream stream) {
            this.mimeType = mimeType;
            this.length = length;
            this.stream = stream;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public InputStream in() throws IOException {
            return stream;
        }
    }
}
