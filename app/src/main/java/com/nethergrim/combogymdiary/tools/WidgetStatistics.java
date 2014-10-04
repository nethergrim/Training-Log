package com.nethergrim.combogymdiary.tools;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.StatisticsActivity;
import com.nethergrim.combogymdiary.tools.Statistics;

public class WidgetStatistics extends AppWidgetProvider {

    private static Statistics statistics;

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, SharedPreferences sp, int widgetID) {
        if (widgetID == -1) {
            return;
        }

        Intent intent = new Intent(context, StatisticsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_stats);
        views.setOnClickPendingIntent(R.id.tvtext_fav_exe_result, pendingIntent);
        views.setTextViewText(R.id.tvtext_fav_exe_result, statistics.getMainExercise());
        views.setTextViewText(R.id.text_weight_delta_result, statistics.getBodyWeightDelta(30));

        appWidgetManager.updateAppWidget(widgetID, views);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        statistics = new Statistics(context);
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putInt("widget_id", appWidgetId).apply();
            updateWidget(context, appWidgetManager,
                    PreferenceManager.getDefaultSharedPreferences(context),
                    appWidgetId);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        if (statistics != null)
            statistics.close();
    }

}
