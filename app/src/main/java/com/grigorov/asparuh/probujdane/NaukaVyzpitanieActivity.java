package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.END;
import static android.view.Gravity.START;
import static android.widget.Toast.LENGTH_LONG;

public class NaukaVyzpitanieActivity extends AppCompatActivity {

    private NaukaVyzDBHelper mydb;

    private boolean searchControlsShown;
    private boolean searchKeyboardShown;
    private int numberSearchResults;

    private Cursor rs;

    private ArrayList<ChapterNaukaVyz> listChapters = new ArrayList<ChapterNaukaVyz>();

    private String searchQuery;

    private ArrayList<BookMarker> listBookMarkers= new ArrayList<BookMarker>();

    private LinearLayout mLinearLayout;

    private EditText editSearchTextBookInput;
    private LinearLayout linearLayoutSearchControls;
    private LinearLayout linearLayoutEmpty1;
    private LinearLayout linearLayoutEmpty2;

    private int scrollChapterX;
    private boolean scrollChapterInTitle;
    private int scrollChapterIndex;
    private TextView scrollTextView;
    private ScrollView scrollViewBook;
    private int scrollSearchResultIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String bookScrollIndeces  = intent.getStringExtra("com.grigorov.asparuh.probujdane.BookScrollIndecesVar");
        String[] bScrollIndeces = bookScrollIndeces.split(" "); // Split to " " to read integers
        scrollChapterX = Integer.parseInt(bScrollIndeces[0]);
        scrollChapterInTitle = Integer.parseInt(bScrollIndeces[1]) != 0;
        scrollChapterIndex = Integer.parseInt(bScrollIndeces[2]);

        listBookMarkers.clear();
        String bookMarkers = intent.getStringExtra("com.grigorov.asparuh.probujdane.BookMarkersVar");
        if (bookMarkers.equals("")==false) {
            String[] inputBookMarkers = bookMarkers.split(" "); // Split to " " to read integers
            // prepare input list of markers
            if ( inputBookMarkers.length > 1) {
                for (int marker_loop = 0; marker_loop < inputBookMarkers.length; marker_loop = marker_loop + 4) {
                    boolean inTitle;
                    inTitle = Integer.parseInt(inputBookMarkers[marker_loop + 1]) != 0;
                    listBookMarkers.add(
                            new BookMarker(
                                    Integer.parseInt(inputBookMarkers[marker_loop]),
                                    inTitle,
                                    Integer.parseInt(inputBookMarkers[marker_loop + 2]),
                                    Integer.parseInt(inputBookMarkers[marker_loop + 3])
                            )
                    );
                }
            }

            // combine adjacent markers
            ArrayList<BookMarker> listBookMarkersCopy = new ArrayList<BookMarker>();
            // listBesedaMarkersCopy = listBesedaMarkers;
            listBookMarkersCopy.clear();
            for (int marker_loop=0; marker_loop<listBookMarkers.size();marker_loop=marker_loop+1) {
                listBookMarkersCopy.add(
                        new BookMarker(
                                listBookMarkers.get(marker_loop).getChapterIndex(),
                                listBookMarkers.get(marker_loop).getInTitle(),
                                listBookMarkers.get(marker_loop).getStartIndex(),
                                listBookMarkers.get(marker_loop).getEndIndex()
                        )
                );
            }
            listBookMarkers.clear();
            listBookMarkers.add(
                    new BookMarker(
                            listBookMarkersCopy.get(0).getChapterIndex(),
                            listBookMarkersCopy.get(0).getInTitle(),
                            listBookMarkersCopy.get(0).getStartIndex(),
                            listBookMarkersCopy.get(0).getEndIndex()
                    )
            );
            int listIndex = 0;
            for (int marker_loop=1; marker_loop<listBookMarkersCopy.size();marker_loop=marker_loop+1) {
                if (
                        (listBookMarkers.get(listIndex).getChapterIndex() == listBookMarkersCopy.get(marker_loop).getChapterIndex() ) &&
                                (listBookMarkers.get(listIndex).getInTitle() == listBookMarkersCopy.get(marker_loop).getInTitle() ) &&
                                ( (listBookMarkers.get(listIndex).getEndIndex() + 1)== listBookMarkersCopy.get(marker_loop).getStartIndex() )
                ) {
                    listBookMarkers.get(listIndex).setEndIndex(listBookMarkersCopy.get(marker_loop).getEndIndex());
                } else {
                    listBookMarkers.add(
                            new BookMarker(
                                    listBookMarkersCopy.get(marker_loop).getChapterIndex(),
                                    listBookMarkersCopy.get(marker_loop).getInTitle(),
                                    listBookMarkersCopy.get(marker_loop).getStartIndex(),
                                    listBookMarkersCopy.get(marker_loop).getEndIndex()
                            )
                    );
                    listIndex++;
                }
            }
            // find the scrollSearchResultIndex value, so that move next / previous search results are possible
            for (int marker_loop=0; marker_loop<listBookMarkers.size();marker_loop=marker_loop+1) {
                if ( (scrollChapterX == listBookMarkers.get(marker_loop).getChapterIndex() ) &&
                        (scrollChapterInTitle == listBookMarkers.get(marker_loop).getInTitle() ) &&
                        (scrollChapterIndex == listBookMarkers.get(marker_loop).getStartIndex()) ) {
                    scrollSearchResultIndex = marker_loop;
                }
            }
            searchControlsShown = true;
            numberSearchResults = listBookMarkers.size();
        } else {
            searchControlsShown = false;
            numberSearchResults = 0;
            scrollSearchResultIndex= 0;
        }
        searchKeyboardShown = false;

