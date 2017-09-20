package net.aprille.bloissavoirecouter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import models.Location;

public class AddPlaceActivity extends AppCompatActivity {

    TextView placeNameText;
    TextView placeAddressText;
    TextView placeIDText;
    TextView placeLongLat;
    WebView attributionText;
    Button getPlaceButton;


    Place place;

    final Context context = this;
    int REQUESTCODE;

    Realm realm;
    Location thislocation;
    String callingQuadID;

    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String DirectoryFinal;

    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int PLACE_PICKER_REQUEST = 1;
    private final static LatLngBounds bounds = new LatLngBounds(new LatLng(47.536636,1.257858), new LatLng(47.623715,1.364288));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }

       requestPermission();

        placeNameText = (TextView) findViewById(R.id.tvPlaceName);
        placeAddressText = (TextView) findViewById(R.id.tvPlaceAddress);
        placeIDText = (TextView) findViewById(R.id.tvPlaceID);
        placeLongLat = (TextView) findViewById(R.id.tvPlaceLongLat);
        attributionText = (WebView) findViewById(R.id.wvAttribution);
        getPlaceButton = (Button) findViewById(R.id.btGetPlace);
        getPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(bounds);
                try {
                    Intent intent = builder.build(AddPlaceActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) { // guard against weird low-budget phones
            realm.close();
            realm = null;
        }
    }

    public void buttonClickCancelPlace (View v) {

        setResult(RESULT_CANCELED, null );

        finish();

    }



    public void buttonClickSavePlace (View v) {


        if (place.getId() != null) {

            Intent intent = this.getIntent();
            intent.putExtra("placeID", place.getId());
            this.setResult(RESULT_OK, intent);
            finish();
            Log.e("myApp :: ", "this nside place.id() is not null " + place.getId());

        } else {
            Log.e("myApp :: ", "this nside place.id() IS  nulle" + place.getId());
            setResult(RESULT_CANCELED, null );

            finish();

        }




    }


    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                place = PlacePicker.getPlace(AddPlaceActivity.this, data);
                placeNameText.setText(place.getName());
                placeAddressText.setText(place.getAddress());
                placeIDText.setText(place.getId());
                String thisPlaceID = place.getId();
                Double latitude = place.getLatLng().latitude;
                Double longitude = place.getLatLng().longitude;
                String address = String.valueOf(latitude)+ ", "+String.valueOf(longitude);
                placeLongLat.setText("Long" + address);

                thislocation = realm.where(Location.class).equalTo("locationID", place.getId() ).findFirst();

                if ((thislocation == null) && (place.getId() != null)) {
                    savePlaceLocationData(place);
                } else {
                    Log.e("myApp :: ", "this Location is already in the realm " + place.getId());
                }

                if (place.getAttributions() == null) {
                    attributionText.loadData("no attribution", "text/html; charset=utf-8", "UFT-8");
                } else {
                    attributionText.loadData(place.getAttributions().toString(), "text/html; charset=utf-8", "UFT-8");
                }

            }
        }
    }

    public void savePlaceLocationData(Place thisPlace) {

        realm.beginTransaction();
        Location newLocation = realm.createObject(Location.class, thisPlace.getId());
        newLocation.setLocationName(thisPlace.getName().toString());
        newLocation.setLocationAddress(thisPlace.getAddress().toString());
        newLocation.setLatitude(thisPlace.getLatLng().latitude);
        newLocation.setLongitiude(thisPlace.getLatLng().longitude);

        realm.commitTransaction();

        RealmResults<Location> savedLocations = realm.where(Location.class).findAll();
        int numSavedLocations = savedLocations.size();
        Log.e("myApp :: ", "Number of locations already in the realm " + String.valueOf(numSavedLocations) );

    }
}
