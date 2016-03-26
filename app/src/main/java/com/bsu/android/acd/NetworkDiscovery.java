package com.bsu.android.acd;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by surajdeuja on 3/3/16.
 */
public class NetworkDiscovery {
    private Context mContext;
    public static final int SERVER_DISCOVERY_PORT = 9124; // ACD device listens on this port
    private final String TAG = "NetworkDiscovery";

    public NetworkDiscovery(Context context) {
        mContext = context;
    }

    /**
     * Send broadcast message for ACD device
     */
    public void discoverDevice() {
        String broadcastMsg = JsonUtils.createBroadcastPayload();
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(SERVER_DISCOVERY_PORT);
            // Send broadcast for ACD device
            datagramSocket.setBroadcast(true);
            DatagramPacket broadcastPacket = new DatagramPacket(broadcastMsg.getBytes(),
                    broadcastMsg.length(),
                    getBroadcastAddress(),
                    SERVER_DISCOVERY_PORT);
            datagramSocket.send(broadcastPacket);

            // Create receive buffer
            byte[] buf = new byte[1024];
            DatagramPacket recvPacket  = new DatagramPacket(buf, buf.length);

            // Wait for response
            while(true) {
                datagramSocket.setSoTimeout(5000);      // Set timeout after 5 seconds
                datagramSocket.receive(recvPacket);
                String msg = new String(recvPacket.getData(),0,recvPacket.getLength());
                if (JsonUtils.isBroadcastAck(msg)) {
                    broadcastDevice(JsonUtils.getDeviceName(msg), recvPacket.getAddress().getHostAddress());
                }
                Log.d(TAG, new String(recvPacket.getData(), 0, recvPacket.getLength()));
            }
        } catch (SocketException e) {
            Log.e(TAG, "Could not initialize udp socket at PORT " + SERVER_DISCOVERY_PORT);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get broadcast address");
        } finally {
            if (datagramSocket != null)
                datagramSocket.close();
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi.getDhcpInfo();

        int broadcast = (dhcpInfo.ipAddress & dhcpInfo.netmask) | ~dhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xff);
        }
        return InetAddress.getByAddress(quads);
    }

    /**
     * Function to broadcast the new device found
     */
    public void broadcastDevice(String deviceName, String deviceIp) {
        Log.d(TAG, "Sending Device found at " + deviceIp);
        Intent intent = new Intent(MainActivity.ADD_DEVICE);
        intent.putExtra("device-name", deviceName);
        intent.putExtra("device-ip", deviceIp);
        LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(intent);
    }

}
