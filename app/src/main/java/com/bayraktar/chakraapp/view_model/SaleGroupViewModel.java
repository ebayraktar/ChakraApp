package com.bayraktar.chakraapp.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bayraktar.chakraapp.model.SaleGroup;

import java.util.ArrayList;
import java.util.List;

public class SaleGroupViewModel extends ViewModel {
    MutableLiveData<List<SaleGroup>> mData;

    public SaleGroupViewModel() {
        mData = new MutableLiveData<>();
    }

    public LiveData<List<SaleGroup>> getSaleGroupList() {
        //TODO: WebService Connection
        List<SaleGroup> saleGroupList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SaleGroup tempGroup = new SaleGroup();
            tempGroup.setId(String.valueOf(i));
            tempGroup.setName("Satış Grubu " + (i + 1));
            saleGroupList.add(tempGroup);
        }
        //return values
        mData.setValue(saleGroupList);
        return mData;
    }
}
