package menudroid.aybars.arslan.menudroid.main;

import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import menudroid.aybars.arslan.menudroid.R;


public class MenuExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> menuCollections;
    private List<String> foods;

    public MenuExpandableListAdapter(Activity context, List<String> foods, Map<String, List<String>> menuCollections) {
        this.context = context;
        this.menuCollections = menuCollections;
        this.foods = foods;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return menuCollections.get(foods.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String food = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.menu_child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.child_food);

        ImageView item_add = (ImageView) convertView.findViewById(R.id.item_add);
        item_add.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setMessage("Do you want to remove?");
//                builder.setCancelable(false);
//                builder.setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                List<String> child =
//                                        menuCollections.get(foods.get(groupPosition));
//                                child.remove(childPosition);
//                                notifyDataSetChanged();
//                            }
//                        });
//                builder.setNegativeButton("No",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
                Toast.makeText(context, "click add", Toast.LENGTH_LONG).show();
            }
        });

        item.setText(food);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return menuCollections.get(foods.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return foods.get(groupPosition);
    }

    public int getGroupCount() {
        return foods.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String foodName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.menu_group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.group_food);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(foodName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
