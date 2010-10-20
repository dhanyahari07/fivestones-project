package org.me.five_stones_project;


import android.view.*;
import android.widget.*;

import android.content.Context;

/**
 *
 * @author Tangl
 */

//for android menu dialog
class IconArrayAdapter<T> extends ArrayAdapter<T>{
    private Context context;
    private int rowResource;
    private int labelResource;
    private int iconResource;
    private int[] icons;
    private T[] items;

    public IconArrayAdapter(Context context,int rowResource,int iconResource,
          int labelResource,int[] icons,T[] items){
        super(context, rowResource, items);
        this.context=context;
        this.rowResource=rowResource;
        this.labelResource=labelResource;
        this.iconResource=iconResource;
        this.icons=icons;
        this.items=items;
  }

    @Override
    public View getView(int position,View reusableView,ViewGroup parent){
        LayoutInflater inflater=(LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(this.rowResource,null);
        TextView label=(TextView)row.findViewById(this.labelResource);
        ImageView icon=(ImageView)row.findViewById(this.iconResource);

        label.setText((String)this.items[position]);
        icon.setImageResource(this.icons[position]);

        return row;
    }
}
