package com.ganna.uber.communication;

import android.util.Log;

import com.ganna.uber.Constants;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Ahmed on 10/13/2016.
 */

public class FireRequests {

    public static void sendRideRequest(LatLng latLng,String name) {
        Log.d("Name", name);
        HashMap updateMap = new HashMap<>();
        updateMap.put(Constants.RIDE_STATUS_KEY, Constants.STATUS_PICK_REQUESTED);
        updateMap.put(Constants.RIDE_RIDER_NAME, name);
        updateMap.put(Constants.PICKED_LATLNG+"/lat",latLng.latitude);
        updateMap.put(Constants.PICKED_LATLNG + "/lng", latLng.longitude);
        FireManager.getRideNode().
                updateChildren(updateMap);
    }
}
