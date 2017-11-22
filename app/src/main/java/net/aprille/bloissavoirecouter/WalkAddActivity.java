package net.aprille.bloissavoirecouter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import models.AppSpecificDetails;
import models.OrderedWalkSound;
import models.Walk;

import static net.aprille.bloissavoirecouter.R.id.addWalkImageView;

public class WalkAddActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;

    String thisPrimaryUserKey;
    String thisWalkID;
    Walk thisWalk;
    Realm realm;

    public String DirectoryFinal;
    public File BloisUserDir;
  //  public File BloisSoundDir;
    public File BloisWalkDir;

    public File BloisDir;
    public String BloisUserDirPath;
   // public String BloisSoundDirPath;
  //  public String thisSoundImageFilePath;
    public String thisIconThumbFilePath;

    String BloisSoundWebUrl;
    boolean isLandscape;
    Context context = this;

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

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int PICK_FROM_GALLERY = 2;
    int CAMERA_PIC_REQUEST = 1337;
    Bitmap thumbnail = null;
    private static final int OG = 4;

    private static final int CAMERA_IMAGE_CAPTURE = 0;
    Uri u;
    ImageView imgview;
    // int z=0;
    String z = null;
    byte b[];
    String largeImagePath = "";
    Uri uriLargeImage;
    Uri uriThumbnailImage;
    Cursor myCursor;

    CheckBox checkAccess;
    CheckBox checkLoop;
    ImageView accessIconIV;
    ImageView walkImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Log.e("myApp", "(float)displayMetrics.widthPixels "+ String.valueOf((float)displayMetrics.widthPixels));
        Log.e("myApp", "(float)displayMetrics.heightPixels "+ String.valueOf((float)displayMetrics.heightPixels));


        //      get intent

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        thisWalkID = extras.getString("walkID");
        Log.e("myApp", "walkID " + thisWalkID );


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

        // Make sure walk directory exists

        BloisWalkDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/walks");
        if(BloisWalkDir.exists() && BloisWalkDir.isDirectory()) {
            Log.w("myApp", "sounds directory exist"  );
        } else {
            Log.w("myApp", "sound directory does't exist"  );
            BloisWalkDir.mkdirs();

        }



        // setup walkID  and walkCreatorID;


        AppSpecificDetails thisAppDetails = realm.where(AppSpecificDetails.class).findFirst();


        if (thisAppDetails == null) {
            startActivity(new Intent(WalkAddActivity.this, StartupActivity.class));
            Toast.makeText(WalkAddActivity.this, "appDetails in null", Toast.LENGTH_LONG)
                    .show();

        } else {
            Log.w("myApp", ".getPrimaryUserID " + thisAppDetails.getPrimaryUserID());
            Log.w("myApp", "getPrimaryUserName " + thisAppDetails.getPrimaryUserName());
            thisPrimaryUserKey = thisAppDetails.getPrimaryUserID();
        }

        thisWalk = realm.where(Walk.class).equalTo("walkID", thisWalkID).findFirst();

        accessIconIV = (ImageView) findViewById(R.id.walk_add_icon_access);
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_terrain_black_24dp)
                .into(accessIconIV);

        if (thisWalk == null) { //create the walk
            Log.e("myApp", "thisWalk IS NOT valid " + thisWalkID );
            realm.beginTransaction();
            thisWalk = realm.createObject(Walk.class, thisWalkID);
            thisWalk.setWalkCreatorID(thisPrimaryUserKey);
            thisWalk.setWalkPhoto(thisWalkID+".jpg");
            realm.commitTransaction();

        } else {   //set up values during life cycle

            Log.e("myApp", "thisWalk is valid " );
            TextInputEditText walkNameTImput = (TextInputEditText) findViewById(R.id.walk_name_imput);
            if (thisWalk.getWalkName() != null) {
                walkNameTImput.setText(thisWalk.getWalkName());
            }
            TextInputEditText walkDescriptionTImput = (TextInputEditText) findViewById(R.id.walk_desc_imput);
            if (thisWalk.getWalkName() != null) {
                walkDescriptionTImput.setText(thisWalk.getWalkDesc());
            }



            checkAccess = (CheckBox) findViewById(R.id.checkBoxAcess);
            checkAccess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if ( isChecked )
                    {
                        Log.e("myApp", "access on check " );
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.ic_accessible_black_24dp)
                                .into(accessIconIV);

                    } else {
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.ic_terrain_black_24dp)
                                .into(accessIconIV);
                    }

                }
            });
            if ( thisWalk.isAccessiable() ) {
                Picasso.with(getApplicationContext())
                        .load(R.drawable.ic_accessible_black_24dp)
                        .into(accessIconIV);
                checkAccess.setChecked(true);
                realm.beginTransaction();
                thisWalk.setAccessiable(true);
                realm.commitTransaction();


            } else {
                Picasso.with(getApplicationContext())
                        .load(R.drawable.ic_terrain_black_24dp)
                        .into(accessIconIV);
                checkAccess.setChecked(false);
                realm.beginTransaction();
                thisWalk.setAccessiable(false);
                realm.commitTransaction();
            }

            checkLoop = (CheckBox) findViewById(R.id.checkBoxLoop);
            checkLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if ( isChecked ) {
                        Log.e("myApp", "access on check " );
                        realm.beginTransaction();
                        thisWalk.setLoop(true);
                        realm.commitTransaction();


                    } else {
                        Log.e("myApp", "loop UNCHECK ! " );
                        realm.beginTransaction();
                        thisWalk.setLoop(false);
                        realm.commitTransaction();
                    }

                }
            });
            if ( thisWalk.isLoop() ) {
                checkLoop.setChecked(true);
            }  else {
                checkLoop.setChecked(false);
            }
            if (thisWalk.getWalkDistance() != null) {

            }

            File walkphotoFile = new File(BloisWalkDir, thisWalk.getWalkPhoto());
            int currentWidth = displayMetrics.widthPixels;
            int currentHeight = displayMetrics.widthPixels / 3;
            ImageView addWalkImageViewIV = (ImageView) findViewById(R.id.addWalkImageView);
            if (walkphotoFile.exists()) {
                    Log.e("myApp :: ", "thisWalkFileName  in on create " );
                    Picasso.with(getApplicationContext())
                            .load(walkphotoFile)
                            .resize(currentWidth, currentHeight)
                            .centerCrop()
                            .placeholder(R.drawable.sound_defaul_image)
                            .into(addWalkImageViewIV);
            } else {
                String thisWalkFileName = BloisSoundWebUrl + "/" + thisWalk.getWalkPhoto();
                Picasso.with(getApplicationContext())
                        .load(thisWalkFileName)
                        .resize(currentWidth, currentHeight)
                        .centerCrop()
                        .placeholder(R.drawable.user_default_image)
                        .into(addWalkImageViewIV);

            }



        }


        // set up adaptor

        RealmResults<OrderedWalkSound> OrderedWalkResults = realm.where(OrderedWalkSound.class).equalTo("orderedWalkID", thisWalkID).findAll();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("message",largeImagePath );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
                updateWalkText();
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
                updateWalkText();
                startActivity(intentPlan);
                return true;


            case R.id.explore_keyword:
                // User chose search by keyword
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                updateWalkText();
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
                updateWalkText();

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
                updateWalkText();
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





    public void buttonCalculateDistances(View v)
    {



    }



    public void buttonClickSaveWalk(View v)
    {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        updateWalkText();


    }


    public void buttonClickAddWalkPhoto(View v)
    {
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
                if (options[item].equals(takePhotoDialog))
                {
                    String BX1 =  android.os.Build.MANUFACTURER;
                    Log.e("myApp", "inside samsung exception " + "Device man "+ BX1);

                    if(BX1.equalsIgnoreCase("samsung")) {
                        Log.e("myApp", "inside samsung exception " + "Device man "+ BX1);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA_IMAGE_CAPTURE);

                    } else {
                        Log.e("myApp", "inside OTHER " + "Device man "+ BX1);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                        Uri photoURI = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                f);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoURI));
                        startActivityForResult(intent, 1);

                    }

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
        updateWalkText();

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

        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == -1) ) {
            Log.e("myApp", "we have a problem with result code"  );
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                Log.w("myApp", "inside result RESULT_OK "+ String.valueOf(RESULT_OK) );
                File f = new File(BloisWalkDir.toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals(thisWalk.getWalkPhoto())) {
                        f = temp;
                        Log.e("myApp", "found temp file"  );
                        break;
                    }
                }
                File SavedFromCameraFile = new File(BloisWalkDir, thisWalk.getWalkPhoto());
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(SavedFromCameraFile.getAbsolutePath(),
                            bitmapOptions);
                    walkImageView = (ImageView)findViewById(addWalkImageView);
                    walkImageView .setImageBitmap(bitmap);

                    OutputStream outFile = null;
                    File file = new File(BloisWalkDir, thisWalk.getWalkPhoto());

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

                // get metrics
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                File walkphotoFile = new File(BloisWalkDir, thisWalk.getWalkPhoto());
                int currentWidth = displayMetrics.widthPixels;
                int currentHeight = displayMetrics.widthPixels / 3;
                ImageView addWalkImageViewIV = (ImageView) findViewById(R.id.addWalkImageView);
                if (walkphotoFile.exists()) {
                    Log.e("myApp :: ", "thisWalkFileName  in on create " );
                    Picasso.with(getApplicationContext())
                            .load(walkphotoFile)
                            .resize(currentWidth, currentHeight)
                            .centerCrop()
                            .placeholder(R.drawable.sound_defaul_image)
                            .into(addWalkImageViewIV);
                } else {
                    String thisWalkFileName = BloisSoundWebUrl + "/" + thisWalk.getWalkPhoto();
                    Picasso.with(getApplicationContext())
                            .load(thisWalkFileName)
                            .resize(currentWidth, currentHeight)
                            .centerCrop()
                            .placeholder(R.drawable.user_default_image)
                            .into(addWalkImageViewIV);
                }

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap SaveFromGalleryBitmap = (BitmapFactory.decodeFile(picturePath));

                File SavedFromGalleryFile = new File(BloisWalkDir, thisWalk.getWalkPhoto());

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

                File savedCameraFile = new File(BloisWalkDir, thisWalk.getWalkPhoto());
                String savedCameraFileDirPath = savedCameraFile.toString();
                // get metrics
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                File walkphotoFile = new File(BloisWalkDir, thisWalk.getWalkPhoto());
                int currentWidth = displayMetrics.widthPixels;
                int currentHeight = displayMetrics.widthPixels / 3;
                ImageView addWalkImageViewIV = (ImageView) findViewById(R.id.addWalkImageView);
                if (walkphotoFile.exists()) {
                    Log.e("myApp :: ", "thisWalkFileName  in on create " );
                    Picasso.with(getApplicationContext())
                            .load(walkphotoFile)
                            .resize(currentWidth, currentHeight)
                            .centerCrop()
                            .placeholder(R.drawable.sound_defaul_image)
                            .into(addWalkImageViewIV);
                } else {
                    String thisWalkFileName = BloisSoundWebUrl + "/" + thisWalk.getWalkPhoto();
                    Picasso.with(getApplicationContext())
                            .load(thisWalkFileName)
                            .resize(currentWidth, currentHeight)
                            .centerCrop()
                            .placeholder(R.drawable.user_default_image)
                            .into(addWalkImageViewIV);
                }


            } else if ((requestCode == CAMERA_IMAGE_CAPTURE) && (resultCode== Activity.RESULT_OK) ) {  // this is requestcode for samsung phone
                // Describe the columns you'd like to have returned. Selecting from the Thumbnails location gives you both the Thumbnail Image ID, as well as the original image ID
                Log.e("myApp", "requestCode == CAMERA_IMAGE_CAPTURE" );

                String[] projection = {
                        MediaStore.Images.Thumbnails._ID,  // The columns we want
                        MediaStore.Images.Thumbnails.IMAGE_ID,
                        MediaStore.Images.Thumbnails.KIND,
                        MediaStore.Images.Thumbnails.DATA};
                String selection = MediaStore.Images.Thumbnails.KIND + "="  + // Select only mini's
                        MediaStore.Images.Thumbnails.MINI_KIND;

                String sort = MediaStore.Images.Thumbnails._ID + " DESC";

//At the moment, this is a bit of a hack, as I'm returning ALL images, and just taking the latest one. There is a better way to narrow this down I think with a WHERE clause which is currently the selection variable
                Cursor myCursor = this.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, selection, null, sort);

                long imageId = 0l;
                long thumbnailImageId = 0l;
                String thumbnailPath = "";

                try {
                    myCursor.moveToFirst();
                    imageId = myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));
                    thumbnailImageId = myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID));
                    thumbnailPath = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
                } finally {
                    myCursor.close();
                }

                //Create new Cursor to obtain the file Path for the large image

                String[] largeFileProjection = {
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA
                };

                String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
                myCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, largeFileProjection, null, null, largeFileSort);
                largeImagePath = "";

                try {
                    myCursor.moveToFirst();

                    //This will actually give yo uthe file path location of the image.
                    largeImagePath = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                } finally {
                    myCursor.close();
                }
                // These are the two URI's you'll be interested in. They give you a handle to the actual images
                uriLargeImage = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
                uriThumbnailImage = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, String.valueOf(thumbnailImageId));

                // I've left out the remaining code, as all I do is assign the URI's to my own objects anyways...
                // Toast.makeText(this, ""+largeImagePath, Toast.LENGTH_LONG).show();
                // Toast.makeText(this, ""+uriLargeImage, Toast.LENGTH_LONG).show();
                // Toast.makeText(this, ""+uriThumbnailImage, Toast.LENGTH_LONG).show();


                if (largeImagePath != null) {
                    Toast.makeText(this, "LARGE YES"+largeImagePath, Toast.LENGTH_LONG).show();
                    Log.e("myApp", "LARGE YES in  largeImagePath != null " +largeImagePath );

                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(largeImagePath,
                            bitmapOptions);


                    OutputStream outFile = null;
                    File file = new File(BloisWalkDir, thisWalk.getWalkPhoto());

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


//                    BitmapFactory.Options opts = new BitmapFactory.Options();
//                    opts.inSampleSize = OG;
//                    thumbnail = BitmapFactory.decodeFile((largeImagePath), opts);
                    System.gc();
                    if (thumbnail != null) {
                        Toast.makeText(this, "Try Without Saved Instance", Toast.LENGTH_LONG).show();
                        Log.e("myApp", "Try Without Saved Instance in  thumbnail != null" );
                        //imageCam(thumbnail);
                    }
                }
                if (uriLargeImage != null) {
                    Toast.makeText(this, ""+uriLargeImage, Toast.LENGTH_LONG).show();
                }
                if (uriThumbnailImage != null) {
                    Toast.makeText(this, ""+uriThumbnailImage, Toast.LENGTH_LONG).show();
                }
                File savedCameraFile = new File(BloisWalkDir, thisWalk.getWalkPhoto());
                String savedCameraFileDirPath = savedCameraFile.toString();
//                Picasso.with(this)
//                        .load(new File(savedCameraFileDirPath))
//                        .memoryPolicy(MemoryPolicy.NO_CACHE)
//                        .placeholder(R.drawable.people_placeholder)
//                        .into(walkImageView);



            }

        }


    }

    public void updateWalkText() {


        TextInputEditText walkNameTImput = (TextInputEditText) findViewById(R.id.walk_name_imput);
        if (walkNameTImput.getText() != null) {
            realm.beginTransaction();
            thisWalk.setWalkName(walkNameTImput.getText().toString());
            realm.commitTransaction();
        }
        TextInputEditText walkDescriptionTImput = (TextInputEditText) findViewById(R.id.walk_desc_imput);
        if (walkDescriptionTImput.getText() != null) {
            realm.beginTransaction();
            thisWalk.setWalkDesc(walkDescriptionTImput.getText().toString());
        }

    }

}




