package helperfunctions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import models.Keyword;
import models.Location;
import models.Quadrant;
import models.Sound;
import models.User;

/**
 * Created by aprillebestglover on 8/9/17.
 */


public class Util {


    public static String getCurrDateString() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        String DateToStr = format.format(curDate);
        return DateToStr;
    }


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 380);
        return noOfColumns;
    }

    /**
     * @author Lonkly
     * @param variableName - name of drawable, e.g R.drawable.<b>image</b>
     * @param с - class of resource, e.g R.drawable.class or R.raw.class
     * @return integer id of resource
     */

    public static int getResId(String variableName, Class<?> с) {

        Field field = null;
        int resId = 0;
        try {
            field = с.getField(variableName);
            try {
                resId = field.getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resId;

    }

    public void addNewUsers() {
        // userID|userName|userBio|userPhoto


    }


    public static void email(Context context, String emailTo, String emailCC,
                             String subject, String emailText, String zipMailFilePath)
    {
        //need to "send multiple" to get more than one attachment

        File zippyFile = new File(zipMailFilePath);

        Uri uriToZip = Uri.fromFile(zippyFile);

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{emailTo});
        emailIntent.putExtra(android.content.Intent.EXTRA_CC,
                new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        // emailIntent.setType("application/zip");
        emailIntent.setType("*/*");
        emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, uriToZip);

 //       emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(zipMailFilePath));

        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public static void zip(String[] _files, String zipFileName) {

        int BUFFER = 2048;

        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateCSVOnSD(Context context, String sFileName, String sBody) {

        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BloisData");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);


            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(gpxfile), "utf-8");

                outputStreamWriter.write(sBody);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("my app", "File write failed: " + e.toString());
            }

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(sBody), "UTF-8"
            ));
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(sBody);
//            writer.flush();
//            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean placeInfoExists(Sound thisSound) {
        Location tryPlace;
        try {
            tryPlace = thisSound.getSoundLocation().first();
            return true;
        } catch (IndexOutOfBoundsException e) {
            Log.e("IndexOutOfBounds","IndexOutOfBoundsException"+ e);
            return false;
        }

    }

    public static boolean userInfoExists(Sound thisSound) {
        User tryUser;
        try {
            tryUser = thisSound.getSoundUser().first();
            return true;
        } catch (IndexOutOfBoundsException e) {
            Log.e("IndexOutOfBounds","IndexOutOfBoundsException"+ e);
            return false;
        }

    }

    public static boolean quadInfoExists(Sound thisSound) {
        Quadrant tryQuad;
        try {
            tryQuad = thisSound.getSoundQuad().first();
            return true;
        } catch (IndexOutOfBoundsException e) {
            Log.e("IndexOutOfBounds","IndexOutOfBoundsException"+ e);
            return false;
        }

    }

    public static boolean keywordInfoExists(Sound thisSound) {
        Keyword trykeyword;
        try {
            trykeyword = thisSound.getKeywords().first();
            return true;
        } catch (IndexOutOfBoundsException e) {
            Log.e("IndexOutOfBounds","IndexOutOfBoundsException"+ e);
            return false;
        }

    }






}
