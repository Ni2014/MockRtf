package advance.nothing.mockrtf_0111;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.List;

import advance.nothing.lib.Callback;
import advance.nothing.lib.GsonConverter;
import advance.nothing.lib.RestAdapter;
import advance.nothing.lib.annotations.GET;
import advance.nothing.lib.annotations.Path;

public class MainActivity extends AppCompatActivity {

    static final String API_URL = "https://api.github.com";
    private final String tag = this.getClass().getSimpleName();

    Button btnSync;
    Button btnAsync;
    RestAdapter restAdapter;

    interface GithubService{
        @GET("/repos/{owner}/{repo}/contributors")
        List<Contributor> contributorSync(@Path("owner") String owner,@Path("repo") String repo);

        @GET("/repos/{owner}/{repo}/contributors")
        void contributorAsync(@Path("owner") String owner, @Path("repo") String repo, Callback<List<Contributor>> callback);
    }

    class Contributor {
        String login;
        int contributions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSync = findViewById(R.id.btn_sync);
        btnAsync = findViewById(R.id.btn_async);

        restAdapter = new RestAdapter.Builder()
                .server(API_URL)
                .convert(new GsonConverter(new Gson()))
                .build();
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSyncRequest();
            }
        });

        btnAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAsyncRequest();
            }
        });

    }

    private void doAsyncRequest() {
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
    }

    private void doSyncRequest() {
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
    }
}
