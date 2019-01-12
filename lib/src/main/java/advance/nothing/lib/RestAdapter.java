package advance.nothing.lib;

import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import advance.nothing.lib.annotations.GET;
import advance.nothing.lib.annotations.Path;
import advance.nothing.lib.utils.Types;

public class RestAdapter {


    private final String tag = this.getClass().getSimpleName();

    private String mServerUrl;
    private Converter mConverter;
    private Executor mExecutor;

    public RestAdapter(Builder builder) {
        this.mServerUrl = builder.mServerUrl;
        this.mConverter = builder.mConverter;
        mExecutor = Executors.newCachedThreadPool();
    }


    public static class Builder{
        private String mServerUrl;
        private Converter mConverter;

        public Builder server(String serverUrl){
            this.mServerUrl = serverUrl;
            return this;
        }


        public Builder convert(Converter converter){
            this.mConverter = converter;
            return this;
        }

        public RestAdapter build(){
            return new RestAdapter(this);
        }

    }


    public <T> T create(Class<T> apiServiceClass){
        if (!apiServiceClass.isInterface()){
            throw new RuntimeException("support interface only!");
        }
        return (T) Proxy.newProxyInstance(apiServiceClass.getClassLoader(),new Class<?>[]{apiServiceClass},new RestHandler());
    }


    class RestHandler implements InvocationHandler{

        @Override
        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
            Type returnType = method.getGenericReturnType();
            Object returnObj = null;
            if (returnType == void.class){
                // 异步
                Callback<?> callback = (Callback<?>) args[args.length -1];
                // 1 取到Callback上泛型类型参数Type
                Type responseObjectType = null;
                Type lastArgType = null;
                Class<?> lastArgClass = null;
                Type[] parameterTypes = method.getGenericParameterTypes();

                Type typeToCheck = parameterTypes[parameterTypes.length - 1];
                lastArgType = typeToCheck;

                if (typeToCheck instanceof ParameterizedType) {
                    typeToCheck = ((ParameterizedType) typeToCheck).getRawType();
                }
                if (typeToCheck instanceof Class) {
                    lastArgClass = (Class<?>) typeToCheck;
                }
                lastArgType = Types.getSupertype(lastArgType, Types.getRawType(lastArgType), Callback.class);
                if (lastArgType instanceof ParameterizedType) {
                    Log.e(tag,"lastArgType instanceof ParameterizedType");
                    Type[] types = ((ParameterizedType) lastArgType).getActualTypeArguments();
                    for (int i = 0; i < types.length; i++) {
                        Type type = types[i];
                        if (type instanceof WildcardType) {
                            types[i] = ((WildcardType) type).getUpperBounds()[0];
                        }
                    }
                    responseObjectType = types[0];
                }

                mConverter.setType(responseObjectType);
                final Type finalEntityType = responseObjectType;
                // 2 执行
                mExecutor.execute(new CallbackRunnable(callback,new MainThreadExecutor()) {

                    @Override
                    public Object obtainEntity() {
                        Log.e(tag,"obtainEntity in：" + Thread.currentThread().getName());
                        return mConverter.fromBody(parseAnnotationAndReq(method, args).getBody(),finalEntityType);
                    }
                });

            }else {
                // 同步 阻塞
                Response response = parseAnnotationAndReq(method, args);
                mConverter.setType(returnType);
                returnObj = mConverter.fromBody(response.getBody(), returnType);
                return  returnObj;
            }
            return null;
        }
    }

    /**
     * 同步异步都会调用的方法，解析注解并发请求返回Response
     * @param method
     * @param args
     * @return
     */
    private Response parseAnnotationAndReq(Method method,Object[] args){
        Annotation[] annotations = method.getAnnotations();
        if (annotations.length > 0 ){
            for (Annotation annotation : annotations) {
                if (annotation instanceof GET){
                    String apiPath = ((GET) annotation).value();
                    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                    int count = parameterAnnotations.length;
                    String finalApiPath= apiPath;
                    for (int i = 0; i < count; i++) {
                        Annotation[] parameterAnnotation = parameterAnnotations[i];
                        if (parameterAnnotation != null){
                            for (Annotation annotationInner : parameterAnnotation) {
                                if (annotationInner instanceof Path){
                                    String value = ((Path) annotationInner).value();
                                    String oldStr = "{" + value +"}";
                                    if (apiPath.contains(oldStr)){
                                        finalApiPath = finalApiPath.replace(oldStr, (CharSequence) args[i]);
                                    }
                                }
                            }
                        }
                    }


                    Request request = new Request("GET",mServerUrl + finalApiPath,null,null);
                    Response response = null;
                    try {
                        response = new UrlConnectionClient().execute(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return response;
                }
            }
        }
        return null;

    }
}
