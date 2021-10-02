/*
 * TexturedGradients.java
 *
 * Created on October 23, 2002, 8:35 AM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Color3f;

/**
 *
 * @author  karl
 */
class TexturedGradients extends TriangleGradients {

    float[]     mCosAlpha;
    float       mdCosAlphaOverdX, mdCosAlphaOverdY;
    float[]     mUOverW;
    float[]     mVOverW;
    float       mdUOverWdX, mdUOverWdY;		
    float       mdVOverWdX, mdVOverWdY;

    /** 
     * Default constructor. 
     */
    TexturedGradients() {
        mCosAlpha   = new float[3];
        mUOverW     = new float[3];
        mVOverW     = new float[3];                
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

        mZ       [0]    = p0.mZ;
        mCosAlpha[0]    = p0.mCosAlpha;
        mOneOverW[0]    = p0.mOneOverW;
        mUOverW  [0]    = p0.mU * p0.mOneOverW;
        mVOverW  [0]    = p0.mV * p0.mOneOverW;
        mZ       [1]    = p1.mZ;
        mCosAlpha[1]    = p1.mCosAlpha;
        mOneOverW[1]    = p1.mOneOverW;
        mUOverW  [1]    = p1.mU * p1.mOneOverW;
        mVOverW  [1]    = p1.mV * p1.mOneOverW;
        mZ       [2]    = p2.mZ;
        mCosAlpha[2]    = p2.mCosAlpha;
        mOneOverW[2]    = p2.mOneOverW;
        mUOverW  [2]    = p2.mU * p2.mOneOverW;
        mVOverW  [2]    = p2.mV * p2.mOneOverW;                   
        
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
 
        mdCosAlphaOverdX = oneOverdX * (((mCosAlpha[1] - mCosAlpha[2]) * y02) -
                                        ((mCosAlpha[0] - mCosAlpha[2]) * y12));
        mdCosAlphaOverdY = oneOverdY * (((mCosAlpha[1] - mCosAlpha[2]) * x02) -
                                        ((mCosAlpha[0] - mCosAlpha[2]) * x12));
        
        mdUOverWdX = oneOverdX * (((mUOverW[1] - mUOverW[2]) * y02) -
                                  ((mUOverW[0] - mUOverW[2]) * y12));
        mdUOverWdY = oneOverdY * (((mUOverW[1] - mUOverW[2]) * x02) -
                                  ((mUOverW[0] - mUOverW[2]) * x12));

        mdVOverWdX = oneOverdX * (((mVOverW[1] - mVOverW[2]) * y02) -
                                  ((mVOverW[0] - mVOverW[2]) * y12));
        mdVOverWdY = oneOverdY * (((mVOverW[1] - mVOverW[2]) * x02) -
                                  ((mVOverW[0] - mVOverW[2]) * x12));        
        
        if (envMapping) {
            mEnvUOverW[0]  = p0.mEnvU * p0.mOneOverW;
            mEnvVOverW[0]  = p0.mEnvV * p0.mOneOverW;
            mEnvUOverW[1]  = p1.mEnvU * p1.mOneOverW;
            mEnvVOverW[1]  = p1.mEnvV * p1.mOneOverW;
            mEnvUOverW[2]  = p2.mEnvU * p2.mOneOverW;
            mEnvVOverW[2]  = p2.mEnvV * p2.mOneOverW;
            mdEnvUOverWdX = oneOverdX * (((mEnvUOverW[1] - mEnvUOverW[2]) * y02) -
                                         ((mEnvUOverW[0] - mEnvUOverW[2]) * y12));
            mdEnvUOverWdY = oneOverdY * (((mEnvUOverW[1] - mEnvUOverW[2]) * x02) -
                                         ((mEnvUOverW[0] - mEnvUOverW[2]) * x12));

            mdEnvVOverWdX = oneOverdX * (((mEnvVOverW[1] - mEnvVOverW[2]) * y02) -
                                         ((mEnvVOverW[0] - mEnvVOverW[2]) * y12));
            mdEnvVOverWdY = oneOverdY * (((mEnvVOverW[1] - mEnvVOverW[2]) * x02) -
                                         ((mEnvVOverW[0] - mEnvVOverW[2]) * x12));
        }
    }    
}