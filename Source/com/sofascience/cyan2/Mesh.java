/*
 * Mesh.java
 *
 * Created on September 26, 2002, 12:08 PM
 */

package com.sofascience.cyan2;

import java.util.Vector;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class Mesh extends NamedObject {

    int           mId;
    boolean       mShowToolTip;
    int           mColor;
    Vector3f      mBbMin;
    Vector3f      mBbMax;
    Matrix4f      mMatrix;
    Matrix4f      mMatrixInv;
    Vertex[]      mVertices;
    Face[]        mFaces;
    BoxMap        mBoxMap;
    MapData       mMapData;    
    int           mNextNormalFace;
    
    /** 
     * Creates a new instance of Mesh.
     */
    Mesh(String name) {
        super(name);
        mBbMin = new Vector3f();
        mBbMax = new Vector3f();
        mMatrix = new Matrix4f();
        mMatrix.setIdentity();
        mMatrixInv = new Matrix4f();
        mBoxMap = new BoxMap();
        mMapData = new MapData();        
    }
 
    /**
     *
     */
    final int getId() {
        return mId;
    }
    
    /**
     *
     */
    final void setId(int id) {
        mId = id;
    }
    
    /**
     *
     */
    final void meshMatrixValid() {
        invertMeshMatrix();
        //System.out.println("Mesh matrix of mesh " + mName);
        //dumpMatrix(0, mMatrix);
    }
    
    /**
     *
     */
    final void invertMeshMatrix() {
        mMatrixInv.invert(mMatrix);
    }
        
    /**
     *
     */
    final void calcBoundingBox() {
        if (mVertices == null)
            return;
        for (int i = 0; i < mVertices.length; i++) {
            Vertex v = mVertices[i];
            if (v == null)
                continue;
            if (v.mWorldCoords.x < mBbMin.x)
                mBbMin.x = v.mWorldCoords.x;
            if (v.mWorldCoords.x > mBbMax.x)
                mBbMax.x = v.mWorldCoords.x;
            if (v.mWorldCoords.y < mBbMin.y)
                mBbMin.y = v.mWorldCoords.y;
            if (v.mWorldCoords.y > mBbMax.y)
                mBbMax.y = v.mWorldCoords.y;
            if (v.mWorldCoords.z < mBbMin.z)
                mBbMin.z = v.mWorldCoords.z;
            if (v.mWorldCoords.z > mBbMax.z)
                mBbMax.z = v.mWorldCoords.z;
        }
    }
    
    /**
     *
     */
    final Vector3f getBoundingBoxMin() {
        return mBbMin;
    }
    
    /**
     *
     */
    final Vector3f getBoundingBoxMax() {
        return mBbMax;
    }
        
    /**
     *
     */
    final void setFlag(int v, int flags) {
        if (mVertices == null || v >= mVertices.length)
            return;
        mVertices[v].setFlags(flags);
    }
    
    /**
     *
     */
    final int getNrVertices() {
        if (mVertices == null)
            return 0;
        return mVertices.length;
    }
    
    /**
     *
     */
    final void setNrVertices(int nrVertices) {
        mVertices = new Vertex[nrVertices];
        for (int v = 0; v < nrVertices; v++) {
            mVertices[v] = new Vertex();
        }
    }
    
    /**
     *
     */
    final Vertex getVertex(int vertex) {
        if (mVertices == null || vertex >= mVertices.length)
            return null;
        return mVertices[vertex];
    }
    
    /**
     *
     */
    final void setVertex(int vertex, float x, float y, float z) {
        if (mVertices == null || vertex >= mVertices.length)
            return;
        mVertices[vertex].setWorldCoords(x, y, z);
    }
    
    /**
     *
     */
    final void setVertex(int vertex, Vertex v) {
        if (mVertices == null || vertex >= mVertices.length)
            return;
        mVertices[vertex].set(v);
    }
    
    /**
     *
     */
    final Vertex getVertex(int t, int p) {
        if (mVertices == null || mFaces == null || t >= mFaces.length || p >= 3)
            return null;
        return mVertices[mFaces[t].getVertexIndex(p)];
    }
    
    /**
     *
     */
    final int getVertexIndex(int t, int p) {
        if (mVertices == null || mFaces == null || t >= mFaces.length || p >= 3)
            return 0;
        return mFaces[t].getVertexIndex(p);
    }
    
    /**
     *
     */
    final void setTexCoords(int vertex, float u, float v) {
        if (mVertices == null || vertex >= mVertices.length)
            return;
        mVertices[vertex].setTexCoords(u, v);
    }
    
    /**
     *
     */
    final int getNrFaces() {
        if (mFaces == null)
            return 0;
        return mFaces.length;
    }
    
    /**
     *
     */
    final void setNrFaces(int nrFaces) {
        mFaces = new Face[nrFaces];
        for (int f = 0; f < nrFaces; f++)
            mFaces[f] = new Face();
    }
    
    /**
     *
     */
    final Face getFace(int face) {
        if (mFaces == null || face >= mFaces.length)
            return null;
        return mFaces[face];
    }
    
    /**
     *
     */
    final Vector3f getFaceNormal(int t) {
        if (mFaces == null || t >= mFaces.length)
            return null;
        return mFaces[t].mNormal;
    }       
    
    /**
     *
     */
    final void setFaceVertices(int face, int a, int b, int c) {
        if (mFaces == null || face >= mFaces.length)
            return;
        mFaces[face].setVertexIndices(a, b, c);
    }
    
    /**
     *
     */
    final void calcNormals() {
        calcFaceNormals();
        mNextNormalFace = 0;
        //calcVertexNormals();        
    }
    
    /**
     *
     */
    final void calcFaceNormals() {
        if (mVertices == null || mFaces == null)
            return;
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        for (int face = 0; face < mFaces.length; face++) {
            v1.sub(getVertex(face, 0).mWorldCoords, getVertex(face, 1).mWorldCoords);
            v2.sub(getVertex(face, 0).mWorldCoords, getVertex(face, 2).mWorldCoords);
            Vector3f normal = getFaceNormal(face);
            normal.cross(v1, v2);
            normal.normalize();
            
            Face f = getFace(face);
            f.setAllVertexNormals(normal);
        }
    }
    
    final boolean allVertexNormalsCalculated() {
        return mNextNormalFace == getNrFaces();
    }
    
    final void calcNextVertexNormals() {
        Vector3f n = new Vector3f();
        
        for (int i = 0; i < 30; i++) {
            if (mNextNormalFace >= getNrFaces())
                break;
            
            Face face1 = getFace(mNextNormalFace);                    
            if (face1.getSmoothingGroup() > 0) {
                n.set(0.0f, 0.0f, 0.0f);
                for (int g = 0; g < getNrFaces(); g++) {
                    if (mNextNormalFace == g)
                        continue;
                    Face face2 = getFace(g);
                    for (int k = 0; k < 3; k++) {
                        if (face2.usesSimilarVertex(getVertex(face1.getVertexIndex(k)), 0.001f) && 
                            face2.getSmoothingGroup() == face1.getSmoothingGroup()) {
                            Vector3f vn = face1.getVertexNormal(k);
                            vn.add(face2.getNormal());
                        }
                    }
                }
            }

            for (int k = 0; k < 3; k++) {
                Vector3f vn = face1.getVertexNormal(k);
                vn.normalize();
            }

            mNextNormalFace++;
        }
    }
    
    /**
     *
     */
    final void calcVertexNormals() {
        if (mFaces == null || mVertices == null)
            return;

        Vector3f n = new Vector3f();
        for (int f = 0; f < getNrFaces(); f++) {
            Face face1 = getFace(f);
            
            if (face1.getSmoothingGroup() > 0) {
                n.set(0.0f, 0.0f, 0.0f);
                for (int g = 0; g < getNrFaces(); g++) {
                    if (f == g)
                        continue;
                    Face face2 = getFace(g);
                    for (int k = 0; k < 3; k++) {
                        if (face2.usesSimilarVertex(getVertex(face1.getVertexIndex(k)), 0.001f) && 
                            face2.getSmoothingGroup() == face1.getSmoothingGroup()) {
                            Vector3f vn = face1.getVertexNormal(k);
                            vn.add(face2.getNormal());
                        }
                    }
                }
            }
            
            for (int k = 0; k < 3; k++) {
                Vector3f vn = face1.getVertexNormal(k);
                vn.normalize();
            }
        }
        
        /*
        FaceList[] fl = new FaceList[getNrVertices()];
        FaceList[] fa = new FaceList[3 * getNrFaces()];
        for (int i = 0; i < fa.length; i++)
            fa[i] = new FaceList();

        int k = 0;
        for (int f = 0; f < getNrFaces(); f++) {
            Face face = getFace(f);
            for (int j = 0; j < 3; j++) {
              FaceList list = fa[k++];              
              list.mFace = face;
              list.mNext = fl[face.getVertexIndex(j)];
              fl[face.getVertexIndex(j)] = list;
            }
        }

        Vector3f n = new Vector3f();
        Vector3f[] N = new Vector3f[64];
        for (int i = 0; i < N.length; i++)
            N[i] = new Vector3f();
        
        for (int f = 0; f < getNrFaces(); f++) {
            Face face = getFace(f);
            
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < N.length; i++)
                    N[i].set(0.0f, 0.0f, 0.0f);
                if (face.getSmoothingGroup() > 0) {
                    n.set(0.0f, 0.0f, 0.0f);
                    k = 0;
                    for (FaceList p = fl[face.getVertexIndex(j)]; p != null; p = p.mNext) {
                        boolean found = false;
                        for (int l = 0; l < k; l++) {
                              if (Math.abs(N[l].dot(p.mFace.getNormal()) - 1.0f) < 1e-5f) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            if ((face.getSmoothingGroup() & p.mFace.getSmoothingGroup()) != 0) {
                                n.add(p.mFace.getNormal());
                                N[k].set(p.mFace.getNormal());
                                k++;
                            }
                        }
                    }
                } 
                else {   
                    n.set(face.getNormal());
                }
                n.normalize();
                face.setVertexNormal(j, n);
            }
        }
         */
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- MESH " + mName);
        indent(level + 1); System.out.println("color: " + mColor);
        indent(level + 1); System.out.println("mesh matrix: ");
        dumpMatrix(level + 2, mMatrix);
        if (mVertices == null) {
            indent(level + 1); System.out.println("*** Vertex array is null ***");
        }
        else {
            indent(level + 1); System.out.println("nr vertices: " + mVertices.length);
        }
        dumpObjects(level + 1, mVertices);
        if (mFaces == null) {
            indent(level + 1); System.out.println("*** Face array is null ***"); 
        }
        else {
            indent(level + 1); System.out.println("nr faces: " + mFaces.length);
        }
        dumpObjects(level + 2, mFaces);
        mBoxMap.dump(level + 1);
        mMapData.dump(level + 1);
    }
     */
}
