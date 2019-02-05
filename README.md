# SimpleBluetooth

<p align="center">
  <img src="https://github.com/prateekro/SimpleBluetooth/blob/master/screen/Screenshot_20190205-084207.jpg" height="100" title="Discovered Devices">
  <img src="https://github.com/prateekro/SimpleBluetooth/blob/master/screen/Screenshot_20190205-084215.jpg" height="100" title="Receiver - Demo Screenshot" alt="Receiver - Demo Screenshot">
</p>

## Native Usage of Bluetooth API's from 
### Create Adapter
    private BluetoothAdapter mBluetoothAdapter;

### Check Bluetooth Support
    if (mBluetoothAdapter == null) {
        // Device doesn't support Bluetooth
    }

### Initialise Adapter
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

### Enable Bluetooth
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

