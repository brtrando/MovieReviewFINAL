package com.example.moviereview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PageActivity extends AppCompatActivity {
    private TextView nameTextview;
    private Button btCreatereview, btMyreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        nameTextview = findViewById(R.id.namePage);
        btCreatereview = findViewById(R.id.btCreatereview);
        btMyreview = findViewById(R.id.btMyreview);

        Intent intent = getIntent();
        String akun = intent.getStringExtra("username");
        nameTextview.setText(akun);

        btCreatereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PageActivity.this,CreateActivity.class);
                intent.putExtra("username",akun);
                startActivity(intent);
            }
        });

        btMyreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PageActivity.this,ReviewActivity.class);
                startActivity(intent);
            }
        });

    }
}