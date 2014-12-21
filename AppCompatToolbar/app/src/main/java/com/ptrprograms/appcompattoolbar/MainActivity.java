package com.ptrprograms.appcompattoolbar;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );

        AnalogClock view = (AnalogClock) LayoutInflater.from( this ).inflate( R.layout.ab_clock, null );
        toolbar.addView( view );

        setSupportActionBar( toolbar );
    }

    private void showDefaultLogo() {
        getSupportActionBar().setLogo( R.drawable.ic_launcher );
        getSupportActionBar().setDisplayUseLogoEnabled( true );
    }

    private void showCustomLogo() {
        getSupportActionBar().setLogo( android.R.drawable.ic_btn_speak_now );
        getSupportActionBar().setDisplayUseLogoEnabled( true );
    }

    private void hideLogo() {
        getSupportActionBar().setDisplayUseLogoEnabled( false );
    }

    private void showDefaultTitleAndSubtitle() {
        getSupportActionBar().setSubtitle( null );
        getSupportActionBar().setTitle( R.string.app_name );
        getSupportActionBar().setDisplayShowTitleEnabled( true );
    }

    private void showCustomTitleAndSubtitle() {
        getSupportActionBar().setTitle( "Custom Title");
        getSupportActionBar().setSubtitle( "subtitle" );
        getSupportActionBar().setDisplayShowTitleEnabled( true );
    }

    private void hideTitleAndSubtitle() {
        getSupportActionBar().setDisplayShowTitleEnabled( false );
    }

    private void showHomeUpArrow() {
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( true );
    }

    private void hideHomeUpArrow() {
        getSupportActionBar().setDisplayHomeAsUpEnabled( false );
        getSupportActionBar().setHomeButtonEnabled( false );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch( id ) {
            case R.id.action_default_title: {
                showDefaultTitleAndSubtitle();
                break;
            }
            case R.id.action_custom_title: {
                showCustomTitleAndSubtitle();
                break;
            }
            case R.id.action_hide_title: {
                hideTitleAndSubtitle();
                break;
            }
            case R.id.action_default_logo: {
                showDefaultLogo();
                break;
            }
            case R.id.action_custom_logo: {
                showCustomLogo();
                break;
            }
            case R.id.action_hide_logo: {
                hideLogo();
                break;
            }
            case R.id.action_show_up_arrow: {
                showHomeUpArrow();
                break;
            }
            case R.id.action_hide_up_arrow: {
                hideHomeUpArrow();
                break;
            }
            case android.R.id.home: {
                Toast.makeText( this, "Up Arrow", Toast.LENGTH_SHORT ).show();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
