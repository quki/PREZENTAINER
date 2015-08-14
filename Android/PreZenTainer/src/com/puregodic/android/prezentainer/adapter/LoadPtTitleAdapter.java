package com.puregodic.android.prezentainer.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.puregodic.android.prezentainer.R;

public class LoadPtTitleAdapter extends BaseAdapter{
    
    private ArrayList<LoadPtTitleData> loadPtTitleDataList;
    private Activity activity;
    private LayoutInflater inflater;
    

    public LoadPtTitleAdapter(Activity activity, ArrayList<LoadPtTitleData> loadPtTitleDataList) {
        this.activity = activity;
        this.loadPtTitleDataList = loadPtTitleDataList;
    }
    
    @Override
    public int getCount() {
        return loadPtTitleDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return loadPtTitleDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if(inflater == null)
            inflater =(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_items_load, null);
        
        
        TextView ptTitle = (TextView) convertView.findViewById(R.id.ptTitle);
        TextView currentTime = (TextView) convertView.findViewById(R.id.currentTime);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageSample);
 
        ptTitle.setText(loadPtTitleDataList.get(position).ptTitle);
        currentTime.setText(loadPtTitleDataList.get(position).currentTime);
        image.setImageResource(R.drawable.ic_launcher);
        
        
        
        return convertView;
    }

    
    
    
}
