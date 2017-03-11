package com.example.lingfeng.dopeaf1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;


public class ViewIntroSplash extends AppCompatActivity {
    private GifImageView gifImageView;
    private static int SPLASH_TIME_OUT = 1800; // set the time for splash screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        gifImageView = (GifImageView)findViewById(R.id.gifImageView);

        // set the gif Image
        try {
            InputStream in = getAssets().open("logo.gif");
            byte[] bytes = IOUtils.toByteArray(in);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // wait for 2s and then display the main screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(ViewIntroSplash.this, ControllerLogin.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
