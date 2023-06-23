package com.example.moviereview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText scoreUpdate, commentUpdate;
    Button btSubmitUpdate;
    private static final String UPDATE_URL = "http://172.22.104.156:7000/reviews";

    private Spinner titleSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        scoreUpdate = findViewById(R.id.scoreUpdate);
        commentUpdate = findViewById(R.id.commentUpdate);
        btSubmitUpdate = findViewById(R.id.btSubmitUpdate);
        titleSpinner = findViewById(R.id.spinnerUpdate);

        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");

        // Mengambil data judul film dari server API
        String url = "http://172.22.104.156:7000/myreviews/" + akun;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray reviewsArray = response.getJSONArray("response");
                            titleList = new ArrayList<>();

                            for (int i = 0; i < reviewsArray.length(); i++) {
                                JSONObject reviewObj = reviewsArray.getJSONObject(i);
                                String title = reviewObj.getString("title");
                                titleList.add(title);
                            }

                            // Mengatur adapter spinner dengan daftar judul film
                            spinnerAdapter = new ArrayAdapter<>(UpdateActivity.this, android.R.layout.simple_spinner_item, titleList);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            titleSpinner.setAdapter(spinnerAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Tambahkan permintaan ke antrian permintaan Volley
        Volley.newRequestQueue(this).add(request);

        btSubmitUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReview();
                Intent intent = new Intent(UpdateActivity.this, PageActivity.class);
                intent.putExtra("username", akun);
                startActivity(intent);
            }
        });
    }

    private void updateReview() {
        String selectedTitle = titleSpinner.getSelectedItem().toString();
        String rating = scoreUpdate.getText().toString().trim();
        String comment = commentUpdate.getText().toString().trim();

        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");

        // Validasi apakah movie review sudah ada dalam database
        if (selectedTitle.isEmpty()) {
            Toast.makeText(this, "Please select a title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek JSON untuk dikirim ke API
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", akun); // Ganti dengan username yang sesuai
            requestBody.put("title", selectedTitle);
            requestBody.put("rating", rating);
            requestBody.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Kirim permintaan GET HTTP ke API untuk memeriksa keberadaan judul dalam database
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, UPDATE_URL + "?title=" + selectedTitle, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Cek apakah movie review dengan judul yang diberikan ada dalam database
                            boolean reviewExists = response.getBoolean("exists");
                            if (reviewExists) {
                                // Kirim permintaan PUT HTTP ke API untuk memperbarui movie review
                                updateReviewRequest(requestBody);
                            } else {
                                Toast.makeText(UpdateActivity.this, "Movie review not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Tambahkan permintaan ke antrian permintaan Volley
        Volley.newRequestQueue(this).add(getRequest);
    }

    private void updateReviewRequest(JSONObject requestBody) {
        // Kirim permintaan PUT HTTP ke API untuk memperbarui movie review
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, UPDATE_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Tambahkan permintaan ke antrian permintaan Volley
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedTitle = parent.getItemAtPosition(position).toString();
        // Lakukan tindakan tertentu jika judul film yang dipilih berubah
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Metode ini akan dipanggil jika tidak ada yang dipilih dalam spinner
    }
}
