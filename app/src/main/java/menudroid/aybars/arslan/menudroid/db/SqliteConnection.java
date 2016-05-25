package menudroid.aybars.arslan.menudroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by renesotolira on 21/03/15.
 */
public class SqliteConnection  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DroidRestaurant.db";
    public static final String TABLE_NAME = "OrderClient";
    public static final String TABLE_PRICE = "PriceClient";
    private static final int DATABASE_VERSION = 1;

    String sqlCreateTableOrder= "CREATE TABLE OrderClient (_id INTEGER PRIMARY KEY, index_category INTEGER, index_food INTEGER, quantity INTEGER, price TEXT, food_name TEXT)";

    String sqlCreateTableTotal= "CREATE TABLE PriceClient (_id INTEGER PRIMARY KEY, total TEXT)";


    public SqliteConnection(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    //create the table order
        db.execSQL(sqlCreateTableOrder);
        db.execSQL(sqlCreateTableTotal);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


}