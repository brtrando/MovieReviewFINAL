package com.example.moviereview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.usrLogin);
        passwordEditText = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.btLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String url = "http://192.168.11.124:7000/login";
                JSONObject object = new JSONObject();
                try {
                    object.put("username",username);
                    object.put("password",password);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                JsonObjectRequest stringRequest = new JsonObjectRequest(
                        Request.Method.POST,url,object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    String msg = response.getString("message");

                                    if (msg.equals("Login successful"))
                                    {
                                        Intent intent = new Intent(LoginActivity.this,PageActivity.class);
                                        intent.putExtra("username",username);
                                        startActivity(intent);

                                    }
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("apiresult", error.getMessage() != null ? error.getMessage() : "Unknown error");
                                Toast.makeText(LoginActivity.this, "Login gagal", Toast.LENGTH_SHORT).show();
                            }

                        });
                // Add the request to the RequestQueue.
                Volley.newRequestQueue(LoginActivity.this).add(stringRequest);


            }

        });
    }

}
