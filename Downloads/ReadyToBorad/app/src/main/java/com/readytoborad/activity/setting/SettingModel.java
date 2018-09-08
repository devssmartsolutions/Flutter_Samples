package com.readytoborad.activity.setting;

public class SettingModel {
    private String name;
    private int icon;
    private boolean isSwitch;

    private boolean isToggleOn;

    public boolean isToggleOn() {
        return isToggleOn;
    }

    public void setToggleOn(boolean toggleOn) {
        isToggleOn = toggleOn;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    public boolean isSwitch() {
        return isSwitch;
    }

    public void setSwitch(boolean aSwitch) {
        isSwitch = aSwitch;
    }


}
