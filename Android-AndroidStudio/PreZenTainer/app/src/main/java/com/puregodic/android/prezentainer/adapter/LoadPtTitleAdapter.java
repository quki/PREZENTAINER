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
    
    // 생성자, 초기화 작업
    public LoadPtTitleAdapter(Context context, ArrayList<LoadPtTitleData> loadPtTitleDataList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.loadPtTitleDataList = loadPtTitleDataList;
    }
    
    /*
     * ViewHolder사용 !! 
     * Sub class로써 list_items_load.xml에 있는 모든 View(위젯)를 inflate한다.
     * 한번 생성한 클래스를 통해 서브 클래스(뷰)를 빠르게 다시 액세스 할 수 있다.
     * 
     * */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title,date;
        public ImageView imageSample;
        
        // 생성자로써 inflate된 View의 초기화 작업
        public MyViewHolder(View v) {
            super(v);
            imageSample = (ImageView)v.findViewById(R.id.imageSample);
            title = (TextView)v.findViewById(R.id.loadPtTitle);
            date = (TextView)v.findViewById(R.id.loadCurrDate);
        }
    }

    // list_items_load.xml을 inflate하고 MyViewHolder객체를 return한다 (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {

        View v = inflater.inflate(R.layout.list_items_load, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    // View에 모든 Content들을 셋팅 (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        
        holder.imageSample.setImageResource(R.drawable.ic_gmail);
        holder.title.setText(loadPtTitleDataList.get(position).getTitle());
        holder.date.setText(loadPtTitleDataList.get(position).getDate());
    }

    // ArrayList<LoadPtTitleData> 의 전체 길이 return
    @Override
    public int getItemCount() {
        return loadPtTitleDataList.size();
    }
    
}
