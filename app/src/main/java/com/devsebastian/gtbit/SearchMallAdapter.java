package com.devsebastian.gtbit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchMallAdapter extends RecyclerView.Adapter<SearchMallAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public int setIterms(ArrayList<String> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTv, addressTv;
        ImageView icon;

        public MyViewHolder(View itemView){
            super(itemView);
            nameTv = itemView.findViewById(R.id.item_title);
            addressTv = itemView.findViewById(R.id.item_quantity);
            icon = itemView.findViewById(R.id.item_icon);
        }
    }

    public SearchMallAdapter(Context context, ArrayList<String> dataSet){
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_mall_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }
    private String capitalize(String capString){
        if(capString != null) {
            return capString.toUpperCase();
        }else {
            return "";
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final String mallId = dataSet.get(position);
        Glide.with(context).load("http://lorempixel.com/400/200/city").into(holder.icon);

        databaseReference.child("malls").child(mallId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.nameTv.setText(capitalize(dataSnapshot.child("name").getValue(String.class)));
                holder.addressTv.setText(dataSnapshot.child("address").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context,SearchResultActivity.class).putExtra("mallId",mallId));
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
