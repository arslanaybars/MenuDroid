package menudroid.aybars.arslan.menudroid.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * Clase para manipular las operaciones simples con db local como son insert,delete,update,etc
 */
public class SqlOperations {

    //the next two variable it is only for debugging test.
    private String TAG = this.getClass().getSimpleName();
    private boolean LogDebug=true;

    // Database fields
    private SQLiteDatabase database;
    private SqliteConnection sqliteconnection;
    private static final String KEY_INDEX_CATEGORY = "index_category";
    private static final String KEY_INDEX_FOOD = "index_food";
    private static final String KEY_QTY = "quantity";
    private static final String KEY_FOOD_NAME = "food_name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_TOTAL = "total_sum";


    public SqlOperations(Context context) {
        sqliteconnection = new SqliteConnection(context);  //conexion y/o creacion de DB
    }

    public void open() throws SQLException {
        database = sqliteconnection.getWritableDatabase(); // avaliable to write in the db.
    }

    public void close() {
        sqliteconnection.close(); //close db
    }

    public  ArrayList<HashMap<String,String>>  getOrder (){

        Cursor cursor;
        ArrayList<HashMap<String, String>> allElementsDictionary = new ArrayList<HashMap<String, String>>();
        String select = "SELECT quantity,price,food_name from OrderClient";
        cursor = database.rawQuery(select,null);
        if(cursor.getCount()==0) // if there are no elements do nothing
        {
            Log.d(TAG,"no elements");
        }
        else
        { //if there are elemnts
            Log.d(TAG,"there are elemnets");
            //get all the rows and pass the data to allElements dictionary.
                float totalByOrder=0;

                while(cursor.moveToNext()){
                    int qty=Integer.parseInt(cursor.getString(0));

                    if(qty>0){
                        float totalByFood=Float.parseFloat(cursor.getString(0))*Float.parseFloat(cursor.getString(1));// qty * price
                        totalByOrder+=totalByFood;
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("totalByFood", String.valueOf(totalByFood));
                        map.put(KEY_QTY, cursor.getString(0));
                        map.put(KEY_PRICE, cursor.getString(1));
                        map.put(KEY_FOOD_NAME, cursor.getString(2));
                        allElementsDictionary.add(map);
                        if (LogDebug) {
                            Log.d(TAG, "qty : " + cursor.getString(0) +
                                            "\n price :" + cursor.getString(1)+
                                            "\n foodname :" + cursor.getString(2)+
                                            "\n totalByFood :"+ String.valueOf(totalByFood)
                            );
                        }
                    }



                    Log.d(TAG,"total is :"+totalByOrder);


            }



        }
        if (cursor!=null)
        {
            cursor.close();//It is important close the cursor when you finish your process.
        }

        return allElementsDictionary;
    }

    public void AddOrSubstractProduct (int category_index,int food_index,String food,float price, int kindOperation){
        /* kind Operation = add or Subtract
         1= add
         2= substract
         */

     /*NOTE when you close the Session we have to delete this data to create a new order*/
        Cursor cursor;
        String select = "SELECT quantity,_id FROM OrderClient where "+KEY_INDEX_CATEGORY+"="+category_index+
                " and "+ KEY_INDEX_FOOD+"="+food_index;
        cursor = database.rawQuery(select,null);
        if(cursor.getCount()==0 && kindOperation==1) // if there are no elements and the operation is ADD , set QTY in  1
        {
            ContentValues row = new ContentValues();
            row.put(KEY_INDEX_CATEGORY, category_index);
            row.put(KEY_INDEX_FOOD, food_index);
            row.put(KEY_QTY, 1);
            row.put(KEY_FOOD_NAME, food);
            row.put(KEY_PRICE, String.valueOf(price));
            database.insert(SqliteConnection.TABLE_NAME, null, row); //insert in DB the request
            //add this food with qty 1
        }
        else if(cursor.getCount()>0){
                //this means the product exist with some qty
            cursor.moveToFirst();
            int oldQty=Integer.parseInt(cursor.getString(0));//get the qty from Database
            int idSum=Integer.parseInt(cursor.getString(1));//get the id from database
            ContentValues row = new ContentValues();

                switch(kindOperation){
                    case 1:
                    //its a sum, update the qty in 1
                        row.put(KEY_QTY, oldQty+1);//add  1
                        database.update(SqliteConnection.TABLE_NAME, row, "_id=" + idSum, null); //update qty in DB the request
                        break;

                    case 2:
                        if(oldQty>0) row.put(KEY_QTY, oldQty-1);//substract -1 if the qty is greater than 0,
                        database.update(SqliteConnection.TABLE_NAME,row,"_id="+idSum, null); //update qty DB the request
                        break;
                }
        }

        if (cursor!=null)
        {
            cursor.close();//It is important close the cursor when you finish your process.
        }

    }



}
