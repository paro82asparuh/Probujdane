package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
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

public class FormulaActivity extends AppCompatActivity {

    private String formulaTitle;
    private String formulaText;
    private String screenWidthInPixels;

    private int srollColumn;
    private int srollTextIndex;

    private ArrayList<FormulaMarker> listFormulaMarkers= new ArrayList<FormulaMarker>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formula);

        Intent intent = getIntent();

        formulaTitle = intent.getStringExtra("com.grigorov.asparuh.probujdane.FormulaTitleVar");
        if (formulaTitle.equals("")) {
            formulaTitle = getResources().getString(R.string.formula);
        }
        formulaText = intent.getStringExtra("com.grigorov.asparuh.probujdane.FormulaTextVar");
        screenWidthInPixels = intent.getStringExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels");

        // scrolling in formula is not used for now
        //String formulaScrollIndeces  = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaScrollIndecesVar");
        //String[] mScrollIndeces = formulaScrollIndeces.split(" "); // Split to " " to read integers
        //srollColumn = Integer.parseInt(mScrollIndeces[0]);
        //srollTextIndex = Integer.parseInt(mScrollIndeces[1]);

        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

        listFormulaMarkers.clear();
        String formulaMarkers = intent.getStringExtra("com.grigorov.asparuh.probujdane.FormulaMarkersVar");
        if (formulaMarkers.equals("")==false) {
            String[] inputFormulaMarkers = formulaMarkers.split(" "); // Split to " " to read integers
            for (int marker_loop=0; marker_loop<inputFormulaMarkers.length;marker_loop=marker_loop+3) {
                listFormulaMarkers.add(
                        new FormulaActivity.FormulaMarker(
                                Integer.parseInt(inputFormulaMarkers[marker_loop]),
                                Integer.parseInt(inputFormulaMarkers[marker_loop+1]),
                                Integer.parseInt(inputFormulaMarkers[marker_loop+2])
                        )
                );
            }
        }

        SpannableStringBuilder formulaTitleBuilder = new SpannableStringBuilder();
        for (int textIndex=0; textIndex < formulaTitle.length(); textIndex++) {
            String c = String.valueOf(formulaTitle.charAt(textIndex));
            SpannableString spannableString = new SpannableString(c);
            boolean marked = false;
            for (int markerIndex=0;markerIndex<listFormulaMarkers.size();markerIndex=markerIndex+1) {
                if (
                        (listFormulaMarkers.get(markerIndex).getColumnIndex()==1) &&
                                (listFormulaMarkers.get(markerIndex).getStartIndex()>=textIndex) &&
                                (listFormulaMarkers.get(markerIndex).getEndIndex()<=textIndex)
                ) {
                    marked = true;
                }
            }
            if (marked==false) {
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorFormulaTitleText, null)),
                        0, spannableString.length(), flag);
            } else {
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), flag);
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        0, spannableString.length(), flag);
            }
            formulaTitleBuilder.append(spannableString);
        }

        TextView textViewTitle = (TextView) findViewById(R.id.textFormulaTitle);
        textViewTitle.setText(formulaTitleBuilder);

        SpannableStringBuilder formulaTextBuilder = new SpannableStringBuilder();
        for (int textIndex=0; textIndex < formulaText.length(); textIndex++) {
            String c = String.valueOf(formulaText.charAt(textIndex));
            SpannableString spannableString = new SpannableString(c);
            boolean marked = false;
            for (int markerIndex=0;markerIndex<listFormulaMarkers.size();markerIndex=markerIndex+1) {
                if (
                        (listFormulaMarkers.get(markerIndex).getColumnIndex()==2) &&
                                (listFormulaMarkers.get(markerIndex).getStartIndex()<=textIndex) &&
                                (listFormulaMarkers.get(markerIndex).getEndIndex()>textIndex)
                ) {
                    marked = true;
                }
            }
            if (marked==false) {
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorFormulaText, null)),
                        0, spannableString.length(), flag);
            } else {
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), flag);
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        0, spannableString.length(), flag);
            }
            formulaTextBuilder.append(spannableString);
        }

        TextView textViewText = (TextView) findViewById(R.id.textFormulaText);
        textViewText.setGravity(CENTER_HORIZONTAL);
        textViewText.setText(formulaTextBuilder);

        updateTextSize();
    }

    public void onResume () {
        super.onResume();
        updateTextSize ();
    }
    
    private void updateTextSize () {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String formulaTextSizeString = sharedPref.getString("com.grigorov.asparuh.probujdane.textsize", "14");
        int formulaTextSize = Integer.parseInt(formulaTextSizeString);
        int formulaNameSize = formulaTextSize + 4;

        TextView textViewTitle = (TextView) findViewById(R.id.textFormulaTitle);
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, formulaNameSize);

        TextView textViewText = (TextView) findViewById(R.id.textFormulaText);
        textViewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, formulaTextSize);

    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_SOURCE_FORMULI");
        startActivity(intent);
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }
    
    private class FormulaMarker {
        private int columnIndex;
        private int startIndex;
        private int endIndex;

        public FormulaMarker (int inputColumnIndex, int inputStartIndex, int inputEndIndex) {
            columnIndex = inputColumnIndex;
            startIndex = inputStartIndex;
            endIndex = inputEndIndex;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }
}
