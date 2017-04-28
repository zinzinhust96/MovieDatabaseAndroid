package group2.ictk59.moviedatabase.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import group2.ictk59.moviedatabase.AlertDialogWrapper;
import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.provider.MySuggestionProvider;

public class SearchActivity extends BaseActivity {

    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        activateToolbarWithHomeEnable();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SearchActivity.this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPref.edit().putString(Constants.IMDB_QUERY, query).apply();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem menuItem = menu.findItem(R.id.search_view);
        mSearchView = (SearchView) menuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SearchActivity.this,
//                        MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
//                suggestions.saveRecentQuery(query, null);
//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                sharedPref.edit().putString(Constants.IMDB_QUERY, query).apply();
//                mSearchView.clearFocus();
//                finish();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return true;
//            }
//        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
            return true;
        }
        if (id == R.id.clear_history){
            AlertDialogWrapper.clearHistoryAlert(SearchActivity.this);
        }

        return super.onOptionsItemSelected(item);
    }
}
