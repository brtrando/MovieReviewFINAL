package com.example.moviereview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btSignin, btSignup;
    ImageView logoPetra;
    TextView welcomeTxt, madebyTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Animation aFade;

        aFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);

        btSignin = (Button) findViewById(R.id.btSignin);
        btSignup = (Button) findViewById(R.id.btSignup);
        logoPetra = (ImageView)findViewById(R.id.petraLogo);
        welcomeTxt = (TextView)findViewById(R.id.textViewWelcome);
        madebyTxt = (TextView) findViewById(R.id.textViewMadeBy);


        logoPetra.setVisibility(View.VISIBLE);
        logoPetra.startAnimation(aFade);

        btSignin.setVisibility(View.VISIBLE);
        btSignin.startAnimation(aFade);

        btSignup.setVisibility(View.VISIBLE);
        btSignup.startAnimation(aFade);

        welcomeTxt.setVisibility(View.VISIBLE);
        welcomeTxt.startAnimation(aFade);

        madebyTxt.setVisibility(View.VISIBLE);
        madebyTxt.startAnimation(aFade);


        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
    }
}