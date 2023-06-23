package com.example.moviereview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class ReqmovieActivity extends AppCompatActivity {
    EditText titleReq, year_release;
    Button btSubmitReq;
    private static final String REQUEST_URL = "http://172.22.104.156:7000/reqmovies";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reqmovie);

        titleReq = findViewById(R.id.titleReq);
        year_release = findViewById(R.id.yearReq);
        btSubmitReq = findViewById(R.id.btSubmitRequest);

        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");

        btSubmitReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                Intent intent = new Intent(ReqmovieActivity.this, PageActivity.class);
                intent.putExtra("username",akun);
                startActivity(intent);
            }
        });
    }

    private void sendRequest() {
        String title = titleReq.getText().toString().trim();
        String releaseYear = year_release.getText().toString().trim();

        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");

        // Validasi apakah judul dan tahun rilis sudah diisi
        if (title.isEmpty() || releaseYear.isEmpty()) {
            Toast.makeText(this, "Please enter title and release year", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek JSON untuk dikirim ke API
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", akun);
            requestBody.put("title", title);
            requestBody.put("release_year", releaseYear);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Kirim permintaan POST HTTP ke API untuk meminta film
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REQUEST_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(ReqmovieActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReqmovieActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Tambahkan permintaan ke antrian permintaan Volley
        Volley.newRequestQueue(this).add(request);
    }
}
