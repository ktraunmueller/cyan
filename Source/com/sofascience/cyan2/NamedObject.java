/*
 * NamedObject.java
 *
 * Created on September 28, 2002, 1:58 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class NamedObject extends SceneGraphObject {

    String    mName;
    
    /** 
     * Creates a new instance of NamedObject.
     */
    NamedObject(String name) {
        mName = name;
    }

    /**
     *
     */
    final String getName() {
        return mName;
    }
    
    /*
    void dump(int level) {
    }
     */
    
}
