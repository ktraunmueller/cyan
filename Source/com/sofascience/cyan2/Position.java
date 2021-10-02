/*
 * Position.java
 *
 * Created on January 21, 2003, 10:01 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl
 */
class Position {
    
    private static int POSITION_STEPS = 15;
    
    float x, y, z;
    float rotX, rotY;
    private float scale, scaleFactor;
    float dX, dY, dZ;
    float dRotX, dRotY;
    private float dScale;
    float currentScale;
    boolean deltasValid;
    private int nrSteps;
    
    /** 
     *
     */
    public Position(float x, float y, float z, float rotX, float rotY, float scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotX = rotX;
        this.rotY = rotY;
        this.scale = scale;
    }
    
    void prepareDeltas(float x, float y, float z, float rotX, float rotY, float scale) {
        dX = (this.x - x) / POSITION_STEPS;
        dY = (this.y - y) / POSITION_STEPS;
        dZ = (this.z - z) / POSITION_STEPS;
        dRotX = (this.rotX - rotX) / POSITION_STEPS;
        dRotY = (this.rotY - rotY) / POSITION_STEPS;
        dScale = (this.scale - scale) / POSITION_STEPS;
        currentScale = scale + dScale;
        deltasValid = true;
        nrSteps = 0;
    }
    
    boolean step() {
        nrSteps++;
        currentScale += dScale;
        if (nrSteps >= POSITION_STEPS)
            deltasValid = false;
        return (nrSteps < POSITION_STEPS);
    }
    
}
