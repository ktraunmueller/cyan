/*
 * Integer.java
 *
 * Created on June 9, 2002, 4:01 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class IntegerDivMod {
    
    /** 
     * Default constructor.
     */
    IntegerDivMod() {
    }

    /**
     *
     */
    static final void floorDivMod(int numerator, int denominator, FloorMod floorMod) {
        if (numerator >= 0) {
            // positive case, C is okay
            floorMod.mFloor = numerator / denominator;
            floorMod.mMod = numerator % denominator;

        } 
        else {
            // Numerator is negative, do the right thing
            floorMod.mFloor = -((-numerator) / denominator);
            floorMod.mMod = (-numerator) % denominator;

            if (floorMod.mMod > 0) {
                // there is a remainder
                floorMod.mFloor--; 
                floorMod.mMod = denominator - floorMod.mMod;
            }
        }
    }
    
}
