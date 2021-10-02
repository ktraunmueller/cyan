/*
 * MapData.java
 *
 * Created on September 26, 2002, 12:11 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class MapData extends SceneGraphObject {
    
    static final int MAP_NONE         = 0xFFFF;
    static final int MAP_PLANAR       = 0;
    static final int MAP_CYLINDRICAL  = 1;
    static final int MAP_SPHERICAL    = 2;
    
    int       mMaptype;
    Vector3f  mPos;
    Matrix4f  mMatrix;
    float     mScale;
    Vector2f  mTile;
    Vector2f  mPlanarSize;
    float     mCylinderHeight;

    /** Creates a new instance of MapData */
    MapData() {
        mMaptype = MAP_NONE;
        mPos = new Vector3f();
        mMatrix = new Matrix4f();
        mMatrix.setIdentity();
        mScale = 1.0f;
        mTile = new Vector2f(1.0f, 1.0f);
        mPlanarSize = new Vector2f();
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- MAPDATA");
        indent(level + 1); System.out.print("position: "); dumpTuple(0, mPos);
        indent(level + 1); System.out.println("matrix: "); dumpMatrix(0, mMatrix);
        indent(level + 1); System.out.println("scale: " + mScale);
        indent(level + 1); System.out.print("tile: "); dumpTuple(0, mTile);
        indent(level + 1); System.out.print("planar size: "); dumpTuple(0, mPlanarSize);
        indent(level + 1); System.out.println("cylinder height: " + mCylinderHeight);
    }
     */
}
