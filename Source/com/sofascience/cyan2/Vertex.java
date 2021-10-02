/*
 * Vertex.java
 *
 * Created on February 23, 2002, 3:17 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 * A 3D vertex.
 *
 * @author  Karl Traunmueller
 */
class Vertex extends SceneGraphObject {

    Point3f   mWorldCoords;        
    int       mFlags;
    float     mU, mV;
    
    /**
     * Default constructor.
     */
    Vertex() {
        mWorldCoords = new Point3f();
    }

    /**
     * Constructs a vertex with the given model coordinates.
     */
    Vertex(float x, float y, float z) {
        this();
        mWorldCoords.set(x, y, z);
    }
    
    /**
     * Sets this vertex's state to v's state.
     */
    final void set(Vertex v) {
        mWorldCoords.set(v.mWorldCoords);
        mU = v.mU;
        mV = v.mV;
    }
    
    /**
     *
     */
    final void setWorldCoords(float x, float y, float z) {
        mWorldCoords.set(x, y, z);
    }

    /**
     *
     */    
    final void setFlags(int flags) {
        mFlags = flags;
    }
    
    /**
     *
     */
    final void setTexCoords(float u, float v) {
        mU = u;
        mV = v;
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- VERTEX");
        indent(level + 1); System.out.print("world coords: "); dumpTuple(0, mWorldCoords);
        indent(level + 1); System.out.println("tex coords: (" + mU + ", " + mV + ")");
        indent(level + 1); System.out.println("flags: " + mFlags);
    }
     */
    
}
