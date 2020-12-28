package com.bayraktar.chakraapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bayraktar.chakraapp.App;
import com.bayraktar.chakraapp.R;
import com.bayraktar.chakraapp.model.SaleGroup;
import com.bayraktar.chakraapp.view_model.SaleGroupViewModel;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    SaleGroupViewModel mViewModel;
    List<SaleGroup> saleGroupList;
    Spinner spSaleGroup;
    TextView tvVersion;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.click_back_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvVersion = findViewById(R.id.tvVersion);
        spSaleGroup = findViewById(R.id.spSaleGroup);
        findViewById(R.id.cvLogin).setOnClickListener(v -> login());

        tvVersion.setText(getVersionName());

        mViewModel = new ViewModelProvider(this).get(SaleGroupViewModel.class);
        saleGroupList = new ArrayList<>();

        initialize();
    }

    void initialize() {
        mViewModel.getSaleGroupList().observe(this, saleGroups -> {
            saleGroupList = saleGroups;
            List<String> groupNames = new ArrayList<>();
            for (SaleGroup saleGroup : saleGroups) {
                groupNames.add(saleGroup.getName());
            }
            SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, groupNames);
            spSaleGroup.setAdapter(spinnerAdapter);
        });
    }

    void login() {
        if (spSaleGroup.getSelectedItemPosition() != -1) {
            String saleGroupName = saleGroupList.get(spSaleGroup.getSelectedItemPosition()).getName();
            Intent printLabelActivity = new Intent(LoginActivity.this, PrintLabelActivity.class);
            printLabelActivity.putExtra(App.SALE_GROUP_NAME, saleGroupName);
            startActivity(printLabelActivity);
        }
    }

    String getVersionName() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}