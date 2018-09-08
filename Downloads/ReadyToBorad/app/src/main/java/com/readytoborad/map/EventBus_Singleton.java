package com.readytoborad.map;

import com.squareup.otto.Bus;

/**
 * Created by harendrasinghbisht on 01/03/17.
 */

public class EventBus_Singleton {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    public EventBus_Singleton() {
    }

    public void post(String s) {
        BUS.post(s);
    }

}
