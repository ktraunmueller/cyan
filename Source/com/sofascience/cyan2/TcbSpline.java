/*
 * TCB.java
 *
 * Created on September 27, 2002, 11:53 AM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class TcbSpline extends SceneGraphObject {
    
    static int USE_TENSION    = 0x0001;
    static int USE_CONTINUITY = 0x0002;
    static int USE_BIAS       = 0x0004;
    static int USE_EASE_TO    = 0x0008;
    static int USE_EASE_FROM  = 0x0010;
    
    int   mFrame;
    int   mFlags;
    float mTension;
    float mContinuity;
    float mBias;
    float mEaseTo;
    float mEaseFrom;

    /** Creates a new instance of TCB */
    TcbSpline() {
    }
    
    void dump(int level) {
        indent(level); System.out.println("---- TCB");
        indent(level + 1); System.out.println("frame: " + mFrame);
        indent(level + 1); System.out.println("flags: " + mFlags);
        indent(level + 1); System.out.println("tension: " + mTension);
        indent(level + 1); System.out.println("continuity: " + mContinuity);
        indent(level + 1); System.out.println("bias: " + mBias);
        indent(level + 1); System.out.println("ease from: " + mEaseFrom);
        indent(level + 1); System.out.println("ease to: " + mEaseTo);
    }
    
}
