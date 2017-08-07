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

    List<Address> addressList=new ArrayList<>();
    TextView busNo;
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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot m:dataSnapshot.getChildren()) {
                    BusDetails b=m.getValue(BusDetails.class);
                    if(b.getBus_id().equals(busId)){
                        Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());
                        try{
                            String s="";
                            addressList= geocoder.getFromLocation(Double.parseDouble(b.getLatitude()),Double.parseDouble(b.getLongitude()),1);
                            address.setText(addressList.get(0).getAddressLine(0)+"\n"+addressList.get(0).getAddressLine(1)+"\n"+addressList.get(0).getAddressLine(2)+"\n"+addressList.get(0).getAddressLine(3));
                        }
                        catch (Exception e){
                            e.printStackTrace();
                           // Toast.makeText(this, "Location Not found", Toast.LENGTH_SHORT).show();
                        }
                        speed.setText(b.getBus_speed());
                        busNo.setText(busNumber);


                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
