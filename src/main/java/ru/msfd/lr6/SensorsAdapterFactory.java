package ru.msfd.lr6;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class SensorsAdapterFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String SP_NAME = "tkachenko.sp";
    public static final String SENSOR_ID_EXTRA = "sensor_id";
    public static final String UPDATE_SENSOR_VIEW_ACTION = "tkachenko.lr.updatesensorview";
    Context context;
    Intent intent;

    final ArrayList<SensorModel> sensorModels  = new ArrayList<>();;
    public SensorsAdapterFactory(Context context, Intent intent)
    {
        Log.d("SensorsAdapterFactory", "public SensorsAdapterFactory(Context context, Intent intent)");

        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() { Log.d("SensorsAdapterFactory", "public void onCreate()"); }

    private boolean isUpdating = false;

    @Override
    public void onDataSetChanged()
    {
        Log.d("SensorsAdapterFactory", "public void onDataSetChanged()");

        if (isUpdating) return;
        isUpdating = true;
        synchronized (sensorModels)
        {
            sensorModels.clear();
        }
        int id = 0;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        SensorEventListener listener = new SensorEventListener()
        {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }

            @Override
            public void onSensorChanged(SensorEvent event)
            {
                synchronized (sensorModels)
                {
                    String value = getSensorValues(event.values);

                    for (SensorModel model : sensorModels)
                        if (model.name.equals(event.sensor.getName())) model.value = value;
                }
            }
        };

        for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL))
        {
            String sensorName = sensor.getName();
            synchronized (sensorModels)
            {
                sensorModels.add(new SensorModel(id++, sensorName, "", false));
                sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() ->
        {
            Log.d("SensorsAdapterFactory", "Handler delayed task executed, updating widget");

            sensorManager.unregisterListener(listener);
            synchronized (sensorModels)
            {
                for (SensorModel model : sensorModels) Log.d("MYLOG", model.name + " / " + model.value);
            }
            isUpdating = false;
            Log.d("SensorsAdapterFactory", "Updating finished");
        }, 500);

        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        synchronized (sensorModels)
        {
            for(SensorModel model : sensorModels)
            {
                String stringId = String.valueOf(model.id);
                int value = sp.getInt(stringId, -1);
                if(value == -1) editor.putInt(stringId, 0);
                else model.showValue = value != 0;
            }
            editor.commit();
        }
    }

    private String getSensorValues(float[] values) { return Arrays.toString(values); }

    @Override
    public void onDestroy() { }

    @Override
    public int getCount() { Log.d("SensorsAdapterFactory", "public int getCount() "); synchronized (sensorModels) { return sensorModels.size(); } }

    @Override
    public RemoteViews getViewAt(int i)
    {
        Log.d("SensorsAdapterFactory", "public RemoteViews getViewAt(int i)");
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.sensor_item_layout);
        synchronized (sensorModels)
        {
            rv.setTextViewText(R.id.sensor_data_tv_id, sensorModels.get(i).toString());
        }
        rv.setOnClickFillInIntent(R.id.sensor_data_tv_id, new Intent().putExtra(SensorListWidget.ITEM_POSITION, i));
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() { return null; }

    @Override
    public int getViewTypeCount() { return 1; }

    @Override
    public long getItemId(int i) { synchronized (sensorModels) { return sensorModels.get(i).id;} }

    @Override
    public boolean hasStableIds() { return true; }

}