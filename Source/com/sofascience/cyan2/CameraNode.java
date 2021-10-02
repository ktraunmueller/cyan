/*
 * CameraNode.java
 *
 * Created on September 27, 2002, 3:50 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class CameraNode extends Node {
    
    /** Creates a new instance of CameraNode */
    CameraNode(String name) {
        super(name);
        mType = CAMERA_NODE;
    }

    /**
     *
     */
    boolean eval(int time) {
        boolean posActive = mNodeData.mCameraData.mPosTrack.eval(time);
        boolean fovActive = mNodeData.mCameraData.mFovTrack.eval(time);
        boolean rollActive = mNodeData.mCameraData.mRollTrack.eval(time);
        if (mParent != null)
            mMatrix.set(mParent.mMatrix);
        else
            mMatrix.setIdentity();
        mMatrix.translateBy(mNodeData.mCameraData.mPos);
        
        boolean childrenActive = evalChildren(time);
        return posActive || fovActive || rollActive || childrenActive;
    }
}
