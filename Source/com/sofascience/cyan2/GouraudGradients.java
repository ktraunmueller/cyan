/*
 * GouraudGradients.java
 *
 * Created on October 23, 2002, 8:39 AM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Color3f;

/**
 *
 * @author  karl
 */
class GouraudGradients extends TriangleGradients {
    
    int[]     mRed, mGreen, mBlue;
    float     mdRedOverdX, mdGreenOverdX, mdBlueOverdX;
    float     mdRedOverdY, mdGreenOverdY, mdBlueOverdY;

    /** 
     * Default constructor. 
     */
    GouraudGradients() {
        mRed   = new int[3];
        mGreen = new int[3];
        mBlue  = new int[3];
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

        mZ        [0]  = p0.mZ;
        mOneOverW [0]  = p0.mOneOverW;        
        mZ        [1]  = p1.mZ;
        mOneOverW [1]  = p1.mOneOverW;        
        mZ        [2]  = p2.mZ;
        mOneOverW [2]  = p2.mOneOverW;                
        
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
        float ambientMatX = ambient.x * mat.mAmbient.x;
        float ambientMatY = ambient.y * mat.mAmbient.y;
        float ambientMatZ = ambient.z * mat.mAmbient.z;
        mRed[0]   = (int)(255.0f * (Math.min(ambientMatX + p0.mColor.x, 1.0f)));
        mGreen[0] = (int)(255.0f * (Math.min(ambientMatY + p0.mColor.y, 1.0f)));
        mBlue[0]  = (int)(255.0f * (Math.min(ambientMatZ + p0.mColor.z, 1.0f)));
        mRed[1]   = (int)(255.0f * (Math.min(ambientMatX + p1.mColor.x, 1.0f)));
        mGreen[1] = (int)(255.0f * (Math.min(ambientMatY + p1.mColor.y, 1.0f)));
        mBlue[1]  = (int)(255.0f * (Math.min(ambientMatZ + p1.mColor.z, 1.0f)));
        mRed[2]   = (int)(255.0f * (Math.min(ambientMatX + p2.mColor.x, 1.0f)));
        mGreen[2] = (int)(255.0f * (Math.min(ambientMatY + p2.mColor.y, 1.0f)));
        mBlue[2]  = (int)(255.0f * (Math.min(ambientMatZ + p2.mColor.z, 1.0f)));
        
        mdRedOverdX = oneOverdX * (((mRed[1] - mRed[2]) * y02) -
                                   ((mRed[0] - mRed[2]) * y12));
        mdRedOverdY = oneOverdY * (((mRed[1] - mRed[2]) * x02) -
                                   ((mRed[0] - mRed[2]) * x12));
        mdGreenOverdX = oneOverdX * (((mGreen[1] - mGreen[2]) * y02) -
                                     ((mGreen[0] - mGreen[2]) * y12));
        mdGreenOverdY = oneOverdY * (((mGreen[1] - mGreen[2]) * x02) -
                                     ((mGreen[0] - mGreen[2]) * x12));
        mdBlueOverdX = oneOverdX * (((mBlue[1] - mBlue[2]) * y02) -
                                    ((mBlue[0] - mBlue[2]) * y12));
        mdBlueOverdY = oneOverdY * (((mBlue[1] - mBlue[2]) * x02) -
                                    ((mBlue[0] - mBlue[2]) * x12));
        
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

