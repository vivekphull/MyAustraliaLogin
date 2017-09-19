package com.pavi_developing.myaustralialogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp-core.i5 on 17-08-2017.
 */

public class CounsilListAdapter extends ArrayAdapter {
    List list=new ArrayList();
    public CounsilListAdapter(Context context, int resource) {
        super(context, resource);
    }


    public void add(CounsilDetails object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row=convertView;
        contactholder cholder;
        if(row==null){
            LayoutInflater layoutInflater=(LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.counsil_list_layout,parent,false);
            cholder=new contactholder();
            cholder.tx_name=(TextView)row.findViewById(R.id.name);
            cholder.tx_email=(TextView)row.findViewById(R.id.email);
            cholder.tx_phone=(TextView)row.findViewById(R.id.phone);
            row.setTag(cholder);
        }
        else{
            cholder=(contactholder)row.getTag();
        }
        CounsilDetails counsilDetails=(CounsilDetails) this.getItem(position);
        cholder.tx_name.setText(counsilDetails.getName());
        cholder.tx_email.setText(counsilDetails.getEmail());
        cholder.tx_phone.setText(counsilDetails.getPhone());
        return row;
    }

    static class contactholder{
        TextView tx_name,tx_email,tx_phone;

    }

}

