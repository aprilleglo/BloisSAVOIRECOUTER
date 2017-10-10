package net.aprille.bloissavoirecouter;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.io.IOException;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import models.Quadrant;
import models.Sound;
import models.User;

public class SectorSoundsActivity extends AppCompatActivity {

    int sector;
    String sectorId;
    String sectorTitle;
    String my_image;
    Realm realm;
    RealmConfiguration nRealmConfig;
    RealmRecyclerView nSounds;
    RealmConfiguration config;

    Quadrant quadrant;

    int num_of_Sounds;

    boolean isExhibition = true;


    RealmResults<Sound> sectoQuadSound;
    RealmList<Sound> sectorQuadSoundList;

    RealmResults<Sound> sounds;
    RealmResults<User> users;
    Sound newSound;
    User addUser;
    Quadrant newQuadrant;
    MediaPlayer mediaPlayer;
    Quadrant sectorQuadrant;

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
        setContentView(R.layout.activity_sector_sounds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nSounds = (RealmRecyclerView) findViewById(R.id.sound_realm_recycler_view);

        //      get intent
        sector = 16;
        sectorId = "16";
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        sector = extras.getInt("sectorNum");
        sectorId = String.valueOf(sector);
        Log.w("myApp", sectorId);

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }

        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        BloisSoundDirPath = BloisSoundDir.toString();
        BloisDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");

        BloisSoundWebUrl = "http://savoir-ecouter.aprille.net/wp-content/uploads/";


        Quadrant sectorQuadrant = realm.where(Quadrant.class).equalTo("quadID", sectorId).findFirst();



        sectorQuadSoundList = sectorQuadrant.getQuadSounds();

        sectoQuadSound =  sectorQuadSoundList.where().findAllSorted("soundLikes", Sort.DESCENDING);


        sounds = realm.where(Sound.class).findAll();
        users = realm.where(User.class).findAll();

        if (sectoQuadSound != null) {
            num_of_Sounds = sectoQuadSound.size();
            Log.w("myApp", "num_of_Sounds "+ String.valueOf(num_of_Sounds));
            Log.w("myApp", "sectorQuadSoundList "+ String.valueOf(sectorQuadSoundList.size()));
            Log.w("myApp", "num of users for realm "+ String.valueOf(users.size()));
            Log.w("myApp", "num_of_Sounds for all sounds "+ String.valueOf(sounds.size()));

        }


        if (sectorQuadrant != null) {
                sectorTitle = sectorQuadrant.getQuadTitle();
                TextView tvSector = (TextView) findViewById(R.id.tVSectorInfo);
                tvSector.setText(sectorQuadrant.getQuadTitle());
                TextView descTextView = (TextView) findViewById(R.id.tVSectorDescr);
                descTextView.setText(sectorQuadrant.getQuadDesc());
                TextView numSoundsTextView = (TextView) findViewById(R.id.numQuadSoundTextView);

                String nString = getString(R.string.number_of_sounds);
                Log.w("myApp", "sectorQuadSoundList "+ String.valueOf(sectorQuadSoundList.size()));
                numSoundsTextView.setText(nString + " " + String.valueOf(num_of_Sounds) );
                Log.w("myApp", "getString(R.string.number_of_sounds) "+ String.valueOf(num_of_Sounds));
                Log.w("myApp", "nString "+ nString);
                ImageView quadImageView = (ImageView) findViewById(R.id.sectorImageView);

                switch (sectorId ){
                    case "1" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_1));
                        break;
                    case "2" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_2));
                        break;
                    case "3" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_3));
                        break;
                    case "4" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_4));
                        break;
                    case "5" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_5));
                        break;
                    case "6" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_6));
                        break;
                    case "7" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_7));
                        break;
                    case "8" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_8));
                        break;
                    case "9" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_9));
                        break;
                    case "10" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_10));
                        break;
                    case "11" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_11));
                        break;
                    case "12" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_12));
                        break;
                    case "13" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_13));
                        break;
                    case "14" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_14));
                        break;
                    case "15" :
                        quadImageView.setImageDrawable(ContextCompat.getDrawable(SectorSoundsActivity.this, R.drawable.plan_15));
                        break;
                }


            } else {
                sectorTitle = "here is something from the else clause...";
                TextView tvSector = (TextView) findViewById(R.id.tVSectorInfo);
                tvSector.setText(sectorTitle);
            }


//       my edits for showing sector number



//       adding inflating realmrecycleviewer

        if (sectoQuadSound != null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //Do the work after completion of audio
                    mediaPlayer.reset();
                }
            });

            SoundRecyclerViewAdapter soundAdapter = new SoundRecyclerViewAdapter(getBaseContext(), sectoQuadSound, true, false);
            nSounds.setAdapter(soundAdapter);

        }




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (isExhibition) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), AddSoundActivity.class);
                    intent.putExtra("sectorNum", sector);
                    startActivity(intent);

                }
            });
        }




    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
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

            case R.id.action_plan:
                // User chose the home icon...
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                Intent intentPlan = new Intent(getApplicationContext(), PlanActivity.class);
                startActivity(intentPlan);
                return true;

            case R.id.explore_keyword:
                // User chose search by keyword
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                Intent intentSearch = new Intent(getApplicationContext(), SearchSoundsActivity.class);
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
                startActivity(intentGeo);

                return true;


            case R.id.explore_people:
                // User chose the "Favorite" action, mark the current item
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
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
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                Intent intentPrivacy = new Intent(getApplicationContext(), PrivacyActivity.class);
                startActivity(intentPrivacy);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    public interface IMyViewHolderClicks {
        void onLike(View caller);
        void onPlay(ImageView callerImage);
    }

    public class SoundRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<
                Sound, SoundRecyclerViewAdapter.ViewHolder> {
        Sound thisSound;

        Uri thisSoundUri;
        String thisSoundFileString;

        public SoundRecyclerViewAdapter(
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
            public IMyViewHolderClicks mListener;


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
                    Log.e("myApp ", "the sector id " + sectorId);
                    Log.e("myApp ", "the sound id " + thisSound.getSoundID() );

                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }

                    Intent intent = new Intent(v.getContext(), ZDetailSoundActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("callingtype", "SECTOR");
                    extras.putString("callingId", sectorId);
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
            if (sound.isLocalizeMedia()) {
                thisSoundImageFilePath = BloisSoundDirPath + "/" + sound.getSoundPhoto();
                Log.e("myApp :: ", "BloisSoundDirPath IN ONBIND " + thisSoundImageFilePath );
                Log.e("myApp :: ", "soundPhotofile name IN ONBIND " + thisSoundImageFilePath );
                Log.e("myApp :: ", "sound.getSound IN ONBIND " + sound.getSoundFile() );
                Picasso.with(viewHolder.mImage.getContext())
                        .load(new File(thisSoundImageFilePath))
                        .resize(120, 120)
                        .centerCrop()
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
