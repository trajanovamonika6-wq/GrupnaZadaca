package com.example.studentapp;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

public class CardService extends HostApduService {

    private static String payload = "";

    public static void setPayload(String data) {
        payload = data;
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        return payload.getBytes();
    }

    @Override
    public void onDeactivated(int reason) {}
}