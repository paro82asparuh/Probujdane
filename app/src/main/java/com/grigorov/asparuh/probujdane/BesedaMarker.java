package com.grigorov.asparuh.probujdane;

public class BesedaMarker {

    private int textIndex;
    private int startIndex;
    private int endIndex;

    public BesedaMarker (int inputTextIndex, int inputStartIndex, int inputEndIndex) {
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
