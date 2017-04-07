package com.example.simon.blue426;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Ludo Reynders on 4/6/2017.
 */

public class CharacteristicFloat {
    public byte[] theBytes = new byte[4];
    public int theKey;
    public float value;

    public CharacteristicFloat(byte[] newBytes, Integer newKey){
        theKey = newKey;
        theBytes = newBytes;
        value = ByteBuffer.wrap(theBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public void updateBytes(byte[] newBytes){
        theBytes = newBytes;
    }

    public String getValue(){
        return String.valueOf(value);
    }
}
