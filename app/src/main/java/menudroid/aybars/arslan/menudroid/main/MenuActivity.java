package menudroid.aybars.arslan.menudroid.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import menudroid.aybars.arslan.menudroid.R;
import menudroid.aybars.arslan.menudroid.asyncs.SocketServerTask;
import menudroid.aybars.arslan.menudroid.db.SqlOperations;
import menudroid.aybars.arslan.menudroid.json.JsonDataToSend;


public class MenuActivity extends ActionBarActivity implements View.OnClickListener {
    private SqlOperations sqliteoperation;
    private Toolbar toolbar;
    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> menuCollection;

    ExpandableListView expListView;
    FloatingActionButton fab;
    private JSONObject jsonData;
    private JsonDataToSend jsonDataToSend;
    private Context c=this;

    private SocketServerTask serverAsyncTask;

    private ArrayList<Map<String, String>> ListData;

    private String qrResult,qrComplement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = getIntent();
        qrResult = intent.getStringExtra("qrResult");
        qrComplement = intent.getStringExtra("qrComplement");

//        //set toolbar
//        toolbar = (Toolbar) findViewById(R.id.menu_toolbar);
//        setSupportActionBar(toolbar);
       // getJsonFromWeb(); //with this method I get the json from URL
        //createGroupList();
        //createCollection();

        expListView = (ExpandableListView) findViewById(R.id.food_list);


