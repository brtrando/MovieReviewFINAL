package com.example.moviereview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    ImageView btBackWhiteRegis;
    EditText nameSignup,emailSignup,usrSignup,passwordSignup,passwordCon;
    Button btRegis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameSignup = findViewById(R.id.nameSignup);
        emailSignup = findViewById(R.id.emailSignup);
        usrSignup = findViewById(R.id.usrSignup);
        passwordSignup = findViewById(R.id.passwordSignup);
        passwordCon = findViewById(R.id.passwordConfirm);
        btRegis = findViewById(R.id.btRegis);

        btRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameSignup.getText().toString();
                String email = emailSignup.getText().toString();
                String username = usrSignup.getText().toString();
                String password = passwordSignup.getText().toString();
                String passCon = passwordCon.getText().toString();
                String url = "http://192.168.11.124:7000/signup";
                JSONObject object = new JSONObject();
                try {
                    object.put("name", name);
                    object.put("email", email);
                    object.put("username", username);
                    object.put("password", password);
//                    object.put("passCon",passCon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!password.equals(passCon)) {
                    Toast.makeText(SignupActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                } else if (password.equals("") || username.equals("")) {
                    Toast.makeText(SignupActivity.this, "Username or Password cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObjectRequest stringRequest = new JsonObjectRequest(
                            Request.Method.POST, url, object,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Toast.makeText(SignupActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        String msg = response.getString("message");
                                        if (msg.equals("Pendaftaran pengguna berhasil."))
                                        {
                                            Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                                            startActivity(intent);

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("apiresult", error.getMessage() != null ? error.getMessage() : "Unknown error");
                                    Toast.makeText(SignupActivity.this, "Sign up gagal", Toast.LENGTH_SHORT).show();
                                }
                            });
                    // Add the request to the RequestQueue.
                    Volley.newRequestQueue(SignupActivity.this).add(stringRequest);
                }
            }
        });
    }
}