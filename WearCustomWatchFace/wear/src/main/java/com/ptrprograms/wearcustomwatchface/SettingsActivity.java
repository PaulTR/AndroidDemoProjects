package com.ptrprograms.wearcustomwatchface;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PaulTR on 7/6/14.
 */
public class SettingsActivity extends Activity {

    public static final String SHARED_PREFERENCE_SCHOOL = "shared_preference_school";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
        final SchoolObj schools = new SchoolObj();
        ListView listView = (ListView) findViewById( R.id.list );
        ArrayAdapter<String> adapter = new ArrayAdapter( this, android.R.layout.simple_list_item_1, schools.schoolList );
        listView.setAdapter( adapter );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pref = getSharedPreferences( WatchFaceActivity.SHARED_PREFERENCE, Context.MODE_PRIVATE );
                SharedPreferences.Editor editor = pref.edit();
                editor.putString( SHARED_PREFERENCE_SCHOOL, schools.schoolCodeList.get( position ) );
                editor.commit();
                finish();
            }
        });

    }

    public class SchoolObj {
        public List<String> schoolList = new ArrayList<String>();
        public List<String> schoolCodeList = new ArrayList<String>();

        public SchoolObj() {
            schoolList.add( "CSU Fresno" );
            schoolCodeList.add( "fresno" );
            schoolList.add( "CU Boulder" );
            schoolCodeList.add( "cuboulder" );
            schoolList.add( "Florida State University" );
            schoolCodeList.add( "fsu" );
            schoolList.add( "UC Santa Cruz" );
            schoolCodeList.add( "ucsc" );
            schoolList.add( "UC Berkeley" );
            schoolCodeList.add( "berkeley" );
        }
    }

}
