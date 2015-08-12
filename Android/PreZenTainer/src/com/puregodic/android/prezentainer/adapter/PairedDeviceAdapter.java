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


public class PairedDeviceAdapter extends BaseAdapter{
    
    private LayoutInflater inflater;
    private ArrayList<PairedDeviceData> pairedDevicesList ;
    private Activity activity;
    
    public PairedDeviceAdapter(Activity activity, ArrayList<PairedDeviceData> pairedDevicesList){
        this.activity = activity;
        this.pairedDevicesList = pairedDevicesList;
    }
    
    
    
    @Override
    public int getCount() {
        return pairedDevicesList.size();
    }

    @Override
    public Object getItem(int position) {
        return pairedDevicesList.get(position);
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
            convertView = inflater.inflate(R.layout.list_items_paired, null);
        
        
        TextView textViewPairedName = (TextView) convertView.findViewById(R.id.textViewPairedName);
        TextView textViewPairedAdress = (TextView) convertView.findViewById(R.id.textViewPairedAdress);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
 
        textViewPairedName.setText(pairedDevicesList.get(position).name);
        textViewPairedAdress.setText(pairedDevicesList.get(position).adress);
        image.setImageResource(R.drawable.ic_pc);
        
        return convertView;
    }

}
