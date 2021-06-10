package com.example.myapplication.ElementList.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Data.Local.FlowData;
import com.example.myapplication.Models.ElementDTO;
import com.example.myapplication.Models.Enums.IconItemState;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListSelectitemAdapter extends ArrayAdapter<String> {

    private Context context;
    private int mResource;
    private static ElementDTO elementDTO;
    private static List<Boolean> saveStates;
    private static List<IconItemState> findIconPreviewState;
    private static int selectedItem = 0;
    private static boolean isPreview;

    public ListSelectitemAdapter(Context context, int mResource, List<String> elements, boolean isPreviewFlag, List<IconItemState> iconItemStates)
    {
        super(context, mResource, elements);
        this.context = context;
        this.mResource = mResource;
        saveStates = new ArrayList<>(Arrays.asList(new Boolean[elements.size()]));
        Collections.fill(saveStates, Boolean.FALSE);
        findIconPreviewState = new ArrayList<IconItemState>(Arrays.asList(new IconItemState[elements.size()]));
        Collections.fill(findIconPreviewState, IconItemState.SEARCHING);
        elementDTO = new ElementDTO();
        isPreview = isPreviewFlag;
        findIconPreviewState = iconItemStates;
    }

    public int getSelectedItem()
    {
        return selectedItem;
    }

    @NonNull
    @Override
    public View getView(int poz, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.activity_element, parent, false);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        if(!isPreview)
        {

            checkBox.setText(getItem(poz));
            checkBox.setChecked(saveStates.get(poz));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    List<String> list = elementDTO.getSelectItems();
                    saveStates.set(poz,isChecked);
                    if(isChecked)
                    {

                        selectedItem++;
                        list.add(getItem(poz));
                        elementDTO.setSelectItems(list);

                    }
                    else
                    {

                        selectedItem--;
                        list.remove(list.indexOf(getItem(poz)));
                        elementDTO.setSelectItems(list);
                    }
                }
            });
        }
        else
        {
//            checkBox.setVisibility(View.GONE);
            checkBox.setText(getItem(poz));
            checkBox.setButtonDrawable(null);
            ImageView imageView = convertView.findViewById(R.id.stateIcon);

            if(findIconPreviewState.get(poz) != IconItemState.SEARCHING)
            {
                if(findIconPreviewState.get(poz) == IconItemState.FOUND)
                    imageView.setImageResource(R.drawable.ic_check_mark);
                else
                    imageView.setImageResource(R.drawable.ic_close);
                imageView.setVisibility(View.VISIBLE);
            }

        }



        return convertView;
    }

    public static ElementDTO getElementDTO() {
        return elementDTO;
    }
}
