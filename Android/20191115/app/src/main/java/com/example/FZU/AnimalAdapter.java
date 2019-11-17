package com.example.FZU;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.LinkedList;


public class AnimalAdapter extends BaseAdapter {
    private LinkedList<Animal> mData;
    private Context mContext;

    public AnimalAdapter(LinkedList<Animal> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listlayout,parent,false);
        ImageView img_bicon = (ImageView) convertView.findViewById(R.id.image2) ;
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.image);
        TextView txt_aName = (TextView) convertView.findViewById(R.id.title);
        TextView txt_aSpeak = (TextView) convertView.findViewById(R.id.hobby);

        Glide.with(mContext)
                .load(mData.get(position).getbIcon())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy((DiskCacheStrategy.NONE))
                .into(img_bicon);
//        img_bicon.setBackgroundResource(mData.get(position).getbIcon());
//        img_icon.setBackgroundResource(mData.get(position).getaIcon());
        Glide.with(mContext)
                .load(mData.get(position).getaIcon())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy((DiskCacheStrategy.NONE))
                .into(img_icon);

        txt_aName.setText(mData.get(position).getaName());
        txt_aSpeak.setText(mData.get(position).getaSpeak());
        return convertView;
    }

}
