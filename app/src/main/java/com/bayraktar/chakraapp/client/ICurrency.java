package com.bayraktar.chakraapp.client;

import com.bayraktar.chakraapp.model.Currency;
import com.bayraktar.chakraapp.model.MobileResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ICurrency {
    @Headers(
            {
                    "Accept: application/json",
                    //"app_id: ",
                    //"app_key: "
            })
    @GET("today.json")
    Call<Currency> getTodayCurrency();
}
