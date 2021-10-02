/*
 * MovingAvg.java
 *
 * Created on November 4, 2002, 2:01 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  karl
 */
class MovingAvg {
    
    int     mLen, mCurrent;
    float[] mTaps;
    float   mLastValue;
    boolean mDirty;
    
    /**
     * 
     */
    MovingAvg(int len) {
        mLen = len;
        mTaps = new float[len];
        mDirty = false;
        mLastValue = 0.0f;
    }
    
    /**
     *
     */
    void input(float newValue) {
        mTaps[mCurrent++] = newValue;
        mCurrent %= mLen;
        mDirty = true;
    }
    
    /**
     *
     */
    float value() {
        if (!mDirty)
            return mLastValue;
        
        float sum = 0.0f;
        int current = mCurrent - 1 + mLen;
        int len = mLen;
        while (len-- > 0)
            sum += mTaps[current-- % mLen];
        mLastValue = sum / mLen;
        mDirty = false;
        return mLastValue;
    }
    
    /**
     *
     */
    void set(float value) {
        mDirty = true;
        for (int i = mLen - 1; i >= 0; i--)
            mTaps[i] = value;
    }
    
    /**
     *
     */
    void flush() {
        mCurrent = 0;
        mDirty = false;
        mLastValue = 0.0f;
        
        for (int i = mLen - 1; i >= 0; i--)
            mTaps[i] = 0.0f;
    }
}
