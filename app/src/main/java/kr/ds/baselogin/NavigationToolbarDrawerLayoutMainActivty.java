package kr.ds.baselogin;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Administrator on 2015-12-18.
 */
public class NavigationToolbarDrawerLayoutMainActivty extends AppCompatActivity{
    private String TAG = NavigationToolbarDrawerLayoutMainActivty.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private int mNavItemId;
    private final Handler mDrawerActionHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationtoolbardrawerlayoutmainactivity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView)findViewById(R.id.navigation_view);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);

        mDrawerToggle = new ToolbarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                menuItem.setChecked(true);
                /**초기화**/
                try{
                    for(int i=0; i<mNavigationView.getMenu().size(); i++){
                        MenuItem otherItem = mNavigationView.getMenu().getItem(i);
                        if(!menuItem.equals(otherItem)){
                            otherItem.setChecked(false);
                        }else{
                            mNavItemId = i;
                        }
                    }
                }catch (Exception e){
                    Log.i(TAG,e.toString());
                }
                // allow some time after closing the drawer before performing real navigation
                // so the user can see what is happening
                mDrawerLayout.closeDrawer(GravityCompat.START);
                mDrawerActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigate(menuItem.getItemId());
                    }
                }, DRAWER_CLOSE_DELAY_MS);
                return true;
            }
        });
    }
    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main content fragment etc
    }
    private final class ToolbarDrawerToggle extends ActionBarDrawerToggle {

        public ToolbarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }
        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);

        }
        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

        }
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
}
