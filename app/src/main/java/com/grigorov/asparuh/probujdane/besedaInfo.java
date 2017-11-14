package com.grigorov.asparuh.probujdane;

import java.util.Date;

/**
 * Created by agr on 10/10/2017.
 */

public class besedaInfo {

    private Integer besedaID;
    private String besedaName;
    private String besedaDateYear;
    private String besedaDateMonth;
    private String besedaDateDay;

    public besedaInfo (Integer inputID, String inputName, String inputDateYear, String inputDateMonth, String inputDateDay) {
        besedaID = inputID;
        besedaName = inputName;
        besedaDateYear = inputDateYear;
        besedaDateMonth = inputDateMonth;
        besedaDateDay = inputDateDay;
    }

    public String getbesedaName () {
        return this.besedaName;
    }

    public Integer getBesedaID () {
        return this.besedaID;
    }

    public String getBesedaDateYear () {
        return this.besedaDateYear;
    }

    public String getBesedaDateMonth () {
        return this.besedaDateMonth;
    }

    public String getBesedaDateDay () {
        return this.besedaDateDay;
    }

    public String getBesedaDateString () {
        return new String(this.besedaDateDay+"."+this.besedaDateMonth+"."+this.besedaDateYear);
    }
}