package edu.kiet.www.blackbox.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import edu.kiet.www.blackbox.Fragment.BusDetailsBottomSheet;
import edu.kiet.www.blackbox.R;
import edu.kiet.www.blackbox.util.Data;
import edu.kiet.www.blackbox.util.DataParser;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    FloatingActionButton details;
    List<Address> address=new ArrayList<>();

    FirebaseDatabase firebaseDatabase;
    LatLng busposition;
    LatLng copy=new LatLng(0,0);
    List<String>  list_of_keys=new ArrayList<>();
    List<String>  list_of_values=new ArrayList<>();
    DatabaseReference databaseReference;
    FirebaseDatabase key;
    DatabaseReference keyReference;
    Intent i;
    String busid,busnumber,busKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        i=getIntent();
        busid=i.getStringExtra("bus_id");
        busnumber=i.getStringExtra("bus_number");

        BusDetailsBottomSheet.busId=busid;
        BusDetailsBottomSheet.busNumber=busnumber;
        details=(FloatingActionButton)findViewById(R.id.fab);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusDetailsBottomSheet busDetails=new BusDetailsBottomSheet();
                busDetails.show(getSupportFragmentManager(),"Details");
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        key=FirebaseDatabase.getInstance();
        keyReference=key.getReference().child("bus_details");
        keyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    BusDetails bd=ds.getValue(BusDetails.class);
                    if(bd.getBus_id().equals(busid))
                    {
                        busKey=ds.getKey();
                        Log.e("BUSKEY",busKey);
                        key_func();
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void key_func()
    {
        Log.e("key of bus",busKey);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("bus_details/"+busKey);
        BusDetailsBottomSheet.databaseReference=databaseReference;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_of_values=new ArrayList<String>();
                list_of_keys=new ArrayList<String>();

                for(DataSnapshot m:dataSnapshot.getChildren()) {


/*Log.e("data",m.toString());
                    String getkeywala=m.getKey();
                    Log.e("getKey",getkeywala);
                    String getvaluewala=m.getValue().toString();
                    Log.e("getValue",getvaluewala);*/

                    list_of_keys.add(m.getKey());
                    list_of_values.add(m.getValue().toString());
                }
                    Log.e("keys",String.valueOf(list_of_keys));
                    Log.e("values",String.valueOf(list_of_values));

                   // BusDetails b=m.getValue(BusDetails.class);
                   // Log.e("IDS OF BUS",b.getBus_id());
                    if(list_of_values.get(list_of_keys.indexOf("bus_id")).equals(busid))
                    {
                        Log.e("inside if","yes");
                        busposition=new LatLng(Double.parseDouble(list_of_values.get(list_of_keys.indexOf("latitude"))),Double.parseDouble(list_of_values.get(list_of_keys.indexOf("longitude"))));
                        Log.e("busPos",busposition.toString());
                        //BusDetailsBottomSheet.speedBus=b.getBus_speed();
                        address_func();

                    }

                Log.e("KEYS--->>",list_of_keys.toString());
                Log.e("VALUES--->>",list_of_values.toString());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    public void address_func()
    {
        //LatLng x=new LatLng(23.234,111.123);
        Log.e("out of loop","yes");
        Geocoder geocoder=new Geocoder(MapsActivity.this, Locale.getDefault());

        try {
            Log.e("lattitude",String.valueOf(busposition.latitude));
            Log.e("longitude",String.valueOf(busposition.longitude));
            address= geocoder.getFromLocation(busposition.latitude,busposition.longitude,1);
            Log.e("Address",String.valueOf(address));
            // jsonArray=address.toArray();
            // BusDetailsBottomSheet.add=address.get(0).getAddressLine(1);


            mMap.addMarker(new MarkerOptions().position(busposition).title(address.get(0).getAddressLine(0)).snippet(address.get(0).getAddressLine(1)));

        } catch (IOException e) {
            Log.e("bbbii","catch");
            e.printStackTrace();
            Toast.makeText(this, "Location Not found", Toast.LENGTH_SHORT).show();
        }
        // mMap.addMarker(new MarkerOptions().position(busposition).title("Current Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busposition,50));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }


}
