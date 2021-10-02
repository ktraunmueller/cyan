/*
 * TriangleEdge.java
 *
 * Created on June 9, 2002, 2:56 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
abstract class TriangleEdge {
    
    int         mX, mXStep, mNumerator, mDenominator;
    int         mErrorTerm;
    int         mY, mHeight;
    float       mZ, mZStep, mZStepExtra;
    float       mOneOverW, mOneOverWStep, mOneOverWStepExtra;    
    float       mEnvUOverW, mEnvUOverWStep, mEnvUOverWStepExtra;
    float       mEnvVOverW, mEnvVOverWStep, mEnvVOverWStepExtra;
    FloorMod    mFloorMod;
    
    /** 
     * Default constructor.
     */
    TriangleEdge() {        
        mFloorMod = new FloorMod();
    }

    /**
     * Initializes this edge.
     */
    abstract void init(TriangleGradients gradients, PipelineVertex pTop, PipelineVertex pBottom, 
                       int top, boolean envmapping);
    
    /**
     * Steps this edge to the next scanline.
     */
    abstract int step();
    
}
