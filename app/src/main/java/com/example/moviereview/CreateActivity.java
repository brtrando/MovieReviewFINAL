package com.example.moviereview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    EditText titleCreate, scoreCreate, commentCreate;
    Button btSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        titleCreate = findViewById(R.id.titleCreate);
        scoreCreate = findViewById(R.id.scoreCreate);
        commentCreate = findViewById(R.id.commentCreate);
        btSubmit = findViewById(R.id.btSubmit);
        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleCreate.getText().toString();
                String score = scoreCreate.getText().toString();
                String comment = commentCreate.getText().toString();
                String url = "http://172.20.10.2:7000/reviews";

                JSONObject object = new JSONObject();
                try {
                    object.put("title", title);
                    object.put("score", score);
                    object.put("comment", comment);
                    object.put("username",akun);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest stringRequest = new JsonObjectRequest(
                        Request.Method.POST, url, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String message = response.getString("message");
                                    Toast.makeText(CreateActivity.this, message, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(CreateActivity.this, PageActivity.class));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                                Log.e("apiresult", errorMessage);
                                Toast.makeText(CreateActivity.this, "Create Review gagal", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                RequestQueue requestQueue = Volley.newRequestQueue(CreateActivity.this);
                requestQueue.add(stringRequest);
            }
        });
    }
}
