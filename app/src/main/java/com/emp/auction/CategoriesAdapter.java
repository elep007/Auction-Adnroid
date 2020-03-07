package com.emp.auction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends ArrayAdapter<Categories> {

    private Context mContext;
    private List<Categories> CategoriesList = new ArrayList<>();

    public CategoriesAdapter(Context context, ArrayList<Categories> list) {
        super(context, 0 , list);
        mContext = context;
        CategoriesList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_categories,parent,false);

        Categories currentMovie = CategoriesList.get(position);

        ImageView image = (ImageView) listItem.findViewById(R.id.categoryImage);
        Ion.with(getContext()).load(currentMovie.getmImage()).intoImageView(image);

        TextView name = (TextView) listItem.findViewById(R.id.txtCategoryName);
        name.setText(currentMovie.getmName());

        TextView count = (TextView) listItem.findViewById(R.id.txtCategoryCount);
        count.setText(String.valueOf(currentMovie.getmCategoryCount()));

        return listItem;
    }
}
