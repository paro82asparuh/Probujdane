package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class BesedaActivity extends AppCompatActivity {

    private besediDBHelper mydb;
    private ViewGroup mLinearLayout;

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

        String besedaText1 = rs.getString(rs.getColumnIndex("Text1"));

        TextView textview1 = (TextView) findViewById(R.id.textBesedaName);
        textview1.setText(besedaName);

        TextView textview2 = (TextView) findViewById(R.id.textBesedaDetails);
        String besedaDetails = rs.getString(rs.getColumnIndex("Type_1"))+", ";
        for (int i=2; i<=4; i++) {
            String typeX = rs.getString(rs.getColumnIndex("Type_"+i));
            if (typeX.equals("")==false) {
                besedaDetails = besedaDetails + typeX + ", ";
            }
        }
        String besedaLocation = rs.getString(rs.getColumnIndex("Location"));
        if (besedaLocation.equals("")==false) {
            besedaDetails = besedaDetails + besedaLocation +", ";
        }
        besedaDetails = besedaDetails + " " + besedaDateDay+"."+besedaDateMonth+"."+besedaDateYear;
        String besedaHour = rs.getString(rs.getColumnIndex("Hour_"));
        if (besedaHour.equals("")==false) {
            besedaDetails = besedaDetails + ", " + besedaHour;
        }
        besedaDetails = besedaDetails + "\n";
        textview2.setText(besedaDetails);

        TextView textview3 = (TextView) findViewById(R.id.textBesedaText1);
        //textview3.setText(besedaText);
        textview3.setText(createIndentedText(besedaText1, 100, 0));

        mLinearLayout = (ViewGroup) findViewById(R.id.textBesedaLinearLayout);

        Integer numberOfImages = rs.getInt(rs.getColumnIndex("Number_of_Images"));

        for (int i=0; i<numberOfImages; i++) {
            View layout2 = LayoutInflater.from(this).inflate(R.layout.beseda_extention_item, mLinearLayout, false);

            ImageView imageViewExtention = (ImageView) layout2.findViewById(R.id.imageBesedaExtention);
            String imageName = rs.getString(rs.getColumnIndex("Image"+(i+1)));
            String imageNameMain = imageName.substring(0,imageName.length()-4);
            String imageNameExtention = imageName.substring(imageName.length()-3,imageName.length());
            imageNameMain = imageNameMain.replace("-", "_dash_");
            imageNameMain = imageNameMain.replace(".", "_dot_");
            imageNameMain = imageNameMain.replace(" ", "_s0p_");
            imageNameMain = imageNameMain.replace("(", "_obrack_");
            imageNameMain = imageNameMain.replace(")", "_cbrack_");
            imageNameMain = imageNameMain.replace("+", "_plus_");
            imageNameMain = imageNameMain.replace(",", "_comma_");
            imageNameMain = "img_probuj_"+imageNameMain;
            imageName = imageNameMain + "." + imageNameExtention;
            int res = getResources().getIdentifier(imageNameMain.toLowerCase(), "drawable", this.getPackageName());
            imageViewExtention.setImageResource(res);

            besedaTextView besedaTextExtention = (besedaTextView) layout2.findViewById(R.id.textBesedaExtention);
            String besedaTextX = rs.getString(rs.getColumnIndex("Text"+(i+2)));
            besedaTextExtention.setText(createIndentedText(besedaTextX, 100, 0));

            mLinearLayout.addView(layout2);
        }


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
