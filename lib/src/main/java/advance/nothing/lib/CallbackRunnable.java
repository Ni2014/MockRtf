package advance.nothing.lib;

import java.util.concurrent.Executor;

public abstract class CallbackRunnable<T> implements Runnable {

    private Callback<T> mCallback;
    private Executor mCallbackExecutor;


    public CallbackRunnable(Callback<T> callback, Executor callbackExecutor) {
        mCallback = callback;
        mCallbackExecutor = callbackExecutor;
    }

    @Override
    public void run() {
        final Object entity = obtainEntity();
        mCallbackExecutor.execute(new Runnable() {
            @Override
            public void run() {

                mCallback.onSuccess((T) entity);
            }
        });
    }

    public abstract Object obtainEntity();
}
