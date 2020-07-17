package com.softglu.bharatshare.activity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.softglu.bharatshare.fragment.EditableListFragment;
import com.softglu.bharatshare.view.EditableListFragmentImpl;
import com.softglu.bharatshare.fragment.ApplicationListFragment;
import com.softglu.bharatshare.fragment.FileExplorerFragment;
import com.softglu.bharatshare.fragment.ImageListFragment;
import com.softglu.bharatshare.fragment.MusicListFragment;
import com.softglu.bharatshare.fragment.VideoListFragment;
import com.softglu.bharatshare.view.SharingActionModeCallback;
import com.softglu.bharatshare.R;
import com.softglu.bharatshare.adapter.SmartFragmentPagerAdapter;
import com.genonbeta.android.framework.widget.PowerfulActionMode;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ContentSharingActivity extends Activity
{
    public static final String TAG = ContentSharingActivity.class.getSimpleName();
    public static final int REQUEST_PERMISSION_ALL = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private PowerfulActionMode mMode;
    private SharingActionModeCallback mSelectionCallback;
    private Activity.OnBackPressedListener mBackPressedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_sharing);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (checkPermission()) {


        } else {
            requestPermission();

        }

        AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);

        mMode = findViewById(R.id.activity_content_sharing_action_mode);
        final TabLayout tabLayout = findViewById(R.id.activity_content_sharing_tab_layout);
        final ViewPager viewPager = findViewById(R.id.activity_content_sharing_view_pager);

        mSelectionCallback = new SharingActionModeCallback(null);
        final PowerfulActionMode.SelectorConnection selectorConnection = new PowerfulActionMode.SelectorConnection(mMode, mSelectionCallback);

        final SmartFragmentPagerAdapter pagerAdapter = new SmartFragmentPagerAdapter(this, getSupportFragmentManager())
        {
            @Override
            public void onItemInstantiated(StableItem item)
            {
                EditableListFragmentImpl fragmentImpl = (EditableListFragmentImpl) item.getInitiatedItem();
                //EditableListFragmentModelImpl fragmentModelImpl = (EditableListFragmentModelImpl) item.getInitiatedItem();

                fragmentImpl.setSelectionCallback(mSelectionCallback);
                fragmentImpl.setSelectorConnection(selectorConnection);
                //fragmentModelImpl.setLayoutClickListener(groupLayoutClickListener);

                if (viewPager.getCurrentItem() == item.getCurrentPosition())
                    attachListeners(fragmentImpl);
            }
        };

        //mMode.setContainerLayout(findViewById(R.id.activity_content_sharing_action_mode_layout));
        Bundle fileExplorerArgs = new Bundle();
        fileExplorerArgs.putBoolean(FileExplorerFragment.ARG_SELECT_BY_CLICK, true);

        pagerAdapter.add(new SmartFragmentPagerAdapter.StableItem(0, ApplicationListFragment.class, null));
        pagerAdapter.add(new SmartFragmentPagerAdapter.StableItem(1, FileExplorerFragment.class, fileExplorerArgs)
                .setTitle(getString(R.string.text_files)));
        pagerAdapter.add(new SmartFragmentPagerAdapter.StableItem(2, MusicListFragment.class, null));
        pagerAdapter.add(new SmartFragmentPagerAdapter.StableItem(3, ImageListFragment.class, null));
        pagerAdapter.add(new SmartFragmentPagerAdapter.StableItem(4, VideoListFragment.class, null));

        pagerAdapter.createTabs(tabLayout, false, true);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());

                final EditableListFragment fragment = (EditableListFragment) pagerAdapter.getItem(tab.getPosition());

                attachListeners(fragment);

                if (fragment.getAdapterImpl() != null)
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            fragment.getAdapterImpl().notifyAllSelectionChanges();
                        }
                    }, 200);
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();
        else
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (mBackPressedListener == null || !mBackPressedListener.onBackPressed()) {
            if (mMode.hasActive(mSelectionCallback))
                mMode.finish(mSelectionCallback);
            else
                super.onBackPressed();
        }
    }

    public void attachListeners(EditableListFragmentImpl fragment)
    {
        mSelectionCallback.updateProvider(fragment);
        mBackPressedListener = fragment instanceof Activity.OnBackPressedListener
                ? (OnBackPressedListener) fragment
                : null;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean contactAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted && contactAccepted)
                        Toast.makeText(this, "Permission Granted, Now you can access this app.", Toast.LENGTH_LONG).show();

                    else {
                        Toast.makeText(this, "Permission Denied, You cannot use this app.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
