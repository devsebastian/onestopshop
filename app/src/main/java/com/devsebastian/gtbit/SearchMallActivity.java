package com.devsebastian.gtbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchMallActivity extends AppCompatActivity {

    private ArrayList<String> mallIds = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    SearchMallAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mall);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchMallAdapter(this, new ArrayList<String>());
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryTxt) {
                Query query1 = databaseReference.child("malls").orderByChild("name").startAt( queryTxt ).endAt(queryTxt + "\uf8ff");
                query1.addValueEventListener(valueEventListener);
                Log.d("Query", "onQueryTextSubmit: " + queryTxt);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query1 = databaseReference.child("malls").orderByChild("name").startAt( newText ).endAt(newText + "\uf8ff");
                query1.addValueEventListener(valueEventListener);
                Log.d("Query", "onQueryTextSubmit: " + newText);

                return false;
            }
        });

        if(searchView.getQuery().toString().isEmpty()){
            databaseReference.child("malls").addValueEventListener(valueEventListener);
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mallIds = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                mallIds.add(snapshot.getKey());
                Log.d("SearchResult", "onDataChange: MAlL id " + snapshot.getKey());
            }
            adapter.setIterms(mallIds);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
