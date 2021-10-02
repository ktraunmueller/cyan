/*
 * AmbientNode.java
 *
 * Created on September 27, 2002, 2:38 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class AmbientNode extends Node {
    
    /** Creates a new instance of AmbientNode */
    AmbientNode(String name) {
        super(name);
        mType = AMBIENT_NODE;
    }

    /**
     *
     */
    boolean eval(int time) {        
        if (mParent != null)
            mMatrix.set(mParent.mMatrix);
        else
            mMatrix.setIdentity();
        boolean colorActive = mNodeData.mAmbientData.mColorTrack.eval(time);        
        boolean childrenActive = evalChildren(time);
        return colorActive || childrenActive;
    }
}
