package com.ledelete.recoverx;
import android.os.AsyncTask;

import java.io.*;
import java.util.ArrayList;

public final class Usage {

    public static ArrayList<String> suBoard = new ArrayList<String>();

    public Void debugSystemOut(String log)
    {
        if(BuildConfig.DEBUG)
        {
            System.out.println(log);
        }
        return null;
    }

    public boolean checkSu() {
        try
        {
            Process suBin = Runtime.getRuntime().exec("su");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(suBin.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(suBin.getInputStream()));

            // Getting the id of the current user to check if this is root
            out.write("id\n");
            out.flush();

            String uid = in.readLine();

            if (uid == null)
            {
                // Can't get root, rejected by the user
                return false;
            }
            else if (uid.contains("uid=0"))
            {
                // Got root access
                out.flush();
                return true;
            }
            else
            {
                // Can't get root
                out.flush();
                return false;
            }
        }
        catch (Exception e)
        {
            // Can't get root access, may due to corrupted binaries

            return false;
        }
    }

    public boolean execSu() {

        // ALWAYS USE THIS FUNCTION WITH ASYNCTASK

        if (suBoard.size() != 0)
        {
            try
            {
                Process suBin = Runtime.getRuntime().exec("su");

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(suBin.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(suBin.getInputStream()));

                // Getting the id of the current user to check if this is root
                out.write("id\n");
                out.flush();

                String uid = in.readLine();

                if (uid == null)
                {
                    // Can't get root, rejected by the user
                    debugSystemOut("Can't get root access");
                    return false;
                }
                else if (uid.contains("uid=0"))
                {
                    // Got root access
                    out.flush();
                    // Execute the cmd variable

                    for (int i = 0; i < suBoard.size(); i++)
                    {
                        out.write(suBoard.get(i) + "\n");
                        out.flush();
                    }

                    suBoard.clear();

                    // Let a small time to the device due to fragments
                    try
                    {
                        Thread.sleep(400);
                    }
                    catch (Exception e)
                    {
                        debugSystemOut(e.toString());
                    }

                    return true;
                }
                else
                {
                    // Can't get root
                    debugSystemOut("Can't get root access");
                    out.flush();
                    return false;
                }
            }
            catch (Exception e)
            {
                // Can't get root access, may due to corrupted binaries
                return false;
            }
        }
        return false;
    }

    public boolean checkExist(String path)
    {
        File file = new File(path);

        if (file.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}