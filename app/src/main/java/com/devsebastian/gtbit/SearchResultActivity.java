package com.devsebastian.gtbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    int i=0;

    RecyclerView recyclerView;
    String shopName;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        stuff(getIntent().getStringExtra("mallId"));

    }
    void stuff(String id) {
        final FeedAdapter feedAdapter = new FeedAdapter(this, new ArrayList<FeedRow>());

        final ArrayList<FeedRow> feedRows = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("shops").child(id).addValueEventListener(new ValueEventListener() {

            ArrayList<String> shopnames = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) { // loop 1
                    shopName = dataSnapshot.child("name").getValue(String.class);
                    if (!shopnames.contains(shopName)) shopnames.add(shopName);
                    final ArrayList<Item> items = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.child("items").getChildren()) {   // loop 2
                        final Integer id = snapshot.child("id").getValue(Integer.class);
                        final String deal = snapshot.child("deal").getValue(String.class);

                        FirebaseDatabase.getInstance().getReference().child("items").child(id + "").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Item item = new Item();
                                item.setId(id);
                                item.setDeal(deal);
                                Log.d("devishan", item.getId() + "");
                                Log.d("devishan", item.getDeal() + "");
                                Log.d("devishan", shopName + "");
                                item.setTitle(dataSnapshot.child("name").getValue(String.class));
                                item.setCost(dataSnapshot.child("Price").getValue(Integer.class));
                                item.setImgUrl(dataSnapshot.child("imgurl").getValue(String.class));
                                items.add(item);
                                if (items.size() >= 5) { // jugaad
                                    if (i >= shopnames.size()) i = 0;
                                    feedRows.add(new FeedRow(shopnames.get(i++), items));
                                    feedAdapter.setItems(feedRows);
                                    feedAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.setAdapter(feedAdapter);
    }

}
