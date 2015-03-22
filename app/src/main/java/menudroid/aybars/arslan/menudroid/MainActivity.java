package menudroid.aybars.arslan.menudroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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


public class MainActivity extends ActionBarActivity implements OnClickListener {

    private Button btnOrder, btnBill, btnWaiter, btnMenu, btnLogin, btnRestaurant;
    private final String SERVER_IP = "192.168.1.73"; //Define the server port
    private final String SERVER_PORT = "8080"; //Define the server port
    private static String qrResult = "NotFound"; // Define qrCodes string form barcode
    private String qrComplement="";
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

        //onClick Events
        btnOrder.setOnClickListener(this);
        btnBill.setOnClickListener(this);
        btnWaiter.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRestaurant.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        ClientAsyncTask clientAST = new ClientAsyncTask();

        switch (v.getId()) {
            case R.id.btnOrder:
                qrComplement="O-";
                showDialogForBarcode();
//                Intent intentOrder = new Intent(MainActivity.this, OrderActivity.class);
//                startActivity(intentOrder);


                break;
            case R.id.btnBill:
                qrComplement="B-";
                showDialogForBarcode();
//                Intent intentBill = new Intent(MainActivity.this, BillActivity.class);
//                startActivity(intentBill);

                break;
            case R.id.btnWaiter:
                showDialogForBarcode();
                qrComplement="w-";
                break;
            case R.id.btnMenu:
//              showToast("Clicked Menu");
                //  Intent intentMenu = new Intent(MainActivity.this, MenuActivity.class);
                //  startActivity(intentMenu);
//              Log.i("qrCodes String :", qrResult);
                break;
            case R.id.btnLogin:
                showToast("Clicked Login");

                //Create an instance of AsyncTask

                //Pass the server ip, port and client message to the AsyncTask
                // send from barcode reader
                clientAST.execute(new String[] {SERVER_IP, SERVER_PORT,qrResult});
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
                //TODO - barcode scan message get from string.xml
                integrator.setPrompt("Scan the barcode from table !");
                integrator.initiateScan();
            }
        });

        dialogBuilder.create().show();
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
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
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
            //       Log.i("Server Message", s);
            showToast(s);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i("codex", ""+resultCode);
        Log.i("codex", ""+requestCode);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Log.i("code", re);
            showToast(re);
            qrResult = re;
            ClientAsyncTask clientASTx = new ClientAsyncTask();
            clientASTx.execute(new String[] {SERVER_IP, SERVER_PORT,qrComplement+qrResult});
            qrComplement="";
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