/*
 * QuatEx.java
 *
 * Created on October 15, 2002, 3:03 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Quat4f;
import com.sofascience.cyan2.vecmath.AxisAngle4f;
import com.sofascience.cyan2.vecmath.Vector3f;

/**
 *
 * @author  karl
 */
class Quat4fEx extends com.sofascience.cyan2.vecmath.Quat4f {
    
    static final double EPSILON = 1.0e-8;
    
    Quat4f  mAb;
    Quat4f  mPq;
    
    /** 
     *
     */
    Quat4fEx() {
        mAb = new Quat4f();
        mPq = new Quat4f();
    }
    
    /** 
     *
     */
    Quat4fEx(float x, float y, float z, float w) {
        this();
        set(x, y, z, w);
    }

    final void setAxisAngle(Vector3f axis, float angle) {
        double omega, s, l;

        l = Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
        if (l < EPSILON) {
            x = y = z = 0.0f;
            w = 1.0f;
        }
        else {
            omega = -0.5 * angle;
            s = Math.sin(omega) / l;
            x = (float)s * axis.x;
            y = (float)s * axis.y;
            z = (float)s * axis.z;
            w = (float)Math.cos(omega);
        }
    }
    
    /**
     *
     */
    final float dot(Quat4fEx a) {
        return(x * a.x + y * a.y + z * a.z + w * a.w);
    }
    
    /**
     *
     */
    final void ln() {
        double om, s, t;

        s = Math.sqrt(x * x + y * y + z * z);
        om = Math.atan2(s, w);
        if (Math.abs(s) < EPSILON)
            t = 0.0f;
        else
            t = om / s;

        x *= t;
        y *= t;
        z *= t;
        w = 0.0f;
    }     
    
    /**
     *
     */
    final void lnDif(Quat4fEx a, Quat4fEx b) {
        set(a);
        inverse();
        mul(b);
        ln();
    }
    
    /**
     *
     */
    final void exp() {
        double om, sinom;

        om = Math.sqrt(x * x + y * y + z * z);
        if (Math.abs(om) < EPSILON)
            sinom = 1.0f;
        else
            sinom = Math.sin(om) / om;

        x *= sinom;
        y *= sinom;
        z *= sinom;
        w = (float)Math.cos(om);
    }
        
    /**
     *
     */
    final void squad(Quat4f a, Quat4f p, Quat4f q, Quat4f b, float t) {
        mAb.interpolate(a, b, (double)t);
        mPq.interpolate(p, q, (double)t);
        interpolate(mAb, mPq, (double)(2.0f * t * (1.0f - t)));
    }
    
}
