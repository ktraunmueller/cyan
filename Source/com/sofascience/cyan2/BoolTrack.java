/*
 * BoolTrack.java
 *
 * Created on September 27, 2002, 12:34 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class BoolTrack extends Track {
    
    /** Creates a new instance of BoolTrack */
    BoolTrack(Object target) {
        super(target);
    }
    
    /**
     *
     */
    String getType() {
        return "BOOLTRACK";
    }
    
    /**
     *
     */
    void setup() {
    }
    
    /**
     *
     */
    boolean eval(int time) {
        BoolValue val = (BoolValue)mTarget;
        if (mKeys == null) {
            val.mValue = false;
            return false;
        }
        if (mKeys.mNext == null) {
            val.mValue = true;
            return false;
        }

        boolean result = false;
        Key k = mKeys;
        while ((time < k.mTcb.mFrame) && (time >= k.mNext.mTcb.mFrame)) {
            if (result) {
                result = false;
            }
            else {
                result = true;
            }
            if (k.mNext == null) {
                if ((mFlags & REPEAT) > 0) {
                    time -= k.mTcb.mFrame;
                    k = mKeys;
                }
                else
                    break;
            }
            else {
                k = k.mNext;
            }
        }
        val.mValue = result;
        return true;
    }
    
}
