package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.widget.Toast.LENGTH_LONG;

public class SearchMenuActivity extends AppCompatActivity {

    private besediDBHelper mydb;

    private EditText editSearchTextInput;

    private ListView listSearchView;
    private Spinner spinnerSearchWhat;
    private Spinner spinnerSearchWhere;
    private String[] optionsSearchWhat;
    private String[] optionsSearchWhere;
    private LinearLayout linearLayoutScrollViewSearchResults;

    private ArrayList<searchResult> listSearchResult= new ArrayList<searchResult>();
    SearchResultAdapter searchResultAdapter;

    private String screenWidthInPixels;

    private Integer searchCounter;

    private int searchItemTextSize;
    private int searchOptionsTextSize;
    private int searchButtonTextSize;

    static private int textMainMaxLenght=500;
    static private String stringSentenceEnds = ".?!\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String besedaTextSizeString = sharedPref.getString("com.grigorov.asparuh.probujdane.textsize", "14");
        searchItemTextSize = Integer.parseInt(besedaTextSizeString);
        searchOptionsTextSize = searchItemTextSize + 2;
        searchButtonTextSize = searchItemTextSize + 4;
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setTextSize(searchButtonTextSize);

        editSearchTextInput   = (EditText)findViewById(R.id.search_text_input);
        editSearchTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    startSearch(editSearchTextInput);
                }
                return false;
            }
        });
        editSearchTextInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editSearchTextInput, 0);

        linearLayoutScrollViewSearchResults = (LinearLayout) findViewById(R.id.search_lenear_layout_scroll_view);

        optionsSearchWhat = new String[]{
                getResources().getString(R.string.search_option_every_word),
                getResources().getString(R.string.search_option_exact_phrase),
                getResources().getString(R.string.search_option_every_word_near),
                getResources().getString(R.string.search_option_fts)
            };

        optionsSearchWhere = new String[]{
                getResources().getString(R.string.search_option_whole_slovo),
                getResources().getString(R.string.search_option_all_besedi),
                getResources().getString(R.string.search_option_one_beseda),
                getResources().getString(R.string.search_option_molitvi),
                getResources().getString(R.string.search_option_formuli)
        };

        spinnerSearchWhat = (Spinner)findViewById(R.id.spinnerSearchWhat);
        SpinnerSearchAdapter adapterSearchWhat = new SpinnerSearchAdapter(this, R.layout.spinner_search_item, optionsSearchWhat);
        spinnerSearchWhat.setAdapter(adapterSearchWhat);
        adapterSearchWhat.notifyDataSetChanged();
        spinnerSearchWhat.setSelection(0,true);

        spinnerSearchWhere = (Spinner)findViewById(R.id.spinnerSearchWhere);
        SpinnerSearchAdapter adapterSearchWhere = new SpinnerSearchAdapter(this, R.layout.spinner_search_item, optionsSearchWhere);
        spinnerSearchWhere.setAdapter(adapterSearchWhere);
        adapterSearchWhere.notifyDataSetChanged();
        spinnerSearchWhere.setSelection(0,true);

        updateBesedaInputVisibility();

        spinnerSearchWhere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Your code here
                updateBesedaInputVisibility();
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        searchCounter=0;

        listSearchResult.clear();
        searchResultAdapter = new SearchResultAdapter(this, listSearchResult);
        listSearchView = (ListView) findViewById(R.id.search_list_view);
        listSearchView.setAdapter(searchResultAdapter);

        mydb = new besediDBHelper(this);

    }

    public void onResume () {
        super.onResume();
        mydb = new besediDBHelper(this);
    }

    public void onPause () {
        super.onPause();
        mydb.close();
    }

    private void updateBesedaInputVisibility() {
        LinearLayout linearLayoutBesedaInput = (LinearLayout) findViewById(R.id.search_beseda_input_linear);
        View view1 = findViewById(R.id.search_beseda_input_v1);
        View view2 = findViewById(R.id.search_beseda_input_v2);
        EditText editTextBesedaInput = (EditText) findViewById(R.id.search_beseda_input_text);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) linearLayoutScrollViewSearchResults.getLayoutParams();
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) linearLayoutBesedaInput.getLayoutParams();
        if  ( optionsSearchWhere[spinnerSearchWhere.getSelectedItemPosition()] ==
                getResources().getString(R.string.search_option_one_beseda) ) {
            linearLayoutBesedaInput.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
            editTextBesedaInput.setVisibility(View.VISIBLE);
            layoutParams1.weight = layoutParams1.weight - layoutParams2.weight;
            linearLayoutScrollViewSearchResults.setLayoutParams(layoutParams1);
        } else {
            linearLayoutBesedaInput.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            editTextBesedaInput.setVisibility(View.GONE);
            layoutParams1.weight = layoutParams1.weight + layoutParams2.weight;
            linearLayoutScrollViewSearchResults.setLayoutParams(layoutParams1);
        }
    }


    /***** Adapter class extends with ArrayAdapter ******/
    public class SpinnerSearchAdapter extends ArrayAdapter<String> {

        private Activity activity;
        private String[] data;
        LayoutInflater inflater;

        /*************  CustomAdapter Constructor *****************/
        public SpinnerSearchAdapter(
                SearchMenuActivity activitySpinner,
                int textViewResourceId,
                String[] objects) {
            super(activitySpinner, textViewResourceId, objects);
            /********** Take passed values **********/
            activity = activitySpinner;
            data     = objects;
            /***********  Layout inflator to call external xml layout () **********************/
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return getCustomView(position, convertView, parent);
            View row = inflater.inflate(R.layout.spinner_search_top, parent, false);
            TextView textView1        = (TextView)row.findViewById(R.id.textViewSpinnerSearchTop);
            textView1.setText(data[position]);
            textView1.setTextSize(searchOptionsTextSize);
            return row;
        }

        // This funtion called for each row ( Called data.size() times )
        public View getCustomView(int position, View convertView, ViewGroup parent) {

            /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
            View row = inflater.inflate(R.layout.spinner_search_item, parent, false);
            TextView textView1        = (TextView)row.findViewById(R.id.textViewSpinnerSearch);
            textView1.setText(data[position]);
            textView1.setTextSize(searchOptionsTextSize);
            return row;
        }
    }


    public class SearchResultAdapter extends ArrayAdapter<searchResult> {

        // View lookup cache
        private class ViewHolder {
            TextView textUpLeft;
            TextView textUpRight;
            TextView textMain;
        }

        public SearchResultAdapter (Context context, ArrayList<searchResult> searchResult) {
            super(context, 0, searchResult);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final searchResult currentSearchResult = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.search_list_item, parent, false);
                viewHolder.textUpLeft = (TextView) convertView.findViewById(R.id.searchTextUpLeft);
                viewHolder.textUpRight = (TextView) convertView.findViewById(R.id.searchTextUpRight);
                viewHolder.textMain = (TextView) convertView.findViewById(R.id.searchTexMain);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.textUpLeft.setText(currentSearchResult.getTextUpLeft());
            viewHolder.textUpRight.setText(currentSearchResult.getTextUpRight());

            Spannable stringToSpan = new SpannableString(currentSearchResult.getTextMain());
            for (int marker_loop=0; marker_loop<currentSearchResult.getLenghtListResultMarkers();marker_loop=marker_loop+1) {
                stringToSpan.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        currentSearchResult.getXmarkerStartIndex(marker_loop),
                        currentSearchResult.getXmarkerEndIndex(marker_loop),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                stringToSpan.setSpan(new StyleSpan(Typeface.ITALIC),
                        currentSearchResult.getXmarkerStartIndex(marker_loop),
                        currentSearchResult.getXmarkerEndIndex(marker_loop),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            viewHolder.textMain.setText(stringToSpan);
            //stringToSpan.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
            //        12, 19, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            //stringToSpan.setSpan(new StyleSpan(Typeface.ITALIC),
            //        12, 19, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            //viewHolder.textMain.setText(stringToSpan);
            //viewHolder.textMain.setText(currentSearchResult.getTextMain());

            viewHolder.textUpLeft.setTextSize(searchItemTextSize);
            viewHolder.textUpRight.setTextSize(searchItemTextSize);
            viewHolder.textMain.setTextSize(searchItemTextSize);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    screenWidthInPixels = ((Integer) (findViewById(R.id.search_lenear_layout_scroll_view).getWidth())).toString();
                    Intent intent = new Intent(SearchMenuActivity.this, BesedaActivity.class);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaNameVar", currentSearchResult.getBesedaName());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaDateYearVar", currentSearchResult.getBesedaDateYear());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaDateMonthVar", currentSearchResult.getBesedaDateMonth());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaDateDayVar", currentSearchResult.getBesedaDateDay());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaLinkVar", currentSearchResult.getBesedaLink());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaVarinatVar", currentSearchResult.getBesedaVariant());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaMarkersVar", currentSearchResult.getBesedaMarkers());
                    intent.putExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels", screenWidthInPixels);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaScrollIndecesVar", currentSearchResult.getScrollIndeces());
                    startActivity(intent);
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }

    }

    public void startSearch (View view) {

        // Hide the keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null :
                getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String searchQuery = editSearchTextInput.getText().toString();

        // Option NEAR on every word
        if (spinnerSearchWhat.getSelectedItem().toString() ==
                getResources().getString(R.string.search_option_every_word_near) ) {
            String[] splitQuery = searchQuery.trim().split("\\s+");
            if (splitQuery.length>1) {
                searchQuery = splitQuery[0];
                for (int sq_loop = 1; sq_loop < splitQuery.length; sq_loop=sq_loop+1) {
                    searchQuery = searchQuery + " NEAR/300 " + splitQuery[sq_loop];
                }
            }
        }

        // Option exact phrase
        if (spinnerSearchWhat.getSelectedItem().toString() ==
                getResources().getString(R.string.search_option_exact_phrase) ) {
            searchQuery = "\"" + searchQuery + "\"";
        }

        // Options every word and direct FTS do not need special query modifications

        Cursor rs = mydb.searchInBesedi(searchQuery);

        if (rs.getCount()<1) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.search_no_results), LENGTH_LONG);
            toast.show();
        } else {

            // Clear previous results
            listSearchResult.clear();
            searchResultAdapter.clear();

            rs.moveToFirst();

            listSearchResult.ensureCapacity(rs.getCount());

            for (int i = 0; i < rs.getCount(); i++) {

                // Name of the beseda
                String newTextUpleft = rs.getString(rs.getColumnIndex("Name"));
                // Date of the beseda
                String besedaDateYear = rs.getString(rs.getColumnIndex("Year_"));
                String besedaDateMonth = rs.getString(rs.getColumnIndex("Month_"));
                String besedaDateDay = rs.getString(rs.getColumnIndex("Day_of_Month"));
                String newTextUpRight = besedaDateDay + "." + besedaDateMonth + "." + besedaDateYear;

                // Beseda text extract
                // Convert the offset output to strings
                String[] offsetsStrings = rs.getString(0).split(" "); // Split to " " to read integers
                //// Convert the offset output to integers
                //Integer[] offsetsInts = new Integer[offsetsStrings.length];
                //for (int iOffs=0;iOffs<offsetsStrings.length;iOffs++ ) {
                //    offsetsInts[iOffs]=Integer.parseInt(offsetsStrings[iOffs]);
                //}
                //Convert the offset output to offsetRes array
                ArrayList<offsetRes> offsets = new ArrayList<offsetRes>();
                offsets.ensureCapacity(offsetsStrings.length/4);
                for (int iOffs=0;iOffs<offsetsStrings.length;iOffs=iOffs+4 ) {
                    offsets.add(new offsetRes(
                            Integer.parseInt(offsetsStrings[iOffs]),
                            Integer.parseInt(offsetsStrings[iOffs+1]),
                            Integer.parseInt(offsetsStrings[iOffs+2]),
                            Integer.parseInt(offsetsStrings[iOffs+3])
                    ));
                }
                // Algorithm description
                // The text extract shall be only one per beseda
                // The text extract shall be from only one column "TextN"
                // If all matches are from different columns,
                //      the match from the smallest column shall be selected (match earliest in text)
                // If there are couple of matches per column(s)
                //      the couple with smallest distances between matches shall be selected.
                //          If the distance is the same, the smallest column shall be selected
                int posMatch1 = 0;
                int posMatch2 = -1;
                int matchesAbsDiffOffsetInColumn = -1;
                if (offsets.size()>1) {
                    for (int i_offs=0; i_offs<offsets.size();i_offs++) {
                        int i_ColumnNumber = offsets.get(i_offs).getColumnNumber();
                        int i_offsetInColumn = offsets.get(i_offs).getOffsetInColumn();
                        for (int j_offs=i_offs+1; j_offs<offsets.size();j_offs++) {
                            int j_ColumnNumber = offsets.get(j_offs).getColumnNumber();
                            int j_offsetInColumn = offsets.get(j_offs).getOffsetInColumn();
                            int ijAbsDiffOffsetInColumn = i_offsetInColumn-j_offsetInColumn;
                            if (ijAbsDiffOffsetInColumn<0) { ijAbsDiffOffsetInColumn = (-1)*ijAbsDiffOffsetInColumn; }
                            if (posMatch2>=0) {
                                matchesAbsDiffOffsetInColumn =
                                        (offsets.get(posMatch1).getOffsetInColumn() - offsets.get(posMatch2).getOffsetInColumn());
                            } else {
                                matchesAbsDiffOffsetInColumn=0;
                            }
                            if (matchesAbsDiffOffsetInColumn<0) { matchesAbsDiffOffsetInColumn = (-1)*matchesAbsDiffOffsetInColumn; }
                            if (i_ColumnNumber==j_ColumnNumber) {
                                if ( (posMatch2==-1) ||
                                        (ijAbsDiffOffsetInColumn < matchesAbsDiffOffsetInColumn) ||
                                            ( (ijAbsDiffOffsetInColumn == matchesAbsDiffOffsetInColumn) &&
                                                    ( (i_ColumnNumber<offsets.get(posMatch1).getColumnNumber()) ||
                                                            ((i_ColumnNumber==offsets.get(posMatch1).getColumnNumber()) &&
                                                                    (i_offsetInColumn<offsets.get(posMatch1).getOffsetInColumn())
                                                            )
                                                    )
                                            )
                                        ) {
                                    if (i_offsetInColumn < j_offsetInColumn) {
                                        posMatch1 = i_offs;
                                        posMatch2 = j_offs;
                                    } else {
                                        posMatch1 = j_offs;
                                        posMatch2 = i_offs;
                                    }
                                }
                            }
                        }
                        if (
                                (posMatch2==-1) &&
                                ( i_ColumnNumber < offsets.get(posMatch1).getColumnNumber() )
                                ) {
                            posMatch1 = i_offs;
                        }
                    }
                }

                // Get the full column string
                //String newTextMainPre = rs.getString(offsets.get(posMatch1).getColumnNumber()-9);
                String newTextMainPre = rs.getString(7+((offsets.get(posMatch1).getColumnNumber()-14)/2));

                // In a string a character is encoded in 2 bytes
                int startCharMatch1 = characterOffsetForByteOffsetInUTF8String((offsets.get(posMatch1).getOffsetInColumn()),newTextMainPre);
                int endCharMatch1 = characterOffsetForByteOffsetInUTF8String(
                        (offsets.get(posMatch1).getOffsetInColumn()) + (offsets.get(posMatch1).getTermLenght()) ,
                        newTextMainPre
                    )-1;
                int startCharMatch2 = -1;
                int endCharMatch2 = -1;
                if (posMatch2>=0) {
                    startCharMatch2 = characterOffsetForByteOffsetInUTF8String((offsets.get(posMatch2).getOffsetInColumn()),newTextMainPre);
                    endCharMatch2 = characterOffsetForByteOffsetInUTF8String(
                            (offsets.get(posMatch2).getOffsetInColumn()) + (offsets.get(posMatch2).getTermLenght()) ,
                            newTextMainPre
                        )-1;
                }


                // Algorithm description
                // searchResult textMain shall be textMainMaxLenght characters long or shorter
                // It shall start from the first sentence start before the match1
                // It shall end at the last sentence end after match2 (if it exists) still within textMainMaxLenght
                int startPrevSentence = 0;
                int endNextSentence = newTextMainPre.length()-1;
                for (int i_sent_loop = 0; i_sent_loop<stringSentenceEnds.length(); i_sent_loop++ ){
                    int currentSententceIndex = 0;
                    currentSententceIndex = 1 +
                            newTextMainPre.lastIndexOf(stringSentenceEnds.charAt(i_sent_loop),startCharMatch1);
                    if ( (currentSententceIndex>0) &&
                            (currentSententceIndex>startPrevSentence)) {
                        startPrevSentence = currentSententceIndex;
                    }
                    if (startCharMatch2>=0) {
                        currentSententceIndex =
                                newTextMainPre.indexOf(stringSentenceEnds.charAt(i_sent_loop), endCharMatch2);
                    } else {
                        currentSententceIndex =
                                newTextMainPre.indexOf(stringSentenceEnds.charAt(i_sent_loop), endCharMatch1);
                    }
                    if ( (currentSententceIndex>=0) &&
                            (currentSententceIndex<endNextSentence)) {
                        endNextSentence = currentSententceIndex;
                    }
                }
                int startCharTextMain = 0;
                int endCharTextMain = newTextMainPre.length()-1;
                Boolean addDotsInFront = new Boolean(false);
                Boolean addDotsInEnd = new Boolean(false);
                if ( (endNextSentence-startPrevSentence) < textMainMaxLenght ) {
                    startCharTextMain = startPrevSentence;
                    endCharTextMain = endNextSentence;
                } else if ( (endCharMatch1-startPrevSentence) < textMainMaxLenght ) {
                    startCharTextMain = startPrevSentence;
                    endCharTextMain = startCharTextMain + textMainMaxLenght-3;
                    addDotsInEnd=true;
                } else {
                    startCharTextMain = endCharMatch1 - (textMainMaxLenght-3);
                    endCharTextMain = endCharMatch1;
                    addDotsInFront=true;
                }

                String newTextMain = newTextMainPre.substring(startCharTextMain,endCharTextMain+1);
                int scrollCharIndex = startCharTextMain;
                if (addDotsInFront==true) {
                    newTextMain = "..."+newTextMain;
                    scrollCharIndex = scrollCharIndex + 3;
                }
                if (addDotsInEnd==true) {
                    newTextMain = newTextMain+"...";
                }
                String scrollIndeces = Integer.toString( 1+((offsets.get(posMatch1).getColumnNumber()-14)/2) ) +
                        " " + Integer.toString(scrollCharIndex);

                String newSearchMarkers = "";
                for (int k_offs=0; k_offs<offsets.size();k_offs++) {
                    if ( offsets.get(posMatch1).getColumnNumber() == offsets.get(k_offs).getColumnNumber() ) {
                        int startMarkerIndex = 0;
                        int endMarkerIndex = newTextMainPre.length()-1;
                        boolean addToMarkers = false;
                        int currentStartCharIndex = characterOffsetForByteOffsetInUTF8String(
                                offsets.get(k_offs).getOffsetInColumn(),newTextMainPre
                        );
                        int currentEndCharIndex = characterOffsetForByteOffsetInUTF8String(
                                offsets.get(k_offs).getOffsetInColumn()+offsets.get(k_offs).getTermLenght(),
                                newTextMainPre
                        );
                        if ( (currentStartCharIndex>=startCharTextMain) &&  (currentEndCharIndex<=endCharTextMain) ) {
                            startMarkerIndex = currentStartCharIndex-startCharTextMain;
                            endMarkerIndex = currentEndCharIndex-startCharTextMain;
                            addToMarkers = true;
                        } else if ( (currentStartCharIndex>=startCharTextMain) &&  (currentStartCharIndex<=endCharTextMain) ) {
                            startMarkerIndex = currentStartCharIndex-startCharTextMain;
                            endMarkerIndex = endCharTextMain-startCharTextMain;
                            addToMarkers = true;
                        } else if ( (currentEndCharIndex>=startCharTextMain) &&  (currentEndCharIndex<=endCharTextMain) ) {
                            startMarkerIndex = startCharTextMain-startCharTextMain;
                            endMarkerIndex = currentEndCharIndex-startCharTextMain;
                            addToMarkers = true;
                        }
                        if (addDotsInFront==true) {
                            startMarkerIndex = startMarkerIndex+3;
                            endMarkerIndex = endMarkerIndex+3;
                        }
                        if (addToMarkers == true) {
                            newSearchMarkers = newSearchMarkers + String.valueOf(startMarkerIndex) + " " + String.valueOf(endMarkerIndex) + " ";
                        }
                    }
                }

                String newBesedaMarkers = "";
                for (int m_offs=0; m_offs<offsets.size();m_offs++) {
                    newBesedaMarkers = newBesedaMarkers + Integer.toString(1+(offsets.get(m_offs).getColumnNumber()-14)/2);
                    newBesedaMarkers = newBesedaMarkers + " ";
                    newBesedaMarkers = newBesedaMarkers + Integer.toString(
                            characterOffsetForByteOffsetInUTF8String(
                                    offsets.get(m_offs).getOffsetInColumn(),
                                    rs.getString(7+((offsets.get(m_offs).getColumnNumber()-14)/2))
                                    ));
                    newBesedaMarkers = newBesedaMarkers + " ";
                    newBesedaMarkers = newBesedaMarkers + Integer.toString(
                            characterOffsetForByteOffsetInUTF8String(
                                    offsets.get(m_offs).getOffsetInColumn() + offsets.get(m_offs).getTermLenght(),
                                    rs.getString(7+((offsets.get(m_offs).getColumnNumber()-14)/2))
                            ));
                    newBesedaMarkers = newBesedaMarkers + " ";
                }

                //String newTextMain = rs.getString(0) + "\n\n" + rs.getString(rs.getColumnIndex("Text1"));
                //newTextMain = newTextMain.substring(0, 99);

                Integer besedaID = 0;
                String besedaName = rs.getString(rs.getColumnIndex("Name"));
                String besedaLink = rs.getString(rs.getColumnIndex("Link"));
                String besedaVariant = rs.getString(rs.getColumnIndex("Variant"));
                besedaInfo bInfo = new besedaInfo(besedaID, besedaName, besedaDateYear, besedaDateMonth, besedaDateDay, besedaLink);

                listSearchResult.add(new searchResult(newTextUpleft, newTextUpRight, newTextMain,
                        newSearchMarkers, newBesedaMarkers, bInfo, besedaVariant, scrollIndeces));
                rs.moveToNext();
            }

            searchResultAdapter.notifyDataSetChanged();
            listSearchView.smoothScrollToPosition(0);

            searchCounter++;

        }
    }

    private class offsetRes {
        private int columnNumber;
        private int termNumber;
        private int offsetInColumn;
        private int termLenght;

        public offsetRes (int newColumnNumber, int newTermNumber,
                          int newOffsetInColumn, int newTermLenght) {
            columnNumber = newColumnNumber;
            termNumber = newTermNumber;
            offsetInColumn = newOffsetInColumn;
            termLenght = newTermLenght;
        }

        public int getColumnNumber () {
            return columnNumber;
        }
        public int getTermNumber () {
            return termNumber;
        }
        public int getOffsetInColumn () {
            return offsetInColumn;
        }
        public int getTermLenght(){
            return termLenght;
        }

    }

    private int characterOffsetForByteOffsetInUTF8String(int byteOffset, String string) {
    /*
     * UTF-8 represents ASCII characters in a single byte. Characters with a code
     * point from U+0080 upwards are represented as multiple bytes. The first byte
     * always has the two most significant bits set (i.e. 11xxxxxx). All subsequent
     * bytes have the most significant bit set, the next most significant bit unset
     * (i.e. 10xxxxxx).
     *
     * We use that here to determine character offsets. We step through the first
     * `byteOffset` bytes of `string`, incrementing the character offset result
     * every time we come across a byte that doesn't match 10xxxxxx, i.e. where
     * (byte & 11000000) != 10000000
     *
     * See also: http://en.wikipedia.org/wiki/UTF-8#Description
     */
        int characterOffset = 0;
        byte[] stringBytes = new byte[0];
        try {
            stringBytes = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < byteOffset; i++) {
            byte c = stringBytes[i];
            if ((c & 0xc0) != 0x80) {
                characterOffset++;
            }
        }
        return characterOffset;
    }


}
