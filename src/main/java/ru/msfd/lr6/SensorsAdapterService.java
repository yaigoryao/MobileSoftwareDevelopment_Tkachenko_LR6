package ru.msfd.lr6;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

public class SensorsAdapterService extends RemoteViewsService {
    public SensorsAdapterService() { }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        Log.d("SensorsAdapterService", "public RemoteViewsFactory onGetViewFactory(Intent intent) ");
        return new SensorsAdapterFactory(getApplicationContext(), intent);
    }
}