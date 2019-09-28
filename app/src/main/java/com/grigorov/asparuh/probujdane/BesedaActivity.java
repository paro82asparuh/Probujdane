package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;

public class BesedaActivity extends AppCompatActivity {

    private besediDBHelper mydb;
    private ViewGroup mLinearLayout;
    private ViewGroup variantsLinearLayout;

    private Boolean variant1Selected;
    private String besedaName;
    private String besedaDateYear;
    private String besedaDateMonth;
    private String besedaDateDay;
    private String besedaInitialVariant;
    private String besedaLink;
    private String screenWidthInPixels;
    private int numberOfImages;
    private int srollTextX;
    private int srollTextIndex;
    private int scrollTextViewCounter;
    private ScrollView scrollViewBeseda;
    private besedaTextView scrollTextView;

    private ArrayList<besedaMarker> listBesedaMarkers= new ArrayList<besedaMarker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        besedaName = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaNameVar");
        besedaLink = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaLinkVar");
        besedaDateYear = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaDateYearVar");
        besedaDateMonth = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaDateMonthVar");
        besedaDateDay = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaDateDayVar");
        screenWidthInPixels = intent.getStringExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels");
        besedaInitialVariant = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaVarinatVar");
        String besedaScrollIndeces  = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaScrollIndecesVar");
        String[] bScrollIndeces = besedaScrollIndeces.split(" "); // Split to " " to read integers
        srollTextX = Integer.parseInt(bScrollIndeces[0]);
        srollTextIndex = Integer.parseInt(bScrollIndeces[1]);
        variant1Selected = besedaInitialVariant.compareTo("1") == 0;
        listBesedaMarkers.clear();
        String besedaMarkers = intent.getStringExtra("com.grigorov.asparuh.probujdane.BesedaMarkersVar");
        if (besedaMarkers.equals("")==false) {
            String[] inputBesedaMarkers = besedaMarkers.split(" "); // Split to " " to read integers
            for (int marker_loop=0; marker_loop<inputBesedaMarkers.length;marker_loop=marker_loop+3) {
                listBesedaMarkers.add(
                        new besedaMarker(
                                Integer.parseInt(inputBesedaMarkers[marker_loop]),
                                Integer.parseInt(inputBesedaMarkers[marker_loop+1]),
                                Integer.parseInt(inputBesedaMarkers[marker_loop+2])
                        )
                );
            }
        }

        mydb = new besediDBHelper(this);

