/*
 * LightNode.java
 *
 * Created on September 27, 2002, 3:52 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class LightNode extends Node {
    
    /** Creates a new instance of LightNode */
    LightNode(String name) {
        super(name);
        mType = LIGHT_NODE;
    }

    /**
     *
     */
    boolean eval(int time) {
        boolean posActive = mNodeData.mLightData.mPosTrack.eval(time);
        boolean colorActive = mNodeData.mLightData.mColorTrack.eval(time);
        boolean hotspotActive = mNodeData.mLightData.mHotspotTrack.eval(time);
        boolean falloffActive = mNodeData.mLightData.mFalloffTrack.eval(time);
        boolean rollActive = mNodeData.mLightData.mRollTrack.eval(time);
        if (mParent != null)
            mMatrix.set(mParent.mMatrix);
        else 
            mMatrix.setIdentity();
        mMatrix.translateBy(mNodeData.mLightData.mPos);
        
        boolean childrenActive = evalChildren(time);
        return posActive || colorActive || hotspotActive || falloffActive || rollActive || childrenActive;
    }
    
}
