package com.example.thanh.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.thanh.instagram.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // == constants ==
    LinearLayout mLoginContainer;
    AnimationDrawable mAnimationDrawable;

    EditText username_et, password_et;
    TextView sign_up_button, forgot_pass_button;
    Button login_btn;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // configuration for the animation
        mLoginContainer = findViewById(R.id.login_container);
        mAnimationDrawable = (AnimationDrawable) mLoginContainer.getBackground(); // connect to the animation background
        mAnimationDrawable.setEnterFadeDuration(5000);
        mAnimationDrawable.setExitFadeDuration(2000);


        username_et = findViewById(R.id.user_name);
        password_et = findViewById(R.id.user_password);
        sign_up_button = findViewById(R.id.sign_up_bnt);
        forgot_pass_button = findViewById(R.id.forgot_pass_bnt);
        login_btn = findViewById(R.id.login_btn);
        mProgressDialog = new ProgressDialog(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // save more memory
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        forgot_pass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void logIn() {
        mProgressDialog.setTitle("Log In");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        final String username = username_et.getText().toString();
        final String password = password_et.getText().toString();

        /*
         * check the input values
         * prevent methods going on
         * use volley library to make request to database*/

        if (TextUtils.isEmpty(username)) {
            username_et.setError("Please fill in this field");
            username_et.requestFocus(); // zoom in to focus
            mProgressDialog.dismiss();
            return; // doesn't moving on
        }

        if (TextUtils.isEmpty(password)) {
            password_et.setError("Please fill in this field");
            password_et.requestFocus(); // zoom in to focus
            mProgressDialog.dismiss();
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URLS.login_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                mProgressDialog.dismiss();
                                JSONObject jsonObjectUser = jsonObject.getJSONObject("user");

                                Log.i("Check: ", "reached");

                                /*
                                 * Create an user object  from JSon data.
                                 * */
                                User user = new User(
                                        jsonObjectUser.getInt("id"),
                                        jsonObjectUser.getString("email"),
                                        jsonObjectUser.getString("username"),
                                        jsonObjectUser.getString("image"));

                                // store user data inside sharePreference
                                SharePreferenceManager.getInstance(getApplicationContext()).storeUserData(user);

                                // let user in
                                finish(); // do not use so much resources
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> authenticationVariables = new HashMap<>();
                authenticationVariables.put("username", username);
                authenticationVariables.put("password", password);
                return authenticationVariables;
            }

        };// end of the stringRequest

        VolleyHandler.getInstance(getApplicationContext()).addRequestToQueued(stringRequest);
    }

    // == start animation ==
    @Override
    protected void onResume() {
        super.onResume();
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        boolean isUserLogIn = SharePreferenceManager.getInstance(getApplicationContext()).isUserLogIn();
        if (isUserLogIn) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }
}