        //float action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        //setGroupIndicatorToRight();
        MenuWebTask  serverAsyncTask= new MenuWebTask(c);
        serverAsyncTask.execute();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab:
                showDialogOrder();
                break;

        }
    }

    private void showDialogOrder(){

        //here get the order
        sqliteoperation = new SqlOperations(getApplicationContext());
        sqliteoperation.open();
        ArrayList<HashMap<String, String>> dictionary =sqliteoperation.getOrder();
        sqliteoperation.close();

        String totalbyFood,quantity,food_name,messageOrder,price;
        messageOrder="\nOrder\nYour ordered";
        float totalbyOrder=0;
        int j;

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < dictionary.size(); i++) {

            j=i+1;
            /*I start at index 0 and finish at the penultimate index */
            HashMap<String, String> map = dictionary.get(i); //Get the corresponding map from the index
            totalbyFood=map.get("totalByFood").toString();
            price=map.get("price").toString();
            quantity=map.get("quantity").toString();
            food_name=map.get("food_name").toString();
            messageOrder+="\n "+j+" - "+food_name+" ("+price+" $  x  "+ quantity +")  "+ totalbyFood+"$";
            totalbyOrder+=Float.parseFloat(totalbyFood);


            JSONObject food = new JSONObject();
            try {
                food.put("totalByFood", totalbyFood);
                food.put("price", price);
                food.put("quantity", quantity);
                food.put("food_name", food_name);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jsonArray.put(food);

        }
      /* JSONObject OrderObj = new JSONObject();
        try {
            OrderObj.put("Order", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */


        //get the qrResult and the qrComplement to create the new JSON
        jsonData = new JSONObject();
        jsonDataToSend= new JsonDataToSend(); //instantiate the new JSON object
        jsonDataToSend.setRequest("O-");
        jsonDataToSend.setMessage(qrResult);
        jsonDataToSend.setMessageJsonArray(jsonArray);//now the JSON is complete
        jsonData=jsonDataToSend.getOurJson();// pass the json object to this variable.
        String jsonStr = jsonData.toString();
        System.out.println(" the json to sent jsonString: "+jsonStr);
        Log.d("JSON", jsonStr);



        //new SocketServerTask().execute(jsonData);



        messageOrder+="\n Total = "+totalbyOrder+"$\n Are you sure the ordered them";

        AlertDialogWrapper.Builder dialogBuilder = new AlertDialogWrapper.Builder(this);
        dialogBuilder.setMessage(messageOrder);//R.string.main_order_message)
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
                showToast("order is ok");
                    //send order to server
                serverAsyncTask= new SocketServerTask(c);
                serverAsyncTask.execute(jsonData);
            }
        });
        dialogBuilder.create().show();
    }




    private void getJsonFromWeb() {
        menuCollection = new LinkedHashMap<String, List<String>>();
        groupList = new ArrayList<String>();

        /* It would be better if  this process will be in a Thread.*/
        if (android.os.Build.VERSION.SDK_INT > 9) {
            /*To avoid the android.os.NetworkOnMainThreadException*/
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String jsonResponse = "";
        HttpPost httppost = new HttpPost(
                "http://arslanaybars.com/MenuDroid/menuDroid-menu.json");
        //the place where you can edit the json
        /* http://pastebin.com/vYSF3Lht
        * click at RAW option to get the url from HttPost method   
        * */
        try {
            List<NameValuePair> postValues = new ArrayList<NameValuePair>();
            httppost.setEntity(new UrlEncodedFormEntity(postValues));
            // timeout params
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 9000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 9000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            // //
            HttpResponse protocolResponse = httpclient.execute(httppost);
            HttpEntity entity = protocolResponse.getEntity();
            InputStream is = entity.getContent();
            String result = StreamToString(is);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonField = new JSONObject(result);
                Log.d("JSON", jsonField.get("success").toString());
                String success = jsonField.get("success").toString();
                if (success.equals("1")) {
                    /* Detect field success has value 1*/
                    JSONArray CategoriesArray = jsonObject.getJSONArray("categories"); /*getting the JSON Array with the key categories */
                    if (CategoriesArray != null) {
                        for (int i = 0; i < CategoriesArray.length(); i++) { //Search inner the Categories array
                            String categoryName = CategoriesArray.getJSONObject(i).getString("name");
                            groupList.add(categoryName); //add to the  groupList
                            Log.d("CATEGORY", "The category is " + categoryName);
                            JSONArray FoodNameArray = new JSONArray(CategoriesArray.getJSONObject(i).getString("content"));
                            childList = new ArrayList<String>();

                            for (int j = 0; j < FoodNameArray.length(); j++) {
                                String Foodname = FoodNameArray.getJSONObject(j).getString("food");//get the value from key food
                                String price = FoodNameArray.getJSONObject(j).getString("price");//get the value from key price
                                Log.d("Food", "The food from category " + categoryName + " is " + Foodname + " this cost : " + price);
                                childList.add(Foodname+"||"+price); //add the food in the childList
                            }
                            menuCollection.put(categoryName, childList);
                        }
                    }
                }

            } catch (JSONException e) {
                jsonResponse = e.toString();
                Log.d("exception", "" + jsonResponse);
            }
        }
        // kind of exceptions.
        catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            jsonResponse = e.toString();
            Log.d("exception", "" + jsonResponse);
        } catch (ClientProtocolException e) {
            // e.printStackTrace();
            jsonResponse = e.toString();
            Log.d("exception", "" + jsonResponse);
        } catch (ConnectTimeoutException e) {
            // jsonResponse="-1";
            jsonResponse = e.toString();
            Log.d("exception", "" + jsonResponse);
        } catch (SocketTimeoutException e) {
            // jsonResponse="-1";
            jsonResponse = e.toString();
            Log.d("exception", "" + jsonResponse);
        } catch (IOException e) {
            jsonResponse = e.toString();
            Log.d("exception", "" + jsonResponse);
            // e.printStackTrace();
        } catch (Exception e) {
            jsonResponse = e.toString();
            Log.d("exception", "" + jsonResponse);
        }
    }

    public static String StreamToString(InputStream is) { //convert Stream to String
        //Create the  Buffer
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            //Read all the lines
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

/*
    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add(getString(R.string.list_breakfast));
        groupList.add(getString(R.string.list_soup));
        groupList.add(getString(R.string.list_pancake));
        groupList.add(getString(R.string.list_main_dish));
        groupList.add(getString(R.string.list_desert));
        groupList.add(getString(R.string.list_drinks));
    }
*/

/*
    private void createCollection() {
        // preparing foods collection(child)
        String[] breakfastModels = {getString(R.string.food_breakfast), getString(R.string.food_breakfast_ottoman)};
        String[] soupModels = {getString(R.string.food_tarhana_soup), getString(R.string.food_yayla_soup), getString(R.string.food_akdene_soup),getString(R.string.food_keskek_soup)};
        String[] pancakeModels = {getString(R.string.food_meat_pancake),getString(R.string.food_meat_pancake),getString(R.string.food_yogurt_pancake),getString(R.string.food_cheese_pancake),getString(R.string.food_potatoes_pancake),getString(R.string.food_spinach_pancake)};
        String[] mainDishModels = { getString(R.string.food_main_1),getString(R.string.food_main_2),getString(R.string.food_main_3)};
        String[] desertModels = {getString(R.string.food_desert_1)};
        String[] drinkModels = {getString(R.string.food_drink_1)};

        menuCollection = new LinkedHashMap<String, List<String>>();

        for (String food : groupList) {
            if (food.equals("Breakfast")) {
                loadChild(breakfastModels);
            } else if (food.equals("Soup"))
                loadChild(soupModels);
            else if (food.equals("Pancake"))
                loadChild(pancakeModels);
            else if (food.equals("Main Dish"))
                loadChild(mainDishModels);
            else if (food.equals("Desert"))
                loadChild(desertModels);
            else
                loadChild(drinkModels);

            menuCollection.put(food, childList);
        }
    }

    private void loadChild(String[] foodModels) {
        childList = new ArrayList<String>();
        for (String model : foodModels)
            childList.add(model);
    }
*/

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            showToast("Settings clicked !");
            return true;
        }*/
        //Clicked favorite icon
        if (id == R.id.action_info) {
            showToast("info clicked !");
            //  startActivity(new intent(this, SubActivity.class));
            return true;
        }
        //Clicked share icon
        if (id == R.id.action_done) {
            showToast("Done clicked !");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Toast m_currentToast;

    //showToast method
    void showToast(String text) {
        if (m_currentToast != null) {
            m_currentToast.cancel();
        }
        m_currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        m_currentToast.show();

    }


    class MenuWebTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog;
        private Context mContext;
        private String TAG="MenuWebTask";

        public MenuWebTask(Context context) {
            mContext = context;
        }



        @Override
        public void onPreExecute(){
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Loading menu....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            getJsonFromWeb();
            return null;
        }

        @Override
        protected void onPostExecute(String  result) {
            pDialog.dismiss();

            final MenuExpandableListAdapter expListAdapter = new MenuExpandableListAdapter(MenuActivity.this, groupList, menuCollection);
            expListView.setAdapter(expListAdapter);
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    final String selected = (String) expListAdapter.getChild(groupPosition, childPosition);

                    Log.d("jj","paretn is "+parent+
                            "\n gorup position is "+ groupPosition+
                            "\n childposition "+ childPosition+
                            " \n and the id is "+ id);

                /*
                * Group position is the category (grouplist), example Soup,Breakfast
                * childposition is the foodname (childList ), example cheese pancake
                * */
                    Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }



    }



}
