package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.Gravity.CENTER_HORIZONTAL;

public class MolitvaActivity extends AppCompatActivity {

    private String molitvaTitle;
    private String molitvaText;
    private String screenWidthInPixels;

    private int srollColumn;
    private int srollTextIndex;

    private final ArrayList<MolitvaMarker> listMolitvaMarkers= new ArrayList<MolitvaMarker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molitva);

        Intent intent = getIntent();

        molitvaTitle = intent.getStringExtra("com.grigorov.asparuh.probujdane.MolitvaTitleVar");
        molitvaText = intent.getStringExtra("com.grigorov.asparuh.probujdane.MolitvaTextVar");
        screenWidthInPixels = intent.getStringExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels");

        // scrolling in molitva is not used for now
        //String molitvaScrollIndeces  = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaScrollIndecesVar");
        //String[] mScrollIndeces = molitvaScrollIndeces.split(" "); // Split to " " to read integers
        //srollColumn = Integer.parseInt(mScrollIndeces[0]);
        //srollTextIndex = Integer.parseInt(mScrollIndeces[1]);

        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

        listMolitvaMarkers.clear();
        String molitvaMarkers = intent.getStringExtra("com.grigorov.asparuh.probujdane.MolitvaMarkersVar");
        if (!molitvaMarkers.equals("")) {
            String[] inputMolitvaMarkers = molitvaMarkers.split(" "); // Split to " " to read integers
            for (int marker_loop=0; marker_loop<inputMolitvaMarkers.length;marker_loop=marker_loop+3) {
                listMolitvaMarkers.add(
                        new MolitvaMarker(
                                Integer.parseInt(inputMolitvaMarkers[marker_loop]),
                                Integer.parseInt(inputMolitvaMarkers[marker_loop+1]),
                                Integer.parseInt(inputMolitvaMarkers[marker_loop+2])
                        )
                );
            }
        }

        SpannableStringBuilder molitvaTitleBuilder = new SpannableStringBuilder();
        for (int textIndex=0; textIndex < molitvaTitle.length(); textIndex++) {
            String c = String.valueOf(molitvaTitle.charAt(textIndex));
            SpannableString spannableString = new SpannableString(c);
            boolean marked = false;
            for (int markerIndex=0;markerIndex<listMolitvaMarkers.size();markerIndex=markerIndex+1) {
                if (
                        (listMolitvaMarkers.get(markerIndex).getColumnIndex()==1) &&
                        (listMolitvaMarkers.get(markerIndex).getStartIndex()<=textIndex) &&
                        (listMolitvaMarkers.get(markerIndex).getEndIndex()>=textIndex)
                ) {
                    marked = true;
                }
            }
            if (!marked) {
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorMolitvaTitleText, null)),
                        0, spannableString.length(), flag);
            } else {
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), flag);
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        0, spannableString.length(), flag);
            }
            molitvaTitleBuilder.append(spannableString);
        }

        TextView textViewTitle = findViewById(R.id.textMolitvaTitle);
        textViewTitle.setText(molitvaTitleBuilder);

        SpannableStringBuilder molitvaTextBuilder = new SpannableStringBuilder();
        for (int textIndex=0; textIndex < molitvaText.length(); textIndex++) {
            String c = String.valueOf(molitvaText.charAt(textIndex));
            SpannableString spannableString = new SpannableString(c);
            boolean marked = false;
            for (int markerIndex=0;markerIndex<listMolitvaMarkers.size();markerIndex=markerIndex+1) {
                if (
                    (listMolitvaMarkers.get(markerIndex).getColumnIndex()==2) &&
                    (listMolitvaMarkers.get(markerIndex).getStartIndex()<=textIndex) &&
                    (listMolitvaMarkers.get(markerIndex).getEndIndex()>=textIndex)
                ) {
                    marked = true;
                }
            }
            if (!marked) {
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorMolitvaText, null)),
                        0, spannableString.length(), flag);
            } else {
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), flag);
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        0, spannableString.length(), flag);
            }
            molitvaTextBuilder.append(spannableString);
        }

        TextView textViewText = findViewById(R.id.textMolitvaText);
        textViewText.setGravity(CENTER_HORIZONTAL);
        textViewText.setText(molitvaTextBuilder);

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

        TextView textViewTitle = findViewById(R.id.textMolitvaTitle);
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, molitvaNameSize);

        TextView textViewText = findViewById(R.id.textMolitvaText);
        textViewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, molitvaTextSize);

    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_SOURCE_MOLITVI");
        startActivity(intent);
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

}
