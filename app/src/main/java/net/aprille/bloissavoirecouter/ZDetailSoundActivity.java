package net.aprille.bloissavoirecouter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import helperfunctions.Util;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import models.Location;
import models.Quadrant;
import models.Sound;
import models.User;


public class ZDetailSoundActivity extends AppCompatActivity {

    String thisCallingType;
    String thisCallingID;
    String thisSoundID;
    int thisQuadNUM;

    int ADD_PLACE_REQUEST_CODE = 101;
    String thisPlaceID;
    String REQUEST_PLACE_ID;
    int sector;
    String sectorId;


    boolean thisLocalizeMedia = true;
    boolean thisCreatedByPrimaryUser = false;
    int thisSoundLikes = 15;

    String thsPrimaryUserKey;

    Sound thisSound;
    Location thisPlace;
    User thisUser;

    boolean isExhibition = true;


    ImageView SoundviewImage;

    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    MediaPlayer mediaPlayer ;

    int permissionCheckStorage;
    int permissionCheckCamera;
    int permissionCheckMicrophone;
    int permissionCheckLocation;

    /**
     * Id to identify a camera permission request.
     */
    private static final int REQUEST_CAMERA = 0;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_STORAGE = 1;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_MICROPHONE = 2;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_LOCATION = 3;

    public File BloisUserDir;
    public File BloisSoundDir;
    public File BloisDir;
    public String BloisUserDirPath;
    public String BloisSoundDirPath;
    public File BloisShareDir;
    public String BloisShareDirPath;
    public String thisSoundImageFilePath;
    public String thisSoundFilePath;
    public String thisIconThumbFilePath;
    boolean isLandscape;
    Context context = this;



    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zdetail_sound);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get intent for soundID and callback activity

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        thisCallingType = extras.getString("callingtype");
        Log.e("myApp ", "the thisCallingType " + thisCallingType);
        thisCallingID = extras.getString("callingId");
        Log.e("myApp ", "the thisCallingID " + thisCallingID);
        thisSoundID = extras.getString("soundID");
        Log.e("myApp ", "the thisSoundID " + thisSoundID);
        if (thisCallingType == "SECTOR") {
            try {
                thisQuadNUM = Integer.parseInt(thisCallingID.toString());
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

        }


        // Assume check permissions for camera and storage
        permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);
        Log.w("myApp", "permissionCheck " + permissionCheckCamera );

        permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.w("myApp", "permissionCheckStorage " + permissionCheckStorage );

        permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO);
        Log.w("myApp", "permissionCheckMicrophone " + permissionCheckMicrophone );

        permissionCheckLocation = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        Log.w("myApp", "permissionCheckMicrophone " + permissionCheckLocation );



        if (permissionCheckCamera == -1) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
            permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA);
            Log.w("myApp", "permissionCheck value after request" + permissionCheckCamera );

        }

        if (permissionCheckStorage == -1) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
            permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.w("myApp", "permissionCheckStorage value after request" + permissionCheckStorage);
        }

        if (permissionCheckMicrophone == -1) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
            permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.RECORD_AUDIO);
            Log.w("myApp", "permissionCheckMicrophone value after request " + permissionCheckMicrophone);
        }

        if (permissionCheckLocation == -1) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            Log.w("myApp", "permissionCheckMicrophone value after request " + permissionCheckMicrophone);
        }
        if ( (permissionCheckStorage == -1) || (permissionCheckMicrophone == -1) || (permissionCheckLocation == -1)) {
            Toast.makeText(this, "Permmission Problem to record please change your app preferences",
                    Toast.LENGTH_LONG).show();


        }

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }


        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        BloisUserDirPath = BloisUserDir.toString();
        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        BloisSoundDirPath = BloisSoundDir.toString();
        BloisShareDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/share");
        if (!BloisShareDir.exists()) {
            if (!BloisShareDir.mkdirs()) {
                Log.e("myApp:: ", "Problem creating Image folder");
            }
            Log.e("myApp:: ", "Creating Image folder");
        }
        BloisShareDirPath = BloisShareDir.toString();

        BloisDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");


        thisSound = realm.where(Sound.class).equalTo("soundID", thisSoundID).findFirst();

        if (thisSound != null) {
            if ( !(thisSound.isCreatedByPrimaryUser() ) ) {
                ImageButton changePictureButton = (ImageButton) findViewById(R.id.changeimageSoundButtonSoundDetail);
                changePictureButton.setVisibility(View.INVISIBLE);
            } else {
                thisCreatedByPrimaryUser = true;
            }

            SoundviewImage = (ImageView) findViewById(R.id.soundImageViewSoundDetail);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //Do the work after completion of audio
                    mediaPlayer.reset();
                }
            });

            TextView tvUserDescPhotoDetail = (TextView) findViewById(R.id.tVQuadPhotoSoundInfo);
            if (thisSound.getSoundPhotoDesc() != null) {
                tvUserDescPhotoDetail.setText(thisSound.getSoundPhotoDesc());
            }



            thisSoundFilePath =  BloisSoundDirPath + "/" + thisSound.getSoundFile();
            Log.e("myApp :: ", "BloisSoundDirPath " + thisSoundImageFilePath);
            Log.e("myApp :: ", "soundPhotofile name  " + thisSoundImageFilePath);
            Log.e("myApp :: ", "sound.getSound  " + thisSound.getSoundFile());
            Log.e("myApp :: ", "thisSound.getSoundDesc()  " + thisSound.getSoundDesc());
            thisSoundImageFilePath = BloisSoundDirPath + "/" + thisSound.getSoundPhoto();
            File thisUserImageFile = new File(thisSoundImageFilePath);
            if (thisUserImageFile.exists()) {

                Log.e("myApp :: ", "BloisSoundDirPath IN ONBIND " + thisSoundImageFilePath );
                Log.e("myApp :: ", "soundPhotofile name IN ONBIND " + thisSoundImageFilePath );
                Log.e("myApp :: ", "sound.getSound IN ONBIND " + thisSound.getSoundFile() );
                Picasso.with(getApplicationContext())
                        .load(thisUserImageFile)
                        .placeholder(R.drawable.sound_defaul_image)
                        .into(SoundviewImage);

            } else {
                String BloisSoundWebUrl = "http://savoir-ecouter.aprille.net/wp-content/uploads/";
                String thisSoundImageFilePath = BloisSoundWebUrl  + thisSound.getSoundPhoto();
                Log.e("myApp :: ", "sound.getSoundPhoto() IN ONBIND " + thisSoundImageFilePath );
                Picasso.with(getApplicationContext())
                        .load(thisSoundImageFilePath)
                        .placeholder(R.drawable.sound_defaul_image)
                        .into(SoundviewImage);

            }



