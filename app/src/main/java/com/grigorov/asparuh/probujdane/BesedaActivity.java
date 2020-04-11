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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
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
    private int scrollSearchResultIndex;
    private ScrollView scrollViewBeseda;
    private besedaTextView scrollTextView;

    private ArrayList<besedaMarker> listBesedaMarkers= new ArrayList<besedaMarker>();
    private ArrayList<String> listBesedaTexts= new ArrayList<String>();

    private LinearLayout linearLayoutSearchControls;
    private LinearLayout linearLayoutEmpty1;
    private LinearLayout linearLayoutEmpty2;
    private EditText editSearchTextBesedatInput;
    private boolean searchControlsShown;
    private boolean searchKeyboardShown;
    private int numberSearchResults;

    private Cursor rs;

    private String searchQuery;

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
            // prepare input list of markers
            for (int marker_loop=0; marker_loop<inputBesedaMarkers.length;marker_loop=marker_loop+3) {
                listBesedaMarkers.add(
                        new besedaMarker(
                                Integer.parseInt(inputBesedaMarkers[marker_loop]),
                                Integer.parseInt(inputBesedaMarkers[marker_loop+1]),
                                Integer.parseInt(inputBesedaMarkers[marker_loop+2])
                        )
                );
            }
            // combine adjacent markers
            ArrayList<besedaMarker> listBesedaMarkersCopy = new ArrayList<besedaMarker>();
            // listBesedaMarkersCopy = listBesedaMarkers;
            listBesedaMarkersCopy.clear();
            for (int marker_loop=0; marker_loop<listBesedaMarkers.size();marker_loop=marker_loop+1) {
                listBesedaMarkersCopy.add(
                        new besedaMarker(
                                listBesedaMarkers.get(marker_loop).getTextIndex(),
                                listBesedaMarkers.get(marker_loop).getStartIndex(),
                                listBesedaMarkers.get(marker_loop).getEndIndex()
                        )
                );
            }
            listBesedaMarkers.clear();
            listBesedaMarkers.add(
                    new besedaMarker(
                            listBesedaMarkersCopy.get(0).getTextIndex(),
                            listBesedaMarkersCopy.get(0).getStartIndex(),
                            listBesedaMarkersCopy.get(0).getEndIndex()
                    )
            );
            int listIndex = 0;
            for (int marker_loop=1; marker_loop<listBesedaMarkersCopy.size();marker_loop=marker_loop+1) {
                if (
                        (listBesedaMarkers.get(listIndex).getTextIndex() == listBesedaMarkersCopy.get(marker_loop).getTextIndex() ) &&
                                ( (listBesedaMarkers.get(listIndex).getEndIndex() + 1)== listBesedaMarkersCopy.get(marker_loop).getStartIndex() )
                ) {
                    listBesedaMarkers.get(listIndex).setEndIndex(listBesedaMarkersCopy.get(marker_loop).getEndIndex());
                } else {
                    listBesedaMarkers.add(
                            new besedaMarker(
                                    listBesedaMarkersCopy.get(marker_loop).getTextIndex(),
                                    listBesedaMarkersCopy.get(marker_loop).getStartIndex(),
                                    listBesedaMarkersCopy.get(marker_loop).getEndIndex()
                            )
                    );
                    listIndex++;
                }
            }
            // find the scrollSearchResultIndex value, so that move next / previous search results are possible
            for (int marker_loop=0; marker_loop<listBesedaMarkers.size();marker_loop=marker_loop+1) {
                if ( (srollTextX == listBesedaMarkers.get(marker_loop).getTextIndex() ) &&
                (srollTextIndex == listBesedaMarkers.get(marker_loop).getStartIndex()) ) {
                    scrollSearchResultIndex = marker_loop;
                }
            }
            searchControlsShown = true;
            numberSearchResults = listBesedaMarkers.size();
        } else {
            searchControlsShown = false;
            numberSearchResults = 0;
        }

        searchQuery = "";
        mydb = new besediDBHelper(this);

        //searchControlsShown = false;
        //searchKeyboardShown = true;
        searchKeyboardShown = false;
        updateFullLayout ();

    }

    private void updateFullLayout () {

        setContentView(R.layout.activity_beseda);
        editSearchTextBesedatInput = findViewById(R.id.edit_search_text_beseda_input);
        linearLayoutSearchControls = findViewById(R.id.linear_layout_search_controls);
        linearLayoutEmpty1 = findViewById(R.id.linear_layout_empty_1);
        linearLayoutEmpty2 = findViewById(R.id.linear_layout_empty_2);
        if (searchControlsShown==false) {
            hideSearchControls(linearLayoutSearchControls);
        } else {
            //showSearchControls(linearLayoutSearchControls);
        }
        if (searchKeyboardShown==true) {
            showSearchKeyboard();
        } else {
            hideSearchkeyboard();
        }

        scrollViewBeseda = findViewById(R.id.scrollViewBeseda);
        scrollTextViewCounter = 0;

        rs = mydb.getbeseda(besedaLink, besedaDateYear, besedaDateMonth, besedaDateDay);
        rs.moveToFirst();
        if (variant1Selected==false) {
            rs.moveToNext();
        }
        if (rs.getCount()<=1) {
            variantsLinearLayout = findViewById(R.id.buttonsVariantsBesedaLinearLaoyt);
            variantsLinearLayout.removeAllViews();
        }

        listBesedaTexts.clear();

        String besedaText1 = rs.getString(rs.getColumnIndex("Text1"));
        listBesedaTexts.add(besedaText1);

        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        SpannableStringBuilder besedaNameBuilder = new SpannableStringBuilder();
        for (int textIndex=0; textIndex < besedaName.length(); textIndex++) {
            String c = String.valueOf(besedaName.charAt(textIndex));
            SpannableString spannableString = new SpannableString(c);
            boolean marked = false;
            for (int markerIndex=0;markerIndex<listBesedaMarkers.size();markerIndex=markerIndex+1) {
                if (
                        (listBesedaMarkers.get(markerIndex).getTextIndex()==0) &&
                                (listBesedaMarkers.get(markerIndex).getStartIndex()<=textIndex) &&
                                (listBesedaMarkers.get(markerIndex).getEndIndex()>=textIndex)
                ) {
                    marked = true;
                }
            }
            if (marked==false) {
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorBesedaNameText, null)),
                        0, spannableString.length(), flag);
            } else {
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), flag);
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        0, spannableString.length(), flag);
            }
            besedaNameBuilder.append(spannableString);
        }

        TextView textViewName = findViewById(R.id.textBesedaName);
        textViewName.setText(besedaNameBuilder);

        TextView textViewDetailes = findViewById(R.id.textBesedaDetails);
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

        Button buttonLink = findViewById(R.id.textBesedaLink);
        //besedaLink = rs.getString(rs.getColumnIndex("Link"));
        String linkToBeinsaBg = getResources().getString(R.string.link_beinsa_bg);
        buttonLink.setText(linkToBeinsaBg);

        besedaTextView textViewText1 = findViewById(R.id.textBesedaText1);
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
            scrollTextView = findViewById(R.id.textBesedaText1);
        }

        mLinearLayout = findViewById(R.id.textBesedaLinearLayout);

        numberOfImages = rs.getInt(rs.getColumnIndex("Number_of_Images"));

        for (int i=0; i<numberOfImages; i++) {
            View layout2 = LayoutInflater.from(this).inflate(R.layout.beseda_extention_item, mLinearLayout, false);

            ImageView imageViewExtention = layout2.findViewById(R.id.imageBesedaExtention);
            String imageName = rs.getString(rs.getColumnIndex("Image"+(i+1)));
            String imageNameMain = imageName.substring(0,imageName.length()-4);
            String imageNameExtention = imageName.substring(imageName.length()-3);
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
            String imageFullPath = getApplicationContext().getExternalFilesDir(null).getPath()
                    + "/besedi_pictures/" + imageName;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmRawImg = BitmapFactory.decodeFile(imageFullPath, options);
            int targetWidth = Integer.parseInt(screenWidthInPixels);
            targetWidth = (int) (((double) targetWidth) * 0.875);
            int targetHeight = bmRawImg.getHeight() * (targetWidth / bmRawImg.getWidth());
            Bitmap bmScaledImg = Bitmap.createScaledBitmap(bmRawImg, targetWidth, targetHeight, true);
            imageViewExtention.setImageBitmap(bmScaledImg);

            besedaTextView besedaTextExtention = layout2.findViewById(R.id.textBesedaExtention);
            String besedaTextX = rs.getString(rs.getColumnIndex("Text"+(i+2)));
            listBesedaTexts.add(besedaTextX);
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
                scrollTextView = layout2.findViewById(R.id.textBesedaExtention);
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
        intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_ALL_BESEDI");
        searchQuery = editSearchTextBesedatInput.getText().toString();
        intent.putExtra("com.grigorov.asparuh.probujdane.searchInputText", searchQuery);
        hideSearchControls(view);
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
            updateFullLayout ();
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
            updateFullLayout ();
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
                        listBesedaMarkers.get(m_loop).getStartIndex() +
                        " " +
                        listBesedaMarkers.get(m_loop).getEndIndex() +
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

        TextView textViewBesedaDetails = findViewById(R.id.textBesedaDetails);
        textViewBesedaDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP,besedaDetailsSize);

        Button buttonBesedaLink = findViewById(R.id.textBesedaLink);
        buttonBesedaLink.setTextSize(TypedValue.COMPLEX_UNIT_SP,besedaLinkSize);

        TextView textViewBesedaName = findViewById(R.id.textBesedaName);
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

        public void setEndIndex (int inputEndIndex) {endIndex = inputEndIndex; }
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


    public void hideSearchControls (View view) {
        searchControlsShown = false;
        searchKeyboardShown = true;
        linearLayoutSearchControls.removeAllViews();
        LinearLayout.LayoutParams param0 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.0f
        );
        linearLayoutSearchControls.setLayoutParams(param0);
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                5.0f
        );
        linearLayoutEmpty1.setLayoutParams(param1);
        linearLayoutEmpty1.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                5.0f
        );
        linearLayoutEmpty2.setLayoutParams(param2);
        linearLayoutEmpty2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));

        hideSearchkeyboard();

    }

    public void showSearchControls (View view) {

        searchControlsShown = true;

        updateFullLayout();

//        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                0,
//                70.0f
//        );
//        linearLayoutSearchControls.setLayoutParams(param);
//        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                0,
//                10.0f
//        );
//        linearLayoutEmpty1.setLayoutParams(param1);
//        linearLayoutEmpty1.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMainBackground));
//        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                0,
//                10.0f
//        );
//        linearLayoutEmpty2.setLayoutParams(param2);
//        linearLayoutEmpty2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorMainBackground));

        //View inflatedLayout= getLayoutInflater().inflate(R.layout.beseda_search_controls, null, false);
        //linearLayoutSearchControls.addView(inflatedLayout);
        editSearchTextBesedatInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    startSearchInBeseda(editSearchTextBesedatInput);
                }
                return false;
            }
        });
        editSearchTextBesedatInput.requestFocus();
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(editSearchTextBesedatInput, 0);
        showSearchKeyboard();
        editSearchTextBesedatInput.setText(searchQuery);
        editSearchTextBesedatInput.setSelection(searchQuery.length());

    }

    private void hideSearchkeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editSearchTextBesedatInput.getWindowToken(), 0);
    }

    private void showSearchKeyboard () {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editSearchTextBesedatInput, 0);
    }

    public void startSearchInBeseda (View view) {

        searchKeyboardShown = false;
        hideSearchkeyboard();

        numberSearchResults = 0;

        String searchBesedaVariant;
        if (variant1Selected==true) {
            searchBesedaVariant="1";
        } else {
            searchBesedaVariant="2";
        }

        searchQuery = editSearchTextBesedatInput.getText().toString();

        listBesedaMarkers.clear();
        
        for (int i=0; i<listBesedaTexts.size(); i++) {
            int index = 0;
            while ((index=listBesedaTexts.get(i).toLowerCase().indexOf(searchQuery.toLowerCase(),(index+1)))>=0) {
                int markerEndIndex = index+searchQuery.length()-1;
                if (markerEndIndex>=listBesedaTexts.get(i).length()) {
                    markerEndIndex = listBesedaTexts.get(i).length()-1;
                }
                listBesedaMarkers.add(
                        new besedaMarker(
                                i+1,
                                index,
                                markerEndIndex
                        )
                );
                numberSearchResults++;
                if (index==listBesedaTexts.get(i).length()) break;
            }
        }

        if (numberSearchResults==0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.search_no_results), LENGTH_LONG);
            toast.show();
            listBesedaMarkers.clear();
            srollTextX=0;
            srollTextIndex = 0;
        } else {
            scrollSearchResultIndex = 0;
            updateScrollFromSearch();
        }
        updateFullLayout();
        editSearchTextBesedatInput.setText(searchQuery);
    }

    public void goToNextSearchResult(View view) {
        if (numberSearchResults>0) {
            scrollSearchResultIndex++;
            if (scrollSearchResultIndex>=numberSearchResults) {
                scrollSearchResultIndex=0;
            }
            updateScrollFromSearch();
            updateFullLayout();
            editSearchTextBesedatInput.setText(searchQuery);
        }
    }

    public void goToPreviousSearchResult(View view) {
        if (numberSearchResults>0) {
            scrollSearchResultIndex--;
            if (scrollSearchResultIndex<0) {
                scrollSearchResultIndex=numberSearchResults-1;
            }
            updateScrollFromSearch();
            updateFullLayout();
            editSearchTextBesedatInput.setText(searchQuery);
        }
    }

    private void updateScrollFromSearch() {
        srollTextX = listBesedaMarkers.get(scrollSearchResultIndex).getTextIndex();
        srollTextIndex = listBesedaMarkers.get(scrollSearchResultIndex).getStartIndex();
    }
}



