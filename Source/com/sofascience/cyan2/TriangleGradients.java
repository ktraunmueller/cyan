/*
 * TriangleGradients.java
 *
 * Created on June 9, 2002, 2:56 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Color3f;

/**
 *
 * @author  Karl Traunmueller
 */
abstract class TriangleGradients {
    
    float[]   mZ;
    float[]   mOneOverW;
    float     mdZOverdX, mdZOverdY;
    float     mdOneOverWdX, mdOneOverWdY;
    
    float[]   mEnvUOverW;
    float[]   mEnvVOverW;
    float     mdEnvUOverWdX, mdEnvUOverWdY;		
    float     mdEnvVOverWdX, mdEnvVOverWdY;

    /** 
     * Default constructor. 
     */
    TriangleGradients() {
        mZ        = new float[3];
        mOneOverW = new float[3];
        
        mEnvUOverW = new float[3];
        mEnvVOverW = new float[3];
    }
    
    /**
     * Inits this TrianlgeGradients from the given pipeline vertices.
     */
    abstract void init(Color3f ambient, PipelineVertex p0, PipelineVertex p1, PipelineVertex p2,
                       boolean envMapping);
    
}
