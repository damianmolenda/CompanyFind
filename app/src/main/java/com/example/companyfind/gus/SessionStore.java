package com.example.companyfind.gus;

// SessionStore.java
public final class SessionStore {
    private static volatile String sid;
    private SessionStore() {}
    public static void setSid(String value) { sid = value; }
    public static String getSid() { return sid; }
}
