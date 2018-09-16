package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 19/07/2018.
 */

public class Formula {

    private int ID;
    private String Title;
    private String Text;

    public Formula( int newID, String newTitle, String newText) {
        ID = newID;
        Title = newTitle;
        Text = newText;
    }

    public int getID () { return ID; }

    public String getTitle () { return Title;}

    public String getText () { return Text;}
}
