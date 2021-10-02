/*
 * Animation.java
 *
 * Created on November 23, 2002, 1:07 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  karl
 */
class Animation extends NamedObject{
    
    int mStart;
    int mEnd;
    
    /** 
     *
     */
    public Animation(String name, int start, int end) {
        super(name);
        mStart = start;
        mEnd = end;
    }
    
    
}
