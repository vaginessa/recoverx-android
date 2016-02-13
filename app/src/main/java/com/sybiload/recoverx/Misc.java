package com.sybiload.recoverx;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Scanner;

public class Misc
{

    public static void log(String message)
    {
        if (BuildConfig.DEBUG)
        {
            Log.e("com.sybiload.recoverx", message);
        }
    }

    public static double pxToDp(final Context context, final double px)
    {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static double dpToPx(final Context context, final double dp)
    {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean isConnected(Context ctx)
    {
        NetworkInfo info = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return info != null ? info.isConnected() : false;
    }

    public static boolean parseJson()
    {
        try
        {
            // reset Static.allRecovery
            Static.allRecovery.clear();

            String str = new String();

            String s = "http://api.sybiload.com/recoverx/manifest.php";
            URL url = new URL(s);

            Scanner scan = new Scanner(url.openStream());

            while (scan.hasNext())
            {
                str += scan.nextLine();
            }

            scan.close();

            // build a JSON object
            JSONObject obj = new JSONObject(str);

            if (!obj.getString("notice").isEmpty())
            {
                Static.notice = obj.getString("notice");
            }

            JSONArray all_devices = obj.getJSONArray("devices");

            for (int i = 0; i < all_devices.length(); i++)
            {
                JSONObject myObj = all_devices.getJSONObject(i);

                if (myObj.getString("namecode").equals("maguro"))
                {
                    Recovery recovery = new Recovery(
                            myObj.getString("brand"),
                            myObj.getString("name"),
                            myObj.getString("namecode"),
                            myObj.getString("recovery"),
                            myObj.getString("recoverycode"),
                            myObj.getString("version"),
                            myObj.getString("info"),
                            myObj.getString("maintainer"),
                            myObj.getString("date"),
                            myObj.getString("trusted").equals("1") ? true : false,
                            myObj.getString("dev").equals("1") ? true : false,
                            myObj.getString("link"),
                            myObj.getString("command")
                    );

                    Static.allRecovery.add(recovery);
                }
            }

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
