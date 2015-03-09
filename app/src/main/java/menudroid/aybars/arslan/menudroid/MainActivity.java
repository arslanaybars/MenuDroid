package menudroid.aybars.arslan.menudroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import menudroid.aybars.arslan.menudroid.main.OrderActivity;

public class MainActivity extends ActionBarActivity implements OnClickListener {

    Button btnOrder, btnBill, btnWaiter, btnMenu, btnContact, btnRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initalize buttons
        initialize();
    }

    private void initialize() {
        btnOrder = (Button) findViewById(R.id.btnOrder);
        btnBill = (Button) findViewById(R.id.btnBill);
        btnWaiter = (Button) findViewById(R.id.btnWaiter);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnContact = (Button) findViewById(R.id.btnContact);
        btnRestaurant = (Button) findViewById(R.id.btnRestaurant);

        //onClick Events
        btnOrder.setOnClickListener(this);
        btnBill.setOnClickListener(this);
        btnWaiter.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnRestaurant.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOrder:
                showDialogForBarcode();
//                Intent intentOrder = new Intent(MainActivity.this, OrderActivity.class);
//                startActivity(intentOrder);
                break;
            case R.id.btnBill:
                showDialogForBarcode();
//                Intent intentBill = new Intent(MainActivity.this, BillActivity.class);
//                startActivity(intentBill);
                break;
            case R.id.btnWaiter:
                showDialogForBarcode();
                break;
            case R.id.btnMenu:
                showToast("Clicked Menu");
                break;
            case R.id.btnContact:
                showToast("Clicked Contact");
                break;
            case R.id.btnRestaurant:
                showToast("Clicked Restaurant Test");
                break;
        }

    }

    private void showDialogForBarcode() {
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage(R.string.mainBarcodeMessage);
        dialogBuilder.setTitle(R.string.mainBarcodeTitle);

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Scan Barcode
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setPrompt("Scan the barcode from table !");
                integrator.initiateScan();
            }
        });

        dialogBuilder.create().show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
//          Log.d("code", re);
            showToast(re);
        }
        // else continue with any other code you need in the method

    }


    Toast m_currentToast;

    void showToast(String text) {
        if (m_currentToast != null) {
            m_currentToast.cancel();
        }
        m_currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        m_currentToast.show();

    }
}