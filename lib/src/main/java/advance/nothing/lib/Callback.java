package advance.nothing.lib;


public interface Callback<T> {
    void onSuccess(T t);

    void onFailure(Throwable exception);
}
