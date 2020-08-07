package com.openxu.core.http.util;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;

/**
 * Author: openXu
 * Time: 2019/3/4 9:38
 * class: TimeOutDns
 * Description: okHttp DNS解析工具（可设置超时时间）
 */
public class TimeOutDns implements Dns {

    private long timeout;
    private TimeUnit unit;

    public TimeOutDns(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public List<InetAddress> lookup(final String hostname) throws UnknownHostException {

        if (hostname == null) {
            throw new UnknownHostException("hostname == null");
        } else {
            try {
                FutureTask<List<InetAddress>> task = new FutureTask<>(
                        () -> {
                            try {
                                return Arrays.asList(InetAddress.getAllByName(hostname));
                            } catch (Exception e) {
                                ArrayList<InetAddress> inetAddresses = new ArrayList<>();
                                return inetAddresses;
                            }
                        });
                new Thread(task).start();
                List<InetAddress> addrs = task.get(timeout, unit);
                return addrs;
            } catch (Exception var4) {
                try {
                    var4.printStackTrace();
                    return Dns.SYSTEM.lookup(hostname);
                } catch (Exception e) {
                    e.printStackTrace();
                    ArrayList<InetAddress> inetAddresses = new ArrayList<>();
                    return inetAddresses;
                }
            }
        }
    }
}
