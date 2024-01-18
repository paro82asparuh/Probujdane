package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static android.widget.Toast.LENGTH_LONG;

public class ZavetActivity extends AppCompatActivity {

    private ZavetDBHelper mydb;

    private boolean searchControlsShown;
    private boolean searchKeyboardShown;
    private int numberSearchResults;

    private Cursor rs;

    private final ArrayList<ChapterZavet> listChapters = new ArrayList<ChapterZavet>();

    private String searchQuery;

    private final ArrayList<ZavetBookMarker> listZavetBookMarkers= new ArrayList<ZavetBookMarker>();

    private LinearLayout mLinearLayout;

    private EditText editSearchTextBookInput;
    private LinearLayout linearLayoutSearchControls;
    private LinearLayout linearLayoutEmpty1;
    private LinearLayout linearLayoutEmpty2;

    private int scrollChapterX;
    private View scrollLinearLayout;
    private ScrollView scrollViewBook;
    private int scrollSearchResultIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        String bookScrollIndeces  = intent.getStringExtra("com.grigorov.asparuh.probujdane.BookScrollIndecesVar");
        String[] bScrollIndeces = bookScrollIndeces.split(" "); // Split to " " to read integers
        scrollChapterX = Integer.parseInt(bScrollIndeces[0]);

        listZavetBookMarkers.clear();
        String bookMarkers = intent.getStringExtra("com.grigorov.asparuh.probujdane.ZavetBookMarkersVar");
        if (!bookMarkers.equals("")) {
            String[] inputZavetBookMarkers = bookMarkers.split(" "); // Split to " " to read integers
            // prepare input list of markers
            if ( inputZavetBookMarkers.length > 1) {
                for (int marker_loop = 0; marker_loop < inputZavetBookMarkers.length; marker_loop = marker_loop + 4) {
                    listZavetBookMarkers.add(
                            new ZavetBookMarker(
                                    Integer.parseInt(inputZavetBookMarkers[marker_loop]),
                                    Integer.parseInt(inputZavetBookMarkers[marker_loop + 1]),
                                    Integer.parseInt(inputZavetBookMarkers[marker_loop + 2]),
                                    Integer.parseInt(inputZavetBookMarkers[marker_loop + 3])
                            )
                    );
                }
            }
            // combine adjacent markers
            ArrayList<ZavetBookMarker> listZavetBookMarkersCopy = new ArrayList<ZavetBookMarker>();
            listZavetBookMarkersCopy.clear();
            for (int marker_loop=0; marker_loop<listZavetBookMarkers.size();marker_loop=marker_loop+1) {
                listZavetBookMarkersCopy.add(
                        new ZavetBookMarker(
                                listZavetBookMarkers.get(marker_loop).getChapterIndex(),
                                listZavetBookMarkers.get(marker_loop).getTextSide(),
                                listZavetBookMarkers.get(marker_loop).getStartIndex(),
                                listZavetBookMarkers.get(marker_loop).getEndIndex()
                        )
                );
            }
            listZavetBookMarkers.clear();
            listZavetBookMarkers.add(
                    new ZavetBookMarker(
                            listZavetBookMarkersCopy.get(0).getChapterIndex(),
                            listZavetBookMarkersCopy.get(0).getTextSide(),
                            listZavetBookMarkersCopy.get(0).getStartIndex(),
                            listZavetBookMarkersCopy.get(0).getEndIndex()
                    )
            );
            int listIndex = 0;
            for (int marker_loop=1; marker_loop<listZavetBookMarkersCopy.size();marker_loop=marker_loop+1) {
                if (
                        (listZavetBookMarkers.get(listIndex).getChapterIndex() == listZavetBookMarkersCopy.get(marker_loop).getChapterIndex() ) &&
                                (listZavetBookMarkers.get(listIndex).getTextSide() == listZavetBookMarkersCopy.get(marker_loop).getTextSide() ) &&
                                ( (listZavetBookMarkers.get(listIndex).getEndIndex() + 1)== listZavetBookMarkersCopy.get(marker_loop).getStartIndex() )
                ) {
                    listZavetBookMarkers.get(listIndex).setEndIndex(listZavetBookMarkersCopy.get(marker_loop).getEndIndex());
                } else {
                    listZavetBookMarkers.add(
                            new ZavetBookMarker(
                                    listZavetBookMarkersCopy.get(marker_loop).getChapterIndex(),
                                    listZavetBookMarkersCopy.get(marker_loop).getTextSide(),
                                    listZavetBookMarkersCopy.get(marker_loop).getStartIndex(),
                                    listZavetBookMarkersCopy.get(marker_loop).getEndIndex()
                            )
                    );
                    listIndex++;
                }
            }
            // find the scrollSearchResultIndex value, so that move next / previous search results are possible
            for (int marker_loop=0; marker_loop<listZavetBookMarkers.size();marker_loop=marker_loop+1) {
                if (scrollChapterX == listZavetBookMarkers.get(marker_loop).getChapterIndex() ) {
                    scrollSearchResultIndex = marker_loop;
                }
            }
            searchControlsShown = true;
            numberSearchResults = listZavetBookMarkers.size();
        } else {
            searchControlsShown = false;
            numberSearchResults = 0;
            scrollSearchResultIndex= 0;
        }

