package com.emp.auction;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends ArrayAdapter<Products> {

    private Context mContext;
    private List<Products> ProductsList = new ArrayList<>();

    public ProductsAdapter(Context context, ArrayList<Products> list) {
        super(context, 0 , list);
        mContext = context;
        ProductsList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_products,parent,false);

        Products currentMovie = ProductsList.get(position);

        ImageView image = (ImageView) listItem.findViewById(R.id.Image);
        String imagePath=Common.getInstance().getBaseURL()+currentMovie.getmImage().split("_split_")[0];
        Ion.with(getContext()).load(imagePath).intoImageView(image);

        TextView name = (TextView) listItem.findViewById(R.id.txtName);
        name.setText(currentMovie.getmName());

        TextView price = (TextView) listItem.findViewById(R.id.txtPrice);
        price.setText(Integer.toString(currentMovie.getmPrice()));

        TextView countdown = (TextView) listItem.findViewById(R.id.txtCountdown);
        countdown.setText(Integer.toString(currentMovie.getmCountdown()));

        final TextView endTime = (TextView) listItem.findViewById(R.id.txtEndTime);
        long currentTime=System.currentTimeMillis()/1000;
        if(currentTime>currentMovie.getEndTime()) {
            endTime.setText("Ended");
        }
        else{
            long countdownSeconds=currentMovie.getEndTime()-currentTime;
            long s=countdownSeconds % 60;
            long min=countdownSeconds/60;
            long h=countdownSeconds/3600;
            long i=min % 60;
            endTime.setText(String.format("%02d:%02d:%02d",h,i,s));
        }

        return listItem;
    }
}