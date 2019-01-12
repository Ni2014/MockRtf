## MockRtf

> 类似Retrofit库

### 用法

1.定义接口Interface(支持同步和异步切换回主线程，支持默认Gson反序列化为业务对象)

```java
    interface GithubService{
        @GET("/repos/{owner}/{repo}/contributors")
        List<Contributor> contributorSync(@Path("owner") String owner,@Path("repo") String repo);

        @GET("/repos/{owner}/{repo}/contributors")
        void contributorAsync(@Path("owner") String owner, @Path("repo") String repo, 
        Callback<List<Contributor>> callback);
    }

```
2.构建RestAdapter对象
```java
    RestAdapter restAdapter = new RestAdapter.Builder()
        .server(API_URL)
        .convert(new GsonConverter(new Gson()))
        .build();

```
3.调用即发起请求

* 同步
```java
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Contributor> contributors = restAdapter.create(GithubService.class)
                        .contributorSync("Ni2014", "ApplyApt");
                for (Contributor contributor : contributors) {
                    Log.e(tag,"=> " + contributor.login + ":" + contributor.contributions);
                }
            }
        }).start();

```

* 异步
```java
        restAdapter.create(GithubService.class)
                .contributorAsync("Ni2014", "ApplyApt", new Callback<List<Contributor>>() {
                    @Override
                    public void onSuccess(List<Contributor> contributors) {
                        Log.e(tag,"now in :" + Thread.currentThread().getName());
                        for (Contributor contributor : contributors) {
                            Log.e(tag,"=> " + contributor.login + ":" + contributor.contributions);
                        }
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Log.e(tag,"error :" + exception);
                    }
                });

```

### V1

1. 仅仅熟悉源码还是不太够，或者说最好自己写一个类似的库能进一步加深理解，不求完备不顾核心思路要类似；
2. 流行(成熟)的三方库源码都是经过一些演化后的，很多intent(意图)和code noise不好逆向揣度，所以推荐看库的早起版本代码；
3. 也因为这样所以才会发现Retrofit早期的用法结合早期的OkHttp库，至于同步异步的分野在后续Okhttp的Call都做了区分，而第一个版本的Retrofit显然不是这样的，
另外，也发现了第一个版本的判断@Path字段的正则是错误的，这些彩蛋也只有在推敲源码并实现的时候才意外发现～；
4. 后续完善其他Http谓词和相关功能演进；













