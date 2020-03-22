package com.grigorov.asparuh.probujdane;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by agr on 28/01/2018.
 */

public class searchResult {

    public final static int SEARCH_RESULT_BESEDI=1;
    public final static int SEARCH_RESULT_MOLITVI=2;
    public final static int SEARCH_RESULT_FORMULI=3;
    public final static int SEARCH_RESULT_MUSIC=4;

    private int type;

    private String textUpLeft;
    private String textUpRight;
    private String textMain;
    private ArrayList<resultMarker> listResultMarkers= new ArrayList<resultMarker>();
    private besedaInfo bInfo;
    private String besedaVariant;
    private String itemMarkers;
    private String scrollIndeces;

    private Molitva molitva;
    private Formula formula;
    private Song song;

    private int numberMatches;

    public searchResult (int inputType, int inputNumberMatches, String inputTextUpLeft, String inputTextUpRight, String inputTextMain,
                         String inputSearchMarker, String inputItemMarkers,
                         besedaInfo inputBesedaInfo, String inputBesedaVariant,
                         String inputScrollIndeces) {
        type = inputType;
        numberMatches = inputNumberMatches;
        bInfo = inputBesedaInfo;
        besedaVariant = inputBesedaVariant;
        textUpLeft = inputTextUpLeft;
        textUpRight = inputTextUpRight;
        textMain = inputTextMain;
        listResultMarkers.clear();
        String[] inputSearchMarkers = inputSearchMarker.split(" "); // Split to " " to read integers
        for (int marker_loop=0; marker_loop<inputSearchMarkers.length;marker_loop=marker_loop+2) {
            listResultMarkers.add(
                    new resultMarker(
                        Integer.parseInt(inputSearchMarkers[marker_loop]),
                        Integer.parseInt(inputSearchMarkers[marker_loop+1])
                    )
            );
        }
        itemMarkers = inputItemMarkers;
        scrollIndeces = inputScrollIndeces;
    }

    public searchResult (int inputType, int inputNumberMatches, String inputTextUpLeft, String inputTextUpRight, String inputTextMain,
                         String inputSearchMarker, String inputItemMarkers,
                         Molitva inputMolitva,
                         String inputScrollIndeces) {
        type = inputType;
        numberMatches = inputNumberMatches;
        molitva = inputMolitva;
        textUpLeft = inputTextUpLeft;
        textUpRight = inputTextUpRight;
        textMain = inputTextMain;
        listResultMarkers.clear();
        String[] inputSearchMarkers = inputSearchMarker.split(" "); // Split to " " to read integers
        for (int marker_loop=0; marker_loop<inputSearchMarkers.length;marker_loop=marker_loop+2) {
            listResultMarkers.add(
                    new resultMarker(
                            Integer.parseInt(inputSearchMarkers[marker_loop]),
                            Integer.parseInt(inputSearchMarkers[marker_loop+1])
                    )
            );
        }
        itemMarkers = inputItemMarkers;
        scrollIndeces = inputScrollIndeces;
    }

    public searchResult (int inputType, int inputNumberMatches, String inputTextUpLeft, String inputTextUpRight, String inputTextMain,
                         String inputSearchMarker, String inputItemMarkers,
                         Formula inputFormula,
                         String inputScrollIndeces) {
        type = inputType;
        numberMatches = inputNumberMatches;
        formula = inputFormula;
        textUpLeft = inputTextUpLeft;
        textUpRight = inputTextUpRight;
        textMain = inputTextMain;
        listResultMarkers.clear();
        String[] inputSearchMarkers = inputSearchMarker.split(" "); // Split to " " to read integers
        for (int marker_loop=0; marker_loop<inputSearchMarkers.length;marker_loop=marker_loop+2) {
            listResultMarkers.add(
                    new resultMarker(
                            Integer.parseInt(inputSearchMarkers[marker_loop]),
                            Integer.parseInt(inputSearchMarkers[marker_loop+1])
                    )
            );
        }
        itemMarkers = inputItemMarkers;
        scrollIndeces = inputScrollIndeces;
    }

    public searchResult (int inputType, int inputNumberMatches, String inputTextUpLeft, String inputTextUpRight, String inputTextMain,
                         String inputSearchMarker, String inputItemMarkers,
                         Song inputSong,
                         String inputScrollIndeces) {
        type = inputType;
        numberMatches = inputNumberMatches;
        song = inputSong;
        textUpLeft = inputTextUpLeft;
        textUpRight = inputTextUpRight;
        textMain = inputTextMain;
        listResultMarkers.clear();
        String[] inputSearchMarkers = inputSearchMarker.split(" "); // Split to " " to read integers
        for (int marker_loop=0; marker_loop<inputSearchMarkers.length;marker_loop=marker_loop+2) {
            listResultMarkers.add(
                    new resultMarker(
                            Integer.parseInt(inputSearchMarkers[marker_loop]),
                            Integer.parseInt(inputSearchMarkers[marker_loop+1])
                    )
            );
        }
        itemMarkers = inputItemMarkers;
        scrollIndeces = inputScrollIndeces;
    }

    public String getTextUpLeft() {
        return textUpLeft;
    }

    public String getTextUpRight() {
        return textUpRight;
    }

    public String getTextMain() {
        return textMain;
    }

    public int getLenghtListResultMarkers () { return listResultMarkers.size(); }
    public int getXmarkerStartIndex (int X) { return listResultMarkers.get(X).getStartIndex(); }
    public int getXmarkerEndIndex (int X) { return listResultMarkers.get(X).getEndIndex(); }

    private class resultMarker {
        private int startIndex;
        private int endIndex;

        public resultMarker (int inputStartIndex, int inputEndIndex) {
            startIndex = inputStartIndex;
            endIndex = inputEndIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }

    public String getBesedaName () {
        return this.bInfo.getBesedaName();
    }

    public String getBesedaDateYear () {
        return this.bInfo.getBesedaDateYear();
    }

    public String getBesedaDateMonth () {
        return this.bInfo.getBesedaDateMonth();
    }

    public String getBesedaDateDay () {
        return this.bInfo.getBesedaDateDay();
    }

    public String getBesedaLink () {
        return this.bInfo.getBesedaLink();
    }

    public String getBesedaVariant () {
        return this.besedaVariant;
    }

    public String getItemMarkers () { return itemMarkers; }

    public String getScrollIndeces () { return scrollIndeces; }

    public Molitva getMolitva () { return molitva; }

    public Formula getFormula () { return formula; }

    public Song getSong () { return song; }

    public int getType () { return type; }

    public int getNumberMatches() { return numberMatches; }

    public static Comparator<searchResult> numberMatchesComparator = new Comparator<searchResult>() {
        @Override
        public int compare(searchResult result1, searchResult result2) {
            return (result2.getNumberMatches() < result1.getNumberMatches() ? -1 :
                    (result2.getNumberMatches() == result1.getNumberMatches() ? 0 : 1));
        }
    };

}
