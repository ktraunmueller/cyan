/*
 * SpotNode.java
 *
 * Created on September 27, 2002, 3:52 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class SpotNode extends Node {
    
    /** Creates a new instance of SpotNode */
    SpotNode(String name) {
        super(name);
        mType = SPOT_NODE;
    }

    /**
     *
     */
    boolean eval(int time) {
        boolean posActive = mNodeData.mSpotData.mPosTrack.eval(time);
        if (mParent != null)
            mMatrix.set(mParent.mMatrix);
        else 
            mMatrix.setIdentity();
        mMatrix.translateBy(mNodeData.mSpotData.mPos);
        
        boolean childrenActive = evalChildren(time);
        return posActive || childrenActive;
    }
}
