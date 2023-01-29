package com.m2z.activity.tracker.controller.impl;

import java.util.List;

public class Command {
    public static Command REQUEST_TOKEN = new Command("requestToken");
    public static Command ACCESS_TOKEN = new Command("accessToken");
    public static Command REQUEST = new Command("request");

    private final String name;

    Command(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String names() {
        return List.of(REQUEST_TOKEN, ACCESS_TOKEN, REQUEST)
                .toString();
    }

    public static Command fromString(String name) {
        if (name != null) {
            if(REQUEST.getName().equals(name)){
                return REQUEST;

            } else if(REQUEST_TOKEN.getName().equals(name)){
                return REQUEST_TOKEN;

            } else if(ACCESS_TOKEN.getName().equals(name)){
                return ACCESS_TOKEN;
            }
        }
        return null;
    }
}
