package group2.ictk59.moviedatabase.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;
import group2.ictk59.moviedatabase.fragment.ActorListFragment;
import group2.ictk59.moviedatabase.fragment.ActorProfileFragment;
import group2.ictk59.moviedatabase.fragment.CelebsFragment;
import group2.ictk59.moviedatabase.fragment.HomeFragment;
import group2.ictk59.moviedatabase.fragment.MovieListFragment;
import group2.ictk59.moviedatabase.fragment.MovieProfileFragment;
import group2.ictk59.moviedatabase.fragment.MoviesFragment;
import group2.ictk59.moviedatabase.fragment.OnItemSelectedListener;
import group2.ictk59.moviedatabase.fragment.SearchResultFragment;
import group2.ictk59.moviedatabase.fragment.WatchlistFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemSelectedListener {

    public static final String LOG_TAG = "Main Activity";

    private SearchView mSearchView;
    private MenuItem searchItem;

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
            setUpUser();
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

    private void setUpUser(){
        //get stored username, new access_token and log in state
        RESTServiceApplication.getInstance().setUsername(app_preferences.getString(Constants.USERNAME, ""));
        RESTServiceApplication.getInstance().setLogin(true);
        final String refreshToken = app_preferences.getString(Constants.REFRESH_TOKEN, "");
        Log.d(Constants.REFRESH_TOKEN, refreshToken);
        Ion.with(getApplicationContext())
                .load("GET", Constants.BASE_URL + "/api/user/refresh_token?" + Constants.REFRESH_TOKEN + "=" + refreshToken)
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
                                Log.d(Constants.TOKEN, Constants.BASE_URL + "/api/user?" + Constants.ACCESS_TOKEN + "=" + accessToken);
                                getRESTApplicationInfo(accessToken);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        } catch (NullPointerException e1){
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onMovieSelected(Long id) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ID, id);
        final MovieProfileFragment movieProfileFragment = new MovieProfileFragment();
        movieProfileFragment.setArguments(bundle);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ft.replace(R.id.content_frame, movieProfileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }, 500);
    }

    @Override
    public void onActorSelected(Long id) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ID, id);
        final ActorProfileFragment actorProfileFragment = new ActorProfileFragment();
        actorProfileFragment.setArguments(bundle);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ft.replace(R.id.content_frame, actorProfileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }, 500);
    }

    @Override
    public void onViewAddSelected(final Long id) {
        JsonObject object = new JsonObject();
        object.addProperty("action", "modify_watchlist");
        object.addProperty("movie_id", id.toString());
        if (RESTServiceApplication.getInstance().isLogin()){
            Ion.with(this)
                    .load(Constants.BASE_URL + "/api/user/action?" + Constants.ACCESS_TOKEN + "=" + RESTServiceApplication.getInstance().getAccessToken())
                    .setJsonObjectBody(object)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String status = jsonObject.getString(Constants.STATUS);
                                if (status.equalsIgnoreCase(Constants.SUCCESS)){
                                    //add to list<long> watchlistId
                                    List<Long> watchlistId = RESTServiceApplication.getInstance().getWatchlistId();
                                    watchlistId.add(id);
                                    RESTServiceApplication.getInstance().setWatchlistId(watchlistId);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        }else {
            Toast.makeText(this, R.string.login_alert, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onViewRemoveSelected(final Long id) {
        JsonObject object = new JsonObject();
        object.addProperty("action", "modify_watchlist");
        object.addProperty("movie_id", id.toString());
        Ion.with(this)
                .load(Constants.BASE_URL + "/api/user/action?" + Constants.ACCESS_TOKEN + "=" + RESTServiceApplication.getInstance().getAccessToken())
                .setJsonObjectBody(object)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString(Constants.STATUS);
                            if (status.equalsIgnoreCase(Constants.SUCCESS)){
                                //add to list<long> watchlistId
                                List<Long> watchlistId = RESTServiceApplication.getInstance().getWatchlistId();
                                watchlistId.remove(id);
                                RESTServiceApplication.getInstance().setWatchlistId(watchlistId);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void toMovieListFragment(String orderBy, boolean desc) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("orderby", orderBy);
        bundle.putBoolean("desc", desc);
        MovieListFragment movieListFragment = new MovieListFragment();
        movieListFragment.setArguments(bundle);
        ft.replace(R.id.content_frame, movieListFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void toActorListFragment(String orderBy, boolean desc) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("orderby", orderBy);
        bundle.putBoolean("desc", desc);
        ActorListFragment actorListFragment = new ActorListFragment();
        actorListFragment.setArguments(bundle);
        ft.replace(R.id.content_frame, actorListFragment);
        ft.addToBackStack(null);
        ft.commit();
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

        searchItem = menu.findItem(R.id.menu_search);
        mSearchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                FragmentManager fm = getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.IMDB_QUERY, query);
                SearchResultFragment searchResultFragment = new SearchResultFragment();
                searchResultFragment.setArguments(bundle);
                fm.beginTransaction().replace(R.id.content_frame, searchResultFragment).addToBackStack(null).commit();
//                mSearchView.clearFocus();
                // anywhere you have a search item selected and are ready to close the search view...
                // clear the search query in the toolbar so it is empty the next time the user
                // opens the search view
                mSearchView.setQuery("", false);  // 2nd argument is false so it doesn't re-submit a blank search query
                // hide the SearchView action so the toolbar returns to normal mode showing the title and other menu items, etc.
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
