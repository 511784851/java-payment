package com.guzhi.pay.access.thrift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.util.PayOrderUtil;

public class PayServiceHandlerTest {

    @Test
    public void siftAppChInfo() {
        List<AppChInfo> infos = new ArrayList<AppChInfo>();
        AppChInfo a = new AppChInfo();
        a.setChWeight(200);
        infos.add(a);
        AppChInfo b = new AppChInfo();
        b.setChWeight(300);
        infos.add(b);
        AppChInfo c = new AppChInfo();
        c.setChWeight(500);
        infos.add(c);

        List<AppChInfo> list = new ArrayList<AppChInfo>();
        for (int i = 0; i < 100000; i++) {
            list.add(PayOrderUtil.siftAppChInfo(infos));
        }
        System.out.println("次数统计:a=" + Collections.frequency(list, a) + ";b=" + Collections.frequency(list, b) + ";c="
                + Collections.frequency(list, c));
    }
}
