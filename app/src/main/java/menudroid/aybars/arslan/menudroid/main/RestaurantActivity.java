package menudroid.aybars.arslan.menudroid.main;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import menudroid.aybars.arslan.menudroid.R;

public class RestaurantActivity extends ActionBarActivity{

    RestaurantItem itemsData[] = { new RestaurantItem(getString(R.string.res_contact),R.drawable.res_contact),
            new RestaurantItem(getString(R.string.res_events),R.drawable.res_event),
            new RestaurantItem(getString(R.string.res_reservation),R.drawable.res_reservation),
            new RestaurantItem(getString(R.string.res_work_time),R.drawable.res_work_time),
            new RestaurantItem(getString(R.string.res_survey),R.drawable.res_survey),
            new RestaurantItem(getString(R.string.res_gallery),R.drawable.res_gallery),
            new RestaurantItem(getString(R.string.res_facebook),R.drawable.res_facebook),
            new RestaurantItem(getString(R.string.res_twitter),R.drawable.res_twitter),
            new RestaurantItem(getString(R.string.res_instagram),R.drawable.res_instagram),
            new RestaurantItem(getString(R.string.res_foursquare),R.drawable.res_foursquare)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    Toast m_currentToast;

    void showToast(String text) {
        if (m_currentToast != null) {
            m_currentToast.cancel();
        }
        m_currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        m_currentToast.show();

    }

}