//            Picasso.with(getApplicationContext())
//                    .load(new File(thisSoundImageFilePath))
//                    .placeholder(R.drawable.sound_defaul_image)
//                    .into(SoundviewImage);


            TextView tvSoundNameDetailTV = (TextView) findViewById(R.id.tVsoundNameSoundDetail);
            tvSoundNameDetailTV.setText(thisSound.getSoundName());

            TextView tvSoundDescDetailTV = (TextView) findViewById(R.id.tVsoundDiscSoundDetail);
            tvSoundDescDetailTV.setText(thisSound.getSoundDesc());


        }

        if (Util.placeInfoExists(thisSound)) {
            Location thisPlace = thisSound.getSoundLocation().first();


            TextView tvPlaceName = (TextView) findViewById(R.id.tvPlaceSoundDetail);
            tvPlaceName.setText(thisPlace.getLocationName());

            TextView tvPlaceAddress = (TextView) findViewById(R.id.tvPlaceAddressSoundDetail);
            tvPlaceAddress.setText(thisPlace.getLocationAddress());



        }


        if (Util.userInfoExists(thisSound)) {

            thisUser = thisSound.getSoundUser().first();

            if (thisUser != null) {
                ImageButton iVuserImageView = (ImageButton) findViewById(R.id.showUserButtonSoundDetail);
                String thisUserImageFilePath = BloisUserDirPath + "/" + thisUser.getUserPhoto();
                File thisUserImageFile = new File(thisSoundImageFilePath);
                if (thisUserImageFile.exists()) {
                    Log.e("myApp :: ", "BloisUerDirPath with user " + thisUserImageFilePath);
                    Log.e("myApp :: ", "thisUser.getUserName() " + thisUser.getUserName());

                    Picasso.with(this)
                            .load(new File(thisUserImageFilePath))
                            .resize(96, 96)
                            .centerCrop()
                            .placeholder(R.drawable.user_default_image)
                            .into(iVuserImageView);
                } else {

                    String BloisSoundWebUrl = "http://savoir-ecouter.aprille.net/wp-content/uploads/";

                    String thisUserImageURLPath = BloisSoundWebUrl  + thisUser.getUserPhoto();
                    Log.e("myApp :: ", "sound.getSoundPhoto() IN ONBIND " + thisSoundImageFilePath );
                    Picasso.with(getApplicationContext())
                            .load(thisUserImageURLPath)
                            .placeholder(R.drawable.user_default_image)
                            .into(iVuserImageView);

                }

//                ImageButton iVuserImageView = (ImageButton) findViewById(R.id.showUserButtonSoundDetail);
//                String thisUserImageFilePath = BloisUserDirPath + "/" + thisUser.getUserPhoto();
//                Log.e("myApp :: ", "BloisUerDirPath with user " + thisUserImageFilePath);
//                Log.e("myApp :: ", "thisUser.getUserName() " + thisUser.getUserName());
//
//                Picasso.with(this)
//                        .load(new File(thisUserImageFilePath))
//                        .resize(96, 96)
//                        .centerCrop()
//                        .placeholder(R.drawable.user_default_image)
//                        .into(iVuserImageView);


                TextView tvUserNameSoundDetailTV = (TextView) findViewById(R.id.tvUserNameSoundDetail);
                tvUserNameSoundDetailTV.setText(thisUser.getUserName());


            }


        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (thisCreatedByPrimaryUser ) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        shareSoundContent();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(view, R.string.exception, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    Snackbar.make(view, R.string.lets_share, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });


        } else {
            fab.setVisibility(View.INVISIBLE);
            ImageButton editImageButtom = (ImageButton) findViewById(R.id.editSoundDetail);
            editImageButtom.setVisibility(View.INVISIBLE);
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
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }



    public void buttonClickAddSoundPhoto(View v)
    {
        if ((permissionCheckCamera == -1) && (permissionCheckStorage == 0)) {
            selectImageNoCamera();
        } else {
            selectImage();
        }


    }

    public void buttonClickEditSoundDetail (View v)
    {



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



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    public void buttonClickPlaySoundDetail(View v) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            ImageButton playButton = (ImageButton) findViewById(R.id.playButtonSoundDetail);
            //        playButton.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        } else {

            if ( thisSound.isLocalizeMedia() )  {
                Log.e("myApp", "onplay inside islocalized mediatrue " + thisSound.getSoundName()) ;

                String thisSoundFileString = BloisSoundDirPath + "/" + thisSound.getSoundFile();
                Uri thisSoundUri = Uri.parse(thisSoundFileString);
                try {
                    mediaPlayer.setDataSource(this, thisSoundUri);
                    mediaPlayer.prepare();
                    ImageButton playButton = (ImageButton) findViewById(R.id.playButtonSoundDetail);
                    playButton .setImageResource(R.drawable.ic_pause_black_24dp);
                } catch (IOException e) {
                    e.printStackTrace();

                }
                mediaPlayer.start();
            } else {     // not localized
                mediaPlayer.reset();
                Log.e("myApp", "onplay inside islocalized mediafalse " + thisSound.getSoundName()) ;
                Log.e("myApp", "onplay inside islocalized sounddesc " + thisSound.getSoundDesc()) ;
                Log.e("myApp", "onplay inside islocalized soundfile " + thisSound.getSoundFile()) ;
                String BloisSoundWebUrl = "http://savoir-ecouter.aprille.net/wp-content/uploads/";
                String thisSoundFileString = BloisSoundWebUrl + "/" + thisSound.getSoundFile();
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
                    ImageButton playButton = (ImageButton) findViewById(R.id.playButtonSoundDetail);
                    playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

        }




    }


    public void buttonClickShowUserInfo(View v) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        Intent intent = new Intent(getApplicationContext(), UserSoundsActivity.class);
        intent.putExtra("userID", thisUser.getUserID());
        startActivity(intent);



    }



    public void buttonClickAddPlaceInfo(View v) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        if (Util.placeInfoExists(thisSound) ) {
            Location thisPlace = thisSound.getSoundLocation().first();
            Intent intent = new Intent(context, AddLocationMapsActivity.class);

            intent.putExtra("placeID", thisPlace.getLocationID());
            startActivity(intent);

        } else {
            Intent i = new Intent(context, AddLocationActivity.class);

            Log.w("myApp", "b4 pressed - about to launch sub-activity");

            // the results are called on widgetActivityCallback
            startActivityForResult(i, ADD_PLACE_REQUEST_CODE);

        }


    }



    public void buttonClickAddPhoto(View v) {
        if (permissionCheckCamera == -1) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
            permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA);
            Log.w("myApp", "permissionCheck value after request" + permissionCheckCamera);

        }
        if ((permissionCheckCamera == -1) && (permissionCheckStorage == 0)) {
            selectImageNoCamera();
        } else {
            selectImage();
        }


    }

    private void selectImage() {
        final String addPhotoDialogTitle = getString(R.string.addPhoto);
        final String takePhotoDialog = getString(R.string.takePhoto);
        final String choosePhotoDialog = getString(R.string.choosePhotoGallery);
        final String cancelDialog = getString(R.string.cancel);


        final CharSequence[] options = { takePhotoDialog, choosePhotoDialog, cancelDialog };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(addPhotoDialogTitle);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(takePhotoDialog ))
                {
                    String thisSoundPhoto = thisSound.getSoundPhoto();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(BloisSoundDir, thisSoundPhoto);
                    Uri photoURI = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals(choosePhotoDialog))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals(cancelDialog)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectImageNoCamera() {
        final String addPhotoDialogTitle = getString(R.string.addPhoto);
        final String choosePhotoDialog = getString(R.string.choosePhotoGallery);
        final String cancelDialog = getString(R.string.cancel);

        final CharSequence[] options = { choosePhotoDialog, cancelDialog };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(addPhotoDialogTitle);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(choosePhotoDialog))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals(cancelDialog)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(BloisSoundDir.exists() && BloisSoundDir.isDirectory()) {
            Log.w("myApp", "sounds directory exist"  );
        } else {
            Log.w("myApp", "sound directory does't exist"  );
            BloisSoundDir.mkdirs();
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {


                File f = new File(BloisSoundDir.toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals(thisSound.getSoundPhoto())) {
                        f = temp;
                        Log.w("myApp", "found temp file"  );
                        break;
                    }
                }
                File SavedFromCameraFile = new File(BloisSoundDir, thisSound.getSoundPhoto());
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(SavedFromCameraFile.getAbsolutePath(),
                            bitmapOptions);
                    SoundviewImage = (ImageView)findViewById(R.id.soundImageViewSoundDetail);
                    SoundviewImage.setImageBitmap(bitmap);

                    OutputStream outFile = null;
                    File file = new File(BloisSoundDir, thisSound.getSoundPhoto());

                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                File savedCameraFile = new File(BloisSoundDir, thisSound.getSoundPhoto());
                String savedCameraFileDirPath = savedCameraFile.toString();
                Picasso.with(this)
                        .load(new File(savedCameraFileDirPath))
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .placeholder(R.drawable.people_placeholder)
                        .into(SoundviewImage);

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap SaveFromGalleryBitmap = (BitmapFactory.decodeFile(picturePath));

                File SavedFromGalleryFile = new File(BloisSoundDir, thisSound.getSoundPhoto());

                OutputStream outFile = null;

                try {
                    outFile = new FileOutputStream(SavedFromGalleryFile);
                    SaveFromGalleryBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }



                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("myApp", "path of image from gallery" + picturePath + "");
                if (thumbnail != null) {
                    SoundviewImage = (ImageView) findViewById(R.id.soundImageViewSoundDetail);
                    SoundviewImage.setImageBitmap(thumbnail);
                }

            } else if (requestCode == 101) {   // this is requestcode for subactivity AddPlaceActivity
                Bundle res = data.getExtras();
                thisPlaceID = res.getString("placeID");
                Log.d("FIRST", "result: "+ thisPlaceID);
                if (thisPlaceID != null) {
                    Location newLocation = realm.where(Location.class).equalTo("locationID", thisPlaceID).findFirst();

                    if (newLocation != null) {
                        realm.beginTransaction();
                        if ((thisSound != null) && (newLocation != null)){
                            newLocation.getLocationSounds().add(thisSound);
                        }
                        realm.commitTransaction();

                        Log.e("myApp", "thisLocation.getLocationName() " + newLocation.getLocationName());
                        TextView tVPLaceNameDetail =(TextView) findViewById(R.id.tvPlaceSoundDetail);
                        tVPLaceNameDetail.setText(newLocation.getLocationName());
                        TextView tvAddressSoundDetail =(TextView) findViewById(R.id.tvPlaceAddressSoundDetail);
                        tvAddressSoundDetail.setText(newLocation.getLocationAddress());

                    }

                }


            }

        }
    }



    public boolean saveSoundImageFile(String imgFileName){

//        SoundviewImageSoundviewImage.buildDrawingCache();
        File file = new File(BloisSoundDir, thisSound.getSoundPhoto());
        if(file.exists() ) {
            Log.w("myApp", "file exist"  );
            return true;
        } else {
            Log.w("myApp", "file  doesn exist"  );
        }

        Bitmap bitmap = SoundviewImage.getDrawingCache();
        Log.w("myApp", "inside saveSoundImageFile" + imgFileName);
        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        if(BloisSoundDir.exists() && BloisSoundDir.isDirectory()) {
            Log.w("myApp", "sounds directory exist"  );
        } else {
            Log.w("myApp", "sound directory does't exist"  );
            BloisSoundDir.mkdirs();

        }

        Log.w("myApp", "path of image from BloisSoundDir " + BloisSoundDir );

        Log.w("myApp", "external storage is writable"  );
        if ( Util.isExternalStorageWritable() ) {
            Log.w("myApp", "external storage is writable"  );
        } else {
            Log.w("myApp", "bummer external storage IS NOT writable"  );
        }
        File fileAlreadyExists = new File(BloisSoundDir, thisSound.getSoundPhoto());
        if(fileAlreadyExists.exists() ) {
            Log.w("myApp", "file exist"  );
            return true;
        } else {
            Log.w("myApp", "file  doesn exist"  );
        }
        Log.w("myApp", "AbsolutePath from file " + file.getAbsolutePath() );

        try {
            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.w("myApp", "Success we seem to have written the sound photo file! "  );
        }
        catch (FileNotFoundException e) {
            Log.w("myApp", "IOException FileNotFoundException" + file.getAbsolutePath() );
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            Log.w("myApp", "IOException catch " + file.getAbsolutePath() );
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    //stuff for sharing via email.....


    public void shareSoundContent() throws Exception {

        // get text data for  email
        String userDataForEmail = "Failed NO User Data !!!\n\n";
        String locationDataForEmail = "Failed NO Location Data !!!\n\n";
        String quadDataForEmail = "Failed NO Quadrant Data !!!\n\n";

        String mailtoAddress = "aprille@aprille.net";
        String subjectLine = getString(R.string.sharing)+ " " + thisSound.getSoundName();
        String permissionTextForEmail = getString(R.string.emailText) + " \n\n" + getString(R.string.soundData) + "\n" ;
        String soundDataForEmail = thisSound.getSoundID()+ "|" + thisSound.getSoundName() + "|" + thisSound.getSoundPhotoDesc()+ "|" + thisSound.getTimeCreated() + thisSound.getSoundDesc() + "|" + thisSound.getSoundFile() + "|" + thisSound.getSoundPhoto()+ "|" + thisSound.getSoundPhotoDesc() +"|" + thisSound.getTimeCreated() + "|" + String.valueOf(thisSound.getSoundLikes()) + "\n\n";
        if(Util.quadInfoExists(thisSound)) {
            Quadrant thisQuad = thisSound.getSoundQuad().first();
            quadDataForEmail = "Sector " + thisQuad.getQuadID() + "\n\n" ;
        } else {
            quadDataForEmail = "Failed NO Quadrant Data !!!\n\n";
        }

        if (Util.userInfoExists(thisSound)){
            thisUser = thisSound.getSoundUser().first();
            userDataForEmail = getString(R.string.userData) +"\n" + thisUser.getUserID() + "|" + thisUser.getUserName()+ "|" + thisUser.getUserDesc()+ "\n\n";
        } else {
            userDataForEmail = "Failed NO User Data !!!\n\n";
        }
        if (Util.placeInfoExists(thisSound)){
            Location thisPlace = thisSound.getSoundLocation().first();
            locationDataForEmail = getString(R.string.locData) +"\n" +  thisPlace.getLocationID() + "|" +thisPlace.getLocationName() + "|" + thisPlace.getLatitude() + "|" + thisPlace.getLongitiude() + "\n";

        } else {
            locationDataForEmail = "Failed NO Location Data !!!\n\n";
        }
        String textForEmail = permissionTextForEmail + soundDataForEmail + quadDataForEmail + userDataForEmail + locationDataForEmail;
        Log.e("myApp",  textForEmail  );


        // make zipfile

        // declare an array for storing the files i.e the path
        // of your source files

        String[] s = new String[2];

        // Type the path of the files in here
        s[0] = thisSoundFilePath;
        s[1] = thisSoundImageFilePath; // /sdcard/ZipDemo/textfile.txt

        String zipFilePath = BloisShareDirPath +"/" + thisSound.getSoundID() + ".vip";

        Util.zip(s, zipFilePath);
        File zipShare = new File (zipFilePath);


        if (zipShare.exists()) {

            Util.email(context, mailtoAddress, mailtoAddress, subjectLine, textForEmail, zipFilePath );

        } else {
            Log.e("myApp", "ZipException catch " + zipFilePath );

        }

    }





}