package group2.ictk59.moviedatabase.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.fragment.CelebsFragment;
import group2.ictk59.moviedatabase.fragment.HomeFragment;
import group2.ictk59.moviedatabase.fragment.MoviesFragment;
import group2.ictk59.moviedatabase.fragment.WatchlistFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LOG_TAG = "Main Activity";

    private String[] mMenuTitles;

    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SharedPreferences app_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mMenuTitles = getResources().getStringArray(R.array.menu_array);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    //show hamburger
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.syncState();
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });

        //first fragment shown
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();

        app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isLogin = app_preferences.getBoolean(Constants.ISLOGIN, false);

        if (isLogin){
            //get stored username, new access_token and log in state
            RESTServiceApplication.getInstance().setUsername(app_preferences.getString(Constants.USERNAME, ""));
            RESTServiceApplication.getInstance().setLogin(true);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //default item check
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    protected void onResume() {
        View headerview = navigationView.getHeaderView(0);
        TextView profilename = (TextView) headerview.findViewById(R.id.nav_username);
        final boolean isLogin = RESTServiceApplication.getInstance().isLogin();
        if (isLogin){
            showLogin(false);
            profilename.setText(RESTServiceApplication.getInstance().getUsername());

            final String refreshToken = app_preferences.getString(Constants.REFRESH_TOKEN, "");
            Log.d(Constants.REFRESH_TOKEN, refreshToken);
            Ion.with(getApplicationContext())
                    .load("GET", "http://localhost:5000/api/user/refresh_token?" + Constants.REFRESH_TOKEN + "=" + refreshToken)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String status = jsonObject.getString(Constants.STATUS);
                                if (status.equalsIgnoreCase(Constants.SUCCESS)){
                                    String accessToken = jsonObject.getString(Constants.NEW_ACCESS_TOKEN);
                                    RESTServiceApplication.getInstance().setAccessToken(accessToken);
                                    Log.d(Constants.TOKEN, "http://localhost:5000/api/user?" + Constants.ACCESS_TOKEN + "=" + accessToken);
                                    Ion.with(getApplicationContext())
                                            .load("GET", "http://localhost:5000/api/user?" + Constants.ACCESS_TOKEN + "=" + accessToken)
                                            .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String result) {
                                                    try {
                                                        JSONArray jsonArray = new JSONObject(result).getJSONArray(Constants.DATA)
                                                                .getJSONObject(0).getJSONObject(Constants.ATTRIBUTES)
                                                                .getJSONObject(Constants.WATCHLIST).getJSONArray(Constants.DATA);
                                                        List<Long> ids = new ArrayList<>();
                                                        for (int i = 0; i < jsonArray.length(); i++){
                                                            JSONObject jsonMovie = jsonArray.getJSONObject(i);
                                                            Long id = jsonMovie.getLong(Constants.ID);
                                                            Log.d(Constants.TOKEN, id.toString());
                                                            ids.add(id);
                                                        }
                                                        RESTServiceApplication.getInstance().setWatchlistId(ids);
                                                    } catch (JSONException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                            });
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    });
        }else{
            showLogin(true);
            profilename.setText("Sign in to IMDb");
        }

        //navigation item listener
        navigationView.setNavigationItemSelectedListener(this);

        //clickable navigation bar header
        LinearLayout header = (LinearLayout) headerview.findViewById(R.id.nav_header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        super.onResume();
    }

    private void showLogin(boolean isLogOut){
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_login).setVisible(isLogOut);
        nav_menu.findItem(R.id.nav_logout).setVisible(!isLogOut);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        } else if (id == R.id.nav_movie) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new MoviesFragment()).commit();
        } else if (id == R.id.nav_celeb) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new CelebsFragment()).commit();
        } else if (id == R.id.nav_watchlist) {
            if (!RESTServiceApplication.getInstance().isLogin()){
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }else{
                navigationView.setCheckedItem(R.id.nav_none);
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().replace(R.id.content_frame, new WatchlistFragment()).commit();
            }
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (id == R.id.nav_logout) {
            RESTServiceApplication.getInstance().setLogin(false);
            app_preferences.edit().putBoolean(Constants.ISLOGIN, false).apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

            //go home after log out
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
