/*
 * LightData.java
 *
 * Created on September 26, 2002, 12:28 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class LightData extends SceneGraphObject {
    
    Vector3f      mPos;
    Lin3Track     mPosTrack;
    Color3f       mColor;
    Lin3Track     mColorTrack;
    FloatValue    mHotspot;
    Lin1Track     mHotspotTrack;
    FloatValue    mFalloff;
    Lin1Track     mFalloffTrack;
    FloatValue    mRoll;
    Lin1Track     mRollTrack;


    /** Creates a new instance of LightData */
    LightData() {
        mPos = new Vector3f();
        mPosTrack = new Lin3Track(mPos);
        mColor = new Color3f();
        mColorTrack = new Lin3Track(mColor);
        mHotspot = new FloatValue();
        mHotspotTrack = new Lin1Track(mHotspot);
        mFalloff = new FloatValue();
        mFalloffTrack = new Lin1Track(mFalloff);
        mRoll = new FloatValue();
        mRollTrack = new Lin1Track(mRoll);
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- LIGHT DATA");
        indent(level + 1); System.out.print("pos: "); dumpTuple(0, mPos);
        mPosTrack.dump(level + 2);
        indent(level + 1); System.out.print("color: "); dumpColor(0, mColor);
        mColorTrack.dump(level + 2);
        indent(level + 1); System.out.println("hotspot: " + mHotspot);
        mHotspotTrack.dump(level + 2);
        indent(level + 1); System.out.println("falloff: " + mFalloff);
        mFalloffTrack.dump(level + 2);
        indent(level + 1); System.out.println("roll: " + mRoll);
        mRollTrack.dump(level + 2);
    }
     */
}
