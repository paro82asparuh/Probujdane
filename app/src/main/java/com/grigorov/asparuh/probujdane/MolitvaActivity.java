package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import static android.view.Gravity.CENTER_HORIZONTAL;

public class MolitvaActivity extends AppCompatActivity {

    private String molitvaTitle;
    private String molitvaText;
    private String screenWidthInPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molitva);

        Intent intent = getIntent();

        molitvaTitle = intent.getStringExtra("com.grigorov.asparuh.probujdane.MolitvaTitleVar");
        molitvaText = intent.getStringExtra("com.grigorov.asparuh.probujdane.MolitvaTextVar");
        screenWidthInPixels = intent.getStringExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels");

        TextView textViewTitle = (TextView) findViewById(R.id.textMolitvaTitle);
        textViewTitle.setText(molitvaTitle);

        TextView textViewText = (TextView) findViewById(R.id.textMolitvaText);
        textViewText.setGravity(CENTER_HORIZONTAL);
        textViewText.setText(molitvaText);

        updateTextSize();

    }

    public void onResume () {
        super.onResume();
        updateTextSize ();
    }

    private void updateTextSize () {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String molitvaTextSizeString = sharedPref.getString("com.grigorov.asparuh.probujdane.textsize", "14");
        int molitvaTextSize = Integer.parseInt(molitvaTextSizeString);
        int molitvaNameSize = molitvaTextSize + 4;

        TextView textViewTitle = (TextView) findViewById(R.id.textMolitvaTitle);
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, molitvaNameSize);

        TextView textViewText = (TextView) findViewById(R.id.textMolitvaText);
        textViewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, molitvaTextSize);

    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        startActivity(intent);
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

}
