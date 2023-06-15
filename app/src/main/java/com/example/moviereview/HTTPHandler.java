package com.example.moviereview;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPHandler {
    private static final String BASE_URL = "http://localhost:3000";

    public interface ReviewListener {
        void onReviewSubmitted(boolean success, String message);
    }

    public static void submitReview(String token, String title, int score, String comment, ReviewListener listener) {
        new SubmitReviewTask(token, title, score, comment, listener).execute();
    }

    private static class SubmitReviewTask extends AsyncTask<Void, Void, String> {
        private final String token;
        private final String title;
        private final int score;
        private final String comment;
        private final ReviewListener listener;

        public SubmitReviewTask(String token, String title, int score, String comment, ReviewListener listener) {
            this.token = token;
            this.title = title;
            this.score = score;
            this.comment = comment;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = BASE_URL + "/reviews";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", token);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Buat JSON payload untuk review
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", title);
                jsonObject.put("score", score);
                jsonObject.put("comment", comment);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return null;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");
                    boolean success = message.equals("Review berhasil dibuat.");
                    listener.onReviewSubmitted(success, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onReviewSubmitted(false, "Terjadi kesalahan pada server.");
                }
            } else {
                listener.onReviewSubmitted(false, "Terjadi kesalahan pada koneksi.");
            }
        }
    }
}
