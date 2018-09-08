package com.readytoborad.model;

/**
 * Created by anchal.kumar on 1/24/2018.
 */

public class ChildrenAlarmTime {
    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(String alarm_time) {
        this.alarm_time = alarm_time;
    }

    String child_id,alarm_time;
}
