package net.aprille.bloissavoirecouter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.picasso.Picasso;

import java.io.File;

import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import models.Location;
import models.LocationItemView;

public class AddLocationActivity extends AppCompatActivity {

    int sector;
    String thisLocationId;
    String thisLocationName;
    String locationAddress;
    Double thisLocationLong;
    Double thisLocationLat;
    String thisLocationPhoto;

    Location editLocation;

    int ADD_PLACE_REQUEST_CODE = 101;
    String thisPlaceID;


    int num_of_Locations;


    RealmResults<Location> LocationClassResults;

    TextView tvLocAddress;
    TextView tvLocLongLatTextView;
    TextView tvNumLocTextView;
    WebView  attributionText;
    TextView vNumLocTextView;

    public String DirectoryFinal;
    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String BloisSoundDirPath;
    public String thisSoundImageFilePath;
    public String thisIconThumbFilePath;
    boolean isLandscape;

    String m_Text = "";

    public String userPrimaryThumbnail = "myimage.png";
    private RealmSearchView realmSearchView;
    private LocationRecyclerViewAdapter adapter;
    private Realm realm;

    Place place;

    final Context context = this;
    int REQUESTCODE;

    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private final static int PLACE_PICKER_REQUEST = 1;
    private final static LatLngBounds bounds = new LatLngBounds(new LatLng(47.536636,1.257858), new LatLng(47.623715,1.364288));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestPermission();

        resetRealm();

        realmSearchView = (RealmSearchView) findViewById(R.id.location_search_view);

        Log.w("myApp", "just before inflating");

      //  realm = Realm.getInstance(getRealmConfig());
        adapter = new LocationRecyclerViewAdapter(this, realm, "locationName");
        realmSearchView.setAdapter(adapter);


//        try {
//            realm = Realm.getDefaultInstance();
//        } catch (IllegalStateException fuckYouTooAndroid) {
//            Realm.init(getApplicationContext());
//            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
//            Realm.setDefaultConfiguration(config);
//            realm = Realm.getDefaultInstance();
//        }



//        TextView tvLocName = (TextView) findViewById(R.id.tvLocationName);

        attributionText = (WebView) findViewById(R.id.wvAttribution);

        tvLocAddress = (TextView) findViewById(R.id.tvLocationAddress);

        tvLocLongLatTextView = (TextView) findViewById(R.id.tvLocationLongLat);

        vNumLocTextView = (TextView) findViewById(R.id.tvnumPlaces);

        ImageView iVLocationImageView = (ImageView) findViewById(R.id.locationImageView);

