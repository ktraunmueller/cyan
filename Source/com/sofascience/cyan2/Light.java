/*
 * Light.java
 *
 * Created on February 23, 2002, 3:00 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 * The base class for all lights.
 *
 * @author  Karl Traunmueller
 */
class Light extends NamedObject {

    boolean     mIsSpotLight;
    boolean     mSeeCone;
    Color3f     mColor;
    Vector3f    mPosition;
    Vector3f    mSpot;
    float       mRoll;
    boolean     mIsOff;
    float       mOuterRange;
    float       mInnerRange;
    float       mMultiplier;
    float       mAttenuation;
    boolean     mIsRectangularSpot;
    boolean     mIsShadowed;
    float       mShadowBias;
    float       mShadowFilter;
    int         mShadowSize;
    float       mSpotAspect;
    boolean     mUseProjector;
    String      mProjector;
    boolean     mSpotOvershoot;
    boolean     mRayShadows;
    float       mRayBias;
    float       mHotSpot;
    float       mFallOff;    
    Vector3f    mSpotView;
    Vector2f    mEnvVec;
    Matrix4f    mView;
    Vector3f    mNormalView;
  
    /**
     * 
     */
    Light(String name) {
        super(name);
        mColor = new Color3f(1.0f, 1.0f, 1.0f);
        mPosition = new Vector3f();
        mSpot = new Vector3f();
        mSpotView = new Vector3f();
        mEnvVec = new Vector2f();
        mView = new Matrix4f();
        mNormalView = new Vector3f();
    }
    
    /**
     *
     */
    final void setViewMatrix(Matrix4f m) {
        mView.set(m);
        mSpotView.set(mSpot);
        mView.transform(mSpotView);
        mSpotView.normalize();
    }

    /**
     *
     */
    void light(PipelineVertex v0, PipelineVertex v1, PipelineVertex v2) {
        float matDiffuseX = v0.mFace.mMaterial.mDiffuse.x * mColor.x;
        float matDiffuseY = v0.mFace.mMaterial.mDiffuse.y * mColor.y;
        float matDiffuseZ = v0.mFace.mMaterial.mDiffuse.z * mColor.z;

        float cosAlpha1 = Math.max(mSpotView.dot(v0.mNormal), 0.0f);
        v0.mCosAlpha = cosAlpha1;
        v0.mColor.set(cosAlpha1 * matDiffuseX, cosAlpha1 * matDiffuseY, cosAlpha1 * matDiffuseZ);
        
        float cosAlpha2 = Math.max(mSpotView.dot(v1.mNormal), 0.0f);            
        v1.mCosAlpha = cosAlpha2;
        v1.mColor.set(cosAlpha2 * matDiffuseX, cosAlpha2 * matDiffuseY, cosAlpha2 * matDiffuseZ);
        
        float cosAlpha3 = Math.max(mSpotView.dot(v2.mNormal), 0.0f);            
        v2.mCosAlpha = cosAlpha3;
        v2.mColor.set(cosAlpha3 * matDiffuseX, cosAlpha3 * matDiffuseY, cosAlpha3 * matDiffuseZ);
        
        if (v0.mFace.mMaterial.mReflectionMap != null) {            
            v0.mEnvU = cosAlpha1;
            v0.mEnvV = cosAlpha1;
            
            v1.mEnvU = cosAlpha2;
            v1.mEnvV = cosAlpha2;
            
            v2.mEnvU = cosAlpha3;
            v2.mEnvV = cosAlpha3;
        }
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- LIGHT " + mName);
        indent(level + 1); System.out.println("color: " + mColor);
        indent(level + 1); System.out.println("is spotlight: " + mIsSpotLight);
        indent(level + 1); System.out.println("see cone: " + mSeeCone);
        indent(level + 1); System.out.print("color: "); dumpColor(0, mColor);
        indent(level + 1); System.out.print("position: "); dumpTuple(0, mPosition);
        indent(level + 1); System.out.print("spot: "); dumpTuple(0, mSpot);
        indent(level + 1); System.out.println("roll: " + mRoll);
        indent(level + 1); System.out.println("off: " + mIsOff);
        indent(level + 1); System.out.println("outer range: " + mOuterRange);
        indent(level + 1); System.out.println("inner range: " + mInnerRange);
        indent(level + 1); System.out.println("multiplier: " + mMultiplier);
        indent(level + 1); System.out.println("attenuation: " + mAttenuation);
        indent(level + 1); System.out.println("is rectangular spot: " + mIsRectangularSpot);
        indent(level + 1); System.out.println("is shadowed: " + mIsShadowed);
        indent(level + 1); System.out.println("shadow bias: " + mShadowBias);
        indent(level + 1); System.out.println("shadow filter: " + mShadowFilter);
        indent(level + 1); System.out.println("shadow size: " + mShadowSize);
        indent(level + 1); System.out.println("spot aspect: " + mSpotAspect);
        indent(level + 1); System.out.println("use projector: " + mUseProjector);
        indent(level + 1); System.out.println("projector: " + mProjector);
        indent(level + 1); System.out.println("spot overshoot: " + mSpotOvershoot);
        indent(level + 1); System.out.println("ray shadows: " + mRayShadows);
        indent(level + 1); System.out.println("ray bias: " + mRayBias);
        indent(level + 1); System.out.println("hot spot: " + mHotSpot);
        indent(level + 1); System.out.println("fall off: " + mFallOff);
    }
     */
}
