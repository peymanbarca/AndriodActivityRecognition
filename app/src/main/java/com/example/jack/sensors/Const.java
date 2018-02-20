package com.example.jack.sensors;

import android.app.Application;

import static android.R.attr.port;

/**
 * Created by zevik on 2/20/2018.
 */

public class Const extends Application
{
    private String ip="192.168.1.6";
    private Integer port=5500;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