        Picasso.with(this)
                .load(R.drawable.location_default)
                .resize(150, 150)
                .centerCrop()
                .into(iVLocationImageView);



//        LocationClassResults = realm.where(Location.class).findAllSorted("locationName", Sort.ASCENDING);



//        nLocations = (RealmRecyclerView) findViewById(R.id.location_realm_recycler_view);
//        Log.e("myApp :: ", "LocationClassResults count " + String.valueOf(LocationClassResults.size()) );
//
//
//        LocationRecyclerViewAdapter locationAdapter = new LocationRecyclerViewAdapter (this, realm, "locationName");
//        nLocations.setAdapter(locationAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent i = new Intent(context, AddPlaceActivity.class);
                Log.w("myApp", "b4 pressed - about to launch sub-activity");
                // the results are called on widgetActivityCallback
//                startActivityForResult(i, ADD_PLACE_REQUEST_CODE);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(bounds);
                try {
                    Intent intent = builder.build(AddLocationActivity.this);
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
        realm.close();
        realm = null;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_plan:
                // User chose the home icon...
                Intent intentPlan = new Intent(getApplicationContext(), PlanActivity.class);
                startActivity(intentPlan);
                return true;

            case R.id.explore_keyword:
                // User chose search by keyword
                Intent intentSearch = new Intent(getApplicationContext(), SearchSoundsActivity.class);
                startActivity(intentSearch);
                return true;

            case R.id.explore_geocoding:
                // User chose the "Favorite" action, mark the current item

                Intent intentGeo = new Intent(getApplicationContext(), AddLocationMapsActivity.class);
                intentGeo.putExtra("placeID", "8FV3H8QQ+7V33");
                startActivity(intentGeo);

                return true;


            case R.id.explore_people:
                // User chose the "Favorite" action, mark the current item

                Intent intentPeople = new Intent(getApplicationContext(), PeopleActivity.class);
                startActivity(intentPeople);
                return true;

            case R.id.action_walks:
                // User chose the "Favorite" action, mark the current item

                Intent intentWalks = new Intent(getApplicationContext(), WalksActivity.class);
                startActivity(intentWalks);
                return true;

            case R.id.action_privacy:
                // User chose the "Favorite" action, mark the current item

                Intent intentPrivacy = new Intent(getApplicationContext(), PrivacyActivity.class);
                startActivity(intentPrivacy);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    private void resetRealm() {

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }

 //       Realm.deleteRealm(getRealmConfig());
    }
    public void buttonClickEditLocationName (View v) {

        editLocation = realm.where(Location.class).equalTo("locationID", thisPlaceID).findFirst();

        if (editLocation != null) {

            m_Text = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Edit Location Name");
            // I'm using fragment here so I'm using getView() to provide ViewGroup
            // but you can provide here any other instance of ViewGroup from your FragmLayoutInflater inflater = this.getLayoutInflater();


            View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.text_input_place_name, null);
            // Set up the input
            final EditText input = (EditText) viewInflated.findViewById(R.id.place_name_imput);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(viewInflated);
            input.setText(editLocation.getLocationName());
            // Set up the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    m_Text = input.getText().toString();
                    if (m_Text != null) {
                        Log.e("myApp:: ", "onBindRealmViewHolder inside if " + editLocation.getLocationID());

                        realm.beginTransaction();
                        editLocation.setLocationName(m_Text);
                        realm.commitTransaction();
                        TextView tvPlaceNameTV =(TextView) findViewById(R.id.tvLocationName);
                        tvPlaceNameTV.setText(editLocation.getLocationName());

                    }
                    Log.e("myApp:: ", "onBindRealmViewHolder onclick " + editLocation.getLocationID());
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }


    }

    public void buttonClickCancelLocation (View v) {
        setResult(RESULT_CANCELED, null );

        finish();
    }
    public void buttonClickSaveLocation (View v) {


        if (thisPlaceID != null) {

            Intent intent = this.getIntent();
            intent.putExtra("placeID", thisPlaceID);
            this.setResult(RESULT_OK, intent);
            finish();
            Log.e("myApp :: ", "this nside place.id() is not null " + thisPlaceID);

        } else {
            Log.e("myApp :: ", "this nside place.id() IS  nulle" + thisPlaceID);
            setResult(RESULT_CANCELED, null );

            finish();

        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST){
                if (resultCode == RESULT_OK){
                    place = PlacePicker.getPlace(AddLocationActivity.this, data);
                    Log.e("myApp", "thisGooglePlace.getLocationName() " + place.getName());
                    TextView tvPlaceNameTV =(TextView) findViewById(R.id.tvLocationName);
                    tvPlaceNameTV.setText(place.getName());

                    tvLocAddress.setText(place.getAddress());
                    Log.e("myApp", "thisGooglePlace.getLocationID() " + place.getId());

                    thisPlaceID = place.getId();
                    Double latitude = place.getLatLng().latitude;
                    Double longitude = place.getLatLng().longitude;
                    String address = String.valueOf(latitude)+ ", "+String.valueOf(longitude);
                    tvLocLongLatTextView.setText(getString(R.string.LongLat) + address);

                    Location thislocation = realm.where(Location.class).equalTo("locationID", place.getId() ).findFirst();

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
            if (requestCode == 101) {   // this is requestcode for subactivity AddPlaceActivity
                Bundle res = data.getExtras();
                thisPlaceID = res.getString("placeID");
                Log.d("FIRST", "result: "+ thisPlaceID);
                if (thisPlaceID != null) {
                    Location newLocation = realm.where(Location.class).equalTo("locationID", thisPlaceID).findFirst();

                    if (newLocation != null) {

                        Log.e("myApp", "thisLocation.getLocationName() " + newLocation.getLocationName());
                        TextView tvPlaceNameTV =(TextView) findViewById(R.id.tvLocationName);
                        tvPlaceNameTV.setText(newLocation.getLocationName());
                        TextView tvPlaceAddressTV =(TextView) findViewById(R.id.tvLocationAddress);
                        tvPlaceAddressTV.setText(newLocation.getLocationAddress());
                        TextView tvPlaceLongLatTV  =(TextView) findViewById(R.id.tvLocationLongLat);
                        tvPlaceLongLatTV.setText("LongLat "+ newLocation.getLongitiude().toString() +", "+ newLocation.getLatitude());

                        TextView vNumLocTextView = (TextView) findViewById(R.id.tvnumPlaces);
                        vNumLocTextView.setText("@string/number_of_sounds"+ String.valueOf(newLocation.getLocationSounds().size()));

                    }

                }


            }

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



    public class LocationRecyclerViewAdapter extends RealmSearchAdapter<
                Location, LocationRecyclerViewAdapter.ViewHolder> {


        public LocationRecyclerViewAdapter(
                Context context,
                Realm realm,
                String locationName) {
            super(context, realm, locationName);
        }


//        public LocationRecyclerViewAdapter(
//                Context context,
//                RealmResults<Location> realmResults,
//                boolean automaticUpdate,
//                boolean animateIdType ) {
//            super(context, realmResults, automaticUpdate, animateIdType);
//        }

        public class ViewHolder extends RealmSearchViewHolder {

            private LocationItemView locationItemView;


            public ViewHolder(FrameLayout container, TextView footerTextView) {
                super(container, footerTextView);
            }

            public ViewHolder(LocationItemView locationItemView) {
                super(locationItemView);
                this.locationItemView = locationItemView;


            }
        }

            // The Viewholder which we inflate here the layout for items in recycleview here note_item



            @Override
            public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
                ViewHolder vh = new ViewHolder(new LocationItemView(viewGroup.getContext()));
                return vh;
            }


            @Override
            public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
                final Location mylocation = realmResults.get(position);
                viewHolder.locationItemView.bind(mylocation);
                viewHolder.itemView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                thisPlaceID = mylocation.getLocationID();
                                TextView tvPlaceNameTV =(TextView) findViewById(R.id.tvLocationName);
                                tvPlaceNameTV.setText(mylocation.getLocationName());
                                TextView tvPlaceAddressTV =(TextView) findViewById(R.id.tvLocationAddress);
                                tvPlaceAddressTV.setText(mylocation.getLocationAddress());
                                TextView tvPlaceLongLatTV  =(TextView) findViewById(R.id.tvLocationLongLat);
                                tvPlaceLongLatTV.setText("LongLat "+ mylocation.getLongitiude().toString() +", "+ mylocation.getLatitude());

                                TextView vNumLocTextView = (TextView) findViewById(R.id.tvnumPlaces);
                                vNumLocTextView.setText("@string/number_of_sounds"+ String.valueOf(mylocation.getLocationSounds().size()));

                                Log.e("myApp:: ", "onBindRealmViewHolderclicked " + mylocation.getLocationID());

                            }
                        }
                );

                Log.e("myApp:: ", "onBindRealmViewHolder name " + mylocation.getLocationName());


            }

            @Override
            public ViewHolder onCreateFooterViewHolder(ViewGroup viewGroup) {
                View v = inflater.inflate(R.layout.footer_view, viewGroup, false);
                return new ViewHolder(
                        (FrameLayout) v,
                        (TextView) v.findViewById(R.id.footer_text_view));
            }

            @Override
            public void onBindFooterViewHolder(ViewHolder holder, final int position) {
                super.onBindFooterViewHolder(holder, position);
                holder.itemView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.e("myApp", "Item clicked: " + position);
                            }
                        }
                );
            }


        }

        private void addLocationsFromFile() {

//        InputStream inputStream = getResources().openRawResource(R.raw.location);
//        CSVFile csvFileSound = new CSVFile(inputStream);
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        try {
//            String csvLine;
//            int rowCount = 1;
//            while ((csvLine = reader.readLine()) != null) {
//                String[] row = csvLine.split("\\|");
//
//                int len = row.length;
//                Log.w("myApp :: ", "Value length of row " + len );
//
//                User newUser  = realm.where(User.class).equalTo("userID", row[0]).findFirst();
//                if (newUser == null ){
//                    Log.w("myApp :: ", "Value length of row 0 " + row[0] );
//                    Log.w("myApp :: ", "Value length of row 1 " + row[1] );
//                    Log.w("myApp :: ", "Value length of row 2 " + row[2] );
//                    Log.w("myApp :: ", "Value length of row 3 " + row[3] );
//                    realm.beginTransaction();
//                    newUser = realm.createObject(User.class, row[0]);
//                    Log.w("myApp :: ", "Value length of row 1 " + row[1] );
//                    newUser.setUserName(row[1]);
//                    if (row[2] != null){
//                        newUser.setUserDesc(row[2]);
//                    }
//                    newUser.setTimeJoined(getCurrDateString());
//                    newUser.setUserPhoto(row[3]);
//                    newUser.setUserPhotoDesc(row[1]);
//                    newUser.setPrimaryUserBoolean(false);
//                    newUser.setNumUserSounds(0);
//                    realm.commitTransaction();
//
//                }
//
//
//
//            }
//        }
//        catch (IOException ex) {
//            throw new RuntimeException("Error in reading CSV file: "+ex);
//        }
//        finally {
//            try {
//                inputStream.close();
//            }
//            catch (IOException e) {
//                throw new RuntimeException("Error while closing input stream: "+e);
//            }
//        }
////        List userList = csvFileUser.read();
////
////        int len = userList.size();
////        Log.w("myApp :: ", "Value length of list " + len );
////        for (int i = 0; i < len; ++i) {
////            String[] thisUserRow = userList.get(i);
////
////            Log.w("myApp :: ", "Value  row 0 " + userList[i][1]);
////       }
////
//////        Log.w("myApp :: ", "Value  row 0 " + userList[0][1]);
//////        for (int i = 0; i < len; ++i) {
//////            Log.w("myApp :: ", "Value  row 0 " + userList[i][1]);
//////        }

        }


    }



