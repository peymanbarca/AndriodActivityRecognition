package com.example.jack.sensors;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jack.sensors.MyClientTask;
import com.example.jack.sensors.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.R.attr.data;
import static com.example.jack.sensors.R.id.accel;
import static com.example.jack.sensors.R.id.none;


public class SensorsActivity extends Activity implements SensorEventListener {

    private TextView mAccelTextView;
    private TextView mGyroTextView;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button bm;
    private EditText e1;
    private EditText e2;
    private Chronometer c1;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    Boolean status_socket=false;
    int flag=0; // to start and stop sensor event

    Boolean start_flag=false;


    List<Float> acc_x = new ArrayList<Float>();
    List<Float> acc_y = new ArrayList<Float>();
    List<Float> acc_z = new ArrayList<Float>();

    List<Float> gyro_x = new ArrayList<Float>();
    List<Float> gyro_y = new ArrayList<Float>();
    List<Float> gyro_z = new ArrayList<Float>();


    private SensorManager mSensorManager;

    private Sensor mAccelerometer;
    private Sensor mOrientation;

    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    boolean menu_flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        mAccelTextView = (TextView) findViewById(accel);
        mGyroTextView  = (TextView) findViewById(R.id.gyro);
        b1 = (Button) findViewById(R.id.start);
        b2 = (Button) findViewById(R.id.send);
        b3 = (Button) findViewById(R.id.b3);
        bm = (Button) findViewById(R.id.bm);
        e1 = (EditText) findViewById(R.id.e1);
        e2 = (EditText) findViewById(R.id.e2);
        c1 = (Chronometer) findViewById(R.id.c1) ;

        e1.setText(((Const) this.getApplication()).getIp().toString());
        e2.setText(((Const) this.getApplication()).getPort().toString());

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mOrientation     = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if(mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            Log.e("onCreate", "No Accelerometer found!");
            finish();
        }

        if(mOrientation != null) {
            mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            Log.e("onCreate", "No Orientation found!");
            finish();
        }

        // -----------------

       mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

         //enable ActionBar app icon to behave as action to toggle nav drawer
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);

