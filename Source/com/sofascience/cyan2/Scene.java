/*
 * Scene.java
 *
 * Created on March 16, 2002, 9:21 PM
 */

package com.sofascience.cyan2;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.URL;

import com.sofascience.cyan2.vecmath.*;

/**
 *
 * @author  Karl Traunmueller
 */
class Scene extends SceneGraphObject {
        
    static final int  NO_ID = 0;
    
    String          mName;
    int             mMeshVersion;
    int             mKeyfRevision;
    float           mMasterScale;
    Vector3f        mConstructionPlane;
    Color3f         mAmbient;
    int             mFrames;
    int             mSegmentFrom;
    int             mSegmentTo;
    int             mCurrentFrame;
    Hashtable       mMaterials;
    Hashtable       mMeshes;
    Hashtable       mCameras;
    Hashtable       mLights;
    Hashtable       mTooltips;
    Hashtable       mTextureMaps;
    Vector3f        mBbMin;
    Vector3f        mBbMax;
    Matrix4f        mIdentity;
    int             mNextId;
    TransformNode   mSceneGraph;
    TransformNode   mSceneTransform;
    int             mTime;
    Hashtable       mAnimations;
    Animation       mActiveAnimation;
    boolean         mAllNormalsCalculated;
    
    /**
     * Default constructor.
     */
    Scene() {
        mConstructionPlane = new Vector3f();
        mAmbient = new Color3f();
        //mShadowMap = new ShadowMap();
        //mBackground = new Background();
        //mAtmosphere = new Atmosphere();
        mMaterials = new Hashtable();
        mMeshes = new Hashtable();
        mCameras = new Hashtable();
        mLights = new Hashtable();
        mTooltips = new Hashtable();
        mTextureMaps = new Hashtable();
        mBbMin = new Vector3f();
        mBbMax = new Vector3f();
        mIdentity = new Matrix4f();
        mIdentity.setIdentity();
        mNextId = 1;
        mSceneGraph = new TransformNode("SceneGraph");
        mSceneTransform = new TransformNode("SceneTransform");
        mSceneGraph.addChild(mSceneTransform); 
        mAnimations = new Hashtable();
        
        rotate((float)(Math.PI / 2.0), 0.0f, -(float)(Math.PI / 2.0));
    }
    
    /**
     *
     */
    final void setScale(float scale) {
        mSceneGraph.scale(scale);        
    }
    
    /**
     *
     */
    final void setOrigin(float x, float y, float z) {
        mSceneTransform.moveX(x);
        mSceneTransform.moveY(y);
        mSceneTransform.moveZ(z);
    }
           
    /**
     *
     */
    final void rotate(float dx, float dy, float dz) {
        mSceneTransform.rotX(dx);
        mSceneGraph.rotY(dy);
        mSceneGraph.rotZ(dz);
    }
    
    /**
     *
     */
    final void resetTransform() {
        mSceneTransform.resetTransform();
        mSceneGraph.resetTransform();
        
        rotate((float)(Math.PI / 2.0), 0.0f, -(float)(Math.PI / 2.0));
    }
    
    /**
     *
     */
    final void loadTextureMaps(Canvas3D canvas) {
        Enumeration materials = mMaterials.elements();
        Material mat = null;
        while (materials.hasMoreElements()) {
            mat = (Material)(materials.nextElement());
            mat.loadTextureMaps(this, canvas);
        }
    }
    
    /**
     *
     */
    final boolean render(SceneGraphRenderer renderer) {
        if (mActiveAnimation != null) {
            eval(mTime++);
            if (mTime > mActiveAnimation.mEnd)
                mActiveAnimation = null;
        }
        mSceneGraph.render(mIdentity, renderer);        
        
        if (!mAllNormalsCalculated) {
            mAllNormalsCalculated = true;
            for (int m = 0; m < getNrMeshes(); m++) {
                Mesh mesh = getMesh(m);
                if (!mesh.allVertexNormalsCalculated()) {
                    mesh.calcNextVertexNormals();
                    mAllNormalsCalculated = false;
                }
            }
        }
        
        return (mActiveAnimation != null || !mAllNormalsCalculated);
    }
    
    /**
     *
     */
    final void renderTransparentFaces(SceneGraphRenderer renderer) {
        mSceneGraph.renderTransparentFaces(mIdentity, renderer);
    }
    
    /**
     *
     */
    final TransformNode getSceneGraph() {
        return mSceneGraph;
    }
    
    /**
     *
     */
    final TransformNode getSceneTransform() {
        return mSceneTransform;
    }
    
    /**
     *
     */
    final void meshDataComplete() {
        for (int m = 0; m < getNrMeshes(); m++) {
            Mesh mesh = getMesh(m);
            mesh.calcNormals();
        } 
    }
    
    /**
     *
     */
    final void calcBoundingBox() {
        for (int i = 0; i < mMeshes.size(); i++) {
            Mesh mesh = getMesh(i);
            if (mesh == null)
                continue;
            mesh.calcBoundingBox();
            Vector3f bbMin = mesh.getBoundingBoxMin();
            Vector3f bbMax = mesh.getBoundingBoxMax();
            if (bbMin.x < mBbMin.x)
                mBbMin.x = bbMin.x;
            if (bbMin.x > mBbMax.x)
                mBbMin.x = bbMin.x;
            if (bbMin.y < mBbMin.y)
                mBbMin.y = bbMin.y;
            if (bbMin.y > mBbMax.y)
                mBbMin.y = bbMin.y;
            if (bbMin.z < mBbMin.z)
                mBbMin.z = bbMin.z;
            if (bbMin.z > mBbMax.z)
                mBbMin.z = bbMin.z;
        }
    }
    
