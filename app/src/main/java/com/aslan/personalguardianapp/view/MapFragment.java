package com.aslan.personalguardianapp.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aslan.personalguardianapp.R;
import com.aslan.personalguardianapp.util.MyLocation;
import com.aslan.personalguardianapp.util.XMLreaderLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    private GoogleMap mMap;
    private MarkerOptions markerOption;
    private Marker marker;
    private XMLreaderLocation locationReader;
    private String country;
    private double currLat, currLon;
    private ProgressDialog ringProgressDialog;

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent curr = getActivity().getIntent();
//        Bundle bundle = curr.getExtras();
//        if (bundle != null) {
//            country = bundle.getString("Country");
//            currLat = bundle.getDouble("Lat");
//            currLon = bundle.getDouble("Lon");
//        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            country = bundle.getString("Country");
            currLat = bundle.getDouble("Lat");
            currLon = bundle.getDouble("Lon");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.map)).getMap();
//        mMap = ((SupportMapFragment) FragmentActivity.getSupportFragmentManager()
//                .findFragmentById(R.id.map)).getMap();
        // googleMapOptions = new GoogleMapOptions();
        if (mMap != null) {
            LatLng latLng = new LatLng(currLat, currLon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    mMap.getMaxZoomLevel() - 5));
            markerOption = new MarkerOptions()
                    .position(latLng)
                    .title(String.format("%.4f", latLng.latitude) + ", "
                            + String.format("%.4f", latLng.longitude))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_location_on_red_900_24dp));
            marker = mMap.addMarker(markerOption);
            marker.showInfoWindow();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng result) {
                    // TODO Auto-generated method stub
                    marker.setPosition(result);
                    marker.setTitle(String.format("%.4f", result.latitude)
                            + ", " + String.format("%.4f", result.longitude));
                    marker.showInfoWindow();
                }
            });

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng result) {
                    // TODO Auto-generated method stub
                    marker.setPosition(result);
                    marker.setTitle(String.format("%.4f", result.latitude)
                            + ", " + String.format("%.4f", result.longitude));
                    marker.showInfoWindow();

                    ringProgressDialog = ProgressDialog.show(getContext(), "Please wait...",
                            "Retrieving location detail from the internet...", true);
                    ringProgressDialog.setCancelable(false);

                    String URL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng="
                            + result.latitude
                            + ","
                            + result.longitude
                            + "&key=AIzaSyCyc9xJr_8wXxrmjeadKVhpVp84nkleoyE";

                    locationReader = new XMLreaderLocation();
                    new XmlReader().execute(URL);
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker result) {
                    // TODO Auto-generated method stub

                    ringProgressDialog = ProgressDialog.show(getContext(), "Please wait...",
                            "Retrieving location detail from the internet...", true);
                    ringProgressDialog.setCancelable(false);

                    LatLng latlng = result.getPosition();
                    String URL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng="
                            + latlng.latitude
                            + ","
                            + latlng.longitude
                            + "&key=AIzaSyCyc9xJr_8wXxrmjeadKVhpVp84nkleoyE";

                    locationReader = new XMLreaderLocation();
                    new XmlReader().execute(URL);
                    return false;
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker result) {
                    // TODO Auto-generated method stub
                    LatLng latlng = result.getPosition();
                    result.setTitle(String.format("%.4f", latlng.latitude)
                            + ", " + String.format("%.4f", latlng.longitude));
                    result.showInfoWindow();
                }

                @Override
                public void onMarkerDragEnd(Marker result) {
                    // TODO Auto-generated method stub
                    LatLng latlng = result.getPosition();
                    result.setTitle(String.format("%.4f", latlng.latitude)
                            + ", " + String.format("%.4f", latlng.longitude));
                    result.showInfoWindow();
                }

                @Override
                public void onMarkerDrag(Marker result) {
                    // TODO Auto-generated method stub
                    LatLng latlng = result.getPosition();
                    result.setTitle(String.format("%.4f", latlng.latitude)
                            + ", " + String.format("%.4f", latlng.longitude));
                    result.showInfoWindow();
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker result) {
                    // TODO Auto-generated method stub
                    ringProgressDialog = ProgressDialog.show(getContext(), "Please wait...",
                            "Retrieving location detail from the internet...", true);
                    ringProgressDialog.setCancelable(false);

                    LatLng latlng = result.getPosition();
                    String URL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng="
                            + latlng.latitude
                            + ","
                            + latlng.longitude
                            + "&key=AIzaSyCyc9xJr_8wXxrmjeadKVhpVp84nkleoyE";

                    locationReader = new XMLreaderLocation();
                    new XmlReader().execute(URL);
                }
            });
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class XmlReader extends AsyncTask<String, Void, List<MyLocation>> {
        @Override
        protected List<MyLocation> doInBackground(String... urls) {
            return locationReader.readURL(urls[0]);
        }

        @Override
        protected void onPostExecute(List<MyLocation> result) {
            if (!result.isEmpty() && result != null) {
                Intent tracker = new Intent(getContext(), TrackingActivity.class);
                tracker.putExtra("Destination Latitude", result.get(0)
                        .getLattitude());
                tracker.putExtra("Destination Longitude", result.get(0)
                        .getLongitude());
                tracker.putExtra("Address", result.get(0).getAddress());
                ringProgressDialog.dismiss();
                startActivity(tracker);
            } else {
                // invoked when no data received due to error in internet
                // connection
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.internet_error_msg)
                        .setTitle("Unable to retrive data from internet")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }
}
