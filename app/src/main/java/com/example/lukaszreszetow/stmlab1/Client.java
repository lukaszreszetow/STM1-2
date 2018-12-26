package com.example.lukaszreszetow.stmlab1;


import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Integer, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    ClientActivity activity;

    Client(String addr, int port, ClientActivity activity) {
        dstAddress = addr;
        dstPort = port;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(new Gson().toJson(integers[0]));
            bw.write("\n");
            bw.flush();


            InputStream inputStream = socket.getInputStream();
//
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);

            final String gsonString = br.readLine();
            if(gsonString.equals("KONIEC GRY")){
                Log.d("Wiadomosc", "CLIENT KONIEC GRY");
                activity.finish();
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.zmianaPozycjiSerwera(Integer.parseInt(gsonString));
                    }
                });
            }


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}