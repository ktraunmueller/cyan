/*
 * Chunk.java
 *
 * Created on October 1, 2002, 3:30 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class Chunk {
    
    static final int  HEADER_SIZE = 6;    
    
    Chunk mParent;
    int   mId;
    int   mSize;
    int   mRemainingSize;
    
    /** Creates a new instance of Chunk */
    Chunk(Chunk parent, int id, int size) {
        mParent = parent;
        mId = id;
        mSize = size;
        mRemainingSize = size - HEADER_SIZE;
    }
    
    /**
     *
     */
    final int id() {
        return mId;
    }
    
    /**
     *
     */
    final int size() {
        return mSize;
    }
    
    /**
     *
     */
    final int remainingSize() {
        return mRemainingSize;
    }
    
    /**
     *
     */
    final int length() {
        return mSize - HEADER_SIZE;
    }
    
    /**
     *
     */
    final void crunch(int nrBytes) {
        if (mRemainingSize >= nrBytes) {
            mRemainingSize -= nrBytes;
            if (mParent != null)
                mParent.crunch(nrBytes);
        }
    }
    
    /**
     *
     */
    final boolean complete() {
        return (mRemainingSize == 0);
    }
}
