package com.grigorov.asparuh.probujdane;

public class MolitvaMarker {
    private int columnIndex;
    private int startIndex;
    private int endIndex;

    public MolitvaMarker (int inputColumnIndex, int inputStartIndex, int inputEndIndex) {
        columnIndex = inputColumnIndex;
        startIndex = inputStartIndex;
        endIndex = inputEndIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

}
