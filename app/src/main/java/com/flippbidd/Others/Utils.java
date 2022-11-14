package com.flippbidd.Others;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Html;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.flippbidd.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jk on 22/04/16.
 */

public class Utils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    private static final boolean APP_TEST_MODE = true;
    //! to activate internet checking set APP_MAP_MODE to false
    public static boolean APP_MAP_MODE = false;
    private static AlertDialog dialog = null;
    public static Typeface typeface;


    public static Point getWindowSize(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();

        display.getSize(size);

        return size;

    }

    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, TimeZone.getDefault().getOffset(calendar.getTimeInMillis()));
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); //HH:mm
        Date currenTimeZone = new Date((long) time * 1000);
        String localTime = sdf.format(currenTimeZone);
        return localTime;
    }

    public static String getConvertData(String date) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("MM/dd/yyyy"); //HH:mm

        Date date1 = null;
        String dateIs = "";
        try {
            date1 = input.parse(date);

            dateIs = output.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateIs;
    }

    public static String getDays(String date1,String date2) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//dd/M/yyyy hh:mm:ss
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        String formattedDate = df.format(c);

        try {
            Date startDate = simpleDateFormat.parse(date1);
            Date endDate = simpleDateFormat.parse(formattedDate);

            //milliseconds
            long different = endDate.getTime() - startDate.getTime();

            System.out.println("startDate : " + startDate);
            System.out.println("endDate : " + endDate);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

            return "" + elapsedDays;


        } catch (ParseException e) {
            e.printStackTrace();
        }

//1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400

        return "";
    }

    public static String getConvertData1(String date, String inputFormat, String outPutFormat) {
        SimpleDateFormat input = new SimpleDateFormat(inputFormat);//"mm-dd-yyyy"
        SimpleDateFormat output = new SimpleDateFormat(outPutFormat); //HH:mm//"MM/dd/yyyy"

        Date date1 = null;
        String dateIs = "";
        try {
            date1 = input.parse(date);

            dateIs = output.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateIs;
    }


    public interface InternetConnectionListener {

        public void onConnectionEstablished(int code);

        public void onUserCanceled(int code);
    }


    public static String SHA256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            byte[] digest = md.digest();
            return Base64.encodeToString(digest, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        }
        return null;
    }


    //temp 256hash
    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setImage(Activity act, String imagename, String foldername, ImageView iv) {
        int reschangeID = act.getResources().getIdentifier(imagename, foldername, act.getPackageName());
        iv.setImageResource(reschangeID);
    }


    public static void shareCommon(Activity acContext, String appName, String content, String
            uri) {

        String Body = appName + " " + content;
        Intent share = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(uri);
//        Uri playstoreUrl = Uri.parse("https://play.google.com/store/apps/details?id="+acContext.getPackageName());
        Uri playstoreUrl = Uri.parse("www.flippbiddapp.com");
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, Body + "\n" + screenshotUri + "\nDownload FlippBidd @\n" + playstoreUrl);
        acContext.startActivity(Intent.createChooser(share, "Share via"));
    }


    //CHECK METHOD FOR WHATSUP OR FACEBOOK APP STILL ON DEVICE OR NOT?
    private boolean appInstalledOrNot(String uri, Activity activity) {
        PackageManager pm = activity.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, 0);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    public static boolean isConnectedToInternet(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {

                for (int i = 0; i < info.length; i++)

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        return true;

                    }

            }

        }

        return false;
    }

    //  check if online or not
    public static boolean isOnline(Context cContext) {

        try {

            ConnectivityManager cm = (ConnectivityManager) cContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }


    public static void phoneCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }


    public static void browseUrl(Context context, String url) {
        if (!url.startsWith("http") && !url.startsWith("https"))
            url = "http://" + url;
        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
        openBrowserIntent.setData(Uri.parse(url));
        context.startActivity(openBrowserIntent);
    }


    /**
     * @param context   the application context
     * @param imageName the name of the image file
     * @return \c Uri object
     * @brief methods for getting \c Uri of a drawable from file name
     */
    public static String getDrawableFromFileName(Context context, String imageName) {
        return Uri.parse("android.resource://" + context.getPackageName() + "/mipmap/" + imageName).toString();
    }


    /**
     * @param context the application context
     * @param name    the subject of the mail to be sent
     * @param address the specified email address
     * @brief methods for sending a mail via mail intent
     */
    public static void mailTo(Context context, String name, String address, String description) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, name + " App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(description));
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));

    }


    public static void mailToWithImage(Context context, String name, String address, String
            description, Uri uri) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact with " + name);
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(description));


//        emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml(emailtext.toString()));
//        emailIntent.setType("image/jpeg");

        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));

    }


    /**
     * @param activity the context of the activity
     * @brief methods for showing the soft keyboard by forced
     */
    public static void showSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
        }


    }

    /**
     * @param context the application context
     * @return true or false
     * @brief methods for checking the device's gps is enable or not
     */
    public static boolean isGpsEnable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        // Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }


    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static void showAlertDialog(Context context, String title, String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    public static void showAlertDialogs(Activity activity, String title, String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


}

