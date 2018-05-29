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
import android.view.View;
import android.widget.Button;

import com.facebook.stetho.Stetho;

import io.ebuilder.mobile.services.DeviceDataCollectorFactory;
import io.ebuilder.mobile.services.scheduler.ServiceTrigger;
import io.ebuilder.mobile.services.settings.DeviceIdType;
import io.ebuilder.mobile.services.utils.PermissionUtils;


public class MyActivity extends Activity {

    private final static int REQUEST_CODE = 100;
    private final static String SYSTEM_ID = "MySystemId";

    private ServiceTrigger serviceTrigger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.GET_ACCOUNTS
                    }, REQUEST_CODE);
        }
        final Button cancelBtn = findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceTrigger != null) {
                    serviceTrigger.run(getApplicationContext());
                }
            }
        });
    }

    private void setup() {
        serviceTrigger = requestTrigger();
    }

    @SuppressLint("MissingPermission")
    private ServiceTrigger requestTrigger() {
        if (PermissionUtils.allGranted(this,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.GET_ACCOUNTS)) {
            final TelephonyManager telephonyManager = (TelephonyManager)
                    getSystemService(TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                return DeviceDataCollectorFactory.setup(
                        this,
                        SYSTEM_ID,
                        telephonyManager.getDeviceId(),
                        DeviceIdType.IMEI
                )
                        .loggingEnabled()
                        .build(this);
            }
        }
        return null;
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
