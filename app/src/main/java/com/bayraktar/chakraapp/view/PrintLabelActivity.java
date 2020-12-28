package com.bayraktar.chakraapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bayraktar.chakraapp.App;
import com.bayraktar.chakraapp.R;
import com.bayraktar.chakraapp.model.BaseExchange;
import com.bayraktar.chakraapp.model.Currency;
import com.bayraktar.chakraapp.service.MyBluetoothService;
import com.bayraktar.chakraapp.view_model.CurrencyViewModel;
import com.bayraktar.chakraapp.view_model.SaleGroupViewModel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PrintLabelActivity extends AppCompatActivity {

    private final int LOCATION_PERMISSION_CODE = 989;
    private final int REQUEST_ENABLE_BT = 979;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket mmSocket;

    CurrencyViewModel mViewModel;
    Currency currency;
    TextView tvSaleGroupName;
    EditText etBarcode;

    String saleGroupName;
    BaseExchange currentExchange;

    TextView tvCurrencyInformation, tvCurrencyNameValue, tvCurrencyBuyingValue, tvCurrencySellingValue, tvCurrencyTypeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_label);
        if (getIntent() != null) {
            saleGroupName = getIntent().getStringExtra(App.SALE_GROUP_NAME);
        }
        tvSaleGroupName = findViewById(R.id.tvSaleGroupName);
        etBarcode = findViewById(R.id.etBarcode);

        tvCurrencyInformation = findViewById(R.id.tvCurrencyInformation);
        tvCurrencyNameValue = findViewById(R.id.tvCurrencyNameValue);
        tvCurrencyBuyingValue = findViewById(R.id.tvCurrencyBuyingValue);
        tvCurrencySellingValue = findViewById(R.id.tvCurrencySellingValue);
        tvCurrencyTypeValue = findViewById(R.id.tvCurrencyTypeValue);

        findViewById(R.id.cvPrint).setOnClickListener(v -> print());
        findViewById(R.id.cvBack).setOnClickListener(v -> onBackPressed());

        mViewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        permissions();
    }

    void initialize() {

        mViewModel.getTodayCurrency().observe(this, currencies -> {
            if (currencies != null)
                tvCurrencyInformation.setText("Kur Bilgileri Alındı");
            else
                tvCurrencyInformation.setText("Kur Bilgileri Alınamadı!!");
            currency = currencies;
        });
        tvSaleGroupName.setText(saleGroupName);
        etBarcode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleText();
                return true;
            }
            return false;
        });
    }

    void permissions() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Cihaz Bluetooth desteklemiyor!", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        } else
            initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
            Toast.makeText(this, "Bluetooth olmadan devam edemezsiniz", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            boolean success = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    success = false;
                    break;
                }
            }
            if (success)
                initialize();
            else
                finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void handleText() {
        String text = etBarcode.getText().toString();
        if (TextUtils.isEmpty(text) || currency == null) {
            setCurrency(null);
            return;
        }
        switch (text.toUpperCase()) {
            case "AUD":
                setCurrency(currency.getAud());
                break;
            case "DKK":
                setCurrency(currency.getDkk());
                break;
            case "EURO":
                setCurrency(currency.getEuro());
                break;
            case "GBP":
                setCurrency(currency.getGbp());
                break;
            case "USD":
                setCurrency(currency.getUsd());
                break;
            default:
                setCurrency(null);
        }
    }

    void setCurrency(BaseExchange exchange) {
        currentExchange = exchange;
        if (currentExchange == null) {
            //Clear Texts
            tvCurrencyNameValue.setText("Kur bilgisi alınamadı");
            tvCurrencyBuyingValue.setText("Kur bilgisi alınamadı");
            tvCurrencySellingValue.setText("Kur bilgisi alınamadı");
            tvCurrencyTypeValue.setText("Kur bilgisi alınamadı");
            return;
        }
        tvCurrencyNameValue.setText(currentExchange.getName());
        tvCurrencyBuyingValue.setText(currentExchange.getBuying());
        tvCurrencySellingValue.setText(currentExchange.getSelling());
        tvCurrencyTypeValue.setText(currentExchange.getType());
    }

    void print() {
        if (currentExchange == null) {
            Toast.makeText(this, "Kur bilgisi alınamadı. Lütfen önce kur bilgisini doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mmSocket != null) {
            manageMyConnectedSocket(mmSocket);
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice mDevice = null;
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName.equals("SW_E531")) {
                    mDevice = device;
                    break;
                }
            }
        }
        if (mDevice != null) {
            ConnectThread connectThread = new ConnectThread(mDevice);
            connectThread.start();
        } else {
            Toast.makeText(this, "SW_E531 Bluetooth Cihazı bulunamadı. Cihazın eşleştirilmiş olduğundan emin olun.", Toast.LENGTH_SHORT).show();
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket _mmSocket) {
        this.mmSocket = _mmSocket;
        if (mmSocket.isConnected()) {
            try {
                OutputStream btOutputStream = mmSocket.getOutputStream();
                String printText = "! 0 200 200 210 1\n" +
                        "TEXT 5 0 10 20 Kur Adi:\n" +
                        "TEXT 5 0 10 50 Alis:\n" +
                        "TEXT 5 0 10 80 Satis:\n" +
                        "TEXT 5 0 10 110 Tur:\n" +
                        "TEXT 5 0 160 20 " + currentExchange.getName() + "\n" +
                        "TEXT 5 0 160 50 " + currentExchange.getBuying() + "\n" +
                        "TEXT 5 0 160 80 " + currentExchange.getSelling() + "\n" +
                        "TEXT 5 0 160 110 " + currentExchange.getType() + "\n" +
                        "FORM\n" +
                        "PRINT\n";
                btOutputStream.write(printText.getBytes());
                btOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                UUID uuid = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");
                //0001101-0000-1000-8000-00805F9B34FB
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e("TAG", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                if (!mmSocket.isConnected())
                    mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("TAG", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("TAG", "Could not close the client socket", e);
            }
        }
    }


}