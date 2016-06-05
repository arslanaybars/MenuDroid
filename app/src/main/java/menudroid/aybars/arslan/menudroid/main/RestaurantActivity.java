package menudroid.aybars.arslan.menudroid.main;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import menudroid.aybars.arslan.menudroid.R;

public class RestaurantActivity extends ActionBarActivity{



    ArrayList<RestaurantItem> itemsData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        itemsData= new ArrayList<RestaurantItem>();
        itemsData.add( new RestaurantItem(getString(R.string.res_contact),R.drawable.res_contact));
        itemsData.add( new RestaurantItem(getString(R.string.res_events),R.drawable.res_event));
        itemsData.add( new RestaurantItem(getString(R.string.res_reservation),R.drawable.res_reservation));
        itemsData.add( new RestaurantItem(getString(R.string.res_work_time),R.drawable.res_work_time));
        itemsData.add( new RestaurantItem(getString(R.string.res_survey),R.drawable.res_survey));
        itemsData.add( new RestaurantItem(getString(R.string.res_gallery),R.drawable.res_gallery));
        itemsData.add( new RestaurantItem(getString(R.string.res_facebook),R.drawable.res_facebook));
        itemsData.add( new RestaurantItem(getString(R.string.res_twitter),R.drawable.res_twitter));
        itemsData.add( new RestaurantItem(getString(R.string.res_instagram),R.drawable.res_instagram));
        itemsData.add( new RestaurantItem(getString(R.string.res_foursquare),R.drawable.res_foursquare));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("DATA", "before declare the custom adapter");
       RestaurantAdapter mAdapter = new RestaurantAdapter(itemsData);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Toast mCurrentToast;

    void showToast(String text) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }
        mCurrentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        mCurrentToast.show();

    }

}
