package com.wilsonow.android_sensor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SensorActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, OnChartValueSelectedListener {

    private SensorManager mSensorManager;
    private Sensor sensorAccelerometer;

    private TextView tvAccelerationX;
    private TextView tvAccelerationY;
    private TextView tvAccelerationZ;

    private LineChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    private float[] gravity = {0, 0, 0};
    private float[] linear_acceleration = {0, 0, 0};
    private long lastUpdate;

    private static final int MAX_G = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if ( getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        chartInit();
    }

    private void init() {
        tvAccelerationX = (TextView) findViewById(R.id.tv_acceleration_x);
        tvAccelerationY = (TextView) findViewById(R.id.tv_acceleration_y);
        tvAccelerationZ = (TextView) findViewById(R.id.tv_acceleration_z);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void chartInit() {
        mChart = (LineChart) findViewById(R.id.lc_sensor);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);

        // add data
        //setData(20, 30);

        //mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(mTfLight);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(MAX_G + 100);
        leftAxis.setAxisMinValue(-(MAX_G + 100));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
    }

    private void setData(int count, float range) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (float i = 0; i <= count; i++) {
            float mult = range / 2f;
            float val = i / 2;
            yVals1.add(new Entry(i, val));
        }

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (float i = 0; i <= count; i++) {
            float mult = range;
            float val = i;
            yVals2.add(new Entry(i, val));
        }

        LineDataSet set1, set2;

        if (mChart.getData() != null &&
            mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ContextCompat.getColor(this, R.color.lineBlue));
            set1.setCircleColor(ContextCompat.getColor(this, R.color.lineBlue));
            set1.setLineWidth(2f);
            set1.setCircleRadius(6f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(true);
            set1.setCircleColorHole(ContextCompat.getColor(this, android.R.color.white));
            set1.setCircleHoleRadius(3f);

            // create a dataset and give it a type
            set2 = new LineDataSet(yVals2, "DataSet 2");
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
            set2.setColor(Color.RED);
            set2.setCircleColor(ContextCompat.getColor(this, R.color.lineRed));
            set2.setLineWidth(2f);
            set2.setCircleRadius(6f);
            set2.setFillAlpha(65);
            set2.setFillColor(Color.BLUE);
            set2.setHighLightColor(Color.rgb(244, 117, 117));
            set2.setDrawCircleHole(true);
            set2.setCircleColorHole(ContextCompat.getColor(this, android.R.color.white));
            set2.setCircleHoleRadius(3f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            dataSets.add(set2);

            // create a data object with the datasets
            LineData data = new LineData(dataSets);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(9f);

            // set data
            mChart.setData(data);
        }
    }

    private void addEntry(float val) {

        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40)), 0);
            data.addEntry(new Entry(set.getEntryCount(), val), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(50);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ContextCompat.getColor(this, R.color.lineBlue));
        set.setCircleColor(ContextCompat.getColor(this, R.color.lineBlue));
        set.setLineWidth(2f);
        set.setCircleRadius(6f);
        set.setFillAlpha(65);
        set.setFillColor(ContextCompat.getColor(this, R.color.lineBlue));
        set.setHighLightColor(Color.BLACK);
        set.setValueTextColor(Color.BLACK);
        set.setLabel("TEST");
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setCircleColorHole(ContextCompat.getColor(this, android.R.color.white));
        set.setCircleHoleRadius(3f);
        set.setDrawValues(true);
        return set;
    }

    private Thread thread;

    /*private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry();
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 50){
                // only reads data twice per 100 milli second
                lastUpdate = curTime;

                // alpha is calculated as t / (t + dT)
                // with t, the low-pass filter's time-constant
                // and dT, the event delivery rate

                final float alpha = 0.8f;

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                // Math.round returns LONG and INT only, therefore multiply linear_acceleration[n] by 100
                // to shift decimal points forward and then by dividing it to 100, it'll be shifted back
                // to the original decimal position.
                linear_acceleration[0] = Math.round(linear_acceleration[0] * 100) / 100f;
                linear_acceleration[1] = Math.round(linear_acceleration[1] * 100) / 100f;
                linear_acceleration[2] = Math.round(linear_acceleration[2] * 100) / 100f;

                /*tvAccelerationX.setText("X: " + String.valueOf(event.values[0]));
                tvAccelerationY.setText("Y: " + String.valueOf(event.values[1]));
                tvAccelerationZ.setText("Z: " + String.valueOf(event.values[2]));*/

                tvAccelerationX.setText("X: " + String.format("%.2f", linear_acceleration[0]));
                tvAccelerationY.setText("Y: " + linear_acceleration[1]);
                tvAccelerationZ.setText("Z: " + linear_acceleration[2]);

                addEntry(linear_acceleration[0]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onResume() {
        super.onResume();

        lastUpdate = System.currentTimeMillis();

        // In this example, the default data delay (SENSOR_DELAY_NORMAL) is specified when the registerListener() method is invoked.
        // The data delay (or sampling rate) controls the interval at which sensor events are sent to your application via the onSensorChanged() callback method.
        // The default data delay is suitable for monitoring typical screen orientation changes and uses a delay of 200,000 microseconds.
        // You can specify other data delays, such as SENSOR_DELAY_GAME (20,000 microsecond delay), SENSOR_DELAY_UI (60,000 microsecond delay), or SENSOR_DELAY_FASTEST (0 microsecond delay).
        // As of Android 3.0 (API Level 11) you can also specify the delay as an absolute value (in microseconds).

        // As SENSOR_DELAY_NORMAL is too fast for the application, I choose to have a delay of 2 * 10^7 microseconds = 20 seconds
        // but it would still not work as it is only a hint to the system.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSensorManager.registerListener(this, sensorAccelerometer, 2 * 1000 * 1000 * 10, 2 * 1000 * 1000 * 10);
        } else {
            mSensorManager.registerListener(this, sensorAccelerometer, 2 * 1000 * 1000 * 10);
        }
                //SensorManager.SENSOR_DELAY_NORMAL * 1000);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /*@Override
    public void onBackPressed() {
        //super.onBackPressed();
        this.finish();
    }*/

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
