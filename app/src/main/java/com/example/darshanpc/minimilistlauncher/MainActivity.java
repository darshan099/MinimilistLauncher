package com.example.darshanpc.minimilistlauncher;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    ListView applistview,favouriteListView;
    PackageManager manager;
    SlidingUpPanelLayout slidingPanelLayout;
    ImageButton slideuparrow;
    DatabaseHelper dbhelper;
    HashMap<String, String> apps = new HashMap<>();
    TreeMap<String, String> sortedapps;
    EditText searchapp;
    List<String> appnames;
    ArrayAdapter<String> appListAdapter,favouriteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applistview = findViewById(R.id.manageapplist);
        slideuparrow = findViewById(R.id.slide_up_arrow);
        searchapp = findViewById(R.id.search_app);
        slidingPanelLayout = findViewById(R.id.sliding_panel);
        favouriteListView=findViewById(R.id.favouriteListView);
        applistview.setTextFilterEnabled(true);
        manager = getPackageManager();
        dbhelper = new DatabaseHelper(this);

        getApps();

        addFavouriteList();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean("firsttime", false)) {
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            String[] temp_apps=new String[appnames.size()];
            final boolean[] checked_apps=new boolean[appnames.size()];
            for(int j=0;j<appnames.size();j++)
            {
                temp_apps[j]=appnames.get(j);
                checked_apps[j]=false;
            }
            builder.setMultiChoiceItems(temp_apps, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                    checked_apps[which]=true;
                }
            });
            builder.setCancelable(false);
            builder.setTitle("Enter 5 Favourite Apps");
            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for(int j=0;j<appnames.size();j++)
                    {
                        if(checked_apps[j])
                        {
                            dbhelper.add_favourite_app(appnames.get(j),sortedapps.get(appnames.get(j)));
                        }
                    }
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
            addFavouriteList();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firsttime", true);
            editor.apply();
        }

        applistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = manager.getLaunchIntentForPackage(sortedapps.get(appListAdapter.getItem(position)));
                MainActivity.this.startActivity(i);
            }
        });

        favouriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = manager.getLaunchIntentForPackage(sortedapps.get(favouriteListAdapter.getItem(position)));
                MainActivity.this.startActivity(i);
            }
        });

        searchapp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
                appListAdapter.getFilter().filter(cs.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        slidingPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                slideuparrow.setAlpha(1 - slideOffset);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState==SlidingUpPanelLayout.PanelState.COLLAPSED)
                {
                    searchapp.clearFocus();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if(slidingPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            slidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    private void getApps() {
        manager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivity = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivity) {
            apps.put(ri.loadLabel(manager).toString(), ri.activityInfo.packageName);
        }
        sortedapps = new TreeMap<>(apps);
        appnames = new ArrayList<>(sortedapps.keySet());
        appListAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, appnames);
        applistview.setAdapter(appListAdapter);
    }
    public void addFavouriteList()
    {
        ArrayList favouritelist=dbhelper.get_favourite_app();
        favouriteListAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,favouritelist){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view= super.getView(position, convertView, parent);
                TextView tv=view.findViewById(android.R.id.text1);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                return view;
            }
        };
        favouriteListView.setAdapter(favouriteListAdapter);
    }
}