package advance.nothing.lib;

import java.lang.reflect.Type;

public abstract class Converter {

    private Type type;


    public void setType(Type type) {
        this.type = type;
    }

    public abstract Object fromBody(TypedInput body, Type type);

}
