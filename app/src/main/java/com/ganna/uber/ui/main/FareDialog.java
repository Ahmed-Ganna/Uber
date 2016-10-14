package com.ganna.uber.ui.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ganna.uber.Constants;
import com.ganna.uber.R;
import com.ganna.uber.communication.FireManager;
import com.ganna.uber.communication.FireRequests;
import com.ganna.uber.util.VolleySingleton;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FareDialog extends DialogFragment {


    @BindView(R.id.fare_tv)TextView fareTv;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fare, null);
        ButterKnife.bind(this, view);
        getFareValue();
        builder.setView(view);
        return builder.create();
    }

    @OnClick(R.id.accept_btn)void acceptFare(){
        getDialog().cancel();
        FireRequests.sendRideRequest(new LatLng(getArguments().getDouble("lat"),getArguments().getDouble("lng")), FireManager.getUser().getDisplayName());
    }

    private void getFareValue(){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Constants.FARE_END_URL,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("fare", "onResponse: " + response);
                        try {
                            fareTv.setText(response.getString("fare")+"x");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("fare", "onErrorResponse: ", error.getCause());
            }
        });
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjReq);
    }

}