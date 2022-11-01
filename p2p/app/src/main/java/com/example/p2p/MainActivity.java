package com.example.p2p;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    EditText InputMsg;
    Button SendBtn, DiscoveryBtn;
    ListView DeviceList;
    private WifiP2pManager manager;
    boolean isWifiP2pEnabled = false;
    boolean retryChannel = false;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private final IntentFilter intentFilter = new IntentFilter();
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
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


        DiscoveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this, "Discovery Failed : " + reason, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        DeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

            }
        });
       SendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                String msg = InputMsg.getText().toString();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(msg!=null&& isHost)
                        {
                            serverClass.write(msg.getBytes());

                        }else if(msg!=null && !isHost)
                        {
                            clientClass.write(msg.getBytes());
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "in-connect 3", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }




    private boolean initP2P() {
        SendBtn = (Button) findViewById(R.id.bt1);
        DiscoveryBtn = (Button) findViewById(R.id.bt2);
        DeviceList =(ListView) findViewById(R.id.lv1);
        InputMsg =(EditText) findViewById(R.id.edt1);

        // Device capability definition check
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Toast.makeText(this, "Wi-Fi Direct is not supported by this device.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Hardware capability check
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Toast.makeText(this, "Cannot get Wi-Fi system service.", Toast.LENGTH_SHORT).show();
            return false;
        }

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (manager == null) {
            Toast.makeText(this, "Cannot get Wi-Fi P2P system service.", Toast.LENGTH_SHORT).show();
            return false;
        }

        channel = manager.initialize(this, getMainLooper(), null);
        if (channel == null) {
            Toast.makeText(this, "Cannot initialize Wi-Fi P2P.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if (!wifiP2pDeviceList.equals(peers)) {
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
            }
            ArrayAdapter<String > adapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,deviceNameArray);
            DeviceList.setAdapter(adapter);
            if(peers.size()==0)
            {
                Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
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
    };





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

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    while (socket!=null)
                    {
                        try {
                            bytes =inputStream.read(buffer);
                            if(bytes>0)
                            {
                                int finalBytes=bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tempMSG=new String(buffer,0,finalBytes);
                                        Toast.makeText(MainActivity.this,tempMSG,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    while (socket!=null)
                    {
                        try {
                            bytes =inputStream.read(buffer);
                            if(bytes>0)
                            {
                                int finalBytes=bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tempMSG=new String(buffer,0,finalBytes);
                                        Toast.makeText(MainActivity.this,tempMSG,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}