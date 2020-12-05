package com.grigorov.asparuh.probujdane;

public class ChapterZavet {

    private String ID;
    private String level;
    private String color;
    private String textLeft;
    private String textCenter;
    private String textRight;
    private String boldCenter;

    public ChapterZavet (String inputID, String inputLevel, String inputColor, String inputTextLeft, String inputTextCenter,
                  String inputTextRight, String inputBoldCenter) {
        ID = inputID;
        level = inputLevel;
        color = inputColor;
        textLeft = inputTextLeft;
        textCenter = inputTextCenter;
        textRight = inputTextRight;
        boldCenter = inputBoldCenter;
    }

    public String getID () { return ID; }
    public String getLevel () { return level; }
    public String getColor () { return color; }
    public String getTextLeft () { return textLeft; }
    public String getTextCenter () { return textCenter; }
    public String getTextRight () { return textRight; }
    public String getBoldCenter () { return boldCenter; }

}
