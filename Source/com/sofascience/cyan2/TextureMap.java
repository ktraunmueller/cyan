/*
 * TextureMap.java
 *
 * Created on September 27, 2002, 1:51 PM
 */

package com.sofascience.cyan2;

import java.net.URL;
import com.sofascience.cyan2.vecmath.Color3f;

/**
 *
 * @author  Karl Traunmueller
 */
class TextureMap extends SceneGraphObject {
    
    String    mMapName;
    URL       mUrl;
    Texture   mTexture;
    int       mFlags;
    float     mPercent;
    float     mBlur;
    float[]   mScale;
    float[]   mOffset;
    float     mRotation;
    Color3f   mTint1;
    Color3f   mTint2;
    Color3f   mTintR;
    Color3f   mTintG;
    Color3f   mTintB;

    /**
     *
     */
    TextureMap() {        
        mFlags = 0x10;
        mPercent = 1.0f;
        
        mScale = new float[2];
        mScale[0] = 1.0f;
        mScale[1] = 1.0f;
        mOffset = new float[2];
        
        mTint1 = new Color3f();
        mTint2 = new Color3f();
        mTintR = new Color3f();
        mTintG = new Color3f();
        mTintB = new Color3f();
    }
    
    /**
     *
     */
    TextureMap(URL url) {
        this();
        mUrl = url;
        load();
    }
    
    /**
     *
     */
    final void load() {
        mTexture = new Texture(mUrl);
    }
    
    /**
     *
     */
    final void load(Canvas3D canvas) {
        mTexture = new Texture(mUrl);
        if (canvas != null)
            canvas.textureLoaded();
    }
    
    /**
     *
     */
    final Texture getTexture() {
        return mTexture;
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- TEXTURE MAP");
        indent(level + 1); System.out.println("name: " + mMapName);
        indent(level + 1); System.out.println("flags: " + mFlags);
        indent(level + 1); System.out.println("percent: " + mPercent);
        indent(level + 1); System.out.println("blur: " + mBlur);
        indent(level + 1); System.out.println("scale: (" + mScale[0] + ", " + mScale[1] + ")");
        indent(level + 1); System.out.println("offset: (" + mOffset[0] + ", " + mOffset[1] + ")");
        indent(level + 1); System.out.println("rotation: " + mRotation);
        indent(level + 1); System.out.print("tint 1: "); dumpColor(0, mTint1);
        indent(level + 1); System.out.print("tint 2: "); dumpColor(0, mTint2);
        indent(level + 1); System.out.print("tint R: "); dumpColor(0, mTintR);
        indent(level + 1); System.out.print("tint G: "); dumpColor(0, mTintG);
        indent(level + 1); System.out.print("tint B: "); dumpColor(0, mTintB);
    }
     */
}