    /**
     * Retrieves the scene's name.
     */
    final String getName() {
        return mName;
    }

    /**
     * Adds a material to this scene.
     */
    final void addMaterial(Material material) {
        mMaterials.put(material.mName, material);        
    }
    
    /**
     * Retrieves a material by name.
     */
    final Material getMaterialByName(String name) {
        return (Material)(mMaterials.get(name));
    }        
    
    /**
     *
     */
    final int getNrMeshes() {
        return mMeshes.size();
    }
    
    /**
     *
     */
    final Mesh getMesh(int mesh) {
        if (mesh >= mMeshes.size())
            return null;
        Enumeration meshes = mMeshes.elements();
        Mesh m = null;
        while (meshes.hasMoreElements() && mesh-- >= 0) {
            m = (Mesh)(meshes.nextElement());
        }
        return m;
    }
    
    /**
     * Retrieves a mesh by name.
     */
    final Mesh getMeshByName(String name) {
        return (Mesh)(mMeshes.get(name));
    }
    
    /**
     *
     */
    final Mesh getMeshById(int id) {
        for (int i = 0; i < getNrMeshes(); i++) {
            Mesh mesh = getMesh(i);
            if (mesh.mId == id)
                return mesh;
        }
        return null;
    }
    
    /**
     * Adds a mesh to this scene.
     */
    final void addMesh(Mesh mesh) {        
        mMeshes.put(mesh.mName, mesh);        
        mesh.setId(mNextId++);
    }
    
    /**
     *
     */
    final Texture getTooltip(String name) {
        return (Texture)(mTooltips.get(name));
    }
    
    /**
     * Adds a tooltip to this scene.
     */
    final void addTooltip(String mesh, Texture tooltip) {        
        mTooltips.put(mesh, tooltip);
    }
    
    /**
     *
     */
    final void addCamera(Camera camera) {
        mCameras.put(camera.mName, camera);
    }
    
    /**
     *
     */
    final int getNrCameras() {
        return mCameras.size();
    }
    
    /**
     *
     */
    final Camera getCamera(int camera) {
        if (camera >= mCameras.size())
            return null;
        Enumeration cameras = mCameras.elements();
        Camera c = null;
        while (cameras.hasMoreElements() && camera-- >= 0) {
            c = (Camera)(cameras.nextElement());
        }
        return c;
    }
    
    /**
     * Retrieves a camera by name.
     */
    final Camera getCameraByName(String name) {
        return (Camera)(mCameras.get(name));
    }
    
    /** 
     * Adds a light to this scene.
     */
    final void addLight(Light light) {
        mLights.put(light.mName, light);
    }
    
    /**
     *
     */
    final int getNrLights() {
        return mLights.size();    
    }
    
    /**
     *
     */
    final Light getLight(int light) {
        if (light >= mLights.size())
            return null;
        Enumeration lights = mLights.elements();
        Light l = null;
        while (lights.hasMoreElements() && light-- >= 0) {
            l = (Light)(lights.nextElement());
        }
        return l;
    }
    
    /**
     * Retrieves a light by name.
     */
    final Light getLightByName(String name) {
        return (Light)(mLights.get(name));
    }
         
    /**
     *
     */
    final void setBoundingBox(Vector3f min, Vector3f max) {
    }
    
    /**
     * Adds a node to this scene.
     */
    final void addNode(Node node) {
        if (node == null)
            return;
        
        Node parent = null;
        if (node.getParentId() != Node.NO_PARENT)
            parent = mSceneTransform.getNodeById(node.getParentId());
        node.setParent(parent);

        if (parent == null) {
            mSceneTransform.addChild(node);
        }
        else
            parent.addChild(node);

        node.init();
    }
    
    /**
     * Retrieves a node by name.
     */
    final Node getNodeByName(String name) {
        return mSceneTransform.getNodeByName(name);
    }
    
    /**
     * Retrieves a node by node id.
     */
    final Node getNodeById(int id) {
        return mSceneTransform.getNodeById(id);
    }
    
    /**
     * Adds a texture to this scene.
     */
    final void addTextureMap(String name, TextureMap map) {
        if (getTextureMapByName(name) != null)
            return;
        mTextureMaps.put(name, map);        
    }
    
    /**
     * Retrieves a texture map by name.
     */
    final TextureMap getTextureMapByName(String name) {
        return (TextureMap)(mTextureMaps.get(name));
    }        
    
    /**
     *
     */
    final void setTextureMap(String name, TextureMap map) {
        mTextureMaps.put(name, map);
    }
    
    /**
     * Adds an animation to this scene.
     */
    final void addAnimation(String name, int start, int end) {
        if (getAnimationByName(name) != null)
            return;
        Animation anim = new Animation(name, start, end);
        mAnimations.put(name, anim);
    }
    
    /**
     * Retrieves an animation map by name.
     */
    final Animation getAnimationByName(String name) {
        return (Animation)(mAnimations.get(name));
    }
    
    /**
     *
     */
    final void playAnimation(String name) {        
        if (mActiveAnimation != null) {
            return;
        }
        Animation anim = getAnimationByName(name);
        if (anim == null) {
            return;
        }
        mActiveAnimation = anim;
        mTime = anim.mStart;
    }
    
    /**
     *
     */
    final boolean eval(int time) {
        return mSceneGraph.eval(time);
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        System.out.println("Dumping Scene " + mName);
        dumpObjects(1, mMaterials);
        dumpObjects(1, mMeshes);
        dumpObjects(1, mCameras);
        dumpObjects(1, mLights);
        mSceneTransform.dump(level);
    }
     */
    
}
