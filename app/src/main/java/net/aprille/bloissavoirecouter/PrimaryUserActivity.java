package net.aprille.bloissavoirecouter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import models.Sound;
import models.User;

public class PrimaryUserActivity extends AppCompatActivity {

    String soundsUserID;

    Realm realm;
    RealmConfiguration uRealmConfig;
    RealmRecyclerView pSounds;
    RealmConfiguration config;

    User User_for_Sounds;

    boolean isExhibition = true;

    int num_of_Sounds;

    RealmResults<Sound> user_Sound_Results;
    RealmList<Sound> user_Sound_List;

    MediaPlayer mediaPlayer;

    public String DirectoryFinal;
    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String BloisSoundDirPath;
    public String thisSoundImageFilePath;
    public String thisIconThumbFilePath;

    String BloisSoundWebUrl;
    boolean isLandscape;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // get intent from People Activity

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        soundsUserID = extras.getString("userID");
        Log.e("myApp ", "the thisSoundID " + soundsUserID);

        // must change for final version of realm !!!!!

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }

        // setup filing housekeeping

        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        BloisUserDirPath = BloisUserDir.toString();
        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        BloisSoundDirPath = BloisSoundDir.toString();
        BloisDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");

        BloisSoundWebUrl = "http://savoir-ecouter.aprille.net/wp-content/uploads/";

        // get the user for this instanciation

        User_for_Sounds = realm.where(User.class).equalTo("userID", soundsUserID).findFirst();



        if (User_for_Sounds.getUserID() != null) {
            Log.w("myApp", "num_of_Sounds "+ String.valueOf(User_for_Sounds.getNumUserSounds()));


            String thisUserImageFilePath = BloisUserDirPath + "/" + User_for_Sounds.getUserPhoto();
            Log.e("myApp :: ", "BloisUerDirPath with primary " + thisUserImageFilePath );
            ImageView iVUserImageView = (ImageView) findViewById(R.id.p_sound_ImageView);
            Picasso.with(this)
                    .load(new File(thisUserImageFilePath))
                    .resize(150, 150)
                    .centerCrop()
                    .placeholder(R.drawable.user_default_image)
                    .into(iVUserImageView);


            Log.e("myApp :: ", "User_for_Sounds.getUserName() primary " + User_for_Sounds.getUserName() );
            TextView tvUserName =(TextView) findViewById(R.id.p_sound_UserName);

            tvUserName.setText(User_for_Sounds.getUserName());
            TextView tvUserDescTextView = (TextView) findViewById(R.id.p_sound_UserDescr);
            tvUserDescTextView.setText(User_for_Sounds.getUserDesc());
            Linkify.addLinks(tvUserDescTextView, Linkify.ALL);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.INVISIBLE);






            // setup recycleview

            user_Sound_List = User_for_Sounds.getUserSounds();

            user_Sound_Results =  user_Sound_List.where().findAllSorted("soundLikes", Sort.DESCENDING);



            if ((String.valueOf(User_for_Sounds.getNumUserSounds()) == null)) {
                realm.beginTransaction();
                User_for_Sounds.setNumUserSounds(0);
                realm.commitTransaction();
            }

            if (User_for_Sounds.getNumUserSounds() != user_Sound_Results.size() ) {
                int ResultSize = user_Sound_Results.size();
                Log.e("myApp :: ", "User_for_Sounds.getNumUserSounds()" + String.valueOf(User_for_Sounds.getNumUserSounds()) );
                Log.e("myApp :: ", "user_Sound_Results.size() " + String.valueOf(user_Sound_Results.size()) );
                realm.beginTransaction();
                User_for_Sounds.setNumUserSounds(ResultSize);
                realm.commitTransaction();
            }

            TextView numSoundsPrimaryTextView = (TextView) findViewById(R.id.p_sound_numSound_TextView);
            String nString = getString(R.string.sound_user);

            String makeNumSoundsString = String.valueOf(User_for_Sounds.getNumUserSounds());
            numSoundsPrimaryTextView.setText( nString + " " + makeNumSoundsString );


            pSounds = (RealmRecyclerView) findViewById(R.id.p_sound_realm_recycler_view);


            // set up media player before inflating recycleview

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //Do the work after completion of audio
                    mediaPlayer.reset();
                }
            });

            PSoundRecyclerViewAdapter soundAdapter = new PSoundRecyclerViewAdapter(getBaseContext(), user_Sound_Results, true, false);
            pSounds.setAdapter(soundAdapter);


        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed() ){
            realm.close();
            realm = null;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                finish();
                return true;

            case R.id.action_plan:
                // User chose the home icon...
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                Intent intentPlan = new Intent(getApplicationContext(), PlanActivity.class);
                intentPlan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentPlan.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intentPlan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intentPlan);
                return true;


            case R.id.explore_keyword:
                // User chose search by keyword
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                Intent intentSearch = new Intent(getApplicationContext(), SearchSoundsActivity.class);
                intentSearch.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentSearch);
                return true;

            case R.id.explore_geocoding:
                // User chose the "Favorite" action, mark the current item
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }

                Intent intentGeo = new Intent(getApplicationContext(), AddLocationMapsActivity.class);
                intentGeo.putExtra("placeID", "8FV3H8QQ+7V33");
                intentGeo.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentGeo);

                return true;


            case R.id.explore_people:
                // User chose the "Favorite" action, mark the current item
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                Intent intentPeople = new Intent(getApplicationContext(), PeopleActivity.class);
                intentPeople.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentPeople);
                return true;

            case R.id.action_walks:
                // User chose the "Favorite" action, mark the current item

                Intent intentWalks = new Intent(getApplicationContext(), WalksActivity.class);
                intentWalks.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentWalks);
                return true;

            case R.id.action_privacy:
                // User chose the "Favorite" action, mark the current item
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                Intent intentPrivacy = new Intent(getApplicationContext(), PrivacyActivity.class);
                intentPrivacy.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentPrivacy);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    // Starts Adaptor

    public class PSoundRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<
                Sound, PSoundRecyclerViewAdapter.ViewHolder> {
        Sound thisSound;

        Uri thisSoundUri;
        String thisSoundFileString;

        public PSoundRecyclerViewAdapter(
                Context context,
                RealmResults<Sound> realmResults,
                boolean automaticUpdate,
                boolean animateIdType ) {
            super(context, realmResults, automaticUpdate, animateIdType);
        }

        public class ViewHolder extends RealmViewHolder implements View.OnClickListener {

            private ImageView mImage;
            private ImageView mIconLike;
            private ImageButton mButton_likes;
            private TextView mTitle;
            private TextView mLikes;
            private LinearLayout mLikePress;
            private ImageButton mMore;
            private ImageView mButton_plays;
            File thisSoundFileNamePath;
            public SectorSoundsActivity.IMyViewHolderClicks mListener;


            public ViewHolder(LinearLayout container) {
                super(container);

                Log.e("myApp ", "ViewHolder "   );
                mImage = (ImageView) container.findViewById(R.id.sound_image);

                mButton_plays = (ImageView) container.findViewById(R.id.play_pause);


                this.mTitle = (TextView) container.findViewById(R.id.sound_title);
                mIconLike = (ImageView) container.findViewById(R.id.icon_likes);
                mLikePress = (LinearLayout)  container.findViewById(R.id.likes_press);
                mLikes = (TextView) container.findViewById(R.id.sound_likes);
                mMore = (ImageButton) container.findViewById(R.id.moreInfo);
                mImage.setOnClickListener(this);
                mButton_plays.setOnClickListener(this);
                mLikePress.setOnClickListener(this);
                mMore.setOnClickListener(this);


            }



            @Override
            public void onClick(View v) {
                thisSound = realmResults.get(getAdapterPosition());
                thisSoundFileString = BloisSoundDirPath + "/" + thisSound.getSoundFile();
                thisSoundUri = Uri.parse(thisSoundFileString);
                Log.e("myApp :: ", " onclick " + getAdapterPosition() );


                if (v.getClass().getName().equalsIgnoreCase("android.widget.ImageView")) {
                    Log.e("myApp", "imageview "+v.toString());  // case for playing sound
                    Log.e("myApp", "onplay inside onclick " + thisSound.getSoundName()) ;

                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mButton_plays.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    } else {

                        if (thisSound.isLocalizeMedia()) {
                            Log.e("myApp", "onplay inside islocalized mediatrue " + thisSound.getSoundName()) ;

                            thisSoundFileString = BloisSoundDirPath + "/" + thisSound.getSoundFile();
                            thisSoundUri = Uri.parse(thisSoundFileString);
                            try {
                                mediaPlayer.setDataSource(getContext(), thisSoundUri);
                                mediaPlayer.prepare();
                                mButton_plays.setImageResource(R.drawable.ic_pause_black_24dp);
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                            mediaPlayer.start();
                        } else {     // not localized
                            mediaPlayer.reset();
                            Log.e("myApp", "onplay inside islocalized mediafalse " + thisSound.getSoundName()) ;
                            Log.e("myApp", "onplay inside islocalized sounddesc " + thisSound.getSoundDesc()) ;
                            Log.e("myApp", "onplay inside islocalized soundfile " + thisSound.getSoundFile()) ;
                            thisSoundFileString = BloisSoundWebUrl + "/" + thisSound.getSoundFile();
                            Log.e("myApp", "theURl " + thisSoundFileString) ;
                            //mp3 will be started after completion of preparing...

                            try {
                                mediaPlayer.setDataSource(thisSoundFileString);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mediaPlayer.start();
                                    }

                                });
                                mButton_plays.setImageResource(R.drawable.ic_pause_black_24dp);
                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                        }

                    }

                } else if (v.getClass().getName().equalsIgnoreCase("android.widget.ImageButton")) {
                    Log.e("myApp", "imagebutton " +v.toString()); // case for opening new ZDetailSound activity
                    Log.e("myApp ", "more inside onclick " + getAdapterPosition() );
                    Log.e("myApp ", "the user id " + soundsUserID);
                    Log.e("myApp ", "the sound id " + thisSound.getSoundID() );

                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }

                    Intent intent = new Intent(v.getContext(), ZDetailSoundActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("callingtype", "USER");
                    extras.putString("callingId", soundsUserID);
                    extras.putString("soundID", thisSound.getSoundID());
                    intent.putExtras(extras);
                    startActivity(intent);

                } else if(v instanceof LinearLayout){
                    Log.e("myApp", "LinearLayout " +v.toString()); // case for adding to likes
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    thisSound.setSoundLikes(thisSound.getSoundLikes()+1);
                    realm.commitTransaction();
                    Log.e("myApp:: ", "onlike inside onclick " + thisSound.getSoundLikes() );

                }

            }



        }

        // The Viewholder which we inflate here the layout for items in recycleview here note_item

        @Override
        public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
            Log.e("myApp:: ", "onCreateRealmViewHolder " );
            View v = inflater.inflate(R.layout.grid_item_view, viewGroup, false);

            return new ViewHolder((LinearLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
            final Sound sound = realmResults.get(position);



            thisSoundImageFilePath = BloisSoundDirPath + "/" + sound.getSoundPhoto();
            Log.e("myApp :: ", "BloisSoundDirPath IN ONBIND " + thisSoundImageFilePath );
            Log.e("myApp :: ", "soundPhotofile name IN ONBIND " + thisSoundImageFilePath );
            Log.e("myApp :: ", "sound.getSound IN ONBIND " + sound.getSoundFile() );

            if (sound.isLocalizeMedia()) {
                thisSoundImageFilePath = BloisSoundDirPath + "/" + sound.getSoundPhoto();
                Log.e("myApp :: ", "BloisSoundDirPath IN ONBIND " + thisSoundImageFilePath );
                Log.e("myApp :: ", "soundPhotofile name IN ONBIND " + thisSoundImageFilePath );
                Log.e("myApp :: ", "sound.getSound IN ONBIND " + sound.getSoundFile() );
                Picasso.with(viewHolder.mImage.getContext())
                        .load(new File(thisSoundImageFilePath))
                        .resize(120, 120)
                        .centerCrop()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .placeholder(R.drawable.sound_defaul_image)
                        .into(viewHolder.mImage);


            } else {
                thisSoundImageFilePath = BloisSoundWebUrl  + sound.getSoundPhoto();
                Log.e("myApp :: ", "sound.getSoundPhoto() IN ONBIND " + thisSoundImageFilePath );
                Picasso.with(viewHolder.mImage.getContext())
                        .load(thisSoundImageFilePath)
                        .resize(120, 120)
                        .centerCrop()
                        .placeholder(R.drawable.sound_defaul_image)
                        .into(viewHolder.mImage);

            }


            viewHolder.mButton_plays.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            viewHolder.mTitle.setText(sound.getSoundName());

            viewHolder.mIconLike.setImageResource(R.drawable.ic_thumb_up_black_24dp);

            viewHolder.mMore.setImageResource(R.drawable.ic_more_horiz_black_24dp);


            viewHolder.mLikes.setText(String.valueOf(sound.getSoundLikes()));

        }
    }  // ends Adapter

}
