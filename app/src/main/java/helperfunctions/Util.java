package helperfunctions;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

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



}
