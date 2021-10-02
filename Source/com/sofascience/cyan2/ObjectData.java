/*
 * ObjectData.java
 *
 * Created on September 26, 2002, 12:27 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class ObjectData extends SceneGraphObject {
    
    Vector3f      mPivot;
    Vector3f      mPivotTransformed;
    String        mInstance;
    Vector3f      mBboxMin;
    Vector3f      mBboxMax;
    Vector3f      mPos;
    Lin3Track     mPosTrack;
    Quat4fEx      mRot;
    QuatTrack     mRotTrack;
    Vector3f      mScl;
    Lin3Track     mSclTrack;
    float         mMorphSmooth;
    String        mMorph;
    BoolValue     mHide;
    BoolTrack     mHideTrack;

    /** Creates a new instance of ObjectData */
    ObjectData() {
        mPivot = new Vector3f();
        mPivotTransformed = new Vector3f();
        mBboxMin = new Vector3f();
        mBboxMax = new Vector3f();
        mPos = new Vector3f();
        mPosTrack = new Lin3Track(mPos);
        mRot = new Quat4fEx();
        mRotTrack = new QuatTrack(mRot);
        mScl = new Vector3f();
        mSclTrack = new Lin3Track(mScl);
        mSclTrack.setResetValue(1.0f, 1.0f, 1.0f);
        mMorph = "";
        mHide = new BoolValue();
        mHideTrack = new BoolTrack(mHide);
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- OBJECT DATA");
        indent(level + 1); System.out.print("pivot: "); dumpTuple(0, mPivot);
        indent(level + 1); System.out.println("instance: " + mInstance);
        indent(level + 1); System.out.print("bbox min: "); dumpTuple(0, mBboxMin);
        indent(level + 1); System.out.print("bbox max: "); dumpTuple(0, mBboxMax);
        indent(level + 1); System.out.print("pos: "); dumpTuple(0, mPos);
        mPosTrack.dump(level + 2);
        indent(level + 1); System.out.print("rot: "); dumpQuat(0, mRot);
        mRotTrack.dump(level + 2);
        indent(level + 1); System.out.println("scale: " + mScl);
        mSclTrack.dump(level + 2);
        indent(level + 1); System.out.println("morph smoot: " + mMorphSmooth);
        indent(level + 1); System.out.println("morph: " + mMorph);
        mMorphTrack.dump(level + 2);
        indent(level + 1); System.out.println("hide: " + mHide);
        mHideTrack.dump(level + 2);
    }
     */
}
