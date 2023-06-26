package com.example.moviereview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeleteActivity extends AppCompatActivity {
    Spinner movieSpinner;
    Button btSubmitDelete;
    private static final String URL = "http://192.168.11.124:7000/deletereviews";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        movieSpinner = findViewById(R.id.spinnerDelete);
        btSubmitDelete = findViewById(R.id.btSubmitDelete);

        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");

        btSubmitDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = movieSpinner.getSelectedItem().toString();
                deleteReview(akun, title);
            }
        });

        // Mendapatkan data ulasan pengguna dari server API
        String reviewsUrl = "http://192.168.11.124:7000/myreviews/" + akun;

        JsonObjectRequest reviewsRequest = new JsonObjectRequest(Request.Method.GET, reviewsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray reviewsArray = response.getJSONArray("response");

                            // Membuat array untuk menyimpan judul film yang sudah diberikan review oleh pengguna
                            String[] reviewedMovies = new String[reviewsArray.length()];

                            for (int i = 0; i < reviewsArray.length(); i++) {
                                JSONObject reviewObject = reviewsArray.getJSONObject(i);
                                String reviewedMovie = reviewObject.getString("title");
                                reviewedMovies[i] = reviewedMovie;
                            }

                            // Inisialisasi dan set data spinner
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(DeleteActivity.this, android.R.layout.simple_spinner_item, reviewedMovies);
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
                        Toast.makeText(DeleteActivity.this, "Gagal mendapatkan ulasan pengguna", Toast.LENGTH_SHORT).show();
                    }
                });

        // Menambahkan permintaan ulasan ke antrian permintaan Volley
        Volley.newRequestQueue(DeleteActivity.this).add(reviewsRequest);
    }

    private void deleteReview(String akun, String title) {
        // Validasi apakah judul film sudah dipilih
        if (title.isEmpty()) {
            Toast.makeText(DeleteActivity.this, "Please select a title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek JSON untuk dikirim ke API
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", akun);
            requestBody.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(DeleteActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DeleteActivity.this, PageActivity.class);
                            intent.putExtra("username", akun);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DeleteActivity.this, "Delete Review failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Menambahkan permintaan ke antrian permintaan Volley
        Volley.newRequestQueue(DeleteActivity.this).add(request);
    }
}
