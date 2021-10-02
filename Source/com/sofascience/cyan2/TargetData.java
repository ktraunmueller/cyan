/*
 * TargetData.java
 *
 * Created on September 26, 2002, 12:28 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Vector3f;

/**
 *
 * @author  Karl Traunmueller
 */
class TargetData extends SceneGraphObject {
    
    Vector3f  mPos;
    Lin3Track mPosTrack;

    /** Creates a new instance of TargetData */
    TargetData() {
        mPos = new Vector3f();
        mPosTrack = new Lin3Track(mPos);
    }    
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- TARGET DATA");
        indent(level + 1); System.out.print("pos: "); dumpTuple(0, mPos);
        mPosTrack.dump(level + 2);
    }
     */
}
