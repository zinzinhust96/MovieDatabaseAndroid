package group2.ictk59.moviedatabase.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import group2.ictk59.moviedatabase.GetActorJsonData;
import group2.ictk59.moviedatabase.GetMovieJsonData;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.recycleview.ComplexRecyclerViewAdapter;

public class SearchResultActivity extends BaseActivity {

    private static final String LOG_TAG = "SearchResultActivity";

    private RecyclerView rvSearchResult;
    private ComplexRecyclerViewAdapter mSearchResultViewAdapter;
    private TextView tvNoResults;
    private ProgressBar mProgressBar;
    private ArrayList<Object> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        activateToolbarWithHomeEnable();

        rvSearchResult = (RecyclerView)findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this));

        mSearchResultViewAdapter = new ComplexRecyclerViewAdapter(SearchResultActivity.this, new ArrayList<>());
        rvSearchResult.setAdapter(mSearchResultViewAdapter);

        tvNoResults = (TextView)findViewById(R.id.tvNoResults);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        items = new ArrayList<>();
        String query = getSavedPreferenceData(IMDB_QUERY);
        if (query.length() > 0) {
            ProcessMovieList processMovieList = new ProcessMovieList(query);
            processMovieList.execute();
            ProcessActorList processActorList = new ProcessActorList(query);
            processActorList.execute();
        }
    }

    private String getSavedPreferenceData(String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getString(key, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ProcessMovieList extends GetMovieJsonData {
        public ProcessMovieList(String name) {
            super(name);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData{

            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                items.addAll(getMovies());
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }

    public class ProcessActorList extends GetActorJsonData {
        public ProcessActorList(String name) {
            super(name);
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData{
            @Override
            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);
                items.addAll(getActors());
                if (items.isEmpty()){
                    tvNoResults.setVisibility(View.VISIBLE);
                }else {
                    mSearchResultViewAdapter.loadNewData(items);
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... strings) {
                return super.doInBackground(strings);
            }
        }
    }
}
