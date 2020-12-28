package com.bayraktar.chakraapp.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bayraktar.chakraapp.client.CurrencyClient;
import com.bayraktar.chakraapp.client.ICurrency;
import com.bayraktar.chakraapp.model.Currency;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyViewModel extends ViewModel {
    MutableLiveData<Currency> mData;

    public CurrencyViewModel() {
        mData = new MutableLiveData<>();
    }

    public LiveData<Currency> getTodayCurrency() {
        CurrencyClient.getClient().create(ICurrency.class).getTodayCurrency()
                .enqueue(new Callback<Currency>() {
                    @Override
                    public void onResponse(Call<Currency> call, Response<Currency> response) {

                        mData.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<Currency> call, Throwable t) {
                        mData.setValue(null);
                    }
                });
        return mData;
    }
}