        mydb = new NaukaVyzDBHelper(this);

        listChapters.clear();
        rs = mydb.getChapters();
        rs.moveToFirst();
        for (int i_loop=0; i_loop<rs.getCount(); i_loop++) {
            listChapters.add( new ChapterNaukaVyz(
                    rs.getString(rs.getColumnIndex("ID")),
                    rs.getString(rs.getColumnIndex("Chapter_Level")),
                    rs.getString(rs.getColumnIndex("Chapter_Title")),
                    rs.getString(rs.getColumnIndex("Chapter_Content")),
                    rs.getString(rs.getColumnIndex("Chapter_Indentation"))
            ));
            rs.moveToNext();
        }

        searchQuery = "";

        updateFullLayout ();

    }

    private void updateFullLayout () {
        setContentView(R.layout.activity_nauka_vyzpitanie);

        TextView textBookTitle = findViewById(R.id.textBookTitle);
        textBookTitle.setText(getResources().getString(R.string.nauka_vyzpitanie_string));

        mLinearLayout = findViewById(R.id.textBookLinearLayout);

        editSearchTextBookInput = findViewById(R.id.edit_search_text_nauka_vyz_input);
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

        scrollViewBook = findViewById(R.id.scrollViewBook);

        mLinearLayout.removeAllViews();
        for (int i_loop=0; i_loop < listChapters.size(); i_loop++) {

            View layout2 = LayoutInflater.from(this).inflate(R.layout.chapter_nauka_vyz, mLinearLayout, false);
            //besedaTextView titleTextView = layout2.findViewById(R.id.textChapterTitle);
            //besedaTextView contentTextView = layout2.findViewById(R.id.textChapterContent);
            TextView titleTextView = layout2.findViewById(R.id.textChapterTitle);
            TextView contentTextView = layout2.findViewById(R.id.textChapterContent);

            titleTextView.setTag("title_textview_chapter_"+(new Integer(i_loop)).toString());
            contentTextView.setTag("content_textview_chapter_"+(new Integer(i_loop)).toString());

            /*
            boolean titleMarked = false;
            boolean contentMarked = false;
            for (int markerIndex = 0; markerIndex < listBookMarkers.size(); markerIndex = markerIndex + 1) {
                if ((listBookMarkers.get(markerIndex).getChapterIndex() == i_loop+1) &&
                        (listBookMarkers.get(markerIndex).getInTitle() == true) ) {
                    titleMarked = true;
                }
                if ((listBookMarkers.get(markerIndex).getChapterIndex() == i_loop+1) &&
                        (listBookMarkers.get(markerIndex).getInTitle() == false) ) {
                    contentMarked = true;
                }
            }
            if (titleMarked==true) {
                titleTextView.setText(prepareSpannableStringBuilder(i_loop, true));
            } else {
                titleTextView.setText(listChapters.get(i_loop).getTitle()+"\n");
            }
            if (contentMarked==true) {
                contentTextView.setText(prepareSpannableStringBuilder(i_loop, false));
            } else {
                contentTextView.setText(listChapters.get(i_loop).getContent()+"\n\n");
            }
            */

            titleTextView.setText(listChapters.get(i_loop).getTitle()+"\n");
            contentTextView.setText(listChapters.get(i_loop).getContent()+"\n\n");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                contentTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            }

            if (listChapters.get(i_loop).getIndentation().equals("Center")) {
                titleTextView.setGravity(CENTER);
                contentTextView.setGravity(CENTER);
            } else if (listChapters.get(i_loop).getIndentation().equals("Left")) {
                titleTextView.setGravity(CENTER_VERTICAL|START);
                contentTextView.setGravity(CENTER_VERTICAL|START);
            } else {
                titleTextView.setGravity(CENTER_VERTICAL|END);
                contentTextView.setGravity(CENTER_VERTICAL|END);
            }

            mLinearLayout.addView(layout2);

            if (scrollChapterX==i_loop+1) {
                if (scrollChapterInTitle==true) {
                    scrollTextView = titleTextView;
                } else {
                    scrollTextView = contentTextView;
                }
            }

            updateTextSize();

        }

        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        for (int markerIndex=0; markerIndex<listBookMarkers.size();markerIndex++) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            Integer temp = new Integer(listBookMarkers.get(markerIndex).getChapterIndex());
            temp = temp -1;
            TextView markedTextView;
            if (listBookMarkers.get(markerIndex).getInTitle() == true) {
                markedTextView = mLinearLayout.findViewWithTag("title_textview_chapter_"+temp.toString());
            } else {
                markedTextView = mLinearLayout.findViewWithTag("content_textview_chapter_"+temp.toString());
            }
            SpannableString markedString = new SpannableString( markedTextView.getText() );
            markedString.setSpan(new StyleSpan(Typeface.ITALIC),
                    listBookMarkers.get(markerIndex).getStartIndex(),
                    listBookMarkers.get(markerIndex).getEndIndex()+1, flag);
            markedString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                    listBookMarkers.get(markerIndex).getStartIndex(),
                    listBookMarkers.get(markerIndex).getEndIndex()+1, flag);
            spannableStringBuilder.append(markedString);
            markedTextView.setText(spannableStringBuilder);
        }

        if (scrollTextView!=null) {
            scrollTextView.post(() -> {
                scrollDirectToTarget();
            });
        }

    }

    private SpannableStringBuilder prepareSpannableStringBuilder (int i_loop, Boolean inputInTitle) {
        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String inputString;
        if (inputInTitle==true) {
            inputString= listChapters.get(i_loop).getTitle() + "\n";
        } else {
            inputString= listChapters.get(i_loop).getContent() + "\n\n";
        }
        for (int textIndex=0; textIndex < inputString.length(); textIndex++) {
            String c = String.valueOf(inputString.charAt(textIndex));
            SpannableString spannableString = new SpannableString(c);
            boolean marked = false;
            for (int markerIndex = 0; markerIndex < listBookMarkers.size(); markerIndex = markerIndex + 1) {
                if ((listBookMarkers.get(markerIndex).getChapterIndex() == i_loop+1) &&
                        (listBookMarkers.get(markerIndex).getInTitle() == inputInTitle) &&
                        (listBookMarkers.get(markerIndex).getStartIndex() <= textIndex) &&
                        (listBookMarkers.get(markerIndex).getEndIndex() >= textIndex)
                ) {
                    marked = true;
                }
            }
            if (marked == false) {
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorNaukaVyzText, null)),
                        0, spannableString.length(), flag);
            } else {
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), flag);
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        0, spannableString.length(), flag);
            }
            spannableStringBuilder.append(spannableString);
        }
        return spannableStringBuilder;
    }


    private void scrollDirectToTarget () {
        scrollViewBook = findViewById(R.id.scrollViewBook);
        int scrollY = 0;
        scrollY = scrollTextView.getLayout().getLineForOffset(scrollChapterIndex);
        scrollY = scrollTextView.getLayout().getLineTop(scrollY);
        if ((scrollChapterX != 1) || (scrollChapterIndex != 0)) {
            int [] scrollViewBookCoor = {0,0};
            int [] scrollTextViewCoor = {0,0};
            scrollViewBook.getLocationInWindow(scrollViewBookCoor);
            scrollTextView.getLocationInWindow(scrollTextViewCoor);
            scrollViewBook.scrollTo(0, (scrollTextViewCoor[1] - scrollViewBookCoor[1])+scrollY );
        }
    }


    private void updateTextSize () {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String bookTextSizeString = sharedPref.getString("com.grigorov.asparuh.probujdane.textsize", "14");
        int bookTextSize = Integer.parseInt(bookTextSizeString);
        int bookChapterTitleSize = bookTextSize + 2;
        int bookTitleSize = bookTextSize + 4;

        TextView textViewBookTitle = findViewById(R.id.textBookTitle);
        textViewBookTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,bookTitleSize);

        for (int i=0; i < mLinearLayout.getChildCount(); i++) {
            View view = mLinearLayout.getChildAt(i);
            if (view instanceof LinearLayout) {
                TextView textViewChpaterTitle = (TextView) ((LinearLayout)view).getChildAt(0);
                textViewChpaterTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,bookChapterTitleSize);
                TextView textViewChpaterContent = (TextView) ((LinearLayout)view).getChildAt(1);
                textViewChpaterContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,bookTextSize);
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
        mydb = new NaukaVyzDBHelper(this);
        updateTextSize();
    }

    public void onPause () {
        super.onPause();
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

        listBookMarkers.clear();

        for (int i=0; i<listChapters.size(); i++) {
            searchInBookString(listChapters.get(i).getTitle(),i,true);
            searchInBookString(listChapters.get(i).getContent(),i,false);
        }

        if (numberSearchResults==0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.search_no_results), LENGTH_LONG);
            toast.show();
            listBookMarkers.clear();
            scrollChapterX=0;
            scrollChapterIndex = 0;
        } else {
            scrollSearchResultIndex = 0;
            updateScrollFromSearch();
        }
        updateFullLayout();
        editSearchTextBookInput.setText(searchQuery);
        editSearchTextBookInput.setSelection(searchQuery.length());
    }

    private void searchInBookString (String inputString, int inputChapterIndex, boolean inputInTitle) {
        int index = 0;
        while ((index=inputString.toLowerCase().indexOf(searchQuery.toLowerCase(),(index+1)))>=0) {
            int markerEndIndex = index+searchQuery.length()-1;
            if (markerEndIndex>=inputString.length()) {
                markerEndIndex = inputString.length()-1;
            }
            listBookMarkers.add(
                    new BookMarker(
                            inputChapterIndex + 1,
                            inputInTitle,
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
        scrollChapterX = listBookMarkers.get(scrollSearchResultIndex).getChapterIndex();
        scrollChapterInTitle = listBookMarkers.get(scrollSearchResultIndex).getInTitle();
        scrollChapterIndex = listBookMarkers.get(scrollSearchResultIndex).getStartIndex();
        Integer temp = new Integer(scrollChapterX);
        temp = temp -1;
        if (scrollChapterInTitle == true) {
            scrollTextView = mLinearLayout.findViewWithTag("title_textview_chapter_"+temp.toString());
        } else {
            scrollTextView = mLinearLayout.findViewWithTag("content_textview_chapter_"+temp.toString());
        }
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

    private class BookMarker {

        private int chapterIndex;
        private boolean inTitle;
        private int startIndex;
        private int endIndex;

        public BookMarker (int inputChapterIndex, boolean inputInTitle, int inputStartIndex, int inputEndIndex) {
            chapterIndex = inputChapterIndex;
            inTitle = inputInTitle;
            startIndex = inputStartIndex;
            endIndex = inputEndIndex;
        }

        public int getChapterIndex() {
            return chapterIndex;
        }

        public boolean getInTitle() { return inTitle; }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex (int inputEndIndex) {endIndex = inputEndIndex; }

    }

}