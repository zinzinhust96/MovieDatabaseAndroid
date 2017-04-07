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

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.fragment.CelebsFragment;
import group2.ictk59.moviedatabase.fragment.HomeFragment;
import group2.ictk59.moviedatabase.fragment.MoviesFragment;
import group2.ictk59.moviedatabase.fragment.WatchlistFragment;
import group2.ictk59.moviedatabase.model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LOG_TAG = "Main Activity";

    private String[] mMenuTitles;

    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SharedPreferences app_preferences;
    private FragmentManager fm;

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
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();

        //get stored username, new access_token and log in state
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        User currentUser = new User();
        currentUser.setUsername(app_preferences.getString(Constants.USERNAME, ""));
        RESTServiceApplication.getInstance().setUser(currentUser);
        boolean isLogin = app_preferences.getBoolean(Constants.ISLOGIN, false);
        RESTServiceApplication.getInstance().setLogin(isLogin);
        String refreshToken = app_preferences.getString(Constants.REFRESH_TOKEN, "");
        Log.d(Constants.REFRESH_TOKEN, refreshToken);

//        if (isLogin){
//            Ion.with(getApplicationContext())
//                    .load("http://localhost:5000/api/user/refresh_token")
//                    .setHeader(Constants.TOKEN, refreshToken)
//                    .asString()
//                    .setCallback(new FutureCallback<String>() {
//                        @Override
//                        public void onCompleted(Exception e, String result) {
//                            Log.d(Constants.REFRESH_TOKEN, result);
//                        }
//                    });
//        }
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //default item check
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    protected void onResume() {
        setUpNavigation();
        super.onResume();
    }

    private void setUpNavigation(){
        View headerview = navigationView.getHeaderView(0);
        TextView profilename = (TextView) headerview.findViewById(R.id.nav_username);
        final boolean isLogin = RESTServiceApplication.getInstance().isLogin();
        if (isLogin){
            showLogin(false);
            profilename.setText(RESTServiceApplication.getInstance().getUser().getUsername());
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
            getSupportActionBar().setTitle(mMenuTitles[0]);
            fm.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        } else if (id == R.id.nav_movie) {
            getSupportActionBar().setTitle(mMenuTitles[1]);
            fm.beginTransaction().replace(R.id.content_frame, new MoviesFragment()).commit();
        } else if (id == R.id.nav_celeb) {
            getSupportActionBar().setTitle(mMenuTitles[2]);
            fm.beginTransaction().replace(R.id.content_frame, new CelebsFragment()).commit();
        } else if (id == R.id.nav_watchlist) {
            getSupportActionBar().setTitle(mMenuTitles[3]);
            if (!RESTServiceApplication.getInstance().isLogin()){
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }else{
                int size = navigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
                fm.beginTransaction().replace(R.id.content_frame, new WatchlistFragment()).commit();
            }
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (id == R.id.nav_logout) {
            RESTServiceApplication.getInstance().setLogin(false);
            app_preferences.edit().putBoolean(Constants.ISLOGIN, false).apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            fm.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
            navigationView.setCheckedItem(R.id.nav_home);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
