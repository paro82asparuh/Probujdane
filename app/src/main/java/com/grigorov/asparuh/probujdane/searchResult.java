package com.grigorov.asparuh.probujdane;

import java.util.ArrayList;

/**
 * Created by agr on 28/01/2018.
 */

public class searchResult {

    private String textUpLeft;
    private String textUpRight;
    private String textMain;
    private ArrayList<resultMarker> listResultMarkers= new ArrayList<resultMarker>();
    private besedaInfo bInfo;
    private String besedaVariant;
    private String besedaMarkers;
    private String scrollIndeces;

    public searchResult (String inputTextUpLeft, String inputTextUpRight, String inputTextMain,
                         String inputSearchMarker, String inputBesedaMarkers,
                         besedaInfo inputBesedaInfo, String inputBesedaVariant,
                         String inputScrollIndeces) {
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
        besedaMarkers = inputBesedaMarkers;
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

    public String getBesedaMarkers () { return besedaMarkers; }

    public String getScrollIndeces () { return scrollIndeces; }


}
