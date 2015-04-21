package menudroid.aybars.arslan.menudroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import menudroid.aybars.arslan.menudroid.main.MenuActivity;
import menudroid.aybars.arslan.menudroid.main.OrderActivity;


public class MainActivity extends ActionBarActivity implements OnClickListener {

    private Button btnOrder, btnBill, btnWaiter, btnMenu, btnLogin, btnRestaurant; // Define mainactivity buttons
    private ImageView imgSetIP;

    private static String SERVER_IP = "192.168."; //Define the server port
    private final String SERVER_PORT = "8080"; //Define the server port
    private static String qrResult = "NotFound"; // Define qrCodes string form barcode
    private String qrComplement = ""; // Complement for understanding messafge from order,bill or waiter

    SharedPreferences ipPrefrence;

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
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRestaurant = (Button) findViewById(R.id.btnRestaurant);
        imgSetIP = (ImageView) findViewById(R.id.imgSetIP);

        //onClick Events
        btnOrder.setOnClickListener(this);
        btnBill.setOnClickListener(this);
        btnWaiter.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRestaurant.setOnClickListener(this);

        imgSetIP.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        ClientAsyncTask clientAST = new ClientAsyncTask();

        /*
         * TODO
         * modified if the user register dont showDialogForBarcode()
         * if the user not register show till he registered
         */
        switch (v.getId()) {
            case R.id.btnOrder:
                Log.i("Clicked:", btnOrder.toString());
                qrComplement = "O-";
                if(qrResult == null || qrResult == "NotFound" )
                    showDialogForBarcode();
                else
                    showDialogOrder();

                break;
            case R.id.btnBill:
                Log.i("Clicked:", btnBill.toString());
                qrComplement = "B-";
                if(qrResult == null || qrResult == "NotFound" )
                    showDialogForBarcode();
                else
                    showDialogBill();

//              need a dialog for ask you sure ? dialog have price and question
                break;
            case R.id.btnWaiter:
                Log.i("Clicked:", btnWaiter.toString());
                qrComplement = "W-";
                //TODO session
                //if statemant check the already table register or nor
                if(qrResult == null || qrResult == "NotFound" )
                    showDialogForBarcode();
                else
                    showDialogWaiter();
//              need a dialog for ask you sure ?
                break;
            case R.id.btnMenu:
                Log.i("Clicked:", btnMenu.toString());
                Intent intentMenu = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intentMenu);
                break;
            case R.id.btnLogin:
                Log.i("Clicked:", btnLogin.toString());
                //L- means, the user register the table
                qrComplement = "L-";
                scanBarcode();

                //Create an instance of AsyncTask
                //Pass the server ip, port and client message to the AsyncTask
                //send from barcode reader
                //clientAST.execute(new String[] {SERVER_IP, SERVER_PORT,qrResult});
                break;
            case R.id.btnRestaurant:
                Log.i("Clicked:", btnRestaurant.toString());
                break;

            case R.id.imgSetIP:
                Log.i("Clicked:", imgSetIP.toString());
                showDialogIp();//Define the IP change dialog
                break;
        }

    }

    //TODO need better design edittext and margin
    /*
     * Dialog for get Server IP
     */
    private void showDialogIp() {
        // Get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        ipPrefrence = getApplicationContext().getSharedPreferences("IpData", MODE_PRIVATE);

        final EditText etIP = new EditText(MainActivity.this);
        etIP.setText(ipPrefrence.getString("ipData",SERVER_IP));
        etIP.setHint(getString(R.string.enter_server_ip));
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.set_ip))
                .setView(etIP)
                .setPositiveButton(getString(R.string.set_ip_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ipData = etIP.getEditableText().toString().trim();
                        SERVER_IP = ipData;
                        Editor editor = ipPrefrence.edit();
                        editor.putString("ipData",ipData);
                        editor.commit();

                        Log.i("New Ip is: ", ipData);
                    }
                })
                .setNegativeButton(getString(R.string.set_ip_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Does nothing
                    }
                })
                .show();

    }

    private void showDialogOrder(){
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage(R.string.main_order_message);
        dialogBuilder.setTitle(R.string.main_order_title);

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
                //TODO CALL WAITER
                Log.i("table res : ", qrResult);
                Intent intentOrder = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intentOrder);
            }
        });
        dialogBuilder.create().show();
    }

    private void showDialogWaiter(){
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage(R.string.main_waiter_message);
        dialogBuilder.setTitle(R.string.main_waiter_title);

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
                //TODO CALL WAITER
                Log.i("table res : ", qrResult);
                setSendData();



            }
        });

        dialogBuilder.create().show();

    }

    private void showDialogBill(){
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage(R.string.main_bill_message);
        dialogBuilder.setTitle(R.string.main_bill_title);

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton(R.string.main_bill_pay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Scan Barcode
                //TODO Text all order and total price
                Log.i("table res : ", qrResult);
                setSendData();
            }
        });
        dialogBuilder.create().show();
    }


    private void showDialogForBarcode() {
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage(R.string.main_barcode_message);
        dialogBuilder.setTitle(R.string.main_barcode_title);

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
                scanBarcode();
            }
        });

        dialogBuilder.create().show();
    }

    //scanBarcode method
    private void scanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt(getString(R.string.barcode_scan_title));
        integrator.initiateScan();
    }

    /**
     * AsyncTask which handles the communication with the server
     */
    class ClientAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                //Create a client socket and define internet address and the port of the server
                Socket socket = new Socket(params[0],
                        Integer.parseInt(params[1]));
                //Get the input stream of the client socket
                InputStream is = socket.getInputStream();
                //Get the output stream of the client socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //Write data to the output stream of the client socket
                out.println(params[2]);
                //Buffer the data coming from the input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read data in the input buffer
                result = br.readLine();
                //Close the client socket
                socket.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //Write server message to the text view
            Log.i("Server Message", s);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i("resultCode", "" + resultCode);
        Log.i("requestCode", "" + requestCode);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        try {
            String re = scanResult.getContents(); // getScan result
            Log.i("table", re);
            qrResult = re;
            setSendData();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setSendData() {
        ClientAsyncTask clientASTx = new ClientAsyncTask();
        clientASTx.execute(new String[]{SERVER_IP, SERVER_PORT, qrComplement + qrResult}); // Send string "qrComplement+qrResult"
        qrComplement = ""; // clear qrComplement string
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