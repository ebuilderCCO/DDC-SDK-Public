package io.ebuilder.ddc.example;

import android.os.AsyncTask;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.io.IOException;

import io.ebuilder.mobile.services.DeviceDataCollector;


public class DdcFragment extends Fragment {

    private static DeviceDataCollector ddc;

    private static final String LICENSE_KEY = "YOUR_LICENCE_KEY";

    private static class setAdID extends AsyncTask<Void, Void, Void> {
        private WeakReference<DdcFragment> ddcFragmentWeakReference;
        setAdID(DdcFragment ddcFragment) {
            ddcFragmentWeakReference = new WeakReference<>(ddcFragment);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                AdvertisingIdClient.Info info =  AdvertisingIdClient.getAdvertisingIdInfo(ddcFragmentWeakReference.get().getContext());
                if (!info.isLimitAdTrackingEnabled()) {
                    ddc.advertisingId(info.getId());
                }
                else {
                    ddc.advertisingId("00000000-0000-0000-0000-000000000000");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {

        ddc = DeviceDataCollector.getDefault(getContext().getApplicationContext(), LICENSE_KEY);
        ddc.loggingEnabled(true);
        ddc.externalUserId("c23911a2-c455-4a59-96d0-c6fea09176b8");
        ddc.phoneNumber("+1234567890");
        new setAdID(this).execute();

        return inflater.inflate(R.layout.fragment_ddc_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button sendEventButton = view.findViewById(R.id.sendEvent);
        sendEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (ddc != null) {
                    ddc.run();
                    Toast.makeText(getContext().getApplicationContext(), "Event sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button startSchedulerEvent = view.findViewById(R.id.startScheduler);
        startSchedulerEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (ddc != null) {
                    ddc.startScheduler();
                    Toast.makeText(getContext().getApplicationContext(), "Scheduler started", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button stopSchedulerEvent = view.findViewById(R.id.stopScheduler);
        stopSchedulerEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (ddc != null) {
                    ddc.stopScheduler();
                    Toast.makeText(getContext().getApplicationContext(), "Scheduler stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
