/*
 * Track.java
 *
 * Created on September 27, 2002, 12:24 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
abstract class Track extends SceneGraphObject {
    
    static final int REPEAT    = 0x0001;
    static final int SMOOTH    = 0x0002;
    static final int LOCK_X    = 0x0008;
    static final int LOCK_Y    = 0x0010;
    static final int LOCK_Z    = 0x0020;
    static final int UNLINK_X  = 0x0100;
    static final int UNLINK_Y  = 0x0200;
    static final int UNLINK_Z  = 0x0400;
    
    int       mFlags;
    Key       mKeys;
    Object    mTarget;
        
    /** Creates a new instance of Track */
    Track(Object target) {
        mKeys = null;
        mTarget = target;
    }
    
    abstract String getType();
    
    /**
     *
     */
    void insertKey(Key key) {
        if (mKeys == null) {
            mKeys = key;
            return;
        }
        
        Key k = null;
        Key p = null;
        for (p = null, k = mKeys; k != null; p = k, k = k.mNext) {
            if (k.mTcb.mFrame > key.mTcb.mFrame)
                break;
        }
        if (p == null) {
            key.mNext = mKeys;
            mKeys = key;
        }
        else {
            key.mNext = k;
            p.mNext = key;
        }

        if (k != null && (key.mTcb.mFrame == k.mTcb.mFrame)) {
            key.mNext = k.mNext;
        }
    }    
    
    /**
     *
     */
    abstract void setup();
    
    /**
     *
     */
    abstract boolean eval(int time);
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level + 1); System.out.println("flags: " + mFlags);
        indent(level + 1); System.out.println("keys: " + mFlags);
        Key k = mKeys;
        while (k != null) {
            k.dump(level + 2);
            k = k.mNext;
        }
    }
     */
    
}
