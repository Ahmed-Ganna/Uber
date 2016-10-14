package com.ganna.uber.communication;


import com.ganna.uber.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ahmed on 01/08/2016.
 */

public class FireManager {

    public static DatabaseReference getDriverLocationNode(){
        return FirebaseDatabase.getInstance().getReference().child(Constants.DRIVER_LOCATION_NODE);
    }

    public static DatabaseReference getRideNode() {
        return FirebaseDatabase.getInstance().getReference().child(Constants.RIDE_NODE);
    }

    public static FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
