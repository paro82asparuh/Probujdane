package com.grigorov.asparuh.probujdane;

public class ChapterNaukaVyz {

    private String ID;
    private String level;
    private String title;
    private String content;
    private String indentation;

    public ChapterNaukaVyz (String inputID, String inputLevel, String inputTitle, String inputContent, String inputIndentation) {
        ID = inputID;
        level = inputLevel;
        title = inputTitle;
        content = inputContent;
        indentation = inputIndentation;
    }

    public String getID () { return ID; }
    public String getLevel () { return level; }
    public String getTitle () { return title; }
    public String getContent () { return content; }
    public String getIndentation() { return indentation; }

}
