package com.bsu.android.acd;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by surajdeuja on 3/3/16.
 */
public class NetworkDiscovery {
    public static final int SERVER_DISCOVERY_PORT = 9124; // ACD device listens on this port
    public static final int CLIENT_DISCOVERY_PORT = 9125; // Mobile app listens on this port
    public static final String UDP_CLIENT_BROADCAST_MSG = "ACD_DEVICE_DISCOVERY"; // APP sends UDP packet with this message
    public static final String UDP_ACD_BROADCAST_MSG = "ACD_DEVICE"; // ACD device UDP packet with this message
    private final String TAG = "NetworkDiscovery";
    private Context mContext;
    private InetAddress ACDInetAddress;

    public NetworkDiscovery(Context context) {
        mContext = context;
    }

    /**
     * Listen to the ACD device message after sending broadcast
     */
    public void listenBroadcast() throws Exception {
        byte[] recv_buf = new byte[1024];
        try {
            DatagramSocket datagramSocket = new DatagramSocket(CLIENT_DISCOVERY_PORT);
            datagramSocket.setSoTimeout(5000);
            DatagramPacket packet = new DatagramPacket(recv_buf, recv_buf.length);
            datagramSocket.receive(packet);
            ACDInetAddress = packet.getAddress();
            Log.d(TAG, "Found device " + ACDInetAddress.getHostAddress());
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "Socket timeout");
            throw e;
        } catch (SocketException e) {
            Log.e(TAG, "Could not initialize udp socket at PORT " + CLIENT_DISCOVERY_PORT);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Failed to listen for UDP packet");
            throw e;
        }
    }

    /**
     * Send broadcast message for ACD device
     */
    public void sendBroadcast() throws Exception {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(SERVER_DISCOVERY_PORT);
            datagramSocket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(UDP_CLIENT_BROADCAST_MSG.getBytes(),
                    UDP_CLIENT_BROADCAST_MSG.length(),
                    getBroadcastAddress(),
                    SERVER_DISCOVERY_PORT);
            datagramSocket.send(packet);
            datagramSocket.close();
        } catch (SocketException e) {
            Log.e(TAG, "Could not initialize udp socket at PORT " + SERVER_DISCOVERY_PORT);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Failed to get broadcast address");
            throw e;
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

    public InetAddress getACDIpAddress() {
        if (ACDInetAddress == null) {
            return null;
        }
        return ACDInetAddress;
    }

}
