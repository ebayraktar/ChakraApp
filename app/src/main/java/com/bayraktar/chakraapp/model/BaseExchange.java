package com.bayraktar.chakraapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class BaseExchange {
    @SerializedName("Buying")
    @Expose
    String buying;
    @SerializedName("Selling")
    @Expose
    String selling;
    @SerializedName("Type")
    @Expose
    String type;
    @SerializedName("Name")
    @Expose
    String name;

    public String getBuying() {
        return buying;
    }

    public void setBuying(String buying) {
        this.buying = buying;
    }

    public String getSelling() {
        return selling;
    }

    public void setSelling(String selling) {
        this.selling = selling;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
