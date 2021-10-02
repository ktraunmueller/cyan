/*
 * TransformNode.java
 *
 * Created on October 17, 2002, 9:57 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  karl
 */
class TransformNode extends Node {
    
    /** rotational part of transform */
    Matrix4f  mTransform;
    Matrix3f  mRotation;
    /** helper matrix */
    Matrix3f  mRotHelp;
    /** translational part of transform */
    Vector3f  mTranslation;
    /** scale factor */
    float     mScale;

    /**
     *  Default constructor.
     */
    TransformNode(String name) {
        super(name);
        mTransform = new Matrix4f();
        mTransform.setIdentity();
        
        mMatrix.setIdentity();
        mRotation = new Matrix3f();
        mRotation.setIdentity();
        mRotHelp = new Matrix3f();
        mRotHelp.setIdentity();
        mTranslation = new Vector3f();
        mScale = 1.0f;
    }

    /**
     * Multiplies the transform matrix by the given matrix.
     */
    final void rotate(Matrix3f m) {
        mRotation.mul(m);
    }
    
    /**
     * Sets the rotational part to the given rotation matrix.
     */
    final void setRotation(Matrix3f m) {
        mRotation.set(m);
    }
    
    /**
     * Rotates by d radians counter-clockwise around the X axis. (VERIFY THIS).
     */
    final void rotX(float d) {
        mRotHelp.rotX(d);
        mRotation.mul(mRotHelp);
    }

    /**
     * Rotates by d radians counter-clockwise around the Y axis. (VERIFY THIS).
     */
    final void rotY(float d) {
        mRotHelp.rotY(d);
        mRotation.mul(mRotHelp);
    }
    
    /**
     * Rotates by d radians counter-clockwise around the Z axis. (VERIFY THIS).
     */
    final void rotZ(float d) {
        mRotHelp.rotZ(d);
        mRotation.mul(mRotHelp);
    }
    
    /**
     *
     */
    final void move(Vector3f t) {
        mTranslation.add(t);
    }
    
    /**
     * Translates by dX world units along the X axis. (VERIFY THIS).
     */
    final void moveX(float dX) {
        mTranslation.x += dX;
    }
    
    /**
     * Translates by dX world units along the X axis. (VERIFY THIS).
     */
    final void moveY(float dY) {
        mTranslation.y += dY;
    }
    
    /**
     * Translates by dX world units along the X axis. (VERIFY THIS).
     */
    final void moveZ(float dZ) {
        mTranslation.z += dZ;
    }
    
    /**
     * Scales by dS. (VERIFY THIS).
     */
    final void scale(float dS) {
        mScale *= dS;
    }
    
    /**
     *
     */
    final void scaleAbs(float scale) {
        mScale = scale;
    }

    /**
     * Returns the current scale factor.
     */
    final float getScale() {
        return mScale;
    }
    
    /**
     * Resets the transform to the identity matrix.
     */
    final void resetTransform() {
        mRotation.setIdentity();
        mTranslation.set(0.0f, 0.0f, 0.0f);
        mScale = 1.0f;
    }
    
    /**
     * Updates the local-to-world transform and iterates over child nodes.
     */
    void render(Matrix4f localToWorld, SceneGraphRenderer renderer) {
        mTransform.set(mRotation, mTranslation, mScale);
        mTransform.mul(localToWorld);
        super.render(mTransform, renderer);
    }  
    
    /**
     * Updates the local-to-world transform and iterates over child nodes.
     */
    void renderTransparentFaces(Matrix4f localToWorld, SceneGraphRenderer renderer) {
        mTransform.set(mRotation, mTranslation, mScale);
        mTransform.mul(localToWorld);
        super.renderTransparentFaces(mTransform, renderer);
    } 
    
    /**
     *
     */
    boolean eval(int time) {
        return evalChildren(time);
    }
    
}
