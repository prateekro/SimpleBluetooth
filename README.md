# SimpleBluetooth 
 
 [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/prateekro/SimpleBluetooth/blob/master/LICENSE.md)
 
<p align="center">
  <img src="https://github.com/prateekro/SimpleBluetooth/blob/master/screen/Screenshot_20190205-084207.jpg" height="350" title="Discovered Devices">
  <img src="https://github.com/prateekro/SimpleBluetooth/blob/master/screen/Screenshot_20190205-084215.jpg" height="350" title="Receiver - Demo Screenshot" alt="Receiver - Demo Screenshot">
</p>

## Native Usage of Bluetooth API's from [Android Documentation for Bluetooth](https://developer.android.com/guide/topics/connectivity/bluetooth#java)
---
#### Add Permissions
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
---
### Create Adapter
    private BluetoothAdapter mBluetoothAdapter;

### Check Bluetooth Support
    if (mBluetoothAdapter == null) {
        // Device doesn't support Bluetooth
    }

### Initialise Adapter
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

### Enable Bluetooth
#### Enable Bluetooth
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //REQUEST_ENABLE_BT = 1 
    
#### Force Enable Bluetooth
    mBluetoothAdapter.enable();

### Disable Bluetooth
    mBluetoothAdapter.disable();

### List Paired Devices
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    if (pairedDevices.size() > 0) {
      for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address
      }
    }

### To Scan / Discover Devices
#### Add Permissions in Manifest
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

#### Add filters - Preferably in onResume (reason: Will require the unregister in onPause)
    IntentFilter filter = new IntentFilter();
    filter.addAction(BluetoothDevice.ACTION_FOUND);
    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    registerReceiver(mReceiver, filter); //BroadcastReceiver mReceiver
    
#### Call permission programatically as well (otherwise - Scanning results won't appear, A must TODO since Android.ver > 6.0)
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        
#### Add a Receiver (BroadcastReceiver) to catch the above added intent filters
    if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                
      final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

      if (state == BluetoothAdapter.STATE_ON) {
          Log.d(TAG, "onReceive: Bluetooth is ON");
      }else if(state == BluetoothAdapter.STATE_OFF){
          Log.d(TAG, "onReceive: Bluetooth is OFF");
      }else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
          Log.d(TAG, "onReceive: Started Discovery");
      }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
          Log.d(TAG, "onReceive: Finished Discovery");
      }else if (BluetoothDevice.ACTION_FOUND.equals(action)) {     
          // When discovery finds a device
          // Get the BluetoothDevice object from the Intent
          BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          mBtDiscoveredList.add(device);

          Log.d(TAG, "onReceive: Found device"+ device.getName() +" ::Address: "+device.getAddress()+" ::String: "+device.toString());
      }
    }
#### and, Start Discovery / Scanning for a new device
    mBluetoothAdapter.startDiscovery();

### Stop Discovery / Scan
    mBluetoothAdapter.cancelDiscovery();
    
### Pair Device / Bond with Device
    Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
    Method createBondMethod = class1.getMethod("createBond");
    Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
    return returnValue.booleanValue();
    
### Don't forget 
#### To unregister the receiver while you leave the app
    unregisterReceiver(mReceiver);

#### To stop scanning while you leave the app
##### check with 
    mBluetoothAdapter.isDiscovering();
##### And, cancel with
    mBluetoothAdapter.cancelDiscovery();
    
    

