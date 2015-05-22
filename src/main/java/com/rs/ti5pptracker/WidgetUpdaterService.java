package com.rs.ti5pptracker;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WidgetUpdaterService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TI5Widget.LOG, "Called");
        //android.os.Debug.waitForDebugger();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {
            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.ti5_widget);
            Info currentInfo = new Info();

            views.setProgressBar(R.id.appwidget_progressBar, 100, currentInfo.percentage, false);
            views.setTextViewText(R.id.appwidget_text, currentInfo.moneyF);
            views.setTextViewText(R.id.appwidget_update, currentInfo.updateTime);
            Locale usa = new Locale("us", "US");
            NumberFormat usaFormat = NumberFormat.getCurrencyInstance(usa);
            views.setTextViewText(R.id.appwidget_nextReward, String.format("%s - %s", usaFormat.format(currentInfo.nextReward.money).replaceAll("\\.00", ""), currentInfo.nextReward.description));
            views.setTextViewText(R.id.appwidget_lastReward, String.format("%s - %s", usaFormat.format(currentInfo.lastReward.money).replaceAll("\\.00", ""), currentInfo.lastReward.description));

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(),
                    TI5Widget.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(widgetId, views);
        }
        stopSelf();
        return START_NOT_STICKY;
    }

    private class Info {
        String moneyF;
        int moneyI;
        String updateTime;
        Rewards.Reward lastReward;
        Rewards.Reward nextReward;
        int percentage;

        public Info() {
            moneyF = getPricePool();
            moneyI = Integer.parseInt(moneyF.replaceAll("[^\\d.]", ""));
            updateTime = getCurrentTime();
            Rewards.Reward[] rewards = Rewards.getLastAndNext(moneyI);
            if (rewards.length == 2) {
                lastReward = rewards[0];
                nextReward = rewards[1];
                percentage = Math.round(((float) (moneyI - lastReward.money) / (float) (nextReward.money - lastReward.money)) * 100f);
            }
        }
    }

    private String getCurrentTime() {
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

    private String getPricePool(){
        try {
            final String[] money = new String[1];
            Thread t = new Thread() {
                public void run() {
                    try {
                        Document doc = Jsoup.connect("http://www.dota2.com/international/compendium/").get();
                        Elements prizePoolText = doc.select("#PrizePoolText");
//                        money[0] = (String.format("%,d", prizePoolText)).replace(',', ' ');
                        money[0] = prizePoolText.text();
                        Log.i(TI5Widget.LOG, "got result: " + money[0]);
//                    } catch (IOException e) {
//                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            t.join(9000); //wait a maximum of 9 seconds
            return money[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "not found";
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