        updateVariant ();

    }

    private void updateVariant () {

        setContentView(R.layout.activity_beseda);

        scrollViewBeseda = (ScrollView) findViewById(R.id.scrollViewBeseda);
        scrollTextViewCounter = 0;

        Cursor rs = mydb.getbeseda(besedaLink, besedaDateYear, besedaDateMonth, besedaDateDay);
        rs.moveToFirst();
        if (variant1Selected==false) {
            rs.moveToNext();
        }
        if (rs.getCount()<=1) {
            variantsLinearLayout = (ViewGroup) findViewById(R.id.buttonsVariantsBesedaLinearLaoyt);
            variantsLinearLayout.removeAllViews();
        }

        String besedaText1 = rs.getString(rs.getColumnIndex("Text1"));

        TextView textViewName = (TextView) findViewById(R.id.textBesedaName);
        textViewName.setText(besedaName);

        TextView textViewDetailes = (TextView) findViewById(R.id.textBesedaDetails);
        String besedaDetails = "\n" + rs.getString(rs.getColumnIndex("Type_1"))+", ";
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
        //besedaDetails = besedaDetails + "\n";
        textViewDetailes.setText(besedaDetails);

        Button buttonLink = (Button) findViewById(R.id.textBesedaLink);
        //besedaLink = rs.getString(rs.getColumnIndex("Link"));
        String linkToBeinsaBg = getResources().getString(R.string.link_beinsa_bg);
        buttonLink.setText(linkToBeinsaBg);

        besedaTextView textViewText1 = (besedaTextView) findViewById(R.id.textBesedaText1);
        //TextView textViewText1 = (TextView) findViewById(R.id.textBesedaText1);
        if (variant1Selected==(besedaInitialVariant.equals("1"))) {
            textViewText1.setMarkersString(createMarkersString(1));
            textViewText1.setScrollToChar(srollTextIndex);
        } else {
            textViewText1.setMarkersString("");
            textViewText1.setScrollToChar(0);
        }
        textViewText1.setText(createSpannedBesedaText(besedaText1, 100, 0));
        textViewText1.onPreDraw();

        if (srollTextX==1) {
            scrollTextView = (besedaTextView) findViewById(R.id.textBesedaText1);
        }

        mLinearLayout = (ViewGroup) findViewById(R.id.textBesedaLinearLayout);

        numberOfImages = rs.getInt(rs.getColumnIndex("Number_of_Images"));

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
            // If the image is taken from the resources
            //int res = getResources().getIdentifier(imageNameMain.toLowerCase(), "drawable", this.getPackageName());
            //imageViewExtention.setImageResource(res);

            // If the image is taken from the external files dir
            String imageFullPath = getApplicationContext().getExternalFilesDir(null).getPath().toString()
                    + "/besedi_pictures/" + imageName;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmRawImg = BitmapFactory.decodeFile(imageFullPath, options);
            int targetWidth = Integer.parseInt(screenWidthInPixels);
            targetWidth = (int) (((double) targetWidth) * 0.875);
            int targetHeight = bmRawImg.getHeight() * (targetWidth / bmRawImg.getWidth());
            Bitmap bmScaledImg = Bitmap.createScaledBitmap(bmRawImg, targetWidth, targetHeight, true);
            imageViewExtention.setImageBitmap(bmScaledImg);

            besedaTextView besedaTextExtention = (besedaTextView) layout2.findViewById(R.id.textBesedaExtention);
            String besedaTextX = rs.getString(rs.getColumnIndex("Text"+(i+2)));
            if (variant1Selected==(besedaInitialVariant.equals("1"))) {
                besedaTextExtention.setMarkersString(createMarkersString(i + 2));
                besedaTextExtention.setScrollToChar(srollTextIndex);
            } else {
                besedaTextExtention.setMarkersString("");
                besedaTextExtention.setScrollToChar(0);
            }
            besedaTextExtention.setText(createSpannedBesedaText(besedaTextX, 100, 0));
            besedaTextExtention.onPreDraw();

            mLinearLayout.addView(layout2);

            if (srollTextX==i+2) {
                scrollTextView = (besedaTextView) layout2.findViewById(R.id.textBesedaExtention);
            }

        }

        updateTextSize ();

        if (!rs.isClosed())  {
            rs.close();
        }

    }

    public void openLinkBeinsaBg (View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(besedaLink));
        startActivity(browserIntent);
    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        startActivity(intent);
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

    private void showMessage(String errorMesaage) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, errorMesaage, LENGTH_LONG);
        toast.show();
    }

    public void setVariant1 (View view) {
        if (variant1Selected==true) {
            String errorMessage = getString(R.string.variant1_already_selected);
            showMessage(errorMessage);
        } else {
            variant1Selected = true;
            updateVariant ();
            String variantUpdatedMessage = getString(R.string.variant1_updated);
            showMessage(variantUpdatedMessage);
        }

    }

    public void setVariant2 (View view) {
        if (variant1Selected==false) {
            String errorMessage = getString(R.string.variant2_already_selected);
            showMessage(errorMessage);
        } else {
            variant1Selected = false;
            updateVariant ();
            String variantUpdatedMessage = getString(R.string.variant2_updated);
            showMessage(variantUpdatedMessage);
        }

    }

    public void onResume () {
        super.onResume();
        mydb = new besediDBHelper(this);
        updateTextSize ();
        //scrollDirectToTarget();
    }

    public void onPause () {
        super.onPause();
        mydb.close();
    }

    // was static
    public Spannable createSpannedBesedaText(String text, int marginFirstLine, int marginNextLines) {
        //SpannableString result=new SpannableString(text);
        Spannable result=new SpannableString(text);
        result.setSpan(new LeadingMarginSpan.Standard(marginFirstLine, marginNextLines),0,text.length(),0);
        return result;
    }

    private String createMarkersString (int inputTextIndex) {
        String markersString = "";
        for (int m_loop=0;m_loop<listBesedaMarkers.size();m_loop=m_loop+1) {
            if (listBesedaMarkers.get(m_loop).getTextIndex() == inputTextIndex) {
                markersString = markersString +
                        Integer.toString(listBesedaMarkers.get(m_loop).getStartIndex()) +
                        " " +
                        Integer.toString(listBesedaMarkers.get(m_loop).getEndIndex()) +
                        " ";
            }
        }
        return markersString;
    }

    private void updateTextSize () {

        //scrollTextViewCounter=0;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String besedaTextSizeString = sharedPref.getString("com.grigorov.asparuh.probujdane.textsize", "14");
        int besedaTextSize = Integer.parseInt(besedaTextSizeString);
        int besedaDetailsSize = besedaTextSize + 2;
        int besedaLinkSize = besedaTextSize + 2;
        int besedaNameSize = besedaTextSize + 4;

        TextView textViewBesedaDetails = (TextView) findViewById(R.id.textBesedaDetails);
        textViewBesedaDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP,besedaDetailsSize);

        Button buttonBesedaLink = (Button) findViewById(R.id.textBesedaLink);
        buttonBesedaLink.setTextSize(TypedValue.COMPLEX_UNIT_SP,besedaLinkSize);

        TextView textViewBesedaName = (TextView) findViewById(R.id.textBesedaName);
        textViewBesedaName.setTextSize(TypedValue.COMPLEX_UNIT_SP,besedaNameSize);

        for (int i=0; i < mLinearLayout.getChildCount(); i++) {
            View view = mLinearLayout.getChildAt(i);
            if (view instanceof com.grigorov.asparuh.probujdane.besedaTextView) {
                ((com.grigorov.asparuh.probujdane.besedaTextView)view).setTextSize(TypedValue.COMPLEX_UNIT_SP,besedaTextSize);
            } else if (view instanceof LinearLayout) {
                TextView textViewInExtention = (TextView) ((LinearLayout)view).getChildAt(1);
                textViewInExtention.setTextSize(TypedValue.COMPLEX_UNIT_SP,besedaTextSize);
            }
        }

    }

    private class besedaMarker {
        private int textIndex;
        private int startIndex;
        private int endIndex;

        public besedaMarker (int inputTextIndex, int inputStartIndex, int inputEndIndex) {
            textIndex = inputTextIndex;
            startIndex = inputStartIndex;
            endIndex = inputEndIndex;
        }

        public int getTextIndex() {
            return textIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }

    public void scrollToTarget () {
        // Run only after the last besedaTextView is drawn
        scrollTextViewCounter++;
        //if (scrollTextViewCounter > numberOfImages ) {
        if (scrollTextViewCounter == (numberOfImages+1) ) {
            scrollDirectToTarget();
        }
    }

    private void scrollDirectToTarget () {
        int scrollY = 0;
        scrollY = scrollTextView.getScrollToY();
        if ((srollTextX != 1) || (srollTextIndex != 0)) {
            //scrollViewBeseda.scrollTo(0, scrollTextView.getTop() + scrollY);
            int [] scrollViewBesedaCoor = {0,0};
            int [] scrollTextViewCoor = {0,0};
            scrollViewBeseda.getLocationInWindow(scrollViewBesedaCoor);
            scrollTextView.getLocationInWindow(scrollTextViewCoor);
            scrollViewBeseda.scrollTo(0, (scrollTextViewCoor[1] - scrollViewBesedaCoor[1])+scrollY );
        }
    }


}



