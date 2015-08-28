package com.puregodic.android.prezentainer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.puregodic.android.prezentainer.R;

public class LoadPtTitleAdapter extends RecyclerView.Adapter<LoadPtTitleAdapter.MyViewHolder>{
    
    private ArrayList<LoadPtTitleData> loadPtTitleDataList;
    private LayoutInflater inflater;
    private Context context;
    
    // ������, �ʱ�ȭ �۾�
    public LoadPtTitleAdapter(Context context, ArrayList<LoadPtTitleData> loadPtTitleDataList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.loadPtTitleDataList = loadPtTitleDataList;
    }
    
    /*
     * ViewHolder��� !! 
     * Sub class�ν� list_items_load.xml�� �ִ� ��� View(����)�� inflate�Ѵ�.
     * �ѹ� ������ Ŭ������ ���� ���� Ŭ����(��)�� ������ �ٽ� �׼��� �� �� �ִ�.
     * 
     * */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title,date;
        public ImageView imageSample;
        
        // �����ڷν� inflate�� View�� �ʱ�ȭ �۾�
        public MyViewHolder(View v) {
            super(v);
            imageSample = (ImageView)v.findViewById(R.id.imageSample);
            title = (TextView)v.findViewById(R.id.loadPtTitle);
            date = (TextView)v.findViewById(R.id.loadCurrDate);
        }
    }

    // list_items_load.xml�� inflate�ϰ� MyViewHolder��ü�� return�Ѵ� (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {

        View v = inflater.inflate(R.layout.list_items_load, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    // View�� ��� Content���� ���� (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        
        holder.imageSample.setImageResource(R.drawable.ic_gmail);
        holder.title.setText(loadPtTitleDataList.get(position).getTitle());
        holder.date.setText(loadPtTitleDataList.get(position).getDate());
    }

    // ArrayList<LoadPtTitleData> �� ��ü ���� return
    @Override
    public int getItemCount() {
        return loadPtTitleDataList.size();
    }
    
}
