package com.arnava.netcalc;

public class Main {
    public static void main(String[] args) {
     Server server = new Server(6060, 0);
     server.start();
    }
}
