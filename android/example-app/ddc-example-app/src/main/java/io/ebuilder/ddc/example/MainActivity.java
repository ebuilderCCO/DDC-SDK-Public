package io.ebuilder.ddc.example;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import io.ebuilder.mobile.services.utils.PermissionUtils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends FragmentActivity implements FragmentFacade {

    public static String APP_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // The following prompt will only run on the all permissions flavor:
        ActivityCompat.requestPermissions(this, new String[]{
            GET_ACCOUNTS,
            READ_PHONE_STATE,
            WRITE_EXTERNAL_STORAGE,
            ACCESS_COARSE_LOCATION
        }, 100);
        APP_NAME = (String) getApplicationContext().getApplicationInfo().loadLabel(getApplicationContext().getPackageManager());

        setup();
    }

    private void setup() {
        switchToPage(new DdcFragment(), null);
    }

    @Override
    public <T extends Fragment> void switchToPage(final T page, final Bundle arguments) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            page.setArguments(arguments);
            fragmentManager.beginTransaction()
                .replace(R.id.container, page)
                .commit();
        }
    }

    @Override
    public <T extends Fragment> void addPageToStack(T page, Bundle arguments) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            page.setArguments(arguments);
            fragmentManager.beginTransaction()
                .add(R.id.container, page)
                .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }
}
