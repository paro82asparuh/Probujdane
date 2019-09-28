package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 15/12/2018.
 */

public class Molitva {

    private String molitvaTitle;
    private String molitvaText;

    public Molitva (String inputTitle, String inputText) {
        molitvaTitle = inputTitle;
        molitvaText = inputText;
    }

    public String getMolitvaTitle () { return molitvaTitle; }

    public String getMolitvaText () { return molitvaText; }
}
