/*
 * Node.java
 *
 * Created on September 26, 2002, 12:07 PM
 */

package com.sofascience.cyan2;

import java.util.Vector;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
abstract class Node extends NamedObject {
    
    static final int NO_PARENT    = 65535;
    static final int UNKNOWN_NODE = 0;
    static final int AMBIENT_NODE = 1;
    static final int OBJECT_NODE  = 2;
    static final int CAMERA_NODE  = 3;
    static final int TARGET_NODE  = 4;
    static final int LIGHT_NODE   = 5;
    static final int SPOT_NODE    = 6;
    
    Vector      mChilds; 
    Node        mParent;
    int         mType;
    int         mId;    
    int         mFlags1;
    int         mFlags2;
    int         mParentId;
    Matrix4f    mMatrix;    
    Matrix4f    mLocalToWorld;
    NodeData    mNodeData;
        
    /** 
     * Creates a new instance of Node.
     */
    Node(String name) {
        super(name);
        mType = UNKNOWN_NODE;
        mMatrix = new Matrix4f();
        mMatrix.setIdentity();
        mLocalToWorld = new Matrix4f();
        mNodeData = new NodeData();
        mId = NO_PARENT;
        mParentId = NO_PARENT;
    }
 
    /**
     *
     */
    void init() {
    }
    
    /**
     *
     */
    final int getType() {
        return mType;
    }
    
    /**
     *
     */
    final void setType(int type) {
        mType = type;
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
    final int getNrChilds() {
        if (mChilds == null)
            return 0;
        return mChilds.size();
    }
    
    /**
     *
     */
    final Node getChild(int child) {
        if (mChilds == null || child >= mChilds.size())
            return null;
        return (Node)(mChilds.elementAt(child));
    }
    
    /**
     *
     */
    final void addChild(Node child) {
        if (mChilds == null)
            mChilds = new Vector();
        mChilds.addElement(child);
    }
    
    /**
     *
     */
    final Node getParent() {
        return mParent;
    }
    
    /**
     *
     */
    final void setParent(Node parent) {
        mParent = parent;
    }
    
    /**
     *
     */
    final int getParentId() {
        return mParentId;
    }
    
    /**
     *
     */
    final void setParentId(int id) {
        mParentId = id;
    }
    
    /**
     * Recursive search.
     */
    final Node getNodeById(int id) {
        if (mId == id)
            return this;
        
        if (mChilds == null)
            return null;
        
        for (int i = 0; i < mChilds.size(); i++) {
            Node child = (Node)(mChilds.elementAt(i));
            Node node = child.getNodeById(id);
            if (node != null)
                return node;
        }
        return null;
    }
    
    /**
     *
     */
    final Node getNodeByName(String name) {
        if (mName.compareTo(name) == 0)
            return this;
        
        if (mChilds == null)
            return null;
        
        for (int i = 0; i < mChilds.size(); i++) {
            Node child = (Node)(mChilds.elementAt(i));
            Node node = child.getNodeByName(name);
            if (node != null)
                return node;
        }        
        return null;
    }
    
    /**
     *
     */
    final int getFlags1() {
        return mFlags1;
    }
    
    /**
     *
     */
    final void setFlags1(int flags) {
        mFlags1 = flags;
    }
    
    /**
     *
     */
    final int getFlags2() {
        return mFlags2;
    }
    
    /**
     *
     */
    final void setFlags2(int flags) {
        mFlags2 = flags;
    }
    
    /**
     *
     */
    final Matrix4f getMatrix() {
        return mMatrix;
    }
    
    /**
     *
     */
    final void setMatrix(Matrix4f m) {
        mMatrix.set(m);
        //System.out.println("Node matrix of node " + mName);
        //dumpMatrix(0, mMatrix);
    }
    
    /**
     *
     */
    final NodeData getNodeData() {
        return mNodeData;
    }
    
    /**
     *
     */
    final void setNodeData(NodeData nodeData) {
        mNodeData = nodeData;
    }
    
    /**
     *
     */
    void render(Matrix4f localToWorld, SceneGraphRenderer renderer) {
        mLocalToWorld.mul(localToWorld, mMatrix);
        if (mChilds != null) {
            for (int c = 0; c < mChilds.size(); c++) {
                Node child = getChild(c);
                child.render(mLocalToWorld, renderer);
            }
        }
    }
    
    /**
     *
     */
    void renderTransparentFaces(Matrix4f localToWorld, SceneGraphRenderer renderer) {
        mLocalToWorld.mul(localToWorld, mMatrix);
        if (mChilds != null) {
            for (int c = 0; c < mChilds.size(); c++) {
                Node child = getChild(c);
                child.renderTransparentFaces(mLocalToWorld, renderer);
            }
        }
    }

    /**
     *
     */
    abstract boolean eval(int time);
   
    /**
     *
     */
    boolean evalChildren(int time) {
        boolean childrenActive = false;
        if (mChilds != null && mChilds.size() > 0) {
            for (int c = 0; c < mChilds.size(); c++) {
                Node child = getChild(c);
                boolean childActive = child.eval(time);
                childrenActive |= childActive;
            }
        }
        return childrenActive;
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- NODE " + mName);
        indent(level + 1); System.out.println("type: " + mType);
        indent(level + 1); System.out.println("id: " + mId);
        indent(level + 1); System.out.println("flags1: " + mFlags1);
        indent(level + 1); System.out.println("flags2: " + mFlags2);
        indent(level + 1); System.out.println("parent id: " + mParentId);
        indent(level + 1); System.out.println("node matrix:");
        dumpMatrix(level + 2, mMatrix);
        mNodeData.dump(level + 1);
        
        if (mChilds != null && mChilds.size() > 0) {
            for (int c = 0; c < mChilds.size(); c++) {
                Node child = getChild(c);
                child.dump(level + 1);
            }
        }
    }
     */
}
