package cn.edu.xjtlu.eee.wifiscanner;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView mainText;
	WifiManager mainWifi;
	WifiReceiver receiverWifi;
	List<ScanResult> wifiList;
	StringBuilder sb = new StringBuilder();
	StringBuilder csv = new StringBuilder();
	boolean scanFinished = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainText = (TextView) findViewById(R.id.mainText);
		mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mainWifi.startScan();
		mainText.setText("Starting Scan...\n");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Refresh");
		menu.add(0, 1, 1, "Finish");
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			mainWifi.startScan();
			mainText.setText("Starting Scan...\n");
			break;
		case 1:
			// To return CSV-formatted text back to calling activity (e.g., MIT
			// App Inventor App)
			Intent scanResults = new Intent();
			scanResults.putExtra("AP_LIST", csv.toString());
			setResult(RESULT_OK, scanResults);
			finish();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiverWifi);

		// To return CSV-formatted text back to calling activity (e.g., MIT App
		// Inventor App)
		Intent scanResults = new Intent();
		scanResults.putExtra("AP_LIST", csv.toString());
		setResult(RESULT_OK, scanResults);
		finish();
	}

	protected void onResume() {
		super.onResume();
		registerReceiver(receiverWifi, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		// if (scanFinished == true) {
		// // wait until Wi-Fi scan is finished
		// // Handler handler = new Handler();
		// // handler.postDelayed(new Runnable() {
		// // public void run() {
		// // // TODO: Add runnable later
		// // }
		// // }, 1000);
		// // To return results back to calling activity (e.g., MIT App
		// // Inventor App)
		// Intent scanResults = new Intent();
		// scanResults.putExtra("AP_LIST", sb.toString());
		// setResult(RESULT_OK, scanResults);
		// finish();
		// }
	}

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			sb = new StringBuilder();
			csv = new StringBuilder();
			wifiList = mainWifi.getScanResults();

			// prepare text for display and CSV table
			sb.append("Number of APs Detected: ");
			sb.append((Integer.valueOf(wifiList.size())).toString());
			sb.append("\n\n");
			for (int i = 0; i < wifiList.size(); i++) {
				// sb.append((Integer.valueOf(i + 1)).toString() + ".");
				// SSID
				sb.append("SSID:").append((wifiList.get(i)).SSID);
				sb.append("\n");
				csv.append((wifiList.get(i)).SSID);
				csv.append(",");
				// BSSID
				sb.append("BSSID:").append((wifiList.get(i)).BSSID);
				sb.append("\n");
				csv.append((wifiList.get(i)).BSSID);
				csv.append(",");
				// capabilities
				sb.append("Capabilities:").append(
						(wifiList.get(0)).capabilities);
				sb.append("\n");
				// frequency
				sb.append("Frequency:").append((wifiList.get(i)).frequency);
				sb.append("\n");
				csv.append((wifiList.get(i)).frequency);
				csv.append(",");
				// level
				sb.append("Level:").append((wifiList.get(i)).level);
				sb.append("\n\n");
				csv.append((wifiList.get(i)).level);
				csv.append("\n");
			}

			mainText.setText(sb);

			// notify that Wi-Fi scan has finished
			scanFinished = true;
		}
	}
}