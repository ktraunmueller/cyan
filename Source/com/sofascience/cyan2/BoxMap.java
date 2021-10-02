/*
 * BoxMap.java
 *
 * Created on September 26, 2002, 12:10 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class BoxMap extends SceneGraphObject {
    
    String mFront;
    String mBack;
    String mLeft;
    String mRight;
    String mTop;
    String mBottom;

    /** Creates a new instance of BoxMap */
    BoxMap() {
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- BOXMAP");
        indent(level + 1); System.out.println("front: " + mFront);
        indent(level + 1); System.out.println("back: " + mBack);
        indent(level + 1); System.out.println("left: " + mLeft);
        indent(level + 1); System.out.println("right: " + mRight);
        indent(level + 1); System.out.println("top: " + mTop);
        indent(level + 1); System.out.println("bottom: " + mBottom);
    }
     */
}
