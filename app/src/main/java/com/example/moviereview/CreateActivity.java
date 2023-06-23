package com.example.moviereview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateActivity extends AppCompatActivity {

    Spinner movieSpinner;
    EditText scoreCreate, commentCreate;
    Button btSubmit;
    Set<String> reviewedMovies; // Set untuk menyimpan judul film yang sudah diberikan review sebelumnya

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        movieSpinner = findViewById(R.id.spinnerCreate);
        scoreCreate = findViewById(R.id.scoreCreate);
        commentCreate = findViewById(R.id.commentCreate);
        btSubmit = findViewById(R.id.btSubmit);
        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");

        reviewedMovies = new HashSet<>();

        // Mendapatkan data film dari API server
        String moviesUrl = "http://172.22.104.156:7000/movies";

        JsonObjectRequest moviesRequest = new JsonObjectRequest(Request.Method.GET, moviesUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray moviesArray = response.getJSONArray("response");

                            // Membuat ArrayList untuk menyimpan judul film
                            ArrayList<String> movieTitles = new ArrayList<>();

                            for (int i = 0; i < moviesArray.length(); i++) {
                                JSONObject movieObject = moviesArray.getJSONObject(i);
                                String title = movieObject.getString("title");
                                int year_release = movieObject.getInt("release_year");
                                String year = String.valueOf(year_release);
                                String listMovie = title + " (" + year + ")";

                                movieTitles.add(listMovie);
                            }

                            // Memuat ulasan pengguna dari server API
                            String reviewsUrl = "http://172.22.104.156:7000/myreviews/" + akun;

                            JsonObjectRequest reviewsRequest = new JsonObjectRequest(Request.Method.GET, reviewsUrl, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                JSONArray reviewsArray = response.getJSONArray("response");

                                                // Memperbarui Set reviewedMovies dengan judul film yang sudah diberikan review oleh pengguna
                                                for (int i = 0; i < reviewsArray.length(); i++) {
                                                    JSONObject reviewObject = reviewsArray.getJSONObject(i);
                                                    String reviewedMovie = reviewObject.getString("title");
                                                    reviewedMovies.add(reviewedMovie);
                                                }

                                                // Menghapus judul film yang sudah diberikan review oleh pengguna dari daftar movieTitles
                                                for (String reviewedMovie : reviewedMovies) {
                                                    movieTitles.remove(reviewedMovie);
                                                }

                                                // Inisialisasi dan set data spinner
                                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(CreateActivity.this, android.R.layout.simple_spinner_item, movieTitles);
                                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                movieSpinner.setAdapter(spinnerAdapter);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(CreateActivity.this, "Gagal mendapatkan ulasan pengguna", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Menambahkan permintaan ulasan ke antrian permintaan Volley
                            Volley.newRequestQueue(CreateActivity.this).add(reviewsRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreateActivity.this, "Gagal mendapatkan data film", Toast.LENGTH_SHORT).show();
                    }
                });

        // Menambahkan permintaan film ke antrian permintaan Volley
        Volley.newRequestQueue(CreateActivity.this).add(moviesRequest);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = movieSpinner.getSelectedItem().toString();
                String score = scoreCreate.getText().toString();
                String comment = commentCreate.getText().toString();
                String url = "http://172.22.104.156:7000/reviews";

                JSONObject object = new JSONObject();
                try {
                    object.put("title", title);
                    object.put("rating", score);
                    object.put("comment", comment);
                    object.put("username", akun);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest stringRequest = new JsonObjectRequest(
                        Request.Method.POST, url, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(CreateActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    String msg = response.getString("message");
                                    if (msg != null) {
                                        reviewedMovies.add(title); // Tambahkan judul film yang diberikan review ke set reviewedMovies
                                        Intent intent = new Intent(CreateActivity.this, PageActivity.class);
                                        intent.putExtra("username", akun);
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
                                if (error.networkResponse != null) {
                                    int statusCode = error.networkResponse.statusCode;
                                    Log.e("apiresult", "HTTP Status Code: " + statusCode);
                                }
                                Log.e("apiresult", error.getMessage() != null ? error.getMessage() : "Unknown error");
                                Toast.makeText(CreateActivity.this, "Review gagal", Toast.LENGTH_SHORT).show();
                            }
                        });
                Volley.newRequestQueue(CreateActivity.this).add(stringRequest);
            }
        });
    }
}
