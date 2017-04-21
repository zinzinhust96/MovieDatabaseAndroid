package group2.ictk59.moviedatabase.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 3/25/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    protected Toolbar activateToolbar(){
        if (mToolbar == null){
            mToolbar = (Toolbar) findViewById(R.id.app_bar);
            if (mToolbar != null){
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnable(){
        activateToolbar();
        if (mToolbar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return mToolbar;
    }
}
