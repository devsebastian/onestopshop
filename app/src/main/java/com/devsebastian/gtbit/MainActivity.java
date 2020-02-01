package com.devsebastian.gtbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int i = 0;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ImageView saveLocationBtn, lastSavedSpotBtn, sosBtn, recentBills;

    String shopName;
    private Double userLat = 0d, userLng = 0d;
    private FusedLocationProviderClient client;
    RecyclerView recyclerView;


    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                if (result.getContents().startsWith("mall/")) {
                    stuff((result.getContents().replace("mall/", "")));
                    FirebaseDatabase.getInstance().getReference().child("malls").child(result.getContents().replace("mall/", "")).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            getSupportActionBar().setTitle(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Intent intent = new Intent(MainActivity.this, BillingActivity.class);
                    intent.putExtra("data", result.getContents());
                    startActivity(intent);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void fadeIn(View v) {
        v.setAlpha(0f);
        v.setVisibility(View.VISIBLE);
        v.animate()
                .alpha(0.7f)
                .setDuration(200)
                .setListener(null);
    }

    private void fadeOut(final View v) {
        final int delay = 200;
        v.setAlpha(0.7f);
        v.animate()
                .alpha(0f)
                .setDuration(delay)
                .setListener(null);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                v.setVisibility(View.GONE);
            }
        }, delay);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        databaseReference.child("alarm").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()) {
//                    MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ring);
//                    mediaPlayer.start();
//                    Toast.makeText(MainActivity.this, "Help Help", Toast.LENGTH_LONG).show();
//                }
//            }
//w
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        findViewById(R.id.locate_me_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });

        findViewById(R.id.blackscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        findViewById(R.id.blackscreen).setVisibility(View.GONE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        fadeOut(findViewById(R.id.blackscreen));
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        fadeIn(findViewById(R.id.blackscreen));
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.search_mall)
                    startActivity(new Intent(MainActivity.this, SearchMallActivity.class));
                return false;
            }
        });


        client = LocationServices.getFusedLocationProviderClient(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });

        saveLocationBtn = findViewById(R.id.save_location_btn);
        lastSavedSpotBtn = findViewById(R.id.find_last_spot);
        sosBtn = findViewById(R.id.sos_btn);
        recentBills = findViewById(R.id.recent_bills);

        recentBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecentBillsActivity.class));
            }
        });

        sosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long date = System.currentTimeMillis();
                databaseReference.child("alarm").setValue(true);

                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        databaseReference.child("sosDetails").child("1").child("date").setValue(date);
                        databaseReference.child("sosDetails").child("1").child("lat").setValue(location.getLatitude());
                        databaseReference.child("sosDetails").child("1").child("lng").setValue(location.getLongitude());

                        startActivity(new Intent(MainActivity.this, SOSActivity.class));
                    }
                });
            }
        });

        saveLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("Location", "onSuccess: Latitude " + location.getLatitude());
                            userLat = location.getLatitude();
                            userLng = location.getLongitude();
                            databaseReference.child("parkedLocation").child("1").child("lat").setValue(userLat);
                            databaseReference.child("parkedLocation").child("1").child("lng").setValue(userLng);
                            toggleBottomSheet();
                        }
                    }
                });

            }
        });

        databaseReference.child("parkedLocation").child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    lastSavedSpotBtn.setVisibility(View.GONE);
                } else {
                    lastSavedSpotBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lastSavedSpotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });


        recyclerView = findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        stuff("1");

    }

    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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
