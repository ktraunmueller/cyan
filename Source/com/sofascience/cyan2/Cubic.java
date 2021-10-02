/*
 * Cubic.java
 *
 * Created on October 15, 2002, 1:24 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  karl
 */
class Cubic {
    
    /**
     * 
     */
    Cubic() {
    }
    
    /**
     *
     */
    static float cubic(float a, float p, float q, float b, float t) {
        double x, y, z, w;   
        x = 2.0 * t * t * t - 3.0 * t * t + 1.0;
        y = -2.0 * t * t * t + 3.0 * t * t;
        z = t * t * t - 2.0 * t * t + t;
        w = t * t * t - t * t;
        return ((float)(x * a + y * b + z * p + w * q));
    }
    
    /**
     *
     */
    static void vectorCubic(Tuple3f c, Tuple3f a, Tuple3f p, Tuple3f q, Tuple3f b, float t) {
        float x, y, z, w;   

        x = 2.0f * t * t * t - 3.0f * t * t + 1.0f;
        y = -2.0f * t * t * t + 3.0f * t * t;
        z = t * t * t - 2.0f * t * t + t;
        w = t * t * t - t * t;
        c.x = (x * a.x + y * b.x + z * p.x + w * q.x);
        c.y = (x * a.y + y * b.y + z * p.y + w * q.y);
        c.z = (x * a.z + y * b.z + z * p.z + w * q.z);
    }
}
