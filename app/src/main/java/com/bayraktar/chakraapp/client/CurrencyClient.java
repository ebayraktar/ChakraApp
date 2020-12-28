package com.bayraktar.chakraapp.client;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyClient {
    private static Retrofit retrofit = null;
    private static final String Base_Url = "https://finans.truncgil.com/v2/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Base_Url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient())
                    .build();
        }
        return retrofit;
    }
}
