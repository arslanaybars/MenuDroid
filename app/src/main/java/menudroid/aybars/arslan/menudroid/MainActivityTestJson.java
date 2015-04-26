package menudroid.aybars.arslan.menudroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.nsd.NsdManager;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import menudroid.aybars.arslan.menudroid.main.MenuActivity;
import menudroid.aybars.arslan.menudroid.main.RestaurantActivity;


public class MainActivityTestJson extends ActionBarActivity implements OnClickListener {

    private Button btnOrder, btnBill, btnWaiter, btnMenu, btnLogin, btnRestaurant; // Define mainactivity buttons
    private ImageView imgSetIP;
    private String TAG="SocketTAG";
    private static String SERVER_IP = "192.168."; //Define the server port
    private final String SERVER_PORT = "8080"; //Define the server port

    private int SocketServerPORT = 8080;

    private static String qrResult = "NotFound"; // Define qrCodes string form barcode
    private String qrComplement = ""; // Complement for understanding messafge from order,bill or waiter

    SharedPreferences ipPrefrence;

    private String SERVICE_NAME = "Client Device";
    private String SERVICE_TYPE = "_http._tcp.";

    private InetAddress hostAddress;
    private int hostPort;
    private NsdManager mNsdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        // Initalize buttons
        initialize();


        JSONObject student1 = new JSONObject();
        try {
            student1.put("id", "3");
            student1.put("name", "NAME OF STUDENT");
            student1.put("year", "3rd");
            student1.put("curriculum", "Arts");
            student1.put("birthday", "5/5/1993");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONObject student2 = new JSONObject();
        try {
            student2.put("id", "2");
            student2.put("name", "NAME OF STUDENT2");
            student2.put("year", "4rd");
            student2.put("curriculum", "scicence");
            student2.put("birthday", "5/5/1993");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        JSONArray jsonArray = new JSONArray();

        jsonArray.put(student1);
        jsonArray.put(student2);

        JSONObject studentsObj = new JSONObject();
        try {
            studentsObj.put("Students", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonStr = studentsObj.toString();
        System.out.println("jsonString: "+jsonStr);
        Log.d("JSON", jsonStr);



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
                    showDialogMenu();

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
               /* Intent intentMenu = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intentMenu);
               */
                JSONObject jsonData = new JSONObject();

                try {



                    JSONObject student1 = new JSONObject();
                    try {
                        student1.put("id", "3");
                        student1.put("name", "NAME OF STUDENT");
                        student1.put("year", "3rd");
                        student1.put("curriculum", "Arts");
                        student1.put("birthday", "5/5/1993");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    JSONObject student2 = new JSONObject();
                    try {
                        student2.put("id", "2");
                        student2.put("name", "NAME OF STUDENT2");
                        student2.put("year", "4rd");
                        student2.put("curriculum", "scicence");
                        student2.put("birthday", "5/5/1993");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    JSONArray jsonArray = new JSONArray();

                    jsonArray.put(student1);
                    jsonArray.put(student2);

                    JSONObject studentsObj = new JSONObject();
                    studentsObj.put("Students", jsonArray);
                    studentsObj.put("request", "order");
                    studentsObj.put("success", "1");
                    jsonData =studentsObj;
                   /* jsonData.put("request", "order");
                    jsonData.put("success", "1");
                */
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "can't put request");
                    return;
                }
                new SocketServerTask().execute(jsonData);
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
                Intent intentRestaurant = new Intent(MainActivityTestJson.this, RestaurantActivity.class);
                startActivity(intentRestaurant);
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
        LayoutInflater inflater = MainActivityTestJson.this.getLayoutInflater();

        ipPrefrence = getApplicationContext().getSharedPreferences("IpData", MODE_PRIVATE);

        final EditText etIP = new EditText(MainActivityTestJson.this);
        etIP.setText(ipPrefrence.getString("ipData",SERVER_IP));
        etIP.setHint(getString(R.string.enter_server_ip));
        new AlertDialog.Builder(MainActivityTestJson.this)
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
                Log.i("table res : ", qrResult);
                Intent intentOrder = new Intent(MainActivityTestJson.this, MenuActivity.class);
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
                sendRequest();
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


    /***
     *
     *
     */




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
            sendRequest();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //this method send the request from client
    private void sendRequest() {
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


    private class SocketServerTask extends AsyncTask<JSONObject, Void, Void> {
        private JSONObject jsonData;
        private boolean success;

        @Override
        protected Void doInBackground(JSONObject... params) {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            jsonData = params[0];

            try {
                // Create a new Socket instance and connect to host
                socket = new Socket(SERVER_IP, SocketServerPORT);

                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                // transfer JSONObject as String to the server
                dataOutputStream.writeUTF(jsonData.toString());
                Log.i(TAG, "waiting for response from host");

                // Thread will wait till server replies
                String response = dataInputStream.readUTF();
                if (response != null && response.equals("Connection Accepted")) {
                    success = true;
                } else {
                    success = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            } finally {

                // close socket
                if (socket != null) {
                    try {
                        Log.i(TAG, "closing the socket");
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // close input stream
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // close output stream
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (success) {
                Toast.makeText(MainActivityTestJson.this, "Connection Done", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivityTestJson.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

