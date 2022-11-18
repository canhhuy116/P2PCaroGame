package com.example.ui_caro_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class HomeActivity extends FragmentActivity implements MainCallBacks {
    boolean isSuccess=false;
    EditText InputMsg;
    Button Caro_AI, DiscoveryBtn;
    ListView DeviceList;
    private WifiP2pManager manager;
    boolean isWifiP2pEnabled = false;
    boolean retryChannel = false;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private final IntentFilter intentFilter = new IntentFilter();
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray={};
    WifiP2pDevice[] deviceArray;
    Socket socket;
    ServerClass serverClass;
    ClientClass clientClass;
    FragHome fragHome;
    Fragment_Caro_with_friend fragCaro;
    boolean isHost;
    String deviceName;
    FragmentTransaction ft;
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // add necessary intent values to be matched.
        ft = getSupportFragmentManager().beginTransaction();
        fragHome = FragHome.newInstance("");
        ft.replace(R.id.frame_home, fragHome);
        ft.commit();
        ft = getSupportFragmentManager().beginTransaction();
        fragCaro = Fragment_Caro_with_friend.newInstance("");

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        if (!initP2P()) {
            finish();
        }

    }




    private boolean initP2P() {
        Caro_AI = (Button) findViewById(R.id.play_now);
        DiscoveryBtn = (Button) findViewById(R.id.play_with_friend);
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
            if(peers.size()==0)
            {
                Toast.makeText(HomeActivity.this, "Empty", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeActivity.this, "Connected ", Toast.LENGTH_SHORT).show();
                isHost =true;
                serverClass = new ServerClass();
                serverClass.start();
                replaceFragment(fragCaro);
            }else if(wifiP2pInfo.groupFormed)
            {
                Toast.makeText(HomeActivity.this, "connected ", Toast.LENGTH_SHORT).show();
                isHost =false;
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
                replaceFragment(fragCaro);
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
                                        //Toast.makeText(HomeActivity.this,tempMSG,Toast.LENGTH_SHORT).show();
                                        fragCaro.onMsgFromMainToFragment(tempMSG,0,0);
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
                                       // Toast.makeText(HomeActivity.this,tempMSG,Toast.LENGTH_SHORT).show();
                                        fragCaro.onMsgFromMainToFragment(tempMSG,0,0);

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

    public void Dialog_listdevice()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Device List");
        builder.setItems(deviceNameArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final WifiP2pDevice device =deviceArray[which];
                WifiP2pConfig config=new WifiP2pConfig();
                config.deviceAddress=device.deviceAddress;
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                       // Toast.makeText(HomeActivity.this, "Connected to "+device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        //Toast.makeText(HomeActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    private  void innitFragment() {
        FragHome fragHome = new FragHome() ;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_home,fragHome);
        fragmentTransaction.commit();
    }
    private  void replaceFragment(Fragment fragment){
        if(fragment!=null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_home,fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onMsgFromFragToMain(String sender, Integer strValue,String text) {
        if (sender.equals("abc")) {
            send(text);
            /*try {
                // Toast.makeText(getApplication(),"ahihi"+strValue.toString(),Toast.LENGTH_SHORT).show();
                fragHome.onMsgFromMainToFragment(0,0);
            } catch (Exception e) { Log.e("Error ", "onStrFromFragToMain");}*/
        }
        if (sender.equals("Left-frag"))
        {
            if(strValue==250){
                Intent intent=new Intent(HomeActivity.this,Caro_with_AI.class);
                startActivity(intent);
            }
            if(strValue==251)
            {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(HomeActivity.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
                        if(deviceNameArray.length!=0)Dialog_listdevice();
                        else Toast.makeText(HomeActivity.this,"Device Empty",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(HomeActivity.this, "Discovery Failed ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
           /* try {
                fragCaro.onMsgFromMainToFragment(strValue);
            } catch (Exception e) {
                Log.e("Error ", "onStrFromFragToMain");

            }*/
        }
    }
    private  void send(String msg)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
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
                    Toast.makeText(HomeActivity.this, "in-connect 3", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
