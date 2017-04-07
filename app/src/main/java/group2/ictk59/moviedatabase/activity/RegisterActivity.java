package group2.ictk59.moviedatabase.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
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
import group2.ictk59.moviedatabase.model.User;

public class RegisterActivity extends BaseActivity {

    EditText etUsername, etPassword, etEmail;
    Button btSignIn, btCreateAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activateToolbarWithHomeEnable();

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etUsername = (EditText)findViewById(R.id.etUsername);
        btSignIn = (Button)findViewById(R.id.btSignIn);
        btCreateAcc = (Button)findViewById(R.id.btCreateAcc);

        btCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.setError(null);
                etUsername.setError(null);
                etPassword.setError(null);

                final String username = etUsername.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (!isPasswordValid(password)){
                    etPassword.setError(getString(R.string.error_password_length));
                    focusView = etPassword;
                    cancel = true;
                }else if(TextUtils.isEmpty(username)){
                    etUsername.setError(getString(R.string.error_field_require));
                    focusView = etUsername;
                    cancel = true;
                }

                if (TextUtils.isEmpty(email)){
                    etEmail.setError(getString(R.string.error_field_require));
                    focusView = etEmail;
                    cancel = true;
                }else if(!isEmailValid(email)){
                    etEmail.setError(getString(R.string.error_invalid_email));
                    focusView = etEmail;
                    cancel = true;
                }

                if (cancel){
                    focusView.requestFocus();
                }else{
                    final JsonObject user = new JsonObject();
                    user.addProperty(Constants.USERNAME, username);
                    user.addProperty(Constants.PASSWORD, password);
                    user.addProperty(Constants.EMAIL, email);

                    Ion.with(getApplicationContext())
                            .load("http://localhost:5000/api/register")
                            .setJsonObjectBody(user)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        String status = jsonObject.getString("status");
                                        if (status.equalsIgnoreCase("success")){
//                                            BaseActivity.isLogIn = true;
                                            User currentUser = new User();
                                            currentUser.setUsername(username);
                                            currentUser.setPassword(password);
                                            currentUser.setEmail(email);

                                            RESTServiceApplication.getInstance().setUser(currentUser);
                                            RESTServiceApplication.getInstance().setAccessToken(jsonObject.getString("access_token"));
                                            RESTServiceApplication.getInstance().setLogin(true);
//                                            Log.i("LOG", jsonObject.getString("access_token"));
                                            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = app_preferences.edit();
                                            editor.putString(Constants.USERNAME, username);
                                            editor.putString(Constants.REFRESH_TOKEN, jsonObject.getString(Constants.REFRESH_TOKEN));
                                            editor.putBoolean(Constants.ISLOGIN, true);
                                            editor.apply();
                                            Toast.makeText(RegisterActivity.this, "Successfully registered. Hello " + username + "!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }else{
                                            Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                }
                            });
                }
            }
        });

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private boolean isPasswordValid(String password){
        return password.length() > 4;
    }

    private boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        //return true of false as to whether the email address is considered valid
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
