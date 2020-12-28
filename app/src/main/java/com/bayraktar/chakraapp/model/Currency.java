package com.bayraktar.chakraapp.model;

import com.bayraktar.chakraapp.model.exchange.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Currency {
    @SerializedName("Update_Date")
    @Expose
    String updateDate;
    @SerializedName("AUD")
    @Expose
    AUD aud;

    @SerializedName("DKK")
    @Expose
    DKK dkk;

    @SerializedName("EURO")
    @Expose
    EURO euro;

    @SerializedName("GBP")
    @Expose
    GBP gbp;

    @SerializedName("USD")
    @Expose
    USD usd;

    public Currency() {
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public AUD getAud() {
        return aud;
    }

    public void setAud(AUD aud) {
        this.aud = aud;
    }

    public DKK getDkk() {
        return dkk;
    }

    public void setDkk(DKK dkk) {
        this.dkk = dkk;
    }

    public EURO getEuro() {
        return euro;
    }

    public void setEuro(EURO euro) {
        this.euro = euro;
    }

    public GBP getGbp() {
        return gbp;
    }

    public void setGbp(GBP gbp) {
        this.gbp = gbp;
    }

    public USD getUsd() {
        return usd;
    }

    public void setUsd(USD usd) {
        this.usd = usd;
    }
}
