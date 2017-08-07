package edu.kiet.www.blackbox.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.kiet.www.blackbox.Fragment.BusDetailsBottomSheet;
import edu.kiet.www.blackbox.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FloatingActionButton details;
    List<Address> address=new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    LatLng busposition;
    LatLng copy=new LatLng(0,0);
    DatabaseReference databaseReference;
    Intent i;
    String busid,busnumber;
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




        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("bus_details");
        Log.e("fhiswi","helloooo");
        BusDetailsBottomSheet.databaseReference=databaseReference;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot m:dataSnapshot.getChildren()) {
                    BusDetails b=m.getValue(BusDetails.class);
                    //Log.e("data",b.toString());
                    Log.e("IDS OF BUS",b.getBus_id());
                    if(b.getBus_id().equals(busid))
                    {
                        Log.e("hi","hoii");
                        busposition=new LatLng(Double.parseDouble(b.getLatitude()),Double.parseDouble(b.getLongitude()));
                        Log.e("busPos",busposition.toString());
                        //BusDetailsBottomSheet.speedBus=b.getBus_speed();
                        address_func();
                        break;
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void address_func()
    {
        Log.e("out of loop","yes");
        //LatLng x=new LatLng(23.234,111.123);
        Geocoder geocoder=new Geocoder(MapsActivity.this, Locale.getDefault());

        try {
            address= geocoder.getFromLocation(busposition.latitude,busposition.longitude,1);
            Log.e("Address",String.valueOf(address));
         // BusDetailsBottomSheet.add=address.get(0).getAddressLine(1);
            mMap.addMarker(new MarkerOptions().position(busposition).title(address.get(0).getAddressLine(0)).snippet(address.get(0).getAddressLine(1)));

        } catch (IOException e) {
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
