package ru.msfd.lr6;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SensorItemReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("SensorItemReceiver", "public void onReceive(Context context, Intent intent)");

        if(intent.getAction().equalsIgnoreCase(SensorsAdapterFactory.UPDATE_SENSOR_VIEW_ACTION))
        {
            int sensorId = intent.getIntExtra(SensorsAdapterFactory.SENSOR_ID_EXTRA, -1);
            if (sensorId == -1) return;

            //SensorsAdapterFactory.updateSensorModel(sensorId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widget = new ComponentName(context, SensorListWidget.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(widget), R.id.sensors_lv);
        }
    }
}