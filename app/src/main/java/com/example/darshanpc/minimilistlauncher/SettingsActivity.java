package com.example.darshanpc.minimilistlauncher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);


            Preference preference_edit_favourite = findPreference(getString(R.string.key_edit_favourite));
            preference_edit_favourite.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    editFavourite(getActivity());
                    return true;
                }
            });

            Preference preference_edit_color=findPreference(getString(R.string.key_edit_color));
            preference_edit_color.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    editSearchColor(getActivity());
                    return true;
                }
            });
        }
    }
    public static void editFavourite(Context context) {
        final MainActivity mainActivity=new MainActivity();
        final DatabaseHelper dbhelper=new DatabaseHelper(context);
        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(context);
        String[] temp_apps=new String[MainActivity.appnames.size()];
        final boolean[] checked_apps=new boolean[MainActivity.appnames.size()];
        final ArrayList favouriteApps=dbhelper.get_favourite_app();
        for(int j=0;j<MainActivity.appnames.size();j++)
        {
            temp_apps[j]=MainActivity.appnames.get(j);
            if(favouriteApps.contains(temp_apps[j]))
            {
                checked_apps[j]=true;
            }
            else
            {
                checked_apps[j]=false;
            }
        }
        builder.setMultiChoiceItems(temp_apps, checked_apps, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                checked_apps[which] = isChecked;
            }
        });
        builder.setCancelable(false);
        builder.setTitle("Enter Favourite Apps");
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(int j=0;j<MainActivity.appnames.size();j++)
                {
                    if(checked_apps[j] && !favouriteApps.contains(MainActivity.appnames.get(j)))
                    {
                        dbhelper.add_favourite_app(MainActivity.appnames.get(j),MainActivity.sortedapps.get(MainActivity.appnames.get(j)));
                    }
                    else if(!checked_apps[j] && favouriteApps.contains(MainActivity.appnames.get(j)))
                    {
                        dbhelper.delete_favourite_app(MainActivity.appnames.get(j));
                    }
                }
            }
        });
        android.support.v7.app.AlertDialog dialog=builder.create();
        dialog.show();
    }

    public static void editSearchColor(Context context)
    {
        AmbilWarnaDialog colorpicker=new AmbilWarnaDialog(context, 1, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Log.i("color",String.valueOf(color));
                MainActivity.colorvalue=color;
            }
        });
        colorpicker.show();
    }
}

