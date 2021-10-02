/*
 * Key.java
 *
 * Created on September 27, 2002, 12:29 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
abstract class Key extends SceneGraphObject {
    
    Key       mNext;
    TcbSpline mTcb;
    
    /**
     *
     */
    Key() {
        mTcb = new TcbSpline();
        mNext = null;
    }
}
