package group2.ictk59.moviedatabase;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZinZin on 4/6/2017.
 */

public class RESTServiceApplication extends Application {
    private static RESTServiceApplication instance; //using singleton pattern
    //create a single class
    //that class's only instantiated once for the entire life of the application

    private String username;
    private String accessToken;
    private boolean isLogin;
    private List<Long> watchlistId;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        watchlistId = new ArrayList<>();
        isLogin = false;
    }

    public static RESTServiceApplication getInstance(){
        if (instance == null){
            instance = new RESTServiceApplication();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<Long> getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(List<Long> watchlistId) {
        this.watchlistId = watchlistId;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}