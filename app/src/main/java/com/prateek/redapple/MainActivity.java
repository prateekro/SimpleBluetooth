package com.prateek.redapple;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static String TAG = "MAIN.BluetoothAdapter";
    TextView tv_welcome;
    Switch bt_switch;
    private BluetoothAdapter mBluetoothAdapter;
    Button start_discovery, stop_discovery;
    private ArrayList<BluetoothDevice> mBtDiscoveredList;
    private ArrayList<String> mDiscoveredList;
    private ArrayList<String> mPairedList;
    ListView discovered_devices;
    ArrayAdapter<String> discover_devices_adapter;
    ListView paired_devices;
    ArrayAdapter<String> paired_devices_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    protected void onStart() {
        super.onStart();

        tv_welcome = (TextView) findViewById(R.id.textView);
        bt_switch = (Switch) findViewById(R.id.bt_switch);

        bt_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothAdapter.isEnabled()){
                    mBluetoothAdapter.disable();
                    bt_switch.setChecked(false);
                    bt_switch.setText("BT Disabled");
//                    guiChangesForSwitch(false);
                }else {
                    mBluetoothAdapter.enable();
                    bt_switch.setChecked(true);
                    bt_switch.setText("BT Enabled");
//                    guiChangesForSwitch(true);
                }
            }
        });

        start_discovery = (Button) findViewById(R.id.start_discovery);
        stop_discovery = (Button) findViewById(R.id.stop_discovery);

        discovered_devices = (ListView) findViewById(R.id.lv_discovered_list);
        paired_devices = (ListView) findViewById(R.id.lv_paired_list);

        //ListView and configuration
        mPairedList = new ArrayList<String>();
        paired_devices_adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                mPairedList);
        paired_devices.setAdapter(paired_devices_adapter);

        mDiscoveredList = new ArrayList<String>();
        mBtDiscoveredList = new ArrayList<BluetoothDevice>();

        discover_devices_adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                mDiscoveredList);
        discovered_devices.setAdapter(discover_devices_adapter);

        discovered_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, ((TextView) view).getText() + "-:-Pos: "+i, Toast.LENGTH_SHORT).show();

                //Call to pair

                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                }

                Boolean isBonded = false;

                if(i != 0 && i!=1 && i!=(mDiscoveredList.size()-1)) {
                    try {
                        Log.d(TAG, "onItem - Clicked and found: " + mBtDiscoveredList.get(i - 1).getName() + " :: " + mBtDiscoveredList.get(i - 1).getAddress());
                        isBonded = createBond(mBtDiscoveredList.get(i - 1));
                        if (isBonded) {
                            Log.d(TAG, "onItemClick: I Think Connected");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }//connect(bdDevice);
                    Log.d(TAG, "The bond is created/inprocess: "+ isBonded );
                }
                //.call to pair
            }
        });



//        ***************************************************
//              **************             **************
//        **************        Bluetooth        **************
//              **************             **************
//        ***************************************************

        if (!mBluetoothAdapter.isEnabled()) {

//        ***************************************************
//          Bluetooth - Correct Way to do it. (Without Admin Permission)
//        ***************************************************
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, 1);

            mBluetoothAdapter.enable(); //Admin Permission
            bt_switch.setChecked(true);

            tv_welcome.setText("Status: BT Force Enabled");
            guiChangesForSwitch(true);

            Toast.makeText(this, "Not Enabled", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Not Enabled = BluetoothAdapter");

        } else{

            Toast.makeText(this, "Enabled", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Enabled = BluetoothAdapter");
            bt_switch.setChecked(true);

//          Bluetooth - if connected - Get Address of your hardware / (deprecated from Lollipop API)
//        ***************************************************
            Log.d(TAG, "get Address: "+mBluetoothAdapter.getAddress().toString());
//        ***************************************************

            tv_welcome.setText("Status: BT Enabled");
            guiChangesForSwitch(true);

            Log.d(TAG, "get Name: "+mBluetoothAdapter.getName().toString());  //OnePlus 3


//          Bluetooth - Change Bluetooth name
//        ***************************************************
//            mBluetoothAdapter.setName("");

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                mPairedList.add("** Paired List **");
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    if(!((device.getName().isEmpty() || deviceName.isEmpty()) && (device.getAddress().isEmpty() || deviceHardwareAddress.isEmpty()))) {
                        Log.d(TAG, "onStart: pairedDevices deviceName: "+deviceName +" ::Addr : " + deviceHardwareAddress);
                        mPairedList.add(deviceName+ ",\n"+ deviceHardwareAddress);
                        paired_devices_adapter.notifyDataSetChanged();
                    }else {
                        //Just to be safe - It should never go here.
                        Log.d(TAG, "onStart: pairedDevices: "+"None.(a)");
                    }
                }
            }else {
                Log.d(TAG, "onStart: pairedDevices: "+"None.(b)");
            }

        } //. Bluetooth connected

//        ***************************************************

        start_discovery.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter); //BroadcastReceiver mReceiver

        guiChangesForDiscoveryStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "onPause: Stopped Discovering");
        }
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onClick(View view) {
        if (start_discovery.getId() == view.getId()){
            if (mBluetoothAdapter.isEnabled()) {

                followDiscoverySteps();

                //List of Bluetooth devices discovered.
                mDiscoveredList.add("Test 0: discovered");

                Log.d(TAG, "onClick: Start Discovery");

            }else {

                mBluetoothAdapter.enable();

                followDiscoverySteps();

                mDiscoveredList.add("Test A: discovered");

                Log.d(TAG, "onClick: Enable Bluetooth and Start Discovery");
            }
        }
        if (stop_discovery.getId() == view.getId()){
            Log.d(TAG, "onClick: Stopping Discovering");
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                guiChangesForDiscoveryStatus();
                Log.d(TAG, "onClick: Stopped Discovering");
            }
        }
    }

    private void followDiscoverySteps() {
        callPermissionForDiscovering(); // A must call before discovery
        mBluetoothAdapter.startDiscovery();
        guiChangesForDiscoveryStatus();
    }

    private void callPermissionForDiscovering() {
        //Required since Android 6
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, "onReceive action: "+action.toString());

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    Toast.makeText(MainActivity.this, "(\"ACTION_STATE_CHANGED: STATE_ON\");", Toast.LENGTH_SHORT).show();
                    guiChangesForSwitch(true);
                }else if(state == BluetoothAdapter.STATE_OFF){
                    Toast.makeText(MainActivity.this, "(\"ACTION_STATE_CHANGED: STATE_OFF\");", Toast.LENGTH_SHORT).show();
                    guiChangesForSwitch(false);
                }

            }

            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                showToast("ACTION_DISCOVERY_STARTED");
                Log.d(TAG, "onReceive: Started Discovery");
                tv_welcome.setText("Discovering...");
            }

            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                showToast("ACTION_DISCOVERY_FINISHED");
                Log.d(TAG, "onReceive: Finished Discovery");

                if (mBluetoothAdapter.isEnabled()){
                    tv_welcome.setText("Finished Discovery. Select device to pair.");
                }else{
                    tv_welcome.setText("Finished Discovery. Enable BT and Select to pair.");
                }

                mDiscoveredList.add("Test Final");
                discover_devices_adapter.notifyDataSetChanged();

                guiChangesForDiscoveryStatus();

            }

            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {     // When discovery finds a device
                // Get the BluetoothDevice object from the Intent
                tv_welcome.setText("Listing devices found: Right List");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBtDiscoveredList.add(device);
                mDiscoveredList.add(device.getName()+",\n"+device.getAddress());
                //Update - Populate in the listview
                discover_devices_adapter.notifyDataSetChanged();

//                showToast("Device found = " + device.getName());
                Log.d(TAG, "onReceive: Found device"+ device.getName() +" ::Address: "+device.getAddress()+" ::String: "+device.toString());
            }
        }
    };

    private void guiChangesForDiscoveryStatus() {
        boolean b = mBluetoothAdapter.isDiscovering();

        stop_discovery.setClickable(b);
        start_discovery.setClickable(!b);

        float start = b ? (float) 0.1 : (float) 1;
        float stop = b ? (float) 1 : (float) 0.1;

        start_discovery.setAlpha(start);
        stop_discovery.setAlpha(stop);
    }

    private void guiChangesForSwitch(Boolean bool){
        bt_switch.setChecked(bool);
        bt_switch.setText(bool? "BT Enabled" : "BT Disabled");
        tv_welcome.setText("Status: " + (bool? "BT Enabled" : "BT Disabled"));
    }

    public void showToast(String sr){
        Toast.makeText(this, sr, Toast.LENGTH_SHORT).show();
    }
}

