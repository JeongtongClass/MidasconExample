package com.jinasoft.midasconexample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.BeaconCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;



import java.util.ArrayList;

public class TemHumActivity extends AppCompatActivity implements BeaconCallback {

	private static final String TEXT = "Temperature :\n%s\n\nHumidity :\n%s";

	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

	private ContextManager contextManager;
	private TextView textView;


	// AndroidManifest.xml에 설정된 name 클래스 호출
	public MidasApplication getMidasApplication() {
		return (MidasApplication) getApplication();
	}


	ArrayList<String> TemA = new ArrayList<>();

	float temperature;
	float humidity;

	LineChart Temchart, Humchart;

	int X_RANGE =50;
	int DATA_RANGE =30;

	ArrayList<Entry> xVal;
	LineDataSet setXcomp;
	ArrayList<String> xVals;
	ArrayList<ILineDataSet> lineDataSets;
	LineData lineData;

	float exTemp;


	ArrayList<Entry> HumxVal;
	LineDataSet HumsetXcomp;
	ArrayList<String> HumxVals;
	ArrayList<ILineDataSet> HumlineDataSets;
	LineData HumlineData;

	float exHum;


	TextView tvTemp,tvHum;
	ArrayList<Float> AvgTempArray = new ArrayList<>();
	ArrayList<Float> AvgHumArray= new ArrayList<>();

	Float TempSum,HumSum;
	Float TempAvg,HumAvg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);

		contextManager = getMidasApplication().getContextManager();
		// 맥주소로 수신할 비콘 설정
		contextManager.getBeaconSettings().setAllowBeaconMac(true);
		// 마이다스콘 맥 주소 등록
		// contextManager.getBeaconSettings().setAllowBeaconId("74-f0-7d-00-12-36",
		// true);

//		textView = (TextView) findViewById(R.id.status);

		init();

		tvTemp = findViewById(R.id.AvgTemp);
		tvHum = findViewById(R.id.AvgHum);


	}

	private void init(){
		Temchart = findViewById(R.id.Temchart);
		Humchart = findViewById(R.id.Humchart);
		TemchartInit();
		HumchartInit();
	}

	private void TemchartInit() {

		Temchart.setAutoScaleMinMaxEnabled(true);
		xVal = new ArrayList<>();
		setXcomp = new LineDataSet(xVal, "온도(℃)");
		setXcomp.setColor(Color.RED);
		setXcomp.setLineWidth(2f);
		setXcomp.setValueTextSize(15f);
		setXcomp.setCircleRadius(4f);
		setXcomp.setDrawValues(false);
		setXcomp.setCircleColor(Color.WHITE);
		setXcomp.setDrawCircles(true);
		setXcomp.setAxisDependency(YAxis.AxisDependency.LEFT);
		lineDataSets = new ArrayList<>();
		lineDataSets.add(setXcomp);

		xVals= new ArrayList<>();
		for(int i =0;i<X_RANGE; i++) {
			xVals.add("");
		}
		lineData = new LineData(lineDataSets);
		Temchart.setData(lineData);
		Temchart.invalidate();
	}

	public void TemchartUpdate(float x){
		if(xVal.size()>DATA_RANGE){
			xVal.remove(0);
			AvgTempArray.remove(0);
			for(int i=0; i<DATA_RANGE;i++){
				xVal.get(i).setX(i);
			}
		}

		if(exTemp!=x) {
			AvgTempArray.add(x);
			xVal.add(new Entry(xVal.size(), x));
			setXcomp.notifyDataSetChanged();
			Temchart.notifyDataSetChanged();
			Temchart.invalidate();
		}
	}

	private void HumchartInit() {

		Humchart.setAutoScaleMinMaxEnabled(true);
		HumxVal = new ArrayList<>();
		HumsetXcomp = new LineDataSet(HumxVal, "습도(%)");
		HumsetXcomp.setColor(ColorTemplate.getHoloBlue());
		HumsetXcomp.setLineWidth(2f);
		HumsetXcomp.setValueTextSize(15f);
		HumsetXcomp.setCircleRadius(4f);
		HumsetXcomp.setDrawValues(false);
		HumsetXcomp.setCircleColor(Color.WHITE);
		HumsetXcomp.setDrawCircles(true);
		HumsetXcomp.setAxisDependency(YAxis.AxisDependency.LEFT);
		HumlineDataSets = new ArrayList<>();
		HumlineDataSets.add(HumsetXcomp);

		HumxVals= new ArrayList<>();
		for(int i =0;i<X_RANGE; i++) {
			HumxVals.add("");
		}
		HumlineData = new LineData(HumlineDataSets);
		Humchart.setData(HumlineData);
		Humchart.invalidate();
	}

	public void HumchartUpdate(float x){
		if(HumxVal.size()>DATA_RANGE){
			HumxVal.remove(0);
			AvgHumArray.remove(0);
			for(int i=0; i<DATA_RANGE;i++){
				HumxVal.get(i).setX(i);
			}
		}
		if(exHum!=x) {
			AvgHumArray.add(x);
			HumxVal.add(new Entry(HumxVal.size(), x));
			HumsetXcomp.notifyDataSetChanged();
			Humchart.notifyDataSetChanged();
			Humchart.invalidate();
		}
	}


	@Override
	public void onBeaconCallback(int status, Beacon beacon) {

		switch (status) {
		case STATUS_CODE_ENTER:
		case STATUS_CODE_UPDATE:
			// 온도, 습도 데이터를 TextView에 표시
			temperature = beacon.getTemperature();

			humidity = beacon.getHumidity();

//			/**버튼 누르고 있으면 0-> 255(긴급)**/
			int emergency = beacon.getEmergency();
			Log.d("Emergency",String.valueOf(emergency));
			String Id = beacon.getId();
			Log.d("ID",String.valueOf(Id));
			String Mac = beacon.getMac();
			Log.d("MAC",String.valueOf(Mac));
			int battery = beacon.getBattery();
			Log.d("Battery",String.valueOf(battery));

			//			if(elsewhat==255) {
//				Toast.makeText(TemHumActivity.this, "긴급 상황 발생", Toast.LENGTH_SHORT).show();
//			}


//			TemA.add(String.valueOf(temperature));
//			if(TemA.size() >= 11){
//				TemA.remove(0);
//			}
//			Toast.makeText(TemHumActivity.this,String.valueOf(temperature),Toast.LENGTH_SHORT).show();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
//					if (textView == null)
//						return;
//					textView.setText(String.format(TEXT, temperature + " ℃", humidity + " %"));
					TemchartUpdate(temperature);
					HumchartUpdate(humidity);
					exHum = humidity;
					exTemp = temperature;

					TempSum =(float)0;
					for(int i =0;i<AvgTempArray.size(); i++){
						TempSum += AvgTempArray.get(i);
					}
					TempAvg = TempSum / AvgTempArray.size();
					String Temp2 = String.format("%.2f",TempAvg);
					tvTemp.setText("평균 온도 : " + Temp2+"℃");

					HumSum =(float)0;
					for(int i =0; i< AvgHumArray.size(); i++){
						HumSum += AvgHumArray.get(i);
					}
					HumAvg = HumSum / AvgHumArray.size();
					String Hum2 = String.format("%.0f",HumAvg);
					tvHum.setText("평균 습도 : "+ Hum2+"%");


				}
			});
			break;

		default:
			break;
		}


	}


	@Override
	protected void onResume() {
		super.onResume();

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
				contextManager.setBeaconCallback(this);
				contextManager.startLeScan();
			} else {
				contextManager.stopLeScan();

				Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivity(settingsIntent);
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		contextManager.stopLeScan();
	}

}
