/*
 * FixedPoint.java
 *
 * Created on June 9, 2002, 3:45 PM
 */

package com.sofascience.cyan2;

/**
 * Class for fixed-point math.
 *
 * @author  Karl Traunmueller
 */
class FixedPoint {
    
    /** 
     * Default constructor.
     */
    FixedPoint() {
    }
    
    /**
     * Floating point to 28.4 fixed point conversion.
     */
    static final int floatToFixed284(float value) {
        return (int)(value * 16.0f);
    }

    /**
     * 28.4 fixed point to floatint point conversion.
     */
    static final float fixed284ToFloat(int value) {
        return value / 16.0f;
    }

    /**
     * Floatint point to 16.16 fixed point conversion
     */
    static final int floatToFixed1616(float value) {
        return (int)(value * 65536.0f);
    }

    /**
     * 16.16 fixed point to floatint point conversion.
     */
    static final float fixed1616ToFloat(int value ) {
        return value / 65536.0f;
    }

    /**
     * 28.4 * 28.4 fixed point multiplication.
     */
    static final int fixed284Mul(int a, int b) {
        return (a * b) / 16;	// 28.4 * 28.4 = 24.8 / 16 = 28.4
    }

    /**
     * 28.4 fixed point ceiling function.
     */
    static final int ceil284(int value) {
        int returnValue;
        int numerator = value - 1 + 16;

        if (numerator >= 0) {
            returnValue = numerator / 16;
        } 
        else {
            returnValue = -((-numerator) / 16);
            returnValue -= ((-numerator) % 16) > 0 ? 1 : 0;
        }
        return returnValue;
    }
}
