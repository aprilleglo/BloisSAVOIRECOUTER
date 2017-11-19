package net.aprille.bloissavoirecouter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import models.AppSpecificDetails;
import models.Location;
import models.Quadrant;
import models.Sound;
import models.User;

import static helperfunctions.Util.getCurrDateString;


public class AddSoundActivity extends AppCompatActivity {

    final Context context = this;
    int ADD_PLACE_REQUEST_CODE = 101;
    String thisPlaceID;
    String REQUEST_PLACE_ID;
    int sector;
    String sectorId;

    Location thisLocation;

    Realm realm;
    ImageView viewSoundImage;

    String thisSoundID;
    String thisSoundName ="";
    String thisSoundDesc;
    String thisSoundFile;
    String thisSoundPhoto;
    String thisSoundPhotoDesc = "";
    String thisTimeCreated;
    boolean thisLocalizeMedia = true;
    boolean thisCreatedByPrimaryUser = true;
    int thisSoundLikes = 15;

    String thsPrimaryUserKey;

    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    MediaPlayer mediaPlayer ;

    File soundFilePath;
    Uri photoURI;

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


    public File BloisSoundDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //      get intent
        sector = 16;
        sectorId = "16";
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        sector = extras.getInt("sectorNum");
        sectorId = String.valueOf(sector);
        Log.w("myApp", sectorId);
        //      get intent

        setContentView(R.layout.activity_add_sound);
        // HIDE VIEWS

        Button btnStopREC=(Button)findViewById(R.id.finshRecordButton);
        btnStopREC.setVisibility(View.INVISIBLE);
        Button bntSaveREC=(Button)findViewById(R.id.saveSoundButton);
        bntSaveREC.setVisibility(View.INVISIBLE);
        TextInputLayout bntEditSaveNameLayout = (TextInputLayout)findViewById(R.id.add_sound_name_input_layout);
        bntEditSaveNameLayout.setVisibility(View.INVISIBLE);
        TextInputEditText bntEditSaveName =(TextInputEditText)findViewById(R.id.sound_name_imput);
        bntEditSaveName.setVisibility(View.INVISIBLE);
        TextInputLayout bntEditDescLayout = (TextInputLayout)findViewById(R.id.add_sound_desc_input_layout);
        bntEditDescLayout.setVisibility(View.INVISIBLE);
        TextInputEditText bntEditEditDesc =(TextInputEditText)findViewById(R.id.sound_desc_imput);
        bntEditEditDesc.setVisibility(View.INVISIBLE);


        ImageView bntSoundImage= (ImageView)findViewById(R.id.addSoundImageView);
        bntSoundImage.setVisibility(View.INVISIBLE);
        ImageButton bntSavePhoto=(ImageButton)findViewById(R.id.addimageSoundButton);
        bntSavePhoto.setVisibility(View.INVISIBLE);

        ImageButton bntSoundPlay = (ImageButton) findViewById(R.id.playButton1);
        bntSoundPlay.setVisibility(View.INVISIBLE);
        // HIDE VIEWS

        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
        if(BloisSoundDir.exists() && BloisSoundDir.isDirectory()) {
            Log.w("myApp", "sounds directory exist"  );
        } else {
            Log.w("myApp", "sound directory does't exist"  );
            BloisSoundDir.mkdirs();

        }

        // setup tempSound
        thisSoundID = getCurrDateString();
        thisSoundDesc = "Je t'aime Blois";
        thisSoundFile = thisSoundID +".m4a";
        thisSoundPhoto = thisSoundID +".jpg";
        thisTimeCreated = thisSoundID;


        // Assume check permissions for camera and storage
        permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        Log.w("myApp", "permissionCheck " + permissionCheckCamera );

        permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.w("myApp", "permissionCheckStorage " + permissionCheckStorage );

        permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        Log.w("myApp", "permissionCheckMicrophone " + permissionCheckMicrophone );

        permissionCheckLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        Log.w("myApp", "permissionCheckMicrophone " + permissionCheckLocation );



        if (permissionCheckCamera == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
            permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            Log.w("myApp", "permissionCheck value after request" + permissionCheckCamera );

        }

        if (permissionCheckStorage == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
            permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.w("myApp", "permissionCheckStorage value after request" + permissionCheckStorage);
        }

        if (permissionCheckMicrophone == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
            permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO);
            Log.w("myApp", "permissionCheckMicrophone value after request " + permissionCheckMicrophone);
        }

        if (permissionCheckLocation == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            permissionCheckMicrophone = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            Log.w("myApp", "permissionCheckMicrophone value after request " + permissionCheckMicrophone);
        }
        if ( (permissionCheckStorage == -1) || (permissionCheckMicrophone == -1) || (permissionCheckLocation == -1)) {
            Toast.makeText(this, "Permmission Problem to record please change your app preferences",
                    Toast.LENGTH_LONG).show();


        }

        // Set up Realm not for production

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }

        AppSpecificDetails thisAppDetails = realm.where(AppSpecificDetails.class).findFirst();


        if (thisAppDetails == null) {
            startActivity(new Intent(AddSoundActivity.this, StartupActivity.class));
            Toast.makeText(AddSoundActivity.this, "appDetails in null", Toast.LENGTH_LONG)
                    .show();

        } else {
            Log.w("myApp", ".getPrimaryUserID " + thisAppDetails.getPrimaryUserID());
            Log.w("myApp", "getPrimaryUserName " + thisAppDetails.getPrimaryUserName());
            thsPrimaryUserKey = thisAppDetails.getPrimaryUserID();
        }
//        boolean savedIT = didSaveWithDrawableSoundImage ();
//
//        if  (savedIT) {
//            Log.w("myApp", "check is worked " + savedIT );
//        }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photoURI != null) {
            outState.putString("cameraImageUri", photoURI.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            photoURI = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
    }



    public void buttonClickRecord(View v) {

        MediaRecorderReady();
        Toast.makeText(this, "Recording started",
                Toast.LENGTH_LONG).show();

        Button btnREC = (Button) findViewById(R.id.recordButton1);
        btnREC.setVisibility(View.GONE);
        Button btnStopREC=(Button)findViewById(R.id.finshRecordButton);
        btnStopREC.setVisibility(View.VISIBLE);
        Button btnCancelREC=(Button)findViewById(R.id.cancelAddSoundButton);
        btnCancelREC.setVisibility(View.GONE);

    }

