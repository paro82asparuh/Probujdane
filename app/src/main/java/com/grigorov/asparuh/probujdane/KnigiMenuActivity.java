package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class KnigiMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knigi_menu);
    }

    public void startNaukaVyzpitanieTask (View view) {
        Intent intent = new Intent(this, NaukaVyzpitanieMenuActivity.class);
        startActivity(intent);
    }

    public void startZavetTask (View view) {

    }

}