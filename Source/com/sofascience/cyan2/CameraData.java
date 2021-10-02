/*
 * CameraData.java
 *
 * Created on September 26, 2002, 12:27 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class CameraData extends SceneGraphObject {
    
    Vector3f      mPos;
    Lin3Track     mPosTrack;
    FloatValue    mFov;
    Lin1Track     mFovTrack;
    FloatValue    mRoll;
    Lin1Track     mRollTrack;

    /** Creates a new instance of CameraData */
    CameraData() {
        mPos = new Vector3f();        
        mPosTrack = new Lin3Track(mPos);
        mFov = new FloatValue();
        mFovTrack = new Lin1Track(mFov);
        mRoll = new FloatValue();
        mRollTrack = new Lin1Track(mRoll);
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- CAMERA DATA");
        indent(level + 1); System.out.print("pos: "); dumpTuple(0, mPos);
        mPosTrack.dump(level + 2);
        indent(level + 1); System.out.println("fov: " + mFov);
        mFovTrack.dump(level + 2);
        indent(level + 1); System.out.println("roll: " + mRoll);
        mRollTrack.dump(level + 2);
    }
     */
}
