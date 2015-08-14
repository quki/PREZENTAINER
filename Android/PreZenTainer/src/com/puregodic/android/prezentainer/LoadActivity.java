
package com.puregodic.android.prezentainer;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.puregodic.android.prezentainer.adapter.LoadPtTitleAdapter;
import com.puregodic.android.prezentainer.adapter.LoadPtTitleData;

public class LoadActivity extends AppCompatActivity {
    
    private ListView listViewLoad;
    private LoadPtTitleAdapter adapter;
    private LoadPtTitleData data; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        
        data =  new LoadPtTitleData( "프레젠테이션1", "2015-08-13-21:00");
        
        ArrayList<LoadPtTitleData> al = new ArrayList<LoadPtTitleData>();
        al.add(data);
        adapter = new LoadPtTitleAdapter(this, al);
        
        listViewLoad = (ListView)findViewById(R.id.listViewLoad);
        listViewLoad.setAdapter(adapter);
        
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.load, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
