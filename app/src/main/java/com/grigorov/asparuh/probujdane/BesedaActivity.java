package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.webkit.WebView;
import android.widget.TextView;

public class BesedaActivity extends AppCompatActivity {

    private besediDBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beseda);

        Intent intent = getIntent();

        String besedaName = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaNameVar");
        String besedaDateYear = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaDateYearVar");
        String besedaDateMonth = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaDateMonthVar");
        String besedaDateDay = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaDateDayVar");
        //String besedaText = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaTextVar");

        mydb = new besediDBHelper(this);
        Cursor rs = mydb.getbeseda(besedaName, besedaDateYear, besedaDateMonth, besedaDateDay);
        rs.moveToFirst();

        String besedaText = rs.getString(rs.getColumnIndex("Text1"));

        TextView textview1 = (TextView) findViewById(R.id.textBesedaName);
        textview1.setText(besedaName);

        TextView textview2 = (TextView) findViewById(R.id.textBesedaDate);
        textview2.setText(besedaDateDay+"."+besedaDateMonth+"."+besedaDateYear);

        TextView textview3 = (TextView) findViewById(R.id.textBesedaText);
        //textview3.setText(besedaText);
        textview3.setText(createIndentedText(besedaText, 100, 0));

        //WebView view = (WebView) findViewById(R.id.textBesedaText);
        //String text;
        //text = "<html><body><p align=\"justify\">";
        //text+= besedaText;
        //text+= "</p></body></html>";
        //view.loadData(text, "text/html", "utf-8");

    }

    public void onResume () {
        super.onResume();
        mydb = new besediDBHelper(this);
    }

    public void onPause () {
        super.onPause();
        mydb.close();
    }

    public static SpannableString createIndentedText(String text, int marginFirstLine, int marginNextLines) {
        SpannableString result=new SpannableString(text);
        result.setSpan(new LeadingMarginSpan.Standard(marginFirstLine, marginNextLines),0,text.length(),0);
        return result;
    }

}
