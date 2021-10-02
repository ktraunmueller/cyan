/*
 * NodeData.java
 *
 * Created on September 26, 2002, 12:26 PM
 */

package com.sofascience.cyan2;

/**
 *
 * @author  Karl Traunmueller
 */
class NodeData extends SceneGraphObject {
    
    AmbientData   mAmbientData;
    ObjectData    mObjectData;
    CameraData    mCameraData;
    TargetData    mTargetData;
    LightData     mLightData;
    SpotData      mSpotData;
    
    /** 
     * Creates a new instance of NodeData.
     */
    NodeData() {
        mAmbientData = new AmbientData();
        mObjectData = new ObjectData();
        mCameraData = new CameraData();
        mTargetData = new TargetData();
        mLightData = new LightData();
        mSpotData = new SpotData();
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- NODE DATA");
        mAmbientData.dump(level + 1);
        mObjectData.dump(level + 1);
        mCameraData.dump(level + 1);
        mTargetData.dump(level + 1);
        mLightData.dump(level + 1);
        mSpotData.dump(level + 1);
    }
     */
}
