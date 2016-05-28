package menudroid.aybars.arslan.menudroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import menudroid.aybars.arslan.menudroid.asyncs.SocketServerTask;
import menudroid.aybars.arslan.menudroid.db.SqlOperations;
import menudroid.aybars.arslan.menudroid.json.JsonDataToSend;
import menudroid.aybars.arslan.menudroid.main.MenuActivity;
import menudroid.aybars.arslan.menudroid.main.RestaurantActivity;


public class MainActivity extends ActionBarActivity implements SocketServerTask.OurTaskListener ,OnClickListener {
    // Define mainactivity buttons
    private Button btnOrder;
    private Button btnBill;
    private Button btnWaiter;
    private Button btnMenu;
    private Button btnLogin;
    private Button btnRestaurant;
    private ImageView imgSetIP;

    private static String SERVER_IP = "192.168."; //Define the server port
    private static String qrResult = "NotFound"; // Define qrCodes string form barcode
    private String qrComplement = ""; // Complement for understanding messafge from order,bill or waiter
    private static final String QR_RESULT = "NotFound";
    private static final String CLICKED = "Clicked:";
    private static final String TABLE_RES = "table res : ";
    private static final String QR_COMPLEMENT = "qrComplement";
    private static final String QR_RES = "qrResult";
    SharedPreferences ipPrefrence;

    private JSONObject jsonData;
    private JsonDataToSend jsonDataToSend;
    private Context c=this;
    private SocketServerTask serverAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState != null ) {
            //recovering the states
            qrComplement=savedInstanceState.getString(QR_COMPLEMENT);
            setQrResult(savedInstanceState.getString(QR_RES));
        }
        setContentView(R.layout.activity_main);

        // Initalize buttons
        initialize();
    }
    private static void setQrResult(String data) {
       qrResult=data;
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



        /*
         *  Choose buttons
         */
        switch (v.getId()) {
            case R.id.btnOrder:
                Log.i(CLICKED, btnOrder.toString());
                qrComplement = "O-";
                if(qrResult == null || qrResult == QR_RESULT )
                    showDialogForBarcode();
                else
                    showDialogMenu();

                break;
            case R.id.btnBill:
                Log.i(CLICKED, btnBill.toString());
                qrComplement = "B-";
                if(qrResult == null || qrResult == QR_RESULT )
                    showDialogForBarcode();
                else
                    showDialogBill();
//              need a dialog for ask you sure ? dialog have price and question
                break;
            case R.id.btnWaiter:
                Log.i(CLICKED, btnWaiter.toString());
                qrComplement = "W-";
                //TODO session
                //if statemant check the already table register or nor
                if(qrResult == null || qrResult == QR_RESULT )
                    showDialogForBarcode();
                else
                    showDialogWaiter();
//              need a dialog for ask you sure ?
                break;
            case R.id.btnMenu:
                Log.i(CLICKED, btnMenu.toString());
                Intent intentMenu = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intentMenu);
                break;
            case R.id.btnLogin:
                Log.i(CLICKED, btnLogin.toString());
                //L- means, the user register the table
                qrComplement = "L-";
                scanBarcode();

                //Create an instance of AsyncTask
                //Pass the server ip, port and client message to the AsyncTask
                //send from barcode reader

                break;
            case R.id.btnRestaurant:
                Log.i(CLICKED, btnRestaurant.toString());
                Intent intentRestaurant = new Intent(MainActivity.this, RestaurantActivity.class);
                startActivity(intentRestaurant);
                break;

            case R.id.imgSetIP:
                Log.i(CLICKED, imgSetIP.toString());
                showDialogIp();//Define the IP change dialog
                break;
            default:
                break;
        }

    }

    //TODO need better design edittext and margin
    /*
     * Dialog for get Server IP
     */
    private void showDialogIp() {


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
                        setServerIp(ipData);
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
    private static void   setServerIp(String ipData){
        SERVER_IP=ipData;
    }
    private void showDialogMenu(){
        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage(R.string.main_menu_message);
        dialogBuilder.setTitle(R.string.main_menu_title);

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
                Log.i(TABLE_RES, qrResult);
                Intent intentOrder = new Intent(MainActivity.this, MenuActivity.class);
                intentOrder.putExtra(QR_RES, qrResult);
                intentOrder.putExtra(QR_COMPLEMENT, qrComplement);
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
                Log.i(TABLE_RES, qrResult);
                sendRequest();
            }
        });
        dialogBuilder.create().show();
    }

    private void showDialogBill(){

        //get the total price
        SqlOperations sqlOperations= new SqlOperations(this);
        sqlOperations.open();
        String total=sqlOperations.getTotal();
        sqlOperations.close();


        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage("Pay Bill\n total price" + " " + total );
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
                Log.i(TABLE_RES, qrResult);
                sendRequest();
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

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //save the state of these strings.
        outState.putString(QR_RES, qrResult);
        outState.putString(QR_COMPLEMENT, qrComplement);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i("resultCode", "" + resultCode);
        Log.i("requestCode", "" + requestCode);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

            try {

                String re = scanResult.getContents(); // getScan result
                Log.i("table", re);
                setQrResult(re);
                sendRequest();
            } catch (NullPointerException e) {
                Log.d("ERROR",e.toString());
            }

    }

    //this method send the request from client
    private void sendRequest() {
        //get the qrResult and the qrComplement to create the new JSON
        jsonData = new JSONObject();
        jsonDataToSend= new JsonDataToSend(); //instantiate the new JSON object
        jsonDataToSend.setRequest(qrComplement);
        jsonDataToSend.setMessage(qrResult);//now the JSON is complete
        jsonData=jsonDataToSend.getOurJson();// pass the json object to this variable.
        String jsonStr = jsonData.toString();
        System.out.println("jsonString: "+jsonStr);
        Log.d("JSON", jsonStr);


        //Start the AsyncTask execution
        //Accepted client socket object will pass as the parameter
        serverAsyncTask= new SocketServerTask(this,c);
        serverAsyncTask.execute(jsonData);

    }


    Toast m_currentToast;

    void showToast(String text) {
        if (m_currentToast != null) {
            m_currentToast.cancel();
        }
        m_currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        m_currentToast.show();
    }

    /* INTERFACE METHODS FROM OurTaskListener-SocketServerTask */
    @Override
    public void onOurTaskStarted() {

    }

    @Override
    public void onOurTaskInProcess() {

    }

    @Override
    public void onOurTaskFinished(String result) {
        if(result!=null) {
            if (result.equals("Connection Accepted")) {
                showToast("Connection Done");
                if (qrComplement.equals("B-")) {
                    //because the consumer said YES to the pay , we will erase the data.
                    SqlOperations sqlOperations = new SqlOperations(this);
                    sqlOperations.open();
                    sqlOperations.setEmptyTotal();
                    sqlOperations.close();
                }
            } else {
                showToast("Unable to connect");
            }
        }else{
            showToast("Unable to connect");
        }
    }
}