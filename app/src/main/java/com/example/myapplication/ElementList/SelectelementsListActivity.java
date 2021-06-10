package com.example.myapplication.ElementList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Detection.DetectorActivity;
import com.example.myapplication.ElementList.Adapters.ListSelectitemAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.ElementDTO;
import com.example.myapplication.Models.Enums.ActionEventMain;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SelectelementsListActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> elements;
    private Button startSearching;
    private ListSelectitemAdapter listSelectitemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_elements);

        listView = findViewById(R.id.listElements);
        startSearching = findViewById(R.id.start);

        initializeData();
        setActionStartSearching();
    }

    private void setActionStartSearching() {
        startSearching.setOnClickListener(v -> {
            if(listSelectitemAdapter.getSelectedItem()>0)
            {
                Intent intent = new Intent(this, MainActivity.class);
                ElementDTO elementDTO = ListSelectitemAdapter.getElementDTO();
                intent.putExtra("Action", ActionEventMain.GET_INFO_LOCATION);
                intent.putExtra("Elements",  elementDTO);
                startActivity(intent);
                finish();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Select one or more item!")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                            //do things
//                        firstLoad = false;
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    private void initializeData() {
        elements = new ArrayList<>();
        elements.add("person");
        elements.add("bicycle");
        elements.add("car");
        elements.add("motorcycle");
        elements.add("airplane");
        elements.add("bus");
        elements.add("train");
        elements.add("truck");
        elements.add("boat");
        elements.add("traffic light");
        elements.add("fire hydrant");
        elements.add("stop sign");
        elements.add("parking meter");
        elements.add("bench");
        elements.add("bird");
        elements.add("cat");
        elements.add("dog");
        elements.add("horse");
        elements.add("sheep");
        elements.add("cow");
        elements.add("elephant");
        elements.add("bear");
        elements.add("zebra");
        elements.add("giraffe");
        elements.add("backpack");
        elements.add("umbrella");
        elements.add("handbag");
        elements.add("tie");
        elements.add("suitcase");
        elements.add("frisbee");
        elements.add("skis");
        elements.add("snowboard");
        elements.add("sports ball");
        elements.add("kite");
        elements.add("baseball bat");
        elements.add("baseball glove");
        elements.add("skateboard");
        elements.add("surfboard");
        elements.add("tennis racket");
        elements.add("bottle");
        elements.add("wine glass");
        elements.add("cup");
        elements.add("fork");
        elements.add("knife");
        elements.add("spoon");
        elements.add("bowl");
        elements.add("banana");
        elements.add("apple");
        elements.add("sandwich");
        elements.add("orange");
        elements.add("broccoli");
        elements.add("carrot");
        elements.add("hot dog");
        elements.add("pizza");
        elements.add("donut");
        elements.add("cake");
        elements.add("chair");
        elements.add("couch");
        elements.add("potted plant");
        elements.add("bed");
        elements.add("dining table");
        elements.add("toilet");
        elements.add("tv");
        elements.add("laptop");
        elements.add("mouse");
        elements.add("remote");
        elements.add("keyboard");
        elements.add("cell phone");
        elements.add("microwave");
        elements.add("oven");
        elements.add("toaster");
        elements.add("sink");
        elements.add("refrigerator");
        elements.add("book");
        elements.add("clock");
        elements.add("vase");
        elements.add("scissors");
        elements.add("teddy bear");
        elements.add("hair drier");
        elements.add("toothbrush");

        listSelectitemAdapter = new ListSelectitemAdapter(getApplicationContext(),
                R.layout.activity_element, elements,false,null);
        listView.setAdapter(listSelectitemAdapter);
    }
}
