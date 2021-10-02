/*
 * FlatGradients.java
 *
 * Created on October 23, 2002, 8:34 AM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Color3f;

/**
 *
 * @author  karl
 */
class FlatGradients extends TriangleGradients {
    
    int mRed, mGreen, mBlue;

    /** 
     * Default constructor. 
     */
    FlatGradients() {
    }
    
    /**
     * Inits this TrianlgeGradients from the given pipeline vertices.
     */
    void init(Color3f ambient, PipelineVertex p0, PipelineVertex p1, PipelineVertex p2,
              boolean envMapping) {
        int X1Y0 = FixedPoint.fixed284Mul(p1.mX - p2.mX, p0.mY - p2.mY);
        int X0Y1 = FixedPoint.fixed284Mul(p0.mX - p2.mX, p1.mY - p2.mY);
        
        float oneOverdX = 1.0f / FixedPoint.fixed284ToFloat(X1Y0 - X0Y1);
        float oneOverdY = -oneOverdX;        

        mZ       [0]   = p0.mZ;
        mOneOverW[0]   = p0.mOneOverW;
        mZ       [1]   = p1.mZ;
        mOneOverW[1]   = p1.mOneOverW;        
        mZ       [2]   = p2.mZ;
        mOneOverW[2]   = p2.mOneOverW;
    
        float y02 = FixedPoint.fixed284ToFloat(p0.mY - p2.mY);
        float y12 = FixedPoint.fixed284ToFloat(p1.mY - p2.mY);
        float x02 = FixedPoint.fixed284ToFloat(p0.mX - p2.mX);
        float x12 = FixedPoint.fixed284ToFloat(p1.mX - p2.mX);
        
        mdZOverdX = oneOverdX * (((mZ[1] - mZ[2]) * y02) -
                                 ((mZ[0] - mZ[2]) * y12));
        mdZOverdY = oneOverdY * (((mZ[1] - mZ[2]) * x02) -
                                 ((mZ[0] - mZ[2]) * x12));
        
        mdOneOverWdX = oneOverdX * (((mOneOverW[1] - mOneOverW[2]) * y02) -
                                    ((mOneOverW[0] - mOneOverW[2]) * y12));
        mdOneOverWdY = oneOverdY * (((mOneOverW[1] - mOneOverW[2]) * x02) -
                                    ((mOneOverW[0] - mOneOverW[2]) * x12));
                                    
        Material mat = p0.mFace.mMaterial;
        mRed   = (int)(255.0f * (Math.min(ambient.x * mat.mAmbient.x + p0.mColor.x, 1.0f)));
        mGreen = (int)(255.0f * (Math.min(ambient.y * mat.mAmbient.y + p0.mColor.y, 1.0f)));
        mBlue  = (int)(255.0f * (Math.min(ambient.z * mat.mAmbient.z + p0.mColor.z, 1.0f)));
    }    
}
