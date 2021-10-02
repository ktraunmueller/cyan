/*
 * SpotData.java
 *
 * Created on September 26, 2002, 12:28 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Vector3f;

/**
 *
 * @author  Karl Traunmueller
 */
class SpotData extends SceneGraphObject {
    
    Vector3f  mPos;
    Lin3Track mPosTrack;
    
    /** 
     *
     */
    SpotData() {
        mPos = new Vector3f();
        mPosTrack = new Lin3Track(mPos);
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- SPOT DATA");
        indent(level + 1); System.out.print("pos: "); dumpTuple(0, mPos);
        //mPosTrack.dump(level + 1);
    }
     */
    
}
