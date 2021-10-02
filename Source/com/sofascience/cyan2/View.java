/*
 * View.java
 *
 * Created on February 23, 2002, 4:17 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;
import java.awt.Dimension;
import java.util.Vector;

/**
 * Contains all parameters needed in rendering a three dimensional scene from one 
 * viewpoint. 
 *
 * A view referenes a Canvas3D object that the view is rendered into. 
 * It exists outside of the scene graph, but attaches to a ViewPlatform leaf node 
 * object in the scene graph.
 *
 * @author  Karl Traunmueller
 */
class View {

    /** field-of-view, in radians */
    static final float DEFAULT_FOV              = 0.3f; //0.7f; //1.0f;
    /** default front clip distance */
    static final float DEFAULT_FRONT_CLIP_DIST  = 0.1f;
    /** default back clip distance */
    static final float DEFAULT_BACK_CLIP_DIST   = 100.0f;
    
    /** width and height of the rendered image, in pixels */
    Dimension mViewportDimension;
    /** antialiasing on or off */
    boolean   mAntialias;    
    /** projection matrix */
    Matrix4f  mHproj;
    /** field of view */
    float     mFOV;
    /** front clip distance */
    float     mFrontClipDist;
    /** back clip distance */
    float     mBackClipDist;
    /** translational part of camera position */
    Vector3f  mTranslation;
    Vector3f  mTransTrans;
    /** rotational part of camera position */
    Matrix3f  mRotation;
    Matrix3f  mRotTrans;
    /** combined viewing transform */
    Matrix4f  mViewingTransform;
    
    /**
     * Default constructor.
     */
    View() {                
        mViewportDimension = new Dimension();
        mAntialias = false;        
        
        mHproj = new Matrix4f();
        mHproj.setIdentity();
        
        mFOV = DEFAULT_FOV;
        mFrontClipDist = DEFAULT_FRONT_CLIP_DIST;
        mBackClipDist = DEFAULT_BACK_CLIP_DIST;        
                
        mRotation = new Matrix3f();
        mRotation.setIdentity();
        mRotTrans = new Matrix3f();
        
        mTranslation = new Vector3f(0.0f, 0.0f, 3.0f);
        mTransTrans = new Vector3f();
        
        mViewingTransform = new Matrix4f();
        mViewingTransform.setIdentity();
    }
    
    /**
     * Sets this view's current viewport dimension.
     */
    void setViewportDimension(Dimension d) {
        mViewportDimension = d;
    }
    
    /**
     * Returns the current field of view in radians. 
     */
    float getFieldOfView() {
        return mFOV;
    }
    
    /**
     * Sets the field of view used to compute the projection transform.
     */
    void setFieldOfView(float fov) {
        mFOV = fov;
    }
    
    /** 
     * Retrieves whether antialiasing is enabled for this View.
     */
    boolean getSceneAntialiasing() {
        return mAntialias;
    }
    
    /**
     * Turns antilasing on or off.
     */
    void setSceneAntiAliasing(boolean antialias) {
        mAntialias = antialias;
    }
    
    /**
     * Returns the view model's front clip distance. 
     */
    float getFrontClipDistance() {
        return mFrontClipDist;
    }

    /**
     * Sets the view model's front clip distance. 
     * This value specifies the distance away from the eyepoint in the direction of 
     * gaze where objects stop disappearing. Objects closer to the eye than the front 
     * clip distance are not drawn.
     */
    void setFrontClipDistance(float frontClipDist) {
        mFrontClipDist = frontClipDist;
    }
    
    /**
     * Returns the view model's back clip distance.
     */
    float getBackClipDistance() {
        return mBackClipDist;
    }
    
    /**
     * Sets the view model's back clip distance. 
     * The parameter specifies the distance from the eyepoint in the direction of gaze to 
     * where objects begin disappearing. Objects farther away from the eye than the 
     * back clip distance are not drawn. 
     */
    void setBackClipDistance(float backClipDist) {
        mBackClipDist = backClipDist;
    }
    
    /**
     * Calculates the viewing matrix based on the position and orientation of the camera. 
     *
     * Eberly, p. 87
     */
    Matrix4f getViewingTransform() {
        mRotTrans.set(mRotation);
        mRotTrans.transpose();
        
        mTransTrans.set(mTranslation);
        mRotTrans.transform(mTransTrans);
        mTransTrans.scale(-1.0f);
        
        mViewingTransform.set(mRotTrans, mTransTrans, 1.0f);
        return mViewingTransform;
    }
    
    /**
     * Calculates the projection matrix based on the current view parameters.
     *
     * Eberly, p. 86
     */
    Matrix4f getProjectionMatrix() {
        float wh = (float)mViewportDimension.width / mViewportDimension.height;
        float nearPlaneWidth = 2.0f * mFrontClipDist * (float)java.lang.Math.tan(mFOV / 2.0f);
        float nearPlaneHeight = nearPlaneWidth / wh;
        
        mHproj.m00 = 2.0f * mFrontClipDist / nearPlaneWidth;
        mHproj.m11 = 2.0f * mFrontClipDist / nearPlaneHeight;
        mHproj.m22 = -mBackClipDist / (mBackClipDist - mFrontClipDist);
        mHproj.m23 = -mFrontClipDist * mBackClipDist / (mBackClipDist - mFrontClipDist);
        mHproj.m32 = -1.0f;        
        return mHproj;
    }
        
}
