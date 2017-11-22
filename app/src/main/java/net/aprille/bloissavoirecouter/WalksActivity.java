package net.aprille.bloissavoirecouter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import models.OrderedWalkSound;
import models.Sound;
import models.Walk;

import static helperfunctions.Util.getCurrDateString;

public class WalksActivity extends AppCompatActivity {

    String DirectoryFinal;
    File BloisUserDir;
    File BloisSoundDir;
    File BloisWalkDir;
    File BloisDir;
    String BloisUserDirPath;
    String BloisSoundDirPath;
    String BloisWalkDirPath;
    String thisSoundImageFilePath;
    String BloisSoundWebUrl;

    String thisWalkID;

    Realm realm;
    RealmConfiguration nRealmConfig;


    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Log.e("myApp", "(float)displayMetrics.widthPixels "+ String.valueOf((float)displayMetrics.widthPixels));
        Log.e("myApp", "(float)displayMetrics.heightPixels "+ String.valueOf((float)displayMetrics.heightPixels));



        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }

        BloisDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");
        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        BloisWalkDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/walks");
        BloisSoundDirPath = BloisSoundDir.toString();
        BloisWalkDirPath = BloisWalkDir.toString();

        BloisSoundWebUrl = "http://savoir-ecouter.aprille.net/wp-content/uploads/";


        thisWalkID = "11082017_Aprille";
        Walk thisFirstWalk = realm.where(Walk.class).equalTo("walkID", thisWalkID).findFirst();

        if (thisFirstWalk == null) {

      //  setupWALK();

        } else {
            Log.e("myApp :: ", "else thisFirstWalk == null " + String.valueOf(thisFirstWalk.getOrderedWalkSounds().size()) );
        }

        RealmResults<Walk> walkClassResults = realm.where(Walk.class).findAll();

        RealmRecyclerView nWalks = (RealmRecyclerView) findViewById(R.id.walks_realm_recycler_view);


        WalkRecyclerViewAdapter walkAdapter = new WalkRecyclerViewAdapter(getBaseContext(), walkClassResults, true, false);

        Log.e("myApp :: ", "walkClassResultssize " + String.valueOf(walkClassResults.size()) );
        if (walkAdapter == null) {
            Log.e("myApp :: ", "walkAdapter == null" );
        } else {
            Log.e("myApp :: ", "walkAdapter != null" );
            nWalks.setAdapter(walkAdapter);
        }





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentWalkAdd = new Intent(getApplicationContext(), WalkAddActivity.class);
                thisWalkID = getCurrDateString();
                intentWalkAdd.putExtra("walkID", thisWalkID);
                startActivity(intentWalkAdd);


            }
        });
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

            case android.R.id.home:
                finish();
                return true;

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

    public void buttoncClickGoMap (View v) {

        Intent intentGoMap = new Intent(getApplicationContext(), WalkMapsActivity.class);
        intentGoMap.putExtra("walkID", "11082017_Aprille");

        startActivity(intentGoMap);

    }

    private void setupWALK() {


        realm.beginTransaction();
//
        Walk walk1 = realm.createObject(Walk.class, "thisWalkID");
//


        walk1.setWalkName("Sons d'animaux");
        walk1.setWalkDesc("Venez avec moi et découvrir les animaux du Lycee Horticole de Blois");
        walk1.setWalkPhoto("11082017_Aprille.jpg");
        walk1.setWalkPhotoDesc("photo du projet");
        walk1.setAccessiable(true);
        walk1.setLoop(true);
        walk1.setLocalizedMedia(true);
        String orderWalkSoundString = thisWalkID + "_1";
        OrderedWalkSound orderWalkSound1 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound1.setOrderedWalkSoundID("07112017-183916-2030"); //07112017-183916-2030|Le son de l'eau dans la serre tropicale du lycée horticole
        orderWalkSound1.setOrderedWalkPlaceID("8FV3H88G+CH24"); // 8FV3H88G+CH24|la serre tropicale du Lycée Horticole
        orderWalkSound1.setOrderNumberInWalk(1);

        Sound newOrderSound = realm.where(Sound.class).equalTo("soundID", "07112017-183916-2030").findFirst(); //
        if (newOrderSound  != null ) {
            newOrderSound.getOrderedWalkSounds().add(orderWalkSound1);
        } else {

        }

        walk1.getOrderedWalkSounds().add(orderWalkSound1);


        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        realm.commitTransaction();

        realm.beginTransaction();

        orderWalkSoundString = thisWalkID + "_2";
        OrderedWalkSound orderWalkSound2 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound2.setOrderedWalkSoundID("07112017-183929-5680"); //07112017-183929-5680|nous sommes dans le jardin du Lycée
        orderWalkSound2.setOrderedWalkPlaceID("8FV3H88F+3VH3"); // 8FV3H88F+3VH3|le jardin du Lycée Horticole
        orderWalkSound2.setOrderNumberInWalk(2);

        Sound newOrderSound2 = realm.where(Sound.class).equalTo("soundID", "07112017-183929-5680").findFirst(); //07112017-183929-5680|nous sommes dans le jardin du Lycée
        if (newOrderSound2  != null ) {
            newOrderSound2.getOrderedWalkSounds().add(orderWalkSound2);
        } else {

        }



        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        walk1.getOrderedWalkSounds().add(orderWalkSound2);
        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );

        realm.commitTransaction();

        realm.beginTransaction();

        orderWalkSoundString = thisWalkID + "_3";
        OrderedWalkSound orderWalkSound3 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound3.setOrderedWalkSoundID("19102017-110441-5840"); //19102017-110441-5840|les abeilles bourdonnent autour de la ruche
        orderWalkSound3.setOrderedWalkPlaceID("8FV3H88F+6X5P"); //8FV3H88F+6X5P|les ruches du Lycée Horticole
        orderWalkSound3.setOrderNumberInWalk(3);

        Sound newOrderSound3 = realm.where(Sound.class).equalTo("soundID", "19102017-110441-5840").findFirst(); // 07112017-183922-0830|les pigeons du Lycée Horticole
        if (newOrderSound3  != null ) {
            newOrderSound3.getOrderedWalkSounds().add(orderWalkSound3);
        } else {

        }



        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        walk1.getOrderedWalkSounds().add(orderWalkSound3);
        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );

        realm.commitTransaction();

        realm.beginTransaction();

        orderWalkSoundString = thisWalkID + "_4";
        OrderedWalkSound orderWalkSound4 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound4.setOrderedWalkSoundID("07112017-183841-7350"); //07112017-183841-7350|Expérimentation avec une chanson de baleine
        orderWalkSound4.setOrderedWalkPlaceID("8FV3H87F+FPFP"); // 8FV3H87F+FPFP|le champ expérimental
        orderWalkSound4.setOrderNumberInWalk(4);

        Sound newOrderSound4 = realm.where(Sound.class).equalTo("soundID", "07112017-183841-7350").findFirst(); // 07112017-183922-0830|les pigeons du Lycée Horticole
        if (newOrderSound4 != null ) {
            newOrderSound4.getOrderedWalkSounds().add(orderWalkSound4);
        } else {

        }



        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        walk1.getOrderedWalkSounds().add(orderWalkSound4);
        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );

        realm.commitTransaction();

        realm.beginTransaction();

        orderWalkSoundString = thisWalkID + "_5";
        OrderedWalkSound orderWalkSound5 = realm.createObject(OrderedWalkSound.class, orderWalkSoundString);
        orderWalkSound5.setOrderedWalkSoundID("07112017-183929-9270"); //07112017-183929-9270|nous sommes dans le secteur oiseaux
        orderWalkSound5.setOrderedWalkPlaceID("8FV3H88G+5J49"); // 8FV3H88G+5J49|l'animalerie du lycée
        orderWalkSound5.setOrderNumberInWalk(5);

        Sound newOrderSound5 = realm.where(Sound.class).equalTo("soundID", "07112017-183929-9270").findFirst(); // 07112017-183922-0830|les pigeons du Lycée Horticole
        if (newOrderSound5 != null ) {
            newOrderSound5.getOrderedWalkSounds().add(orderWalkSound5);
        } else {

        }



        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );


        walk1.getOrderedWalkSounds().add(orderWalkSound5);
        Log.e("myApp :: ", "Sounds in Walk " + String.valueOf(walk1.getOrderedWalkSounds().size()) );

        realm.commitTransaction();


    }

    public interface IMyViewHolderWalkClicks {

    }

    public class WalkRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<
                Walk, WalkRecyclerViewAdapter.ViewHolder> {

        Walk thisWalk;


        public WalkRecyclerViewAdapter(
                Context context,
                RealmResults<Walk> realmResults,
                boolean automaticUpdate,
                boolean animateIdType ) {
            super(context, realmResults, automaticUpdate, animateIdType);
        }

        public class ViewHolder extends RealmViewHolder implements View.OnClickListener {
            //implements View.OnClickListener
            private ImageView wImage;
            private ImageView wIconAcess;
            private ImageButton wMore;
            private TextView wTitle;
            private TextView wStages;
            private TextView wPractical;
            private LinearLayout wLinearLayoutAccess;
            //          IMyViewHolderWalkClicks mListener;


            public ViewHolder(LinearLayout container) {
                super(container);

                Log.e("myApp ", "ViewHolder ");
                wImage = (ImageView) container.findViewById(R.id.walk_grid_image);
                wIconAcess = (ImageView) container.findViewById(R.id.walk_grid_icon_accessibility);
                wMore = (ImageButton) container.findViewById(R.id.walk_grid_moreInfo);
                wTitle = (TextView) container.findViewById(R.id.walk_grid_title);
                wStages = (TextView) container.findViewById(R.id.walk_accessibility);
                wLinearLayoutAccess = (LinearLayout) container.findViewById(R.id.walk_linearlayout);
                wPractical = (TextView) container.findViewById(R.id.walk_info_practique);
                wImage.setOnClickListener(this);

            }


            @Override
            public void onClick(View v) {
                thisWalk = realmResults.get(getAdapterPosition());
                Log.e("myApp :: ", " onclick " + getAdapterPosition());
                RealmList<OrderedWalkSound> stagesList = thisWalk.getOrderedWalkSounds();
                if ( (stagesList !=null ) && (stagesList.size()> 1) ) {
                    Intent intent = new Intent(getApplicationContext(), WalkMapsActivity.class);
                    intent.putExtra("walkID", thisWalk.getWalkID());
                    startActivity(intent);

                }



                if (v instanceof ImageView) {
                    Log.e("myApp ", "onplay inside onclick getAdapterPosition " + getAdapterPosition());


                    Log.e("myApp", "onplay inside onclick " + thisWalk.getWalkName());
                    //           mListener.onPlay((ImageView)v);
                } else {
                    Log.e("myApp: ", "onlike inside onclick " + "");

                }

            }
        }

        // The Viewholder which we inflate here the layout for items in recycleview here note_item

        @Override
        public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
            Log.e("myApp:: ", "onCreateRealmViewHolder " );
            View v = inflater.inflate(R.layout.grid_item_view_walks, viewGroup, false);
            return new ViewHolder((LinearLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {

            final Walk walk = realmResults.get(position);
            String thisWalkFileName = BloisWalkDirPath + "/" + walk.getWalkPhoto();
            File thisWalkFileNamePath = new File(thisWalkFileName);
            // get metrics
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            Log.e("myApp", "(float)displayMetrics.widthPixels "+ String.valueOf((float)displayMetrics.widthPixels));
            Log.e("myApp", "(float)displayMetrics.heightPixels "+ String.valueOf((float)displayMetrics.heightPixels));
            int currentWidth = displayMetrics.widthPixels;
            int currentHeight = displayMetrics.widthPixels / 3;

            if (thisWalkFileNamePath.exists()) {
                Log.e("myApp :: ", "thisWalkFileName " + thisWalkFileName );
                Picasso.with(viewHolder.wImage.getContext())
                        .load(new File(thisWalkFileName))
                        .resize(currentWidth, currentHeight)
                        .placeholder(R.drawable.sound_defaul_image)
                        .into(viewHolder.wImage);

            } else {
                thisWalkFileName = BloisSoundWebUrl + "/" + walk.getWalkPhoto();
                Picasso.with(viewHolder.wImage.getContext())
                        .load(thisWalkFileName)
                        .resize(currentWidth, currentHeight)
                        .placeholder(R.drawable.user_default_image)
                        .into(viewHolder.wImage);

            }

            viewHolder.wTitle.setText(walk.getWalkName());

            if (walk.isAccessiable()) {
                viewHolder.wIconAcess.setImageResource(R.drawable.ic_accessible_black_24dp);
            } else {
                viewHolder.wIconAcess.setImageResource(R.drawable.ic_terrain_black_24dp);
            }
            if ((walk.getWalkTime()!= null ) && (walk.getWalkDistance() !=  null)){
                String infoPratical = "Distance : " + String.valueOf(walk.getWalkDistance()) + " Durée : " + walk.getWalkTime();
                viewHolder.wPractical.setText( infoPratical);
            } if ((walk.getWalkTime()== null ) && (walk.getWalkDistance() !=  null)){
                Log.e("myApp:: ", "calculate walking time " );
                Double calculateTime = walk.getWalkDistance() * .002;
                String infoPratical = "Distance : " + String.format("%.2f",walk.getWalkDistance()) + " Durée : " + String.format("%.2f", calculateTime) +" hr";
                viewHolder.wPractical.setText( infoPratical );
            } else {
                viewHolder.wPractical.setText( "error - null values");
            }
            viewHolder.wMore.setImageResource(R.drawable.ic_more_horiz_black_24dp);

            RealmList<OrderedWalkSound> walkStagesList = walk.getOrderedWalkSounds();

            int thisWalkSStageComputed = walkStagesList.size();

            String numSoundString = Integer.toString( thisWalkSStageComputed ) + " lieux";
            viewHolder.wStages.setText( numSoundString );

        }
    }



}
