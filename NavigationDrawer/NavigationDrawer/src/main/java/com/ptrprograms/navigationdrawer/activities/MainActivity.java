package com.ptrprograms.navigationdrawer.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

import com.ptrprograms.navigationdrawer.R;
import com.ptrprograms.navigationdrawer.events.DrawerNavigationItemClickedEvent;
import com.ptrprograms.navigationdrawer.fragments.ImageFragment;
import com.ptrprograms.navigationdrawer.fragments.NumberListFragment;
import com.ptrprograms.navigationdrawer.fragments.TextFragment;
import com.ptrprograms.navigationdrawer.utils.NavigationBus;
import com.squareup.otto.Subscribe;

public class MainActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavigationBus mNavigationBus;
	private String mCurFragmentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		mNavigationBus = NavigationBus.getInstance();

		initActionBar();
		initViews();
		initFragment();
		initDrawer();
    }

	private void initActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	private void initViews() {
		mDrawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
	}

	private void initFragment() {
		getSupportFragmentManager().beginTransaction().add(R.id.container, ImageFragment.getInstance()).commit();
		mCurFragmentTitle = getString( R.string.fragment_image );
	}

	private void initDrawer() {
		mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout,
				R.drawable.ic_navigation_drawer, R.string.drawer_open_title, R.string.drawer_close_title ) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if( getSupportActionBar() == null )
					return;

				getSupportActionBar().setTitle( R.string.drawer_close_title );
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if( getSupportActionBar() == null )
					return;

				getSupportActionBar().setTitle( R.string.drawer_open_title );
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( mDrawerToggle.onOptionsItemSelected( item ) )
			return true;

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mNavigationBus.register(this);
	}

	@Override
	protected void onStop() {
		mNavigationBus.unregister(this);
		super.onStop();
	}

	@Subscribe
	public void onDrawerNavigationClickedEvent( DrawerNavigationItemClickedEvent event ) {
		if( !mCurFragmentTitle.equalsIgnoreCase(event.section) ) {
			if (getString(R.string.fragment_image).equalsIgnoreCase(event.section)) {
				getSupportFragmentManager().beginTransaction().replace(R.id.container, ImageFragment.getInstance()).commit();
			} else if (getString(R.string.fragment_text).equalsIgnoreCase(event.section)) {
				getSupportFragmentManager().beginTransaction().replace(R.id.container, TextFragment.getInstance()).commit();
			} else if (getString(R.string.fragment_number_list).equalsIgnoreCase(event.section)) {
				getSupportFragmentManager().beginTransaction().replace(R.id.container, NumberListFragment.getInstance()).commit();
			}
			mCurFragmentTitle = event.section;
		}
		mDrawerLayout.closeDrawers();
	}
}
