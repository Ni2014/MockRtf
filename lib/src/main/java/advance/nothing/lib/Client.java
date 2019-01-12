package advance.nothing.lib;

import java.io.IOException;

public interface Client {

    Response execute(Request request) throws IOException;


    interface Provider{
        Client get();
    }

}