//        // ActionBarDrawerToggle ties together the the proper interactions
//        // between the sliding drawer and the action bar app icon
//        mDrawerToggle = new ActionBarDrawerToggle(
//                this,                  /* host Activity */
//                mDrawerLayout,         /* DrawerLayout object */
//                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
//                R.string.drawer_open,  /* "open drawer" description for accessibility */
//                R.string.drawer_close  /* "close drawer" description for accessibility */
//        ) {
//            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(mTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }




    // -------------------------------------------------------

        b1.setOnClickListener(new View.OnClickListener()   {

            @Override
                public void onClick(View v)
            {
                start_flag=true;

                if (flag==0)
                {
                    flag = 1; // to run onSensor changed method
                    Toast.makeText(SensorsActivity.this, "Start to record data ! ",
                            Toast.LENGTH_SHORT).show();
                    b1.setText("Stop");
                    c1.setBase(SystemClock.elapsedRealtime());
                    c1.start();

                }
                else
                {
                    flag = 0; // to stop onSensor changed method
                    int len_data=acc_x.size();
                    Toast.makeText(SensorsActivity.this, "Recording data Stopped!" + String.valueOf(len_data) + " data collected !"  ,
                            Toast.LENGTH_SHORT).show();
                    b1.setText("Start");
                    c1.stop();

                }
            }


        });

        b2.setOnClickListener(new View.OnClickListener()   {

            @Override
            public void onClick(View v)
            {
                if (start_flag && flag==0)
                {
                    b2.setText("Dont click Please");

                    Boolean status=sendToServer ();

                    if (status)
                    {
                        b2.setText("Send To Server");
                        start_flag=false;  // to force user to collect new data and send again
                    }

                    else
                    {
                        b2.setText("Send To Server Again");
                    }


                }

                else
                {
                    Toast.makeText(SensorsActivity.this, "You dont have new data to send to server or Still Collecting!",
                            Toast.LENGTH_SHORT).show();
                }
            }


        });

        b3.setOnClickListener(new View.OnClickListener()   {

            @Override
            public void onClick(View v)
            {


                e1.setVisibility(View.VISIBLE);
                e2.setVisibility(View.VISIBLE);

                String host=e1.getText().toString();
                Integer port=Integer.parseInt(e2.getText().toString()) ;

//                MyClientTask myClientTask = new MyClientTask(
//                        "192.168.1.6", 5500,gyro_x,gyro_y,gyro_z);

                if (flag==0 && acc_x.size() != 0)
                {
                    MyClientTask myClientTask = new MyClientTask(
                            host, port,acc_x,acc_y,acc_z);
                    myClientTask.execute(status_socket);
                    acc_x = new ArrayList<Float>();
                    acc_y = new ArrayList<Float>();
                    acc_z = new ArrayList<Float>();
                }
                else
                {
                    Toast.makeText(SensorsActivity.this, "You dont have new data to send to server or Still Collecting!",
                            Toast.LENGTH_SHORT).show();
                }


//                try {
//                    Boolean status_socket1=myClientTask.execute(status_socket).get();
//                    Toast.makeText(SensorsActivity.this, status_socket1.toString(),
//                            Toast.LENGTH_SHORT).show();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }



//                if (status_socket==true)
//                    Toast.makeText(SensorsActivity.this, "Socket Connected and Success!",
//                            Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(SensorsActivity.this, "Socket Not Connected!",
//                            Toast.LENGTH_SHORT).show();

            }
        });

        bm.setOnClickListener(new View.OnClickListener()   {

            @Override
            public void onClick(View v)
            {
                if (menu_flag==false)
                {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    menu_flag=true;
                }
                else
                {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    menu_flag=false;
                }

            }


        });

    }



    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        if (position==1)
        {
            Toast.makeText(SensorsActivity.this, "You are now in Activity Recognition Part!",
                    Toast.LENGTH_SHORT).show();
        }
        else if (position==2)
        {
            Toast.makeText(SensorsActivity.this, "Coming Soon!",
                    Toast.LENGTH_SHORT).show();
        }
        else if (position==3)
        {
            startActivity(new Intent(SensorsActivity.this, SettingActivity.class));
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    //------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mOrientation,     SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }



//    private View.OnClickListener onClickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(final View v) {
//            switch(v.getId()){
//                case R.id.start:
//
//                    flag=1; // to run onSensor changed method
//                    Toast.makeText(SensorsActivity.this, "Start to record data ! ",
//                            Toast.LENGTH_LONG).show();
//                    break;
//                case R.id.send:
//                    //DO something
//                    break;
//            }
//
//        }
//    };

    void processAccelerometer(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        final float alpha = 0.8f;

        float[] gravity             = {0, 0, 0};
        float[] linear_acceleration = {0, 0, 0};
        String[] accel              = {"", "", ""};

        for(int i = 0; i < 3; i++) {
            // Isolate the force of gravity with the low-pass filter.
            gravity[i] = alpha * gravity[i] + (1 - alpha) * event.values[i];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[i] = event.values[i] - gravity[i];

            accel[i] = String.format("%8.5f\n", linear_acceleration[i]);
        }

        acc_x.add(Float.parseFloat(accel[0]));
        acc_y.add(Float.parseFloat(accel[1]));
        acc_z.add(Float.parseFloat(accel[2]));

        mAccelTextView.setText(accel[0] + accel[1] + accel[2]);
    }

    void processGyroscope(SensorEvent event) {
        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = (float)Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > 0.01) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            gyro_x.add(axisX);
            gyro_y.add(axisY);
            gyro_z.add(axisZ);

            mGyroTextView.setText(String.format("%8.5f\n", axisX));
            mGyroTextView.append(String.format("%8.5f\n", axisY));
            mGyroTextView.append(String.format("%8.5f\n", axisZ));

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
    }

    Boolean sendToServer ()
    {

        int len_data=acc_x.size();



        Boolean status=true;
        JSONArray jsonArray = new JSONArray();

        for (int i=0;i<2;i++) //len_data
        {
            Long tsLong = System.currentTimeMillis()/1000;
            JSONObject acc = new JSONObject();
            try {
                acc.put("id", i+1);
                acc.put("x_axis", acc_x.get(i));
                acc.put("y_axis", acc_y.get(i));
                acc.put("z_axis", acc_z.get(i));
                acc.put("time", tsLong);
                jsonArray.put(acc);

                //-------------------------- only for IOT project


            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                status=false;
                continue;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                status=false;
                continue;

            }

        }

        JSONObject finalobject = new JSONObject();
        try {
            finalobject.put("Acc", jsonArray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            status=false;
        }



        if (status)
        {
            Toast.makeText(SensorsActivity.this, "send data succsesfull !" + String.valueOf(len_data) + " data sent !" ,
                    Toast.LENGTH_SHORT).show();

        }

        return status;

    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (flag==1)
        {
            Sensor mySensor = event.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                processAccelerometer(event);
            }
             else if (mySensor.getType() == Sensor.TYPE_ORIENTATION) {
                processGyroscope(event);
            }
        }
    }

}
