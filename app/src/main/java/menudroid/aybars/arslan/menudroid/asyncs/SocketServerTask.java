package menudroid.aybars.arslan.menudroid.asyncs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by renesotolira on 26/04/15.
 */
public class SocketServerTask extends AsyncTask<JSONObject, Void, String> {
    private Context mContext;
    private String TAG="SocketTAG";


    private final OurTaskListener listener;

    private int SocketServerPORT = 8080;

    private JSONObject jsonData;
    private boolean success;


    public SocketServerTask(OurTaskListener listener,Context context) {
        this.mContext = context;
        this.listener = listener;
    }


    @Override
    public void onPreExecute(){
        listener.onOurTaskStarted();
    }


    @Override
        protected String doInBackground(JSONObject... params) {
            listener.onOurTaskInProcess();
            Socket socket = null;
            String result = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            jsonData = params[0];

            try {
                //get the ip from shared preferences
                SharedPreferences settings;
                settings = mContext.getSharedPreferences("IpData",Context.MODE_PRIVATE);
                String ip=settings.getString("ipData", "");

                // Create a new Socket instance and connect to host
                socket = new Socket(ip, SocketServerPORT);

                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                // transfer JSONObject as String to the server
                dataOutputStream.writeUTF(jsonData.toString());
                Log.i(TAG, "waiting for response from host");

                // Thread will wait till server replies
                //Buffer the data coming from the input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(dataInputStream));
                //Read data in the input buffer
                result = br.readLine();
                //Close the client socket
                socket.close();

            } catch (IOException e) {
                Log.e("ERROR",""+e.toString());
                success = false;
            } finally {

                // close socket
                if (socket != null) {
                    try {
                        Log.i(TAG, "closing the socket");
                        socket.close();
                    } catch (IOException e) {
                        Log.e("ERROR",""+e.toString());
                    }
                }

                // close input stream
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        Log.e("ERROR",""+e.toString());
                    }
                }

                // close output stream
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        Log.e("ERROR",""+e.toString());
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ServerMessage", ""+result);
            listener.onOurTaskFinished(result);
        }

    public interface OurTaskListener {
        void onOurTaskStarted();
        void onOurTaskInProcess();
        void onOurTaskFinished(String result);
    }

}

