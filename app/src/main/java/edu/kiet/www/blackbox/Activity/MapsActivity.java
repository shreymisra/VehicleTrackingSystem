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

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBSAie3Ux8f-f0RhIksE7vLDXXul685pls&alternatives=true&traffic_model=best_guess&departure_time=now&mode=driving";


        return url;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            re=result;
            ParserTask2 parserTask2 = new ParserTask2();

            // Invokes the thread for parsing the JSON data
            parserTask2.execute(result);
            Log.e("drt",String.valueOf(result));

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                Log.e("ddd",String.valueOf(jsonData));
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject,position);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask2",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            mMap.clear();

            mMap.addMarker(new MarkerOptions().position(MarkerPoints.get(0)).title(""));
            mMap.addMarker(new MarkerOptions().position(MarkerPoints.get(1)).title(""));
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Col   or.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    private class ParserTask2 extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        List<String> sumary=new ArrayList<String>();
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                Log.e("ddd",String.valueOf(jsonData));
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                Data parser = new Data();
                Log.d("ParserTask", parser.toString());

                routeDetails = parser.parse(jObject,0,MapsActivity.this);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask2",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

        }
    }

}
