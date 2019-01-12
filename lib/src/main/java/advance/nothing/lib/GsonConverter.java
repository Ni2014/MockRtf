package advance.nothing.lib;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import advance.nothing.lib.utils.MimeUtil;

public class GsonConverter extends Converter {
    private Gson gson;



    public GsonConverter(Gson gson) {
        this.gson = gson;
    }


    @Override
    public Object fromBody(TypedInput body, Type type) {
        String charset = MimeUtil.parseCharset(body.mimeType());
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(body.in(),charset);
            return gson.fromJson(isr,type);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (isr != null){
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
