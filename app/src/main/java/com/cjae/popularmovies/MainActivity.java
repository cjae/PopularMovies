package com.cjae.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cjae.popularmovies.utils.CommonUtils;
import com.cjae.popularmovies.utils.NetworkUtils;
import com.cjae.popularmovies.utils.SessionManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String LIFECYCLE_FRAGMENT_CALLBACKS_KEY = "myFragment";

    @Bind(R.id.mainContainer)
    FrameLayout mainContainer;

    private Context mContext;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            //Restore the fragment's instance
            doConfirmNetworkConnection();
        }
    }

    private void doConfirmNetworkConnection() {
        if(!CommonUtils.isNetworkAvailable(this)) {
            SessionManager.setSortType(this, 2);
            doFragmentTransition(FavoriteFragment.getInstance());
            doChangeToolbarTitle(getString(R.string.sort_favorite));
        } else {
            doFragmentChange(SessionManager.getSortType(mContext));
        }
    }

    private void doSortDialog() {
        //Create sequence of items
        final String[] sortSequence = new String[]{getString(R.string.sort_popular),
                getString(R.string.sort_top_rated), getString(R.string.sort_favorite)};
        final int sortId = SessionManager.getSortType(mContext);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.sort_by));
        dialogBuilder.setSingleChoiceItems(sortSequence, sortId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(sortId == i) {
                    dialogInterface.dismiss();
                } else {
                    SessionManager.setSortType(mContext, i);
                    doFragmentChange(i);
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show(); //Show the dialog
    }

    private void doFragmentChange(int i) {
        if(i == 0) {
            doFragmentTransition(MainFragment.getInstance(NetworkUtils.SORT_POPULAR));
            doChangeToolbarTitle(getString(R.string.sort_popular));
        } else if(i == 1) {
            doFragmentTransition(MainFragment.getInstance(NetworkUtils.SORT_TOP_RATED));
            doChangeToolbarTitle(getString(R.string.sort_top_rated));
        } else {
            doFragmentTransition(FavoriteFragment.getInstance());
            doChangeToolbarTitle(getString(R.string.sort_favorite));
        }
    }

    private void doFragmentTransition(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, mContent)
                .commit();
    }

    private void doChangeToolbarTitle(String title) {
        if(getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            doSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        getSupportFragmentManager().putFragment(outState, LIFECYCLE_FRAGMENT_CALLBACKS_KEY, mContent);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}