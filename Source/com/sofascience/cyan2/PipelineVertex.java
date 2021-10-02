/*
 * PipelineVertex.java
 *
 * Created on February 23, 2002, 3:17 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 * A 3D pipeline vertex.
 *
 * @author  Karl Traunmueller
 */
class PipelineVertex {

    /** clip flags */
    static final int CLIP_NEAR   = 0x01;
    static final int CLIP_FAR    = 0x02;
    static final int CLIP_LEFT   = 0x04;
    static final int CLIP_RIGHT  = 0x08;
    static final int CLIP_TOP    = 0x10;
    static final int CLIP_BOTTOM = 0x20;
    static final int[] CLIP_PLANES = { CLIP_NEAR, CLIP_FAR, CLIP_LEFT, CLIP_RIGHT, CLIP_TOP, CLIP_BOTTOM };
    static final float[] CLIP_BOUNDS = { 0.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f };
    
    Face        mFace;
    Point4f     mViewCoords;
    Point4f     mScreenCoords;
    Vector3f    mNormal;
    Color3f     mColor;
    float       mCosAlpha;
    float       mOneOverW;
    int         mX, mY;
    float       mZ;
    float       mU, mV;
    float       mEnvU, mEnvV;
    int         mClipFlags;
    
    /** 
     * Default constructor.
     */
    PipelineVertex() {
        mViewCoords = new Point4f();
        mScreenCoords = new Point4f();
        mNormal = new Vector3f();
        mColor = new Color3f();
    }

    /**
     * Inits a pipeline vertex from a vertex.
     */
    final void init(Face face, int v, Matrix4f Hmvt, Matrix4f Hproj, int w, int h) {                                    
        mFace = face;
        mNormal.set(face.mVertexNormals[v]);        
        
        Vertex vert = face.mMesh.mVertices[face.mVertices[v]];
        mViewCoords.set(vert.mWorldCoords);
        Hmvt.transform(mViewCoords);
        
        mScreenCoords.set(mViewCoords);
        Hproj.transform(mScreenCoords);
        mOneOverW = 1.0f / mScreenCoords.w;
        mScreenCoords.project(mScreenCoords);   // divide by w                
        
        transformToViewport(w, h);      
        clip();        
        
        mU = vert.mU;
        mV = vert.mV;
        Material mat = face.mMaterial;
        if (mat != null && mat.mTexture1Map != null) {
            mU *= mat.mTexture1Map.mScale[0];
            mU = mU < 0.0f ? mU + (float)Math.ceil(-mU) : mU;
            mU = mU > 1.0f ? mU - (float)Math.floor(mU) : mU;
            mV *= mat.mTexture1Map.mScale[1];
            mV = mV < 0.0f ? mV + (float)Math.ceil(-mV) : mV;
            mV = mV > 1.0f ? mV - (float)Math.floor(mV) : mV;
        }
        mEnvU = 0.0f;
        mEnvV = 0.0f;
    }
    
    /**
     * Pipeline vertex assignment.
     */
    final void set(PipelineVertex v) {
        mFace = v.mFace;
        mNormal.set(v.mNormal);
        
        mViewCoords.set(v.mViewCoords);
        mScreenCoords.set(v.mScreenCoords);        

        mX = v.mX;
        mY = v.mY;
        mZ = v.mZ;
        mOneOverW = v.mOneOverW;
        
        mU = v.mU;
        mV = v.mV;
        mEnvU = v.mEnvU;
        mEnvV = v.mEnvV;
        
        mClipFlags = v.mClipFlags;
    }
    
    /**
     * Calculates clip flags for this pipeline vertex.
     */
    final void clip() {
        // assumption: w > 0 (should be valid for triangle models)
        mClipFlags = 0;
        mClipFlags |= mScreenCoords.z >  1.0f ? CLIP_NEAR   : 0;
        mClipFlags |= mScreenCoords.z <  0.0f ? CLIP_FAR    : 0;
        mClipFlags |= mScreenCoords.x >  1.0f ? CLIP_RIGHT  : 0;
        mClipFlags |= mScreenCoords.x < -1.0f ? CLIP_LEFT   : 0;
        mClipFlags |= mScreenCoords.y >  1.0f ? CLIP_TOP    : 0;
        mClipFlags |= mScreenCoords.y < -1.0f ? CLIP_BOTTOM : 0;
    }

    /**
     * Transforms the floating-point 3D screen coordinates into fixed-point
     * viewport coordinates.
     */
    final void transformToViewport(int w, int h) {
        mX = FixedPoint.floatToFixed284((1.0f - mScreenCoords.x/* + 1.0f*/) * (w - 1.0f) / 2.0f);
        mY = FixedPoint.floatToFixed284((mScreenCoords.y + 1.0f) * (h - 1.0f) / 2.0f);
        mZ = mScreenCoords.z;
    }
    
}


