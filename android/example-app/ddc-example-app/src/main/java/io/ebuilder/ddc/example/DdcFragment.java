package io.ebuilder.ddc.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import io.ebuilder.mobile.services.DeviceDataCollector;


public class DdcFragment extends Fragment {

    private DeviceDataCollector ddc;

    private static final String LICENSE_KEY = "eyJ1cmwiOiJodHRwczovL2FwaWd3LmVidWlsZGVyLmlvL21vY2stZW5kIiwgImxpY2Vuc2UiOiJ0aGVzeXN0ZW0iLCAiYXBpLWtleSI6Im5ld3J1bGVzIn0K";

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {

        ddc = DeviceDataCollector.getDefault(getContext().getApplicationContext() ,LICENSE_KEY)
                .advertisingId("HELLO");
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
                    ddc.phoneNumber("123");
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
