package com.grigorov.asparuh.probujdane;

public class NaukaVyzBookMarker {

    private int chapterIndex;
    private boolean inTitle;
    private int startIndex;
    private int endIndex;

    public NaukaVyzBookMarker (int inputChapterIndex, boolean inputInTitle, int inputStartIndex, int inputEndIndex) {
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
