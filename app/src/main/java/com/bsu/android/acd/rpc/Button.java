package com.bsu.android.acd.rpc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surajdeuja on 3/25/16.
 */
public class Button {
    private int id;
    private String text;
    private String uri;

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

    public int getId(int id) {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static class ButtonArray {
        private List<Button> buttons = new ArrayList<>();

        public void setButtons(List<Button> buttons) {
            this.buttons = buttons;
        }

        public List<Button> getButtons() {

            return buttons;
        }
    }
}
