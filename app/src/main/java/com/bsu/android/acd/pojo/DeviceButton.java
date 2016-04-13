package com.bsu.android.acd.pojo;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surajdeuja on 3/25/16.
 */
@Parcel
public class DeviceButton {
    private int id;
    private String text;
    private String uri;
    private int height;
    private int width;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getUri() {
        return uri;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static class ButtonArray {
        @SerializedName("buttons")
        private List<DeviceButton> deviceButtons = new ArrayList<>();

        public void setDeviceButtons(List<DeviceButton> deviceButtons) {
            this.deviceButtons = deviceButtons;
        }

        public List<DeviceButton> getDeviceButtons() {

            return deviceButtons;
        }
    }
}
