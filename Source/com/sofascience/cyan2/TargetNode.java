/*
 * TargetNode.java
 *
 * Created on October 13, 2002, 5:32 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  karl
 */
class TargetNode extends Node {
    
    /** Creates a new instance of TargetNode */
    TargetNode(String name) {
        super(name);
        mType = TARGET_NODE;
    }

    /**
     *
     */
    boolean eval(int time) {
        return false;
    }
    
}
