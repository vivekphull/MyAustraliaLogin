package com.pavi_developing.myaustralialogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;

/**
 * A simple {@link Fragment} subclass.
 */
public class CounsilInfoFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView listView;
    TextView textView;
    CounsilListAdapter counsilListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_counsil_info, container, false);
        listView=(ListView)view.findViewById(R.id.list);
        counsilListAdapter=new CounsilListAdapter(getActivity(),R.layout.counsil_list_layout);
        CounsilDetails counsilDetails=new CounsilDetails("SA Water","complaints@sawater.com.au","1300650950");
        counsilListAdapter.add(counsilDetails);
        CounsilDetails counsilDetail=new CounsilDetails("City of Adelaide","city@cityofadelaide.com.au","82037203");
        counsilListAdapter.add(counsilDetail);
        textView=(TextView)view.findViewById(R.id.email);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setAdapter(counsilListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
    }
}