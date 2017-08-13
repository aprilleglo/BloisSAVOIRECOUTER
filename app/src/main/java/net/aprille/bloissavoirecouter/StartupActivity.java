package net.aprille.bloissavoirecouter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import models.AppSpecificDetails;
import models.User;
import helperfunctions.Util;

public class StartupActivity extends AppCompatActivity {


    final Context context = this;
    Realm realm;
    ImageView viewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        viewImage = (ImageView)findViewById(R.id.primaryUserImageView);

//        not for production because deletes WHOLE REALM IF PROBLEM !!!!

        try {
            realm = Realm.getDefaultInstance();
        } catch (IllegalStateException fuckYouTooAndroid) {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(realm != null) { // guard against weird low-budget phones
            realm.close();
            realm = null;
        }
    }

    public void buttonClickSave(View v)
    {
        setupPrimaryUser();
        Intent intent = new Intent(getApplicationContext(), PlanActivity.class);
        startActivity(intent);
    }


    public void buttonClickAddPhoto(View v)
    {
        selectImage();

    }

    private void setupPrimaryUser() {

        AppSpecificDetails newAppSpecificDetails;
        User newPrimaryUser;
        String newPrimaryKey;


        EditText myEditText = (EditText) findViewById(R.id.signup_input_name);
        EditText myAboutEditText = (EditText) findViewById(R.id.signup_input_about);
        if (myEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "You did not enter a value!", Toast.LENGTH_LONG).show();
            return;
        } else {
            newPrimaryKey = Util.getCurrDateString().trim();
            Log.w("myApp", "newPrimaryKey" + newPrimaryKey);

            if ( saveUserImageFile(newPrimaryKey) ){

                // saveUserImageFile returned a true and save primary image file
                String userImagefileName = newPrimaryKey +".jpg";

                realm.beginTransaction();
                newAppSpecificDetails = realm.createObject(AppSpecificDetails.class, newPrimaryKey);
                newAppSpecificDetails.setPrimaryUserID(newPrimaryKey);
                newAppSpecificDetails.setPrimaryUserName(myEditText.getText().toString());
                newAppSpecificDetails.setPrimaryUserAbout(myAboutEditText.getText().toString());
                newAppSpecificDetails.setPrimaryPhoto(userImagefileName);

                newPrimaryUser = realm.createObject(User.class, newPrimaryKey);
                newPrimaryUser.setUserName(myEditText.getText().toString());
                newPrimaryUser.setUserDesc(myAboutEditText.getText().toString());
                newPrimaryUser.setTimeJoined(newPrimaryKey);
                newPrimaryUser.setUserPhoto(userImagefileName);
                newPrimaryUser.setPrimaryUserBoolean(true);
                realm.commitTransaction();

            }

        }



        Log.w("myApp", "inside setup Primary User");


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
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    viewImage.setImageBitmap(bitmap);

                    String path = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
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
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("myApp", "path of image from gallery" + picturePath + "");
                viewImage.setImageBitmap(thumbnail);
            }
        }
    }

    public boolean saveUserImageFile(String imgFileName){
        File BloisUserDir;
        viewImage.buildDrawingCache();
        Bitmap bitmap = viewImage.getDrawingCache();

        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData/users");
        Log.w("myApp", "path of image from BloisUserDir " + BloisUserDir );
        String filename = imgFileName + ".jpg";

        if ( Util.isExternalStorageWritable() ) {
            Log.w("myApp", "external storage is writable"  );
        } else {
            Log.w("myApp", "bummer external storage IS NOT writable"  );
        }
        File file = new File(BloisUserDir, filename);
        Log.w("myApp", "AbsolutePath from file " + file.getAbsolutePath() );

        try {
            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.w("myApp", "Success we seem to have written the primary user! "  );
        }
        catch (FileNotFoundException e) {
            Log.w("myApp", "IOException " + file.getAbsolutePath() );
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            Log.w("myApp", "IOException " + file.getAbsolutePath() );
            e.printStackTrace();
            return false;
        }
        return true;
    }




}
