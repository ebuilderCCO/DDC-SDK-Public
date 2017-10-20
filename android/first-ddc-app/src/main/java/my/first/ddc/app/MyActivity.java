package my.first.ddc.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import io.ebuilder.mobile.services.DeviceDataCollectorFactory;
import io.ebuilder.mobile.services.settings.DeviceIdType;
import io.ebuilder.mobile.services.utils.PermissionUtils;

/**
 * Created by <a href="mailto:dmitry.gorohov@ebuilder.com">Dmitry Gorohov</a>
 * Date: 10/6/17
 */

public class MyActivity extends Activity {

    private final static int REQUEST_CODE = 100;
    private final static String SYSTEM_ID = "MySystemId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.GET_ACCOUNTS
                    }, REQUEST_CODE);
        } else {
            setup();
        }
    }

    private void setup() {
        if (PermissionUtils.allGranted(this,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.GET_ACCOUNTS)) {
            final TelephonyManager telephonyManager = (TelephonyManager)
                    getSystemService(TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                final String imei = telephonyManager.getDeviceId();
                DeviceDataCollectorFactory.setup(getApplicationContext(),
                        SYSTEM_ID, imei, DeviceIdType.IMEI)
                        .loggingEnabled()
                        .collectGoogleAccounts()
                        .build(getApplicationContext());
            }
        }
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final @NonNull String[] permissions,
                                           final @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            setup();
        }
    }
}
