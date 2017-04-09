package group2.ictk59.moviedatabase.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import group2.ictk59.moviedatabase.Constants;
import group2.ictk59.moviedatabase.R;
import group2.ictk59.moviedatabase.RESTServiceApplication;

public class LoginActivity extends BaseActivity {

    EditText etUsername, etPassword;
    Button btSignIn, btCreateAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activateToolbarWithHomeEnable();

        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btSignIn = (Button)findViewById(R.id.btSignIn);
        btCreateAcc = (Button)findViewById(R.id.btCreateAcc);

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                etUsername.setError(null);
                etPassword.setError(null);

                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(username)){
                    etUsername.setError(getString(R.string.error_field_require));
                    focusView = etUsername;
                    cancel = true;
                }else if (TextUtils.isEmpty(password)) {
                    etPassword.setError(getString(R.string.error_field_require));
                    focusView = etPassword;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                    showProgress(false);
                }else{
                    //magic happen
                    final JsonObject user = new JsonObject();
                    user.addProperty(Constants.USERNAME, username);
                    user.addProperty(Constants.PASSWORD, password);

                    Ion.with(getApplicationContext())
                            .load("http://localhost:5000/api/user")
                            .setJsonObjectBody(user)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        String status = jsonObject.getString(Constants.STATUS);
                                        if (status.equalsIgnoreCase("success")){
                                            RESTServiceApplication.getInstance().setUsername(username);
                                            RESTServiceApplication.getInstance().setAccessToken(jsonObject.getString(Constants.ACCESS_TOKEN));
                                            RESTServiceApplication.getInstance().setLogin(true);
//                                            Log.i("LOG", jsonObject.getString("access_token"));
                                            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = app_preferences.edit();
                                            editor.putString(Constants.USERNAME, username);
                                            editor.putString(Constants.REFRESH_TOKEN, jsonObject.getString(Constants.REFRESH_TOKEN));
                                            editor.putBoolean(Constants.ISLOGIN, true);
                                            editor.apply();

                                            Toast.makeText(LoginActivity.this, "Hello " + username + "!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }else{
                                            showProgress(false);
                                            Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                }
                            });
                }
            }
        });

        btCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void showProgress(final boolean isShow){
        findViewById(R.id.login_progress).setVisibility(isShow ? View.VISIBLE : View.GONE);
        findViewById(R.id.loginForm).setVisibility(isShow ? View.GONE : View.VISIBLE);
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

}
