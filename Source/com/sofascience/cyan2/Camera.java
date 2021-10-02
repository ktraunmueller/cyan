/*
 * Camera.java
 *
 * Created on September 26, 2002, 12:14 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class Camera extends NamedObject {
    
    Vector3f  mPosition;
    Vector3f  mTarget;
    float     mRoll;
    float     mFov;
    boolean   mSeeCone;
    float     mNearRange;
    float     mFarRange;

    /** Creates a new instance of Camera */
    Camera(String name) {
        super(name);
        mPosition = new Vector3f();
        mTarget = new Vector3f();
        mFov = 45.0f;
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- CAMERA " + mName);
        indent(level + 1); System.out.print("position: "); dumpTuple(0, mPosition);
        indent(level + 1); System.out.print("target: "); dumpTuple(0, mTarget);
        indent(level + 1); System.out.println("roll: " + mRoll);
        indent(level + 1); System.out.println("FOV: " + mFov);
        indent(level + 1); System.out.println("see cone: " + mSeeCone);
        indent(level + 1); System.out.println("near range: " + mNearRange);
        indent(level + 1); System.out.println("far range: " + mFarRange);
    }
     */
    
}
