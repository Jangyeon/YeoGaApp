package com.example.leo.mainview.Database;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.leo.mainview.R;


/**
    Database에 정보가 알맞게 저장되었는지 확인하는 기능
    테스트가 끝나면 삭제.
 */
public class DatabaseTEST extends ListActivity {


    static final int NOT_SELECTED = -1;
    int listViewSelectedItemId = NOT_SELECTED;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.databasetest);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewSelectedItemId = position;
            }
        });

        loadData();
    }

    void loadData(){

        DbOpenHelper db = new DbOpenHelper(this);
        ArrayAdapter<Travel> adapter = new ArrayAdapter<Travel>(this, android.R.layout.simple_list_item_single_choice);



        adapter.addAll(db.list());

        this.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        this.setListAdapter(adapter);
    }
}
