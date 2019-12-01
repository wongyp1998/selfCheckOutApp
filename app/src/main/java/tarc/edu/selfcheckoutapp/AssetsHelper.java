package tarc.edu.selfcheckoutapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AssetsHelper
{
    public static String getContent(Context context, String fileName)
    {
        try
        {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;

            System.out.print(Result);
            return Result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
