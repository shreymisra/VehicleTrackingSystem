package edu.kiet.www.blackbox.Fragment;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.kiet.www.blackbox.Activity.BusDetails;
import edu.kiet.www.blackbox.Activity.MapsActivity;
import edu.kiet.www.blackbox.R;

/**
 * Created by shrey on 2/8/17.
 */

public class BusDetailsBottomSheet extends BottomSheetDialogFragment {
// public static  String add="",speedBus="";
    public static String busId,busNumber;
 TextView address;
    TextView speed;
    List<String>  list_of_keys=new ArrayList<>();
    List<String>  list_of_values=new ArrayList<>();

    List<Address> addressList=new ArrayList<>();
    TextView busNo,driverName,driverNo,alcoholic_state;
    FirebaseDatabase firebaseDatabase;
   public static DatabaseReference databaseReference;

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        //super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.details_bottomsheet, null);
        dialog.setContentView(contentView);

        address=(TextView)contentView.findViewById(R.id.add);
        speed=(TextView)contentView.findViewById(R.id.speed);
        busNo=(TextView)contentView.findViewById(R.id.busNo);
        driverName=(TextView)contentView.findViewById(R.id.driverName);
        driverNo=(TextView)contentView.findViewById(R.id.driverNo);
        alcoholic_state=(TextView)contentView.findViewById(R.id.alcoholic_state);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_of_keys=new ArrayList<String>();
                list_of_values=new ArrayList<String>();
                for (DataSnapshot m:dataSnapshot.getChildren()) {

                    list_of_keys.add(m.getKey());
                    list_of_values.add(m.getValue().toString());
                }
//                    BusDetails b=m.getValue(BusDetails.class);
                    if(list_of_values.get(list_of_keys.indexOf("bus_id")).equals(busId)){
                        Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());
                        try{
                            addressList= geocoder.getFromLocation(Double.parseDouble(list_of_values.get(list_of_keys.indexOf("latitude"))),Double.parseDouble(list_of_values.get(list_of_keys.indexOf("longitude"))),1);
                            int size=addressList.get(0).getMaxAddressLineIndex();
                            String s="";
                            for(int i=0;i<=size;i++)
                                s=s+addressList.get(0).getAddressLine(i)+"\n";
                            address.setText(s);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                           // Toast.makeText(this, "Location Not found", Toast.LENGTH_SHORT).show();
                        }
                        speed.setText(list_of_values.get(list_of_keys.indexOf("bus_speed")));
                        busNo.setText(busNumber);
                        //passengers.setText("60");
                        driverName.setText("Chandra Kant");
                        driverNo.setText("9855321298");
                        alcoholic_state.setText(list_of_values.get(list_of_keys.indexOf("alcohol_status")));



                    }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
