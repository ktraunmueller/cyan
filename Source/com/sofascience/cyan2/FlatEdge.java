/*
 * FlatEdge.java
 *
 * Created on October 23, 2002, 9:42 AM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  karl
 */
class FlatEdge extends TriangleEdge {
    
    float mRed, mGreen, mBlue;

    /** 
     * Default constructor.
     */
    FlatEdge() {
    }

    /**
     * Initializes this edge.
     */
    void init(TriangleGradients grad, PipelineVertex pTop, PipelineVertex pBottom, 
              int top, boolean envmapping) {
                  
        FlatGradients gradients = (FlatGradients)grad;

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

            mRed = gradients.mRed;
            mGreen = gradients.mGreen;
            mBlue = gradients.mBlue;
        }
    }
    
    /**
     * Steps this edge to the next scanline.
     */
    int step() {
        mX += mXStep; 
        mY++; 
        mHeight--;
        
        mZ        += mZStep;
        mOneOverW += mOneOverWStep;

        mErrorTerm += mNumerator;
        if (mErrorTerm >= mDenominator) {
            mX++;
            mErrorTerm -= mDenominator;
            
            mZ        += mZStepExtra;
            mOneOverW += mOneOverWStepExtra;
        }
        return mHeight;
    }
    
}
