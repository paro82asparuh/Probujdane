package com.grigorov.asparuh.probujdane;

public class ZavetBookMarker {

    private int chapterIndex;
    private int textSide; //0-left 1-center 2-right
    private int startIndex;
    private int endIndex;

    public ZavetBookMarker (int inputChapterIndex, int inputTextSide, int inputStartIndex, int inputEndIndex) {
        chapterIndex = inputChapterIndex;
        textSide = inputTextSide;
        startIndex = inputStartIndex;
        endIndex = inputEndIndex;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public int getTextSide() { return textSide; }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex (int inputEndIndex) {endIndex = inputEndIndex; }

}