//    public void buttonClickAddPlaceInfo(View v) {
//
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//            mediaPlayer.reset();
//        }
//
//        if (Util.placeInfoExists(thisSound) ) {
//            Location thisPlace = thisSound.getSoundLocation().first();
//            Intent intent = new Intent(context, AddLocationMapsActivity.class);
//
//            intent.putExtra("placeID", thisPlace.getLocationID());
//            startActivity(intent);
//
//        } else {
//            Intent i = new Intent(context, AddLocationActivity.class);
//
//            Log.w("myApp", "b4 pressed - about to launch sub-activity");
//
//            // the results are called on widgetActivityCallback
//            startActivityForResult(i, ADD_PLACE_REQUEST_CODE);
//
//        }
//
//
//    }

    public void MediaRecorderReady(){
        if(BloisSoundDir.exists() && BloisSoundDir.isDirectory()) {
            Log.w("myApp", "sounds directory exist"  );
        } else {
            Log.w("myApp", "sound directory does't exist"  );
            BloisSoundDir.mkdirs();
        }

        soundFilePath = new File(BloisSoundDir, thisSoundFile);
        if(soundFilePath.exists() ) {
            Log.w("myApp", "soundfile exist"  );

        } else {
            Log.w("myApp", " soundfile  doesn exist"  );
            try {
                soundFilePath.createNewFile();
                Log.w("myApp", "createNewFile soundfile exist"  );

            } catch (IOException e) {
                e.printStackTrace();
                Log.w("myApp", "soundfile STILL DONE NOT exist"  );
            }

            Log.w("myApp", "AbsolutePath from file SoundFile) " + soundFilePath.getAbsolutePath() );

        }

 //       AudioSavePathInDevice = soundFile.getAbsolutePath().substring(8);

        ////////////////////////////////////////////////* INCORRECT CODE */
     //   this.mediaRecorder.setOutputFile(this.file.getAbsolutePath());
    /*the above line sets a file url beginning with a "file:///"
    //however, since this setOutputFile requires us to send a
    //string referring to the uri, we will have to get rid of the
    //"file:///" and simply write the uri */
        ////////////////////////////////////////////////* CORRECTED CODE BELOW */
    //    this.mediaRecorder.setOutputFile(this.file.getAbsolutePath().substring(8));
    /*the above line of code extracts the string uri eliminating
    // file:/// */
        AudioSavePathInDevice = soundFilePath.getAbsolutePath();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice); // must have an .m4a extension
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void buttonClickFinishRecord(View v) {

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        Button btnStopREC=(Button)findViewById(R.id.finshRecordButton);
        btnStopREC.setVisibility(View.GONE);



        Toast.makeText(this, R.string.rec_finished,
                Toast.LENGTH_LONG).show();

        TextInputLayout bntEditSaveNameLayout = (TextInputLayout)findViewById(R.id.add_sound_name_input_layout);
        bntEditSaveNameLayout.setVisibility(View.VISIBLE);

        TextInputEditText bntEditSaveName =(TextInputEditText)findViewById(R.id.sound_name_imput);
        bntEditSaveName.setVisibility(View.VISIBLE);

        TextInputLayout bntEditDescLayout = (TextInputLayout)findViewById(R.id.add_sound_desc_input_layout);
        bntEditDescLayout.setVisibility(View.VISIBLE);

        TextInputEditText bntEditDescName =(TextInputEditText)findViewById(R.id.sound_desc_imput);
        bntEditDescName.setVisibility(View.VISIBLE);

        Button bntSaveREC=(Button)findViewById(R.id.saveSoundButton);
        bntSaveREC.setVisibility(View.VISIBLE);

         ImageView bntSoundImage= (ImageView)findViewById(R.id.addSoundImageView);
        bntSoundImage.setVisibility(View.VISIBLE);
        Picasso.with(this)
                .load(R.drawable.people_placeholder)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.people_placeholder)
                .into(bntSoundImage);


        ImageButton bntSavePhoto=(ImageButton)findViewById(R.id.addimageSoundButton);
        bntSavePhoto.setVisibility(View.VISIBLE);


        ImageButton bntSoundPlay = (ImageButton) findViewById(R.id.playButton1);
        bntSoundPlay.setVisibility(View.VISIBLE);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Do the work after completion of audio
                mediaPlayer.reset();
            }
        });



    }

    public void buttonClickAddSoundPhoto(View v)
    {
        if ((permissionCheckCamera == -1) && (permissionCheckStorage == 0)) {
            selectImageNoCamera();
        } else {
            selectImage();
        }


    }

    public void buttonClickCancelRecord(View v) {
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", sector);
        startActivity(intent);
    }



    public void buttonClickPlayAddSound (View v) {


        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            ImageButton playButton = (ImageButton) findViewById(R.id.playButtonSoundDetail);
            //        playButton.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        } else {


            if ( soundFilePath.exists() )  {
                Log.e("myApp", "onplay inside add sound Play " + soundFilePath.toString()) ;


                Uri thisSoundUri = Uri.parse(soundFilePath.toString());
                try {
                    mediaPlayer.setDataSource(this, thisSoundUri);
                    mediaPlayer.prepare();
                    ImageButton playButton = (ImageButton) findViewById(R.id.playButton1);
                    playButton .setImageResource(R.drawable.ic_pause_black_24dp);
                } catch (IOException e) {
                    e.printStackTrace();

                }
                mediaPlayer.start();
            }
        }
    }

    public void buttonClickSaveRecord(View v) {
        setupSound();
        Intent intent = new Intent(getApplicationContext(), SectorSoundsActivity.class);
        intent.putExtra("sectorNum", sector);
        startActivity(intent);
    }

    private void setupSound() {
        EditText myEditSoundText = (EditText) findViewById(R.id.sound_name_imput);
        thisSoundName = myEditSoundText.getText().toString();
        EditText myAboutSoundEditText = (EditText) findViewById(R.id.sound_desc_imput);
        thisSoundDesc = myEditSoundText.getText().toString();
        if (myEditSoundText.getText().toString().trim().isEmpty()) {
            thisSoundName = thisSoundID;
        } else {
            Log.w("myApp", "thisSoundID  " + thisSoundID);
        }
        File checkImageFile = new File(BloisSoundDir, thisSoundFile);
        if( checkImageFile.exists()){
            Quadrant sectorQuadrant = realm.where(Quadrant.class).equalTo("quadID", sectorId).findFirst();
            User thisPrimaryUser = realm.where(User.class).equalTo("userID", thsPrimaryUserKey).findFirst();

            realm.beginTransaction();

            if ( (thisPrimaryUser != null ) && ( sectorQuadrant != null ) ){
                String locationAddress = "";

                Sound newSound1 = realm.createObject(Sound.class, thisSoundID);
                newSound1.setSoundName(thisSoundName);
                newSound1.setSoundDesc(thisSoundDesc);
                newSound1.setSoundFile(thisSoundFile);
                newSound1.setSoundPhoto(thisSoundPhoto);
                newSound1.setTimeCreated(thisSoundID);
                newSound1.setLocalizeMedia(true);
                newSound1.setCreatedByPrimaryUser(true);
                newSound1.setSoundLikes(15);
                thisPrimaryUser.getUserSounds().add(newSound1);
                sectorQuadrant.getQuadSounds().add(newSound1);
                if ((thisPlaceID != null) && (thisLocation != null)){
                    thisLocation.getLocationSounds().add(newSound1);
                    locationAddress = thisLocation.getLocationName() + " " +  thisLocation.getLocationAddress();
                } else {
                    locationAddress = " ";
                }

                String textForSearch = thisSoundName + " " + thisSoundDesc + " " + thisPrimaryUser.getUserName() + " " + thisPrimaryUser.getUserDesc() + " " + locationAddress;
                newSound1.setSoundSearchText(textForSearch);


            }

            realm.commitTransaction();
        }
        if (myEditSoundText.getText().toString().trim().isEmpty()) {
            thisSoundName = thisSoundID;
        } else {
            Log.w("myApp", "thisSoundID  " + thisSoundID );

        }

        Log.w("myApp", "inside setup Sound");


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
                if (options[item].equals(takePhotoDialog))
                {
//                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, m_username);
//                    mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
//                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(BloisSoundDir, thisSoundPhoto);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, thisSoundPhoto);
//                    Uri photoURI = FileProvider.getUriForFile(context,
//                            BuildConfig.APPLICATION_ID + ".provider",
//                            f);
                    photoURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
            if (requestCode == 1 ) {
                File SavedFromCameraFile = new File(BloisSoundDir, thisSoundPhoto);
                File f = new File(BloisSoundDir.toString());
                  for (File temp : f.listFiles()) {
                        if (temp.getName().equals(thisSoundPhoto)) {
                            f = temp;
                            Log.w("myApp", "found temp file"  );
                            break;
                        }
                    }
//                File SavedFromCameraFile = new File(BloisSoundDir, thisSoundPhoto);
                try {
                    System.gc();

                    photoURI = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
//                    Bitmap bitmap = BitmapFactory.decodeFile(SavedFromCameraFile.getAbsolutePath());
//                    viewSoundImage = (ImageView)findViewById(R.id.addSoundImageView);
//                    viewSoundImage.setImageBitmap(bitmap);

                    OutputStream outFile = null;
                    File file = new File(BloisSoundDir, thisSoundPhoto);

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

              File savedCameraFile = new File(BloisSoundDir, thisSoundPhoto);
                String savedCameraFileDirPath = savedCameraFile.toString();
                ImageView viewSoundImage = (ImageView) findViewById(R.id.addSoundImageView);
                Picasso.with(this)
                        .load(new File(savedCameraFileDirPath))
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .placeholder(R.drawable.people_placeholder)
                        .into(viewSoundImage);

            } else if (requestCode == 2) {

                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inJustDecodeBounds = true;

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();

                System.gc();
                Bitmap SaveFromGalleryBitmap;
                SaveFromGalleryBitmap = (BitmapFactory.decodeFile(picturePath));

                File SavedFromGalleryFile = new File(BloisSoundDir, thisSoundPhoto);

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
                String SavedFromGalleryFileDirPath = SavedFromGalleryFile.toString();

                ImageView viewSoundImage = (ImageView) findViewById(R.id.addSoundImageView);
                Picasso.with(this)
                        .load(new File(SavedFromGalleryFileDirPath))
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .placeholder(R.drawable.people_placeholder)
                        .into(viewSoundImage);



            } else if (requestCode == 101) {   // this is requestcode for subactivity AddPlaceActivity
                Bundle res = data.getExtras();
                thisPlaceID = res.getString("placeID");
                Log.d("FIRST", "result: "+ thisPlaceID);
                if (thisPlaceID != null) {
                    Location thisLocation = realm.where(Location.class).equalTo("locationID", thisPlaceID).findFirst();

                    if (thisLocation != null) {

                        Log.e("myApp", "thisLocation.getLocationName() " + thisLocation.getLocationName());

                    }

                }


            }

        }
    }



    public boolean saveSoundImageFile(String imgFileName){

//        viewSoundImage.buildDrawingCache();
        File file = new File(BloisSoundDir, thisSoundPhoto);
        if(file.exists() ) {
            Log.w("myApp", "file exist"  );
            return true;
        } else {
            Log.w("myApp", "file  doesn exist"  );
        }

        Bitmap bitmap = viewSoundImage.getDrawingCache();
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
        File fileAlreadyExists = new File(BloisSoundDir, thisSoundPhoto);
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

//    public boolean didSaveWithDrawableSoundImage (){
//
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sound_defaul_image);
//
//        InputStream is = getResources().openRawResource(R.drawable.sound_defaul_image);
//        Bitmap pisc = BitmapFactory.decodeStream(new BufferedInputStream(is));
//
//        if ( bitmap == null ){
//            Log.w("myApp", "bitmap == null "  );
//        }
//
//        BloisSoundDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/sounds");
//
//        File dest = new File(BloisSoundDir, thisSoundPhoto);
////        Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.ic_launcher);
////        File file = new File(extStorageDirectory, "ic_launcher.PNG");
//        OutputStream outStream;
//
//        try {
//            outStream = new FileOutputStream(dest);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//            outStream.flush();
//            outStream.close();
//            Log.w("myApp", "inside try " + dest.getAbsolutePath().toString() );
//            return true;
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            Log.w("myApp", "inside try FileNotFoundException " + dest.getAbsolutePath() );
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.w("myApp", "IOException " + dest.getAbsolutePath() );
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
////        try {
////            FileOutputStream out;
////            out = new FileOutputStream(dest);
////            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
////            out.flush();
////            out.close();
////
////            return true;
////        } catch (FileNotFoundException e) {
////            // TODO Auto-generated catch block
////            Log.w("myApp", "FileNotFoundException " + dest.getAbsolutePath() );
////            e.printStackTrace();
////        } catch (IOException e) {
////            Log.w("myApp", "IOException " + dest.getAbsolutePath() );
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
//
//        return false;
//    }
//




}
