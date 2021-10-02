/*
 * Material.java
 *
 * Created on February 23, 2002, 3:12 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;

/**
 * Material describes material properties.
 *
 * @author  Karl Traunmueller
 */
class Material extends NamedObject {
    
    /** shading modes */
    static final int WIRE_FRAME   = 0;
    static final int FLAT         = 1; 
    static final int GOURAUD      = 2; 
    static final int PHONG        = 3; 
    static final int METAL        = 4;
    
    /** defaults */
    static final float AMBIENT_DEFAULT    = 0.588235f;
    static final float DIFFUSE_DEFAULT    = 0.588235f;
    static final float SPECULAR_DEFAULT   = 0.898039f;
    static final float SHININESS_DEFAULT  = 0.1f;
    static final float WIRE_SIZE_DEFAULT  = 1.0f;
    static final int   SHADING_DEFAULT    = GOURAUD;

    Color4f       mAmbient;
    Color4f       mDiffuse;
    Color4f       mSpecular;
    float         mShininess;
    float         mShinStrength;
    boolean       mUseBlur;
    float         mBlur;
    float         mTransparency;
    float         mFalloff;
    boolean       mAdditive;
    boolean       mUseFalloff; 
    boolean       mSelfIllum;
    int           mShading;
    boolean       mSoften;
    boolean       mFaceMap;
    boolean       mTwoSided;
    boolean       mMapDecal;
    boolean       mUseWire;
    boolean       mUseWireAbs;
    float         mWireSize;
    TextureMap    mTexture1Map;
    TextureMap    mTexture1Mask;
    TextureMap    mTexture2Map;
    TextureMap    mTexture2Mask;
    TextureMap    mOpacityMap;
    TextureMap    mOpacityMask;
    TextureMap    mBumpMap;
    TextureMap    mBumpMask;
    TextureMap    mSpecularMap;
    TextureMap    mSpecularMask;
    TextureMap    mShininessMap;
    TextureMap    mShininessMask;
    TextureMap    mSelfIllumMap;
    TextureMap    mSelfIllumMask;
    TextureMap    mReflectionMap;
    TextureMap    mReflectionMask;    

    /**
     * Default constructor.
     */
    Material(String name) {
        super(name);
        mAmbient = new Color4f(AMBIENT_DEFAULT, AMBIENT_DEFAULT, AMBIENT_DEFAULT, 1.0f);
        mDiffuse = new Color4f(DIFFUSE_DEFAULT, DIFFUSE_DEFAULT, DIFFUSE_DEFAULT, 1.0f);
        mSpecular = new Color4f(SPECULAR_DEFAULT, SPECULAR_DEFAULT, SPECULAR_DEFAULT, 1.0f);        
        mShininess = SHININESS_DEFAULT;
        mWireSize = WIRE_SIZE_DEFAULT;
        mShading = SHADING_DEFAULT;
    }
    
    /**
     *
     */
    final void loadTextureMaps(Scene scene, Canvas3D canvas) {
        if (mTexture1Map != null) {
            TextureMap map = scene.getTextureMapByName(mTexture1Map.mMapName);
            if (map.mTexture != null) {
                mTexture1Map.mTexture = map.mTexture;
            }
            else {
                mTexture1Map.load(canvas);
                scene.setTextureMap(mTexture1Map.mMapName, mTexture1Map);
            }
        }
        if (mReflectionMap != null) {
            TextureMap map = scene.getTextureMapByName(mReflectionMap.mMapName);
            if (map.mTexture != null) {
                mReflectionMap.mTexture = map.mTexture;
            }
            else {
                mReflectionMap.load(canvas);
                scene.setTextureMap(mReflectionMap.mMapName, mReflectionMap);
            }
        }
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- MATERIAL " + mName + " ----");
        indent(level + 1); System.out.println("ambient: " + mAmbient);
        indent(level + 1); System.out.println("diffuse: " + mDiffuse);
        indent(level + 1); System.out.println("specular: " + mSpecular);
        indent(level + 1); System.out.println("shininess: " + mShininess);
        indent(level + 1); System.out.println("shininess strength: " + mShinStrength);
        indent(level + 1); System.out.println("use blur: " + mUseBlur);
        indent(level + 1); System.out.println("blur: " + mBlur);
        indent(level + 1); System.out.println("transparency: " + mTransparency);
        indent(level + 1); System.out.println("falloff: " + mFalloff);
        indent(level + 1); System.out.println("additive: " + mAdditive);
        indent(level + 1); System.out.println("use falloff: " + mUseFalloff);
        indent(level + 1); System.out.println("self illumination: " + mSelfIllum);
        indent(level + 1); System.out.println("shading type: " + mShading);
        indent(level + 1); System.out.println("soften: " + mSoften);
        indent(level + 1); System.out.println("face map: " + mFaceMap);
        indent(level + 1); System.out.println("two sided: " + mTwoSided);
        indent(level + 1); System.out.println("map decal: " + mMapDecal);
        indent(level + 1); System.out.println("use wire: " + mUseWire);
        indent(level + 1); System.out.println("wire abs: " + mUseWireAbs);
        indent(level + 1); System.out.println("wire size: " + mWireSize);
        mTexture1Map.dump(level + 1);
        mTexture1Mask.dump(level + 1);
        mTexture2Map.dump(level + 1);
        mTexture2Mask.dump(level + 1);
        mOpacityMap.dump(level + 1);
        mOpacityMask.dump(level + 1);
        mBumpMap.dump(level + 1);
        mBumpMask.dump(level + 1);
        mSpecularMap.dump(level + 1);
        mSpecularMask.dump(level + 1);
        mShininessMap.dump(level + 1);
        mShininessMask.dump(level + 1);
        mSelfIllumMap.dump(level + 1);
        mSelfIllumMask.dump(level + 1);
        mReflectionMap.dump(level + 1);
        mReflectionMask.dump(level + 1);
        mAutoreflMap.dump(level + 1);
    }
     */
    
}
