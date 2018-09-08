package com.readytoborad.map;

/**
 * Created by harendrasinghbisht on 01/03/17.
 */

public class EventBus_Poster {
    public String in;
    public String out;
    public String dtap;

    public EventBus_Poster(String in, String out) {
        this.in = in;
        this.out = out;
    }

    EventBus_Poster(String dtap) {
        this.dtap = dtap;
    }

}
