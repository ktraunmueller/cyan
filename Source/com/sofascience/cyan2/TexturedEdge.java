/*
 * TexturedEdge.java
 *
 * Created on October 23, 2002, 9:46 AM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  karl
 */
class TexturedEdge extends TriangleEdge {
    
    float   mCosAlpha, mCosAlphaStep, mCosAlphaStepExtra;
    float   mUOverW, mUOverWStep, mUOverWStepExtra;
    float   mVOverW, mVOverWStep, mVOverWStepExtra;

    /** 
     * Default constructor.
     */
    TexturedEdge() {        
    }

    /**
     * Initializes this edge.
     */
    void init(TriangleGradients grad, PipelineVertex pTop, PipelineVertex pBottom, 
              int top, boolean envmapping) {
        TexturedGradients gradients = (TexturedGradients)grad;

        mY = FixedPoint.ceil284(pTop.mY);
        int YEnd = FixedPoint.ceil284(pBottom.mY);
        mHeight = YEnd - mY;

        if (mHeight > 0) {
            int dN = pBottom.mY - pTop.mY;
            int dM = pBottom.mX - pTop.mX;

            int initialNumerator = dM * 16 * mY - dM * pTop.mY +
                                   dN * pTop.mX - 1 + dN * 16;
            
            IntegerDivMod.floorDivMod(initialNumerator, dN * 16, mFloorMod);
            mX = mFloorMod.mFloor;
            mErrorTerm = mFloorMod.mMod;
            IntegerDivMod.floorDivMod(dM * 16, dN * 16, mFloorMod);
            mXStep = mFloorMod.mFloor;
            mNumerator = mFloorMod.mMod;

            mDenominator = dN * 16;
            float YPrestep = FixedPoint.fixed284ToFloat(mY * 16 - pTop.mY);
            float XPrestep = FixedPoint.fixed284ToFloat(mX * 16 - pTop.mX);

            mZ = gradients.mZ[top]
                            + YPrestep * gradients.mdZOverdY
                            + XPrestep * gradients.mdZOverdX;
            mZStep = mXStep * gradients.mdZOverdX
                            + gradients.mdZOverdY;
            mZStepExtra = gradients.mdZOverdX;
            
            mCosAlpha = gradients.mCosAlpha[top]
                        + YPrestep * gradients.mdCosAlphaOverdY
                        + XPrestep * gradients.mdCosAlphaOverdX;
            mCosAlphaStep = mXStep * gradients.mdCosAlphaOverdX
                        + gradients.mdCosAlphaOverdY;
            mCosAlphaStepExtra = gradients.mdCosAlphaOverdX;
            
            mOneOverW = gradients.mOneOverW[top]
                            + YPrestep * gradients.mdOneOverWdY
                            + XPrestep * gradients.mdOneOverWdX;
            mOneOverWStep = mXStep * gradients.mdOneOverWdX
                            + gradients.mdOneOverWdY;
            mOneOverWStepExtra = gradients.mdOneOverWdX;

            mUOverW = gradients.mUOverW[top]
                            + YPrestep * gradients.mdUOverWdY
                            + XPrestep * gradients.mdUOverWdX;
            mUOverWStep = mXStep * gradients.mdUOverWdX
                            + gradients.mdUOverWdY;
            mUOverWStepExtra = gradients.mdUOverWdX;

            mVOverW = gradients.mVOverW[top]
                            + YPrestep * gradients.mdVOverWdY
                            + XPrestep * gradients.mdVOverWdX;
            mVOverWStep = mXStep * gradients.mdVOverWdX
                            + gradients.mdVOverWdY;
            mVOverWStepExtra = gradients.mdVOverWdX;          
       
            if (envmapping) {
                mEnvUOverW = gradients.mEnvUOverW[top]
                                + YPrestep * gradients.mdEnvUOverWdY
                                + XPrestep * gradients.mdEnvUOverWdX;
                mEnvUOverWStep = mXStep * gradients.mdEnvUOverWdX
                                + gradients.mdEnvUOverWdY;
                mEnvUOverWStepExtra = gradients.mdEnvUOverWdX;

                mEnvVOverW = gradients.mEnvVOverW[top]
                                + YPrestep * gradients.mdEnvVOverWdY
                                + XPrestep * gradients.mdEnvVOverWdX;
                mEnvVOverWStep = mXStep * gradients.mdEnvVOverWdX
                                + gradients.mdEnvVOverWdY;
                mEnvVOverWStepExtra = gradients.mdEnvVOverWdX;                      
            }
        }
    }
    
    /**
     * Steps this edge to the next scanline.
     */
    final int step() {
        mX += mXStep; 
        mY++; 
        mHeight--;
        
        mZ          += mZStep;
        mCosAlpha   += mCosAlphaStep;
        mOneOverW   += mOneOverWStep;        
        mUOverW     += mUOverWStep; 
        mVOverW     += mVOverWStep;         
        mEnvUOverW  += mEnvUOverWStep; 
        mEnvVOverW  += mEnvVOverWStep;   

        mErrorTerm += mNumerator;
        if (mErrorTerm >= mDenominator) {
            mX++;
            mErrorTerm -= mDenominator;
            
            mZ          += mZStepExtra;
            mCosAlpha   += mCosAlphaStepExtra;
            mOneOverW   += mOneOverWStepExtra;
            mUOverW     += mUOverWStepExtra; 
            mVOverW     += mVOverWStepExtra;
            mEnvUOverW  += mEnvUOverWStepExtra; 
            mEnvVOverW  += mEnvVOverWStepExtra;
        }
        return mHeight;
    }
}
