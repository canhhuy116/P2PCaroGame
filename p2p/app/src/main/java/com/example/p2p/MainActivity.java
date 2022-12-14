package com.example.p2p;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity implements WifiP2pManager.ChannelListener, WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {
    public static final String TAG = "Group 10";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    public BreakIterator connectionStatus;

    EditText InputMsg;
    Button SendBtn, DiscoveryBtn, p2pBtn;
    ListView DeviceList;

    private WifiP2pManager manager;
    boolean isWifiP2pEnabled = false;
    boolean retryChannel = false;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private final IntentFilter intentFilter = new IntentFilter();

    List<WifiP2pDevice> peers = new ArrayList<>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    Socket socket;
    ServerClass serverClass;
    ClientClass clientClass;

    boolean isHost;
    String deviceName;

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Fine location permission is not granted!");
                finish();
            }
        }
    }

    private boolean initP2P() {
        SendBtn = (Button) findViewById(R.id.sendBtn);
        DiscoveryBtn = (Button) findViewById(R.id.discoveryBtn);
        p2pBtn = (Button) findViewById(R.id.p2pBtn);
        DeviceList =(ListView) findViewById(R.id.lv1);
        InputMsg =(EditText) findViewById(R.id.edt1);

        // Device capability definition check
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.");
            return false;
        }

        // Hardware capability check
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi system service.");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!wifiManager.isP2pSupported()) {
                Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
                return false;
            }
        }

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (manager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");
            return false;
        }

        channel = manager.initialize(this, getMainLooper(), null);
        if (channel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add necessary intent values to be matched.

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if (!initP2P()) {
            finish();
        }

        p2pBtn.setOnClickListener(this::clickP2P);
        DiscoveryBtn.setOnClickListener(this::clickDiscover);
        SendBtn.setOnClickListener(this::clickSend);

        DeviceList.setOnItemClickListener((parent, view, position, id) -> {
            final WifiP2pDevice device =deviceArray[position];
            WifiP2pConfig config=new WifiP2pConfig();
            config.deviceAddress=device.deviceAddress;
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                    Toast.makeText(MainActivity.this, "Connected to "+device.deviceName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i) {
                    Toast.makeText(MainActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
                }
            });

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }

    }

    public void clickP2P(View view) {
        if (manager != null && channel != null) {

            // Since this is the system wireless settings activity, it's
            // not going to send us a result. We will be notified by
            // WiFiDeviceBroadcastReceiver instead.

            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        } else {
            Log.e(TAG, "channel or manager is null");
        }
    }

    public void clickDiscover(View view) {
        if (!isWifiP2pEnabled) {
            Toast.makeText(MainActivity.this, R.string.p2p_off_warning,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickSend(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        String msg = InputMsg.getText().toString();
        if (msg.length() == 0) {
            Toast.makeText(MainActivity.this, "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        executor.execute(() -> {
            if(isHost)
            {
                serverClass.write(msg.getBytes());

            }else {
                clientClass.write(msg.getBytes());
            }
        });
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        deviceArray = new WifiP2pDevice[0];
        deviceNameArray = new String[0];
        peers.clear();
        String[] empty = new String[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, empty);
        DeviceList.setAdapter(adapter);
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
        if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner)
        {
            Toast.makeText(MainActivity.this, "Connected ", Toast.LENGTH_SHORT).show();
            isHost =true;
            serverClass = new ServerClass();
            serverClass.start();
        }else if(wifiP2pInfo.groupFormed)
        {
            Toast.makeText(MainActivity.this, "connected ", Toast.LENGTH_SHORT).show();
            isHost =false;
            clientClass = new ClientClass(groupOwnerAddress);
            clientClass.start();
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        peers.clear();
        peers.addAll(wifiP2pDeviceList.getDeviceList());
        deviceNameArray = new String[wifiP2pDeviceList.getDeviceList().size()];
        deviceArray = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];
        int index = 0;
        for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
            deviceNameArray[index] = device.deviceName;
            deviceArray[index] = device;
            index++;
        }

        ArrayAdapter<String > adapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,deviceNameArray);
        DeviceList.setAdapter(adapter);
        if (peers.size() == 0) {
            Log.d(MainActivity.TAG, "No devices found");
        }
    }

    public class  ServerClass extends Thread {
        ServerSocket serverSocket;
        private InputStream inputStream;

        private OutputStream outputStream;
        public  void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                byte[] buffer = new byte[1024];
                int bytes;
                while (socket!=null)
                {
                    try {
                        bytes =inputStream.read(buffer);
                        if(bytes>0)
                        {
                            int finalBytes=bytes;
                            handler.post(() -> {
                                String tempMSG=new String(buffer,0,finalBytes);
                                Toast.makeText(MainActivity.this,tempMSG,Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }






    public class ClientClass extends Thread
    {
        String hostAdd;
        private InputStream inputStream;
        private OutputStream outputStream;
        public  void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public ClientClass(InetAddress hostAddress)
        {
            hostAdd=hostAddress.getHostAddress();
            socket=new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler=new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                byte[] buffer = new byte[1024];
                int bytes;
                while (socket!=null)
                {
                    try {
                        bytes =inputStream.read(buffer);
                        if(bytes>0)
                        {
                            int finalBytes=bytes;
                            handler.post(() -> {
                                String tempMSG=new String(buffer,0,finalBytes);
                                Toast.makeText(MainActivity.this,tempMSG,Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}