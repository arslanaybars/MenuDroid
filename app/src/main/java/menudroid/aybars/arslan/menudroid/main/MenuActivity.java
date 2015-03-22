package menudroid.aybars.arslan.menudroid.main;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import menudroid.aybars.arslan.menudroid.R;
import menudroid.aybars.arslan.menudroid.main.MenuExpandableListAdapter;


public class MenuActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private ListView listview;
    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> menuCollection;
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //set toolbar
        toolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        setSupportActionBar(toolbar);

        createGroupList();

        createCollection();

        expListView = (ExpandableListView) findViewById(R.id.food_list);
        final MenuExpandableListAdapter expListAdapter = new MenuExpandableListAdapter(this, groupList, menuCollection);
        expListView.setAdapter(expListAdapter);

        //setGroupIndicatorToRight();
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG).show();
                return true;
            }
        });


    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add(getString(R.string.list_breakfast));
        groupList.add(getString(R.string.list_soup));
        groupList.add(getString(R.string.list_pancake));
        groupList.add(getString(R.string.list_main_dish));
        groupList.add(getString(R.string.list_desert));
        groupList.add(getString(R.string.list_drinks));
    }

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
}
