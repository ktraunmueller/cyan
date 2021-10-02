/*
 * GouraudEdge.java
 *
 * Created on October 23, 2002, 9:45 AM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  karl
 */
class GouraudEdge extends TriangleEdge {
    
    float mRed, mRedStep, mRedStepExtra;
    float mGreen, mGreenStep, mGreenStepExtra;
    float mBlue, mBlueStep, mBlueStepExtra;

    /** 
     * Default constructor.
     */
    GouraudEdge() {
    }

    /**
     * Initializes this edge.
     */
    void init(TriangleGradients grad, PipelineVertex pTop, PipelineVertex pBottom, 
              int top, boolean envmapping) {
                  
        GouraudGradients gradients = (GouraudGradients)grad;

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
            
            mOneOverW = gradients.mOneOverW[top]
                            + YPrestep * gradients.mdOneOverWdY
                            + XPrestep * gradients.mdOneOverWdX;
            mOneOverWStep = mXStep * gradients.mdOneOverWdX
                            + gradients.mdOneOverWdY;
            mOneOverWStepExtra = gradients.mdOneOverWdX;

            mRed = gradients.mRed[top]
                        + YPrestep * gradients.mdRedOverdY
                        + XPrestep * gradients.mdRedOverdX;
            mRedStep = mXStep * gradients.mdRedOverdX
                        + gradients.mdRedOverdY;
            mRedStepExtra = gradients.mdRedOverdX;

            mGreen = gradients.mGreen[top]
                        + YPrestep * gradients.mdGreenOverdY
                        + XPrestep * gradients.mdGreenOverdX;
            mGreenStep = mXStep * gradients.mdGreenOverdX
                        + gradients.mdGreenOverdY;
            mGreenStepExtra = gradients.mdGreenOverdX;

            mBlue = gradients.mBlue[top]
                        + YPrestep * gradients.mdBlueOverdY
                        + XPrestep * gradients.mdBlueOverdX;
            mBlueStep = mXStep * gradients.mdBlueOverdX
                        + gradients.mdBlueOverdY;
            mBlueStepExtra = gradients.mdBlueOverdX;
            
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
        
        mZ        += mZStep;
        mOneOverW += mOneOverWStep;
        mRed      += mRedStep;
        mGreen    += mGreenStep;
        mBlue     += mBlueStep;
        mEnvUOverW += mEnvUOverWStep; 
        mEnvVOverW += mEnvVOverWStep;   

        mErrorTerm += mNumerator;
        if (mErrorTerm >= mDenominator) {
            mX++;
            mErrorTerm -= mDenominator;
            
            mZ        += mZStepExtra;
            mOneOverW += mOneOverWStepExtra;
            mRed      += mRedStepExtra;
            mGreen    += mGreenStepExtra;
            mBlue     += mBlueStepExtra;
            mEnvUOverW += mEnvUOverWStepExtra; 
            mEnvVOverW += mEnvVOverWStepExtra;
        }
        return mHeight;
    }    
}
