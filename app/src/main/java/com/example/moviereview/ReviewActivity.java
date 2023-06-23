package com.example.moviereview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ReviewAdapter reviewAdapter;
    private TextView nameReviews;
    List<Review> reviewList;
    Button btupdateReview, btdeleteReview;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = findViewById(R.id.reviewRecyclerView);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        recyclerView.setAdapter(reviewAdapter);
        nameReviews = findViewById(R.id.nameReviews);
        btupdateReview = findViewById(R.id.btupdateReview);
        btdeleteReview = findViewById(R.id.btdeleteReview);

        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");
        nameReviews.setText(akun);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        url = "http://172.22.104.156:7000/myreviews/"+akun;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("response")) {
                                JSONArray reviews = response.getJSONArray("response");
                                for (int i = 0; i < reviews.length(); i++) {
                                    JSONObject reviewObj = reviews.getJSONObject(i);
                                    String title = reviewObj.getString("title");
                                    int score = reviewObj.getInt("rating");
                                    String comment = reviewObj.getString("comment");
                                    Review review = new Review(title, score, comment);
                                    reviewList.add(review);
                                }
                                reviewAdapter.notifyDataSetChanged();
                            } else {
                                // Invalid response format
                                // Handle the case when response doesn't have "response" key
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // JSON parsing error
                            // Handle the JSON parsing error
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // Error occurred
                // Handle the error
            }
        });

        btupdateReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this,UpdateActivity.class);
                intent.putExtra("username",akun);
                startActivity(intent);

            }
        });

        btdeleteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this,DeleteActivity.class);
                intent.putExtra("username",akun);
                startActivity(intent);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}
