/*
 * Face.java
 *
 * Created on September 26, 2002, 12:10 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Vector3f;
import com.sofascience.cyan2.vecmath.Color3f;

/**
 *
 * @author  Karl Traunmueller
 */
class Face extends SceneGraphObject {
    
    static final int  VERTEX_NOT_FOUND = -1;
    
    Mesh          mMesh;
    int           mFlags;
    Material      mMaterial;
    int[]         mVertices;
    Vector3f[]    mVertexNormals;    
    Color3f[]     mVertexColors;
    int           mSmoothingGroup;
    Vector3f      mNormal;

    /**
     *
     */
    Face() {
        mVertices = new int[3];
        mVertexNormals = new Vector3f[3];
        mVertexNormals[0] = new Vector3f();
        mVertexNormals[1] = new Vector3f();
        mVertexNormals[2] = new Vector3f();
        mVertexColors = new Color3f[3];
        mVertexColors[0] = new Color3f();
        mVertexColors[1] = new Color3f();
        mVertexColors[2] = new Color3f();
        mNormal = new Vector3f();
    }

    /**
     *
     */
    final Mesh getMesh() {
        return mMesh;
    }
    
    /**
     *
     */
    final void setMesh(Mesh mesh) {
        mMesh = mesh;
    }
    
    /**
     *
     */
    final Material getMaterial() {
        return mMaterial;
    }
    /**
     *
     */
    final void setMaterial(Material material) {
        mMaterial = material;
    }
    
    /**
     *
     */
    final boolean usesVertex(int v) {
        return (mVertices[0] == v || mVertices[1] == v || mVertices[2] == v);
    }
    
    final boolean usesSimilarVertex(Vertex v, float epsilon) {
        Vertex test = mMesh.getVertex(mVertices[0]);
        if (test.mWorldCoords.epsilonEquals(v.mWorldCoords, epsilon))
            return true;
        test = mMesh.getVertex(mVertices[1]);
        if (test.mWorldCoords.epsilonEquals(v.mWorldCoords, epsilon))
            return true;
        test = mMesh.getVertex(mVertices[2]);
        if (test.mWorldCoords.epsilonEquals(v.mWorldCoords, epsilon))
            return true;
        return false;
    }
        
    /**
     *
     */
    final int getVertexIndex(int i) {
        return mVertices[i];
    }
    
    /**
     *
     */
    final void setVertexIndices(int a, int b, int c) {
        mVertices[0] = a;
        mVertices[1] = b;
        mVertices[2] = c;
    }
    
    /**
     *
     */
    final Vector3f getVertexNormal(int v) {
        return mVertexNormals[v];
    }

    /**
     *
     */
    final void setVertexNormal(int v, Vector3f n) {
        mVertexNormals[v].set(n);
    }

    final void setAllVertexNormals(Vector3f v) {
        mVertexNormals[0].set(v);
        mVertexNormals[1].set(v);
        mVertexNormals[2].set(v);
    }
    
    /**
     *
     */
    final int getFlags() {
        return mFlags;
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
    final int getSmoothingGroup() {
        return mSmoothingGroup;
    }
    
    /**
     *
     */
    final void setSmoothingGroup(int group) {
        mSmoothingGroup = group;
    }
    
    /**
     *
     */
    final Vector3f getNormal() {
        return mNormal;
    }
    
    /**
     *
     */
    final void setNormal(Vector3f normal) {
        mNormal.set(normal);
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- FACE");
        indent(level + 1); System.out.println("material: " + mMaterial);
        indent(level + 1); System.out.println("vertices: (" + mVertices[0] + ", " + mVertices[1] + ", " + mVertices[2] + ")");
        indent(level + 1); System.out.println("flags: " + mFlags);
        indent(level + 1); System.out.println("smoothing group: " + mSmoothingGroup);
        indent(level + 1); System.out.print("normal: "); dumpTuple(0, mNormal);
    }
     */
    
}
