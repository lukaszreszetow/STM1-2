package com.example.lukaszreszetow.stmlab1;


import android.graphics.Point;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server {
    ServerActivity activity;
    ServerSocket serverSocket;
    static final int socketServerPORT = 8080;

    public Server(ServerActivity activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            try {
                //Log.d("Wiadomosc", "Server started");

                // create ServerSocket using specified port
                serverSocket = new ServerSocket(socketServerPORT);

                while (true) {
                    // block the call until connection is created and return
                    // Socket object
                    Socket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();

                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader br = new BufferedReader(isr);
                    String gsonString = br.readLine();
                    Type type = new TypeToken<Point>(){}.getType();
                    Point punkt = new Gson().fromJson(gsonString, type);
                    activity.rysujSerwer(punkt);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //activity.msg.setText(message);
                        }
                    });

                    SocketServerReplyThread socketServerReplyThread =
                            new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                if(activity.koniecGry){
                    printStream.print("KONIEC GRY");
                    Log.d("Wiadomosc", "KONIEC GRY");
                } else {
                    printStream.print(new Gson().toJson(activity.dane));
                }
                printStream.close();


                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.d("Wiadomosc", "WIADOMOSC SERWER ");
                        //activity.msg.setText(message);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.d("Wiadomosc", "WIADOMOSC SERWER ");
                    //activity.msg.setText(message);
                }
            });

            if(activity.koniecGry){
                activity.finish();
            }
        }

    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        Log.d("Wiadomosc", ip);
        return ip;
    }
}