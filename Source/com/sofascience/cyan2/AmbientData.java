/*
 * AmbientData.java
 *
 * Created on September 26, 2002, 12:27 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class AmbientData extends SceneGraphObject {
    
    Color3f   mColor;
    Lin3Track mColorTrack;

    /** 
     * Creates a new instance of AmbientData.
     */
    AmbientData() {
        mColor = new Color3f();
        mColorTrack = new Lin3Track(mColor);
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- AMBIENT DATA");
        indent(level + 1); System.out.print("color: "); dumpColor(0, mColor);
        mColorTrack.dump(level + 2);
    }
     */
}
