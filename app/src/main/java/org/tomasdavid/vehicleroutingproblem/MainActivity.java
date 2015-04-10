package org.tomasdavid.vehicleroutingproblem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;

    public void unlockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.VISIBLE);
    }

    public void lockDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.INVISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        lockDrawer();

        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_main, fragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main);
        if (fragment instanceof VrpFragment) {
            VrpSolverTask vrpSolverTask =  ((VrpFragment) fragment).getVrpSolverTask();
            if (vrpSolverTask.isRunning()) {
                new SolverRunningDialog().show(getSupportFragmentManager(), null);
                return;
            } else {
                lockDrawer();
            }
        }
        super.onBackPressed();
    }
}
