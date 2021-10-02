/*
 * ObjectNode.java
 *
 * Created on September 27, 2002, 3:49 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class ObjectNode extends Node {
    
    static final String DUMMY_NAME   = "$$$DUMMY";
    static final String SHADOW_NAME  = "shadow";
    
    Matrix4f mHelp;
    
    /** 
     *
     */
    ObjectNode(String name) {
        super(name);
        mType = OBJECT_NODE;
        mHelp = new Matrix4f();
    }

    /**
     *
     */
    void init() {
    }
    
    /**
     *
     */
    boolean eval(int time) {
        boolean posActive = mNodeData.mObjectData.mPosTrack.eval(time);
        boolean rotActive = mNodeData.mObjectData.mRotTrack.eval(time);
        boolean sclActive = mNodeData.mObjectData.mSclTrack.eval(time);
        boolean hideActive = mNodeData.mObjectData.mHideTrack.eval(time);
        //boolean morphActive = mNodeData.mObjectData.mMorphTrack.eval(time);        
        
        boolean childrenActive = evalChildren(time);
        return posActive || rotActive || sclActive || hideActive || childrenActive;
    }
    
    /**
     *
     */
    void render(Matrix4f localToWorld, SceneGraphRenderer renderer) {        
        mMatrix.setIdentity();                          
        mMatrix.translateBy(mNodeData.mObjectData.mPos);
        mMatrix.rotateBy(mNodeData.mObjectData.mRot);                         
        mMatrix.scaleBy(mNodeData.mObjectData.mScl);        
        
        super.render(localToWorld, renderer);
        
        if (mName.compareTo(DUMMY_NAME) == 0)
            return;
        
        Scene scene = renderer.getScene();
        if (scene == null)
            return;
        Mesh mesh = scene.getMeshByName(mName);
        if (mesh == null)
            return;

        mHelp.mul(localToWorld, mMatrix);        
        mHelp.translateBy(mNodeData.mObjectData.mPivot);                
        mHelp.mul(mesh.mMatrixInv);      
        
        RenderPipeline pipe = renderer.getRenderPipeline();
        
        if (mName.compareTo(SHADOW_NAME) == 0)
            pipe.renderShadow(mesh, mHelp);
        else
            pipe.renderMesh(mesh, mHelp);
    }
    
    /**
     *
     */
    void renderTransparentFaces(Matrix4f localToWorld, SceneGraphRenderer renderer) {        
        /*
        mMatrix.setIdentity();                          
        mMatrix.translateBy(mNodeData.mObjectData.mPos);
        mMatrix.rotateBy(mNodeData.mObjectData.mRot);                         
        mMatrix.scaleBy(mNodeData.mObjectData.mScl);        
         */
        
        super.renderTransparentFaces(localToWorld, renderer);
        
        if (mName.compareTo(DUMMY_NAME) == 0)
            return;
        
        Scene scene = renderer.getScene();
        if (scene == null)
            return;
        Mesh mesh = scene.getMeshByName(mName);
        if (mesh == null)
            return;

        mHelp.mul(localToWorld, mMatrix);
        mHelp.translateBy(mNodeData.mObjectData.mPivot);        
        mHelp.mul(mesh.mMatrixInv);      
        
        RenderPipeline pipe = renderer.getRenderPipeline();
        pipe.renderMeshTransparentFaces(mesh, mHelp);
    }
}

