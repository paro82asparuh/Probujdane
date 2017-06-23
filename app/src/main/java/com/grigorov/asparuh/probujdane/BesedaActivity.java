package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BesedaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beseda);

        Intent intent = getIntent();

        String besedaName = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaNameVar");
        String besedaDate = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaDateVar");
        String besedaText = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaTextVar");

        TextView textview1 = (TextView) findViewById(R.id.textBesedaName);
        textview1.setText(besedaName);

        TextView textview2 = (TextView) findViewById(R.id.textBesedaDate);
        textview2.setText(besedaDate);

        TextView textview3 = (TextView) findViewById(R.id.textBesedaText);
        textview3.setText(besedaText);

    }
}
