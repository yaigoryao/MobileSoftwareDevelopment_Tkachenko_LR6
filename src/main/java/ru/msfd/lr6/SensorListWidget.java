package ru.msfd.lr6;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.credentials.CreateCredentialException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

public class SensorListWidget extends AppWidgetProvider {

    public static final String ON_CLICK_ACTION = "tkachenko.lr6.onclickaction";
    public static final String UPDATE_WIDGET_ACTION = "tkachenko.lr6.updatewidget";
    public static final String ITEM_POSITION = "item_position";
    public static final String WIDGET_ID_EXTRAS = "tkachenko.widgetid.extras";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        Log.d("SensorListWidget", "static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {");
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.sensor_list_widget);
        SetListAdapter(context, rv);
        SetListItemClick(context, rv, appWidgetId);
        SetUpdateTextViewClick(context, rv, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.sensors_lv);
    }

    private static void SetListAdapter(Context context, RemoteViews rv)
    {
        Intent adapter = new Intent(context, SensorsAdapterService.class);
        adapter.setData(Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(R.id.sensors_lv, adapter);
    }

    private static void SetListItemClick(Context context, RemoteViews rv, int widgetId)
    {
        Intent template = new Intent(context, SensorListWidget.class);
        template.setAction(ON_CLICK_ACTION);
        template.putExtra(WIDGET_ID_EXTRAS, widgetId);
        template.setData(Uri.parse(template.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setPendingIntentTemplate(R.id.sensors_lv, PendingIntent.getBroadcast(context, 0, template, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private static void SetUpdateTextViewClick(Context context, RemoteViews rv, int widgetId)
    {
        Intent updateIntent = new Intent(context, SensorListWidget.class);
        updateIntent.setAction(UPDATE_WIDGET_ACTION);
        updateIntent.putExtra(WIDGET_ID_EXTRAS, widgetId);
        updateIntent.setData(Uri.parse(updateIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.sensor_lv_label_tv, pendingIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.d("SensorListWidget", "public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) ");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onEnabled(Context context) { Log.d("SensorListWidget", "public void onEnabled(Context context)"); }

    @Override
    public void onDisabled(Context context) { Log.d("SensorListWidget", "public void onDisabled(Context context)"); }

    private static final int WRONG_ACTION = -1;
    private static final int CLICK_ACTION = 1;
    private static final int ONLY_UPDATE_ACTION = 2;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("SensorListWidget", "public void onReceive(Context context, Intent intent)");
        super.onReceive(context, intent);
        int action = WRONG_ACTION;
        if (intent.getAction().equalsIgnoreCase(ON_CLICK_ACTION)) action = CLICK_ACTION;
        if (intent.getAction().equalsIgnoreCase(UPDATE_WIDGET_ACTION)) action = ONLY_UPDATE_ACTION;
        if(action != WRONG_ACTION)
        {
            if(action != ONLY_UPDATE_ACTION)
            {
                int position = intent.getIntExtra(SensorListWidget.ITEM_POSITION, -1);
                Log.d("SensorListWidget", "position " + position);

                if(position != -1)
                {
                    SharedPreferences sp = context.getSharedPreferences(SensorsAdapterFactory.SP_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    String stringId = String.valueOf(position);
                    int value = sp.getInt(stringId, -1);
                    if(value == -1) editor.putInt(stringId, 0);
                    else editor.putInt(stringId, value == 0 ? 1 : 0);
                    editor.commit();
                }
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] widgetsIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, SensorListWidget.class));
            onUpdate(context, appWidgetManager, widgetsIds);
        }
    }
}