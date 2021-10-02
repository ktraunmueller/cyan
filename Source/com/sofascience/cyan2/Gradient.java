/*
 * Gradient.java
 *
 * Created on September 27, 2002, 1:30 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Color3f;

/**
 *
 * @author  Karl Traunmueller
 */
class Gradient extends SceneGraphObject {
    
    boolean   mUse;
    float     mPercent;
    Color3f   mTop;
    Color3f   mMiddle;
    Color3f   mBottom;

    /** Creates a new instance of Gradient */
    Gradient() {
        mTop = new Color3f();
        mMiddle = new Color3f();
        mBottom = new Color3f();
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- GRADIENT");
        indent(level + 1); System.out.println("use: " + mUse);
        indent(level + 1); System.out.println("percent: " + mPercent);
        indent(level + 1); System.out.print("top: "); dumpColor(0, mTop);
        indent(level + 1); System.out.print("middle: "); dumpColor(0, mMiddle);
        indent(level + 1); System.out.print("bottom: "); dumpColor(0, mBottom);
    }
     */
}
