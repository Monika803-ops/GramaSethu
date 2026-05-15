package com.monika.gramasethu;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MapActivity extends FragmentActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        // NOTIFICATION PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        101);
            }
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        // FIREBASE REFERENCE
        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("BridgeReports");

        // NOTIFICATION CHANNEL
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            "bridge_alerts",
                            "Bridge Alerts",
                            NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription(
                    "Submerged Bridge Alerts");

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        databaseReference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        mMap.clear();

                        for(DataSnapshot dataSnapshot
                                : snapshot.getChildren()) {

                            String bridgeName =
                                    dataSnapshot.child("bridgeName")
                                            .getValue(String.class);

                            String village =
                                    dataSnapshot.child("village")
                                            .getValue(String.class);

                            String status =
                                    dataSnapshot.child("status")
                                            .getValue(String.class);

                            String notes =
                                    dataSnapshot.child("notes")
                                            .getValue(String.class);

                            String updatedTime =
                                    dataSnapshot.child("updatedTime")
                                            .getValue(String.class);

                            try {

                                Geocoder geocoder =
                                        new Geocoder(MapActivity.this);

                                List<Address> addressList =
                                        geocoder.getFromLocationName(
                                                village, 1);

                                if(addressList != null
                                        && !addressList.isEmpty()) {

                                    double lat =
                                            addressList.get(0)
                                                    .getLatitude();

                                    double lng =
                                            addressList.get(0)
                                                    .getLongitude();

                                    MarkerOptions markerOptions =
                                            new MarkerOptions()
                                                    .position(
                                                            new LatLng(lat, lng))
                                                    .title("Bridge: " + bridgeName)
                                                    .snippet(
                                                            "Tap to view full details");

                                    // MARKER COLORS
                                    if(status.equalsIgnoreCase("Open")) {

                                        markerOptions.icon(
                                                BitmapDescriptorFactory
                                                        .defaultMarker(
                                                                BitmapDescriptorFactory.HUE_GREEN));

                                    }

                                    else if(status.equalsIgnoreCase("Damaged")) {

                                        markerOptions.icon(
                                                BitmapDescriptorFactory
                                                        .defaultMarker(
                                                                BitmapDescriptorFactory.HUE_YELLOW));

                                    }

                                    else {

                                        markerOptions.icon(
                                                BitmapDescriptorFactory
                                                        .defaultMarker(
                                                                BitmapDescriptorFactory.HUE_RED));

                                    }

                                    // ADD MARKER
                                    Marker marker =
                                            mMap.addMarker(markerOptions);

                                    // SAVE DATA INSIDE MARKER
                                    marker.setTag(
                                            status + "|" +
                                                    bridgeName + "|" +
                                                    village + "|" +
                                                    notes + "|" +
                                                    updatedTime
                                    );

                                    mMap.moveCamera(
                                            CameraUpdateFactory
                                                    .newLatLngZoom(
                                                            new LatLng(lat, lng),
                                                            7));

                                }

                            }

                            catch (Exception e) {

                                e.printStackTrace();

                            }

                        }

                        // CLICK POPUP
                        mMap.setOnInfoWindowClickListener(marker -> {

                            String data =
                                    marker.getTag().toString();

                            String[] parts =
                                    data.split("\\|");

                            String status = parts[0];
                            String bridgeName = parts[1];
                            String village = parts[2];
                            String notes = parts[3];
                            String updatedTime = parts[4];

                            // SUBMERGED ALERT
                            if(status.equalsIgnoreCase("Submerged")) {

                                // WARNING SOUND
                                MediaPlayer mediaPlayer =
                                        MediaPlayer.create(
                                                MapActivity.this,
                                                R.raw.warning);

                                mediaPlayer.setLooping(false);

                                mediaPlayer.start();

                                // STOP SOUND AFTER 5 SECONDS
                                new android.os.Handler().postDelayed(() -> {

                                    if(mediaPlayer.isPlaying()) {

                                        mediaPlayer.stop();

                                        mediaPlayer.release();

                                    }

                                }, 5000);

                                // PUSH NOTIFICATION
                                NotificationCompat.Builder builderNotification =
                                        new NotificationCompat.Builder(
                                                MapActivity.this,
                                                "bridge_alerts")

                                                .setSmallIcon(
                                                        R.mipmap.ic_launcher)

                                                .setContentTitle(
                                                        "⚠ Grama-Sethu Alert")

                                                .setContentText(
                                                        "Submerged bridge near "
                                                                + village)

                                                .setPriority(
                                                        NotificationCompat.PRIORITY_HIGH)

                                                .setAutoCancel(true);

                                NotificationManagerCompat notificationManager =
                                        NotificationManagerCompat.from(
                                                MapActivity.this);

                                if (ActivityCompat.checkSelfPermission(
                                        MapActivity.this,
                                        Manifest.permission.POST_NOTIFICATIONS)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    return;
                                }

                                notificationManager.notify(
                                        101,
                                        builderNotification.build());

                            }

                            String fullDetails =

                                    "Bridge Name: "
                                            + bridgeName +

                                            "\n\nVillage: "
                                            + village +

                                            "\n\nStatus: "
                                            + status +

                                            "\n\nNotes: "
                                            + notes +

                                            "\n\nUpdated Time: "
                                            + updatedTime;

                            android.app.AlertDialog.Builder builder =
                                    new android.app.AlertDialog.Builder(
                                            MapActivity.this);

                            builder.setTitle(
                                    "Bridge Details");

                            builder.setMessage(
                                    fullDetails);

                            // OK BUTTON
                            builder.setPositiveButton(
                                    "OK", null);

                            // ALTERNATE ROUTE BUTTON
                            if(status.equalsIgnoreCase("Submerged")) {

                                builder.setNeutralButton(
                                        "Alternate Route",

                                        (dialog, which) -> {

                                            Uri gmmIntentUri =
                                                    Uri.parse(
                                                            "google.navigation:q="
                                                                    + village);

                                            Intent mapIntent =
                                                    new Intent(
                                                            Intent.ACTION_VIEW,
                                                            gmmIntentUri);

                                            mapIntent.setPackage(
                                                    "com.google.android.apps.maps");

                                            startActivity(mapIntent);

                                        });

                            }

                            builder.show();

                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }

                });

    }
}