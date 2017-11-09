package edu.kiet.www.blackbox.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import edu.kiet.www.blackbox.R;
import edu.kiet.www.blackbox.ui.ColoredSnackbar;
import edu.kiet.www.blackbox.util.NetworkCheck;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton login;
    TextInputLayout userLayout,passLayout;
    EditText username,password,searchBus;
    String name,pass;
    List<String> ids=new ArrayList<String>();
    List<String> numb=new ArrayList<String>();
    ProgressDialog progressDialog;
    int a;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter spinnerAdapter;
    Bundle bus=new Bundle();
    String text;
    private boolean doubleBackToExitPressedOnce = false;
    private ColoredSnackbar coloredSnackBar;
    String busId;
    int flag=0;
    LinearLayout credentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=(AppCompatButton)findViewById(R.id.btn_login);
        credentials=(LinearLayout)findViewById(R.id.credentials);
        userLayout=(TextInputLayout)findViewById(R.id.input_user_layout);
        passLayout=(TextInputLayout)findViewById(R.id.input_password_layout);
       // busLayout=(TextInputLayout)findViewById(R.id.searchBusLayout);
        autoCompleteTextView=(AutoCompleteTextView) findViewById(R.id.search);
        //progressDialog=new ProgressDialog(LoginActivity.this);
         FirebaseDatabase mFirebaseDatabase;
         DatabaseReference busIdReference;

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        //searchBus=(EditText)findViewById(R.id.searchBus);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        busIdReference=mFirebaseDatabase.getReference().child("bus_id");
        busIdReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot m:dataSnapshot.getChildren()) {
                    BusIds b=m.getValue(BusIds.class);
                    ids.add(b.getBus_id());
                    numb.add(b.getBus_name());


                    //Log.e("Bus Id",b.getBus_id());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e("ids",ids.toString());
        Log.e("number",numb.toString());
        spinnerAdapter=new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,numb);
        autoCompleteTextView.setAdapter(spinnerAdapter);
        autoCompleteTextView.setThreshold(1);





        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkCheck.isNetworkAvailable(LoginActivity.this)) {

                    Snackbar snackbar =  Snackbar.make(findViewById(android.R.id.content), R.string.network_error, Snackbar.LENGTH_SHORT);
                    coloredSnackBar.alert(snackbar).show();
                    return;
                }
                if(flag==0) {
                    flag=1;
                    credentials.setVisibility(View.VISIBLE);
                    login.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                    login.setText("Submit");
                }
               else if(flag==1)
                {
                    userLayout.setErrorEnabled(false);
                    passLayout.setErrorEnabled(false);
                    if(username.getText().toString().equals("")||password.getText().toString().equals("")) {
                        if(username.getText().toString().equals(""))
                            userLayout.setError("Enter Username");
                        if(password.getText().toString().equals(""))
                            passLayout.setError("Enter Password");
                    }
                    else
                    {
                        name=username.getText().toString();
                        pass=password.getText().toString();

                        if(name.equals("test")&&pass.equals("one"))
                        {

                                Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                credentials.setVisibility(View.GONE);
                                autoCompleteTextView.setVisibility(View.VISIBLE);
                                login.setText("Track this Bus");
                                flag=2;




                        }
                        else
                        {
                            Snackbar snackbar =  Snackbar.make(findViewById(android.R.id.content), R.string.invalid_credentials, Snackbar.LENGTH_SHORT);
                            coloredSnackBar.alert(snackbar).show();
                        }
                    }
                }
                else if(flag==2)
                {
               String s=autoCompleteTextView.getText().toString();
                    int ind=numb.indexOf(s);
                    Log.e("Bus number here",s);
                    Log.e("INDEX",String.valueOf(ind));
                    bus.putString("bus_id",ids.get(ind));
                    bus.putString("bus_number",s);
                    Log.e("busss",bus.toString());


                    Intent i=new Intent(LoginActivity.this, MapsActivity.class);
                    i.putExtras(bus);
                    startActivity(i);
                }
            }
        });
    }


}
