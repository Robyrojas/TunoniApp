package com.synappsis.carlos.apptunoni;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.synappsis.carlos.apptunoni.receiver.NetworkStateChangeReceiver;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class App extends Application {
  private static final String WIFI_STATE_CHANGE_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";

  @Override
  public void onCreate() {
    super.onCreate();
    registerForNetworkChangeEvents(this);
  }

  @SuppressLint("LongLogTag")
  public static void registerForNetworkChangeEvents(final Context context) {
    Log.e("registerForNetworkChangeEvents","CONNECTIVITY_ACTION");
    NetworkStateChangeReceiver networkStateChangeReceiver = new NetworkStateChangeReceiver();
    context.registerReceiver(networkStateChangeReceiver, new IntentFilter(CONNECTIVITY_ACTION));
    context.registerReceiver(networkStateChangeReceiver, new IntentFilter(WIFI_STATE_CHANGE_ACTION));
  }
}
