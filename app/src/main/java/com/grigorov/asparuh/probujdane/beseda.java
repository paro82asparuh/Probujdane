package com.grigorov.asparuh.probujdane;

import java.util.Date;

/**
 * Created by agr on 07/06/2017.
 */

public class beseda {

    private String besedaText;
    private String besedaName;
    private Date besedaDate;

    public beseda (String inputText, String name, Date givenDate) {
        besedaText = inputText;
        besedaName = name;
        besedaDate = givenDate;
    }

    public String getbesedaName () {
        return this.besedaName;
    }

    public String getBesedaText () {
        return this.besedaText;
    }

    public Date getBesedaDate () {
        return this.besedaDate;
    }
}