        searchKeyboardShown = false;

        mydb = new ZavetDBHelper(this);

        listChapters.clear();
        rs = mydb.getChapters();
        rs.moveToFirst();
        for (int i_loop=0; i_loop<rs.getCount(); i_loop++) {
            listChapters.add(new ChapterZavet(
                    rs.getString(rs.getColumnIndex("ID")),
                    rs.getString(rs.getColumnIndex("Level")),
                    rs.getString(rs.getColumnIndex("Color")),
                    rs.getString(rs.getColumnIndex("Left_Text")),
                    rs.getString(rs.getColumnIndex("Center_Text")),
                    rs.getString(rs.getColumnIndex("Right_Text")),
                    rs.getString(rs.getColumnIndex("Center_Bold"))
            ));
            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }

        searchQuery = "";

        updateFullLayout ();
    }

    private void updateFullLayout () {
        setContentView(R.layout.activity_zavet);

        TextView textBookTitle = findViewById(R.id.textBookTitle);
        textBookTitle.setText(getResources().getString(R.string.zavet_string));

        mLinearLayout = findViewById(R.id.textBookLinearLayout);

        editSearchTextBookInput = findViewById(R.id.edit_search_text_zavet_input);
        linearLayoutSearchControls = findViewById(R.id.linear_layout_search_controls);
        linearLayoutEmpty1 = findViewById(R.id.linear_layout_empty_1);
        linearLayoutEmpty2 = findViewById(R.id.linear_layout_empty_2);
        if (!searchControlsShown) {
            hideSearchControls(linearLayoutSearchControls);
        } else {
            //showSearchControls(linearLayoutSearchControls);
        }
        if (searchKeyboardShown) {
            showSearchKeyboard();
        } else {
            hideSearchkeyboard();
        }

        scrollViewBook = findViewById(R.id.scrollViewBook);

        mLinearLayout.removeAllViews();

        for (int i_loop=0; i_loop < listChapters.size(); i_loop++) {

            View layoutChapter = LayoutInflater.from(this).inflate(R.layout.chapter_zavet, mLinearLayout, false);
            TextView leftTextView = layoutChapter.findViewById(R.id.textChapterLeft);
            TextView centerTextView = layoutChapter.findViewById(R.id.textChapterCenter);
            TextView rightTextView = layoutChapter.findViewById(R.id.textChapterRight);

            layoutChapter.setTag("linear_layout_chapter_"+ (Integer.valueOf(i_loop)));
            leftTextView.setTag("left_textview_chapter_"+ (Integer.valueOf(i_loop)));
            centerTextView.setTag("center_textview_chapter_"+ (Integer.valueOf(i_loop)));
            rightTextView.setTag("right_textview_chapter_"+ (Integer.valueOf(i_loop)));

            leftTextView.setText(listChapters.get(i_loop).getTextLeft());
            centerTextView.setText(listChapters.get(i_loop).getTextCenter());
            rightTextView.setText(listChapters.get(i_loop).getTextRight());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                leftTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                centerTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                rightTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            }

            if (listChapters.get(i_loop).getBoldCenter().equals("1")) {
                centerTextView.setTypeface(centerTextView.getTypeface(), Typeface.BOLD_ITALIC);
            }

            String chapterColor = listChapters.get(i_loop).getColor();
            if (chapterColor.equals("Red")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetRedBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetRedText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetRedText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetRedText, null));
            } else  if (chapterColor.equals("Pink")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPinkBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPinkText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPinkText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPinkText, null));
            } else  if (chapterColor.equals("Orange")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetOrangeBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetOrangeText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetOrangeText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetOrangeText, null));
            } else  if (chapterColor.equals("Yellow")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetYellowBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetYellowText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetYellowText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetYellowText, null));
            } else  if (chapterColor.equals("Green")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetGreenBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetGreenText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetGreenText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetGreenText, null));
            } else  if (chapterColor.equals("Blue")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBlueBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBlueText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBlueText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBlueText, null));
            } else  if (chapterColor.equals("Purple")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPurpleBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPurpleText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPurpleText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPurpleText, null));
            } else  if (chapterColor.equals("Amethyst")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetAmethystBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetAmethystText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetAmethystText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetAmethystText, null));
            } else  if (chapterColor.equals("Diamant")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetDiamantBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetDiamantText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetDiamantText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetDiamantText, null));
            } else  if (chapterColor.equals("Bright")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBrightBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBrightText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBrightText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBrightText, null));
            } else  if (chapterColor.equals("White")) {
                layoutChapter.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetWhiteBackgorund, null));
                leftTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetWhiteText, null));
                centerTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetWhiteText, null));
                rightTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetWhiteText, null));
            }

            mLinearLayout.addView(layoutChapter);

            if (scrollChapterX==i_loop+1) {
                scrollLinearLayout = layoutChapter;
            }

            updateTextSize();
            
        }

        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        for (int markerIndex=0; markerIndex<listZavetBookMarkers.size();markerIndex++) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            Integer chapterIndex = Integer.valueOf(listZavetBookMarkers.get(markerIndex).getChapterIndex());
            chapterIndex = chapterIndex -1;
            TextView markedTextView;
            if (listZavetBookMarkers.get(markerIndex).getTextSide() == 0) {
                markedTextView = mLinearLayout.findViewWithTag("left_textview_chapter_"+ chapterIndex);
            } else if (listZavetBookMarkers.get(markerIndex).getTextSide() == 1) {
                markedTextView = mLinearLayout.findViewWithTag("center_textview_chapter_"+ chapterIndex);
            } else {
                markedTextView = mLinearLayout.findViewWithTag("right_textview_chapter_"+ chapterIndex);
            }
            SpannableString markedString = new SpannableString( markedTextView.getText() );
            markedString.setSpan(new StyleSpan(Typeface.ITALIC),
                    listZavetBookMarkers.get(markerIndex).getStartIndex(),
                    listZavetBookMarkers.get(markerIndex).getEndIndex()+1, flag);
            String color = listChapters.get(chapterIndex).getColor();
            if ( ( color.equals("Red")) ) {
                markedString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorZavetMarkerTextInRedBackground, null)),
                        listZavetBookMarkers.get(markerIndex).getStartIndex(),
                        listZavetBookMarkers.get(markerIndex).getEndIndex() + 1, flag);
            } else if ( ( color.equals("Orange")) ) {
                markedString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorZavetMarkerTextInOrangeBackground, null)),
                        listZavetBookMarkers.get(markerIndex).getStartIndex(),
                        listZavetBookMarkers.get(markerIndex).getEndIndex() + 1, flag);
            } else if ( ( color.equals("Purple")) ) {
                markedString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorZavetMarkerTextInPurpleBackground, null)),
                        listZavetBookMarkers.get(markerIndex).getStartIndex(),
                        listZavetBookMarkers.get(markerIndex).getEndIndex() + 1, flag);
            } else { // includes pink, yellow, green, blue, amethyst, white, bright, diamant
                markedString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        listZavetBookMarkers.get(markerIndex).getStartIndex(),
                        listZavetBookMarkers.get(markerIndex).getEndIndex() + 1, flag);
            }
            spannableStringBuilder.append(markedString);
            markedTextView.setText(spannableStringBuilder);
        }

        if (scrollLinearLayout!=null) {
            scrollLinearLayout.post(() -> {
                scrollDirectToTarget();
            });
        }

    }

    private void scrollDirectToTarget () {
        scrollViewBook = findViewById(R.id.scrollViewBook);
        int scrollY = 0;
        if (scrollChapterX != 1) {
            int [] scrollViewBookCoor = {0,0};
            int [] scrollLinearLayoutCoor = {0,0};
            scrollViewBook.getLocationInWindow(scrollViewBookCoor);
            scrollLinearLayout.getLocationInWindow(scrollLinearLayoutCoor);
            scrollViewBook.scrollTo(0, (scrollLinearLayoutCoor[1] - scrollViewBookCoor[1]) );
        }
    }


    private void updateTextSize () {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String bookTextSizeString = sharedPref.getString("com.grigorov.asparuh.probujdane.textsize", "14");
        int bookTextSize = Integer.parseInt(bookTextSizeString);
        int bookTitleSize = bookTextSize + 4;

        TextView textViewBookTitle = findViewById(R.id.textBookTitle);
        textViewBookTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,bookTitleSize);

        for (int i=0; i < mLinearLayout.getChildCount(); i++) {
            View view = mLinearLayout.getChildAt(i);
            if (view instanceof LinearLayout) {
                TextView leftTextView = (TextView) ((LinearLayout)view).getChildAt(1);
                leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,bookTextSize);
                TextView centerTextView = (TextView) ((LinearLayout)view).getChildAt(3);
                centerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,bookTextSize);
                TextView rightTextView = (TextView) ((LinearLayout)view).getChildAt(5);
                rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,bookTextSize);
            }
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

        //View inflatedLayout= getLayoutInflater().inflate(R.layout.beseda_search_controls, null, false);
        //linearLayoutSearchControls.addView(inflatedLayout);
        editSearchTextBookInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    startSearchInBook(editSearchTextBookInput);
                }
                return false;
            }
        });
        editSearchTextBookInput.requestFocus();
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(editSearchTextBesedatInput, 0);
        showSearchKeyboard();
        editSearchTextBookInput.setText(searchQuery);
        editSearchTextBookInput.setSelection(searchQuery.length());

    }

    private void hideSearchkeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editSearchTextBookInput.getWindowToken(), 0);
    }

    private void showSearchKeyboard () {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editSearchTextBookInput, 0);
    }

    public void onResume () {
        super.onResume();
        updateTextSize();
    }

    public void onDestroy () {
        super.onDestroy();
        mydb.close();
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

    public void startSearchInBook (View view) {

        searchKeyboardShown = false;
        hideSearchkeyboard();

        numberSearchResults = 0;

        searchQuery = editSearchTextBookInput.getText().toString();

        listZavetBookMarkers.clear();

        for (int i=0; i<listChapters.size(); i++) {
            searchInBookString(listChapters.get(i).getTextLeft(),i,0);
            searchInBookString(listChapters.get(i).getTextCenter(),i,1);
            searchInBookString(listChapters.get(i).getTextRight(),i,2);
        }

        if (numberSearchResults==0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.search_no_results), LENGTH_LONG);
            toast.show();
            listZavetBookMarkers.clear();
            scrollChapterX=0;
        } else {
            scrollSearchResultIndex = 0;
            updateScrollFromSearch();
        }
        updateFullLayout();
        editSearchTextBookInput.setText(searchQuery);
        editSearchTextBookInput.setSelection(searchQuery.length());
    }

    private void searchInBookString (String inputString, int inputChapterIndex, int inputTextSide) {
        int index = 0;
        while ((index=inputString.toLowerCase().indexOf(searchQuery.toLowerCase(),(index+1)))>=0) {
            int markerEndIndex = index+searchQuery.length()-1;
            if (markerEndIndex>=inputString.length()) {
                markerEndIndex = inputString.length()-1;
            }
            listZavetBookMarkers.add(
                    new ZavetBookMarker(
                            inputChapterIndex + 1,
                            inputTextSide,
                            index,
                            markerEndIndex
                    )
            );
            numberSearchResults++;
            if (index==inputString.length()) break;
        }
    }

    public void goToNextSearchResult(View view) {
        if (numberSearchResults>0) {
            scrollSearchResultIndex++;
            if (scrollSearchResultIndex>=numberSearchResults) {
                scrollSearchResultIndex=0;
            }
            updateScrollFromSearch();
            scrollDirectToTarget();
            editSearchTextBookInput.setText(searchQuery);
            editSearchTextBookInput.setSelection(searchQuery.length());
        }
    }

    public void goToPreviousSearchResult(View view) {
        if (numberSearchResults>0) {
            scrollSearchResultIndex--;
            if (scrollSearchResultIndex<0) {
                scrollSearchResultIndex=numberSearchResults-1;
            }
            updateScrollFromSearch();
            scrollDirectToTarget();
            editSearchTextBookInput.setText(searchQuery);
            editSearchTextBookInput.setSelection(searchQuery.length());
        }
    }

    private void updateScrollFromSearch() {
        scrollChapterX = listZavetBookMarkers.get(scrollSearchResultIndex).getChapterIndex();
        Integer temp = Integer.valueOf(scrollChapterX);
        temp = temp -1;
        scrollLinearLayout = mLinearLayout.findViewWithTag("linear_layout_chapter_"+ temp);
        scrollViewBook.scrollTo(0,0);
    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_ALL_BOOKS");
        searchQuery = editSearchTextBookInput.getText().toString();
        intent.putExtra("com.grigorov.asparuh.probujdane.searchInputText", searchQuery);
        hideSearchControls(view);
        startActivity(intent);
    }
    
}