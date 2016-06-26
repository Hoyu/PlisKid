package es.uoproject.pliskid.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import es.uoproject.pliskid.R;
import es.uoproject.pliskid.activities.Launcher;

/**
 * Clase que implementa la funcionalidad del widget de la aplicación
 */
public class Widget_Plis extends AppWidgetProvider {

    /**
     * Método que establece el view del widget
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_plis);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Método que establece el comportamiento al interactuar con el widget
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_plis);
        Intent configIntent = new Intent(context, Launcher.class);
        configIntent.putExtra("Version", true);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent,  PendingIntent.FLAG_CANCEL_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget, configPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
