package com.wilsonow.android_sensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SensorManager mSensorManager;
    private Sensor sensorAccelerometer;

    private List<Sensor> deviceSensors;
    private List<String> sensorsStr;
    private ListView sensorsLV;

    private ArrayAdapter strArrAdapter;
    private ArrayAdapter sensorArrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    private void init() {
        findViewById(R.id.btn_names).setOnClickListener(this);
        findViewById(R.id.btn_details).setOnClickListener(this);
        findViewById(R.id.btn_acceleration).setOnClickListener(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorsStr = new ArrayList<>();

        for ( int i = 0; i < deviceSensors.size(); i++ ) {
            sensorsStr.add(i, deviceSensors.get(i).getName());
        }

        sensorsLV = (ListView) findViewById(R.id.lv_sensors);
        sensorArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceSensors);
        strArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorsStr);
        sensorsLV.setAdapter(strArrAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_names:
                sensorsLV.setAdapter(strArrAdapter);
                break;
            case R.id.btn_details:
                sensorsLV.setAdapter(sensorArrAdapter);
                break;
            case R.id.btn_acceleration:
                Intent sensorIntent = new Intent(this, SensorActivity.class);
                startActivity(sensorIntent);
                //finish();
                break;
        }
    }
}
