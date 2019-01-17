package io.ebuilder.mobile.services;

import android.content.Context;

import io.ebuilder.mobile.services.settings.SettingsProvider;

public final class DDCSetup {

    private static final String LICENSE_KEY = "eyJ1cmwiOiJodHRwczovL2FwaWd3LmVidWlsZGVyLmlvL21vY2stZW5kIiwgImxpY2Vuc2UiOiJ0aGVzeXN0ZW0iLCAiYXBpLWtleSI6Im5ld3J1bGVzIn0K";

    public static DeviceDataCollector ddc(final Context context) {


        return DeviceDataCollector.getDefault(context,LICENSE_KEY)
            .advertisingId("HELLO");
    }

    public static SettingsProvider settingsProvider() {
        return new SettingsContent();
    }
}
