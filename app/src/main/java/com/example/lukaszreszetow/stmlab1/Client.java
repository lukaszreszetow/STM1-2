package com.example.lukaszreszetow.stmlab1;


import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Point, Void, MainActivity.DaneOdSerwera> {

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
    protected MainActivity.DaneOdSerwera doInBackground(Point... points) {
       // Log.d("Wiadomosc", "Client started");

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(new Gson().toJson(points[0]));
            bw.write("\n");
            bw.flush();


//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
//                    1024);
//            byte[] buffer = new byte[1024];
//
//            int bytesRead;
           // Log.d("Wiadomosc", "Client czekam");
            InputStream inputStream = socket.getInputStream();
//
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);

            String gsonString = br.readLine();
            if(gsonString.equals("KONIEC GRY")){
                Log.d("Wiadomosc", "CLIENT KONIEC GRY");
                activity.finish();
            } else {
                Type type = new TypeToken<MainActivity.DaneOdSerwera>() {
                }.getType();
                final MainActivity.DaneOdSerwera punkt = new Gson().fromJson(gsonString, type);
                //  Log.d("Wiadomosc", "Client dostalem od serwera");

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.triggerRysowania(punkt);
                    }
                });
            }

//
//            /*
//             * notice: inputStream.read() will block if no data return
            //*/
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response += byteArrayOutputStream.toString("UTF-8");
//            }

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


    @Override
    protected void onPostExecute(MainActivity.DaneOdSerwera result) {
//        activity.triggerRysowania(result);
        //Log.d("Wiadomosc", "Client ended");
        super.onPostExecute(result);
    }

}