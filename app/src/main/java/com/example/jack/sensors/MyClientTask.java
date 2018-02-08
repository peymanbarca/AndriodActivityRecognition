package com.example.jack.sensors;


import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MyClientTask extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    List<Float> gyro_x1 = new ArrayList<Float>();
    List<Float> gyro_y1 = new ArrayList<Float>();
    List<Float> gyro_z1 = new ArrayList<Float>();
    Boolean status1;

    MyClientTask(String addr, int port,List<Float> gyro_x,List<Float> gyro_y,List<Float> gyro_z,Boolean status){
        dstAddress = addr;
        dstPort = port;
        gyro_x1=gyro_x;
        gyro_y1=gyro_y;
        gyro_z1=gyro_z;
        status1=status;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

    /*
     * notice:
     * inputStream.read() will block if no data return
     */
//            while ((bytesRead = inputStream.read(buffer)) != -1){
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response += byteArrayOutputStream.toString("UTF-8");
//            }

//            OutputStream out = socket.getOutputStream();
//            PrintWriter output = new PrintWriter(out);
//            output.println("Hello from Android");
//            out.flush();
//            out.close();

             PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);

            for (int i=0;i<gyro_z1.size();i++)
            {
                out.println(gyro_x1.get(i));
                out.println(gyro_y1.get(i));
                out.println(gyro_z1.get(i));
            }


//            out.flush();
//            out.close();

            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        super.onPostExecute(result);
    }

}

