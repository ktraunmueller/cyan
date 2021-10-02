/*
 * GouraudTexturedRasterizer.java
 *
 * Created on June 3, 2002, 9:36 PM
 */

package com.sofascience.cyan2;

import java.awt.Dimension;

/**
 *
 * @author  Karl Traunmueller
 */
class ShadowRasterizer extends TexturedRasterizer {
    
    /** 
     * Default constructor.
     */
    ShadowRasterizer(SceneGraphRenderer renderer) {
        super(renderer);                
    }
    
    /**
     * Rasterizes and texture-maps the given triangle.
     */
    void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2) {
        if (p0.mFace.mMaterial.mTexture1Map == null)
            return; 
        
        sortAndStore(p0, p1, p2);
        
        mGradients.init(mRenderer.mScene.mAmbient, mP0, mP1, mP2, false);
        mTopBottom.init(mGradients, mP0, mP2, 0, false);
        mTopMiddle.init(mGradients, mP0, mP1, 0, false);
        mMiddleBottom.init(mGradients, mP1, mP2, 1, false);                       
        
        int w = mRenderer.getPixelDimension().width;
        int h = mRenderer.getPixelDimension().height;
                
        Texture tex = mP0.mFace.mMaterial.mTexture1Map.getTexture();
        if (tex == null)
            return;
        
        int[] texPixels = tex.getPixels();
        if (texPixels == null)
            return;        
        
        int texWidth = tex.getWidth();
        int texHeight = tex.getHeight();
        
        float[] zBuf = mRenderer.getZBuffer();
        int[] pixels = mRenderer.getPixels();
        byte[] idBuf = mRenderer.getIdBuffer();

        byte id = (byte)p0.mFace.mMesh.getId();
        
        // calculate mid point on major edge
        float p0x = FixedPoint.fixed284ToFloat(mP0.mX);
        float p0y = FixedPoint.fixed284ToFloat(mP0.mY);
        float p0z = mP0.mViewCoords.z;
        float p1x = FixedPoint.fixed284ToFloat(mP1.mX);
        float p1y = FixedPoint.fixed284ToFloat(mP1.mY);
        float p1z = mP1.mViewCoords.z;
        float p2x = FixedPoint.fixed284ToFloat(mP2.mX);
        float p2y = FixedPoint.fixed284ToFloat(mP2.mY);
        float p2z = mP2.mViewCoords.z;               
        float p3x = p2x - (p2x - p0x) / (p2y - p0y) * (p2y - p1y);
        
        TexturedEdge left, right;
        boolean bMiddleIsLeft;

        if (p3x < p1x) {
            bMiddleIsLeft = false;
            left = mTopBottom; 
            right = mTopMiddle;
        } 
        else {
            bMiddleIsLeft = true;
            left = mTopMiddle; 
            right = mTopBottom;
        }
      
        int height = mTopMiddle.mHeight;
        while (height-- > 0) {
            drawScanLine(id, pixels, zBuf, idBuf, w,
                         mGradients, left, right, 
                         texPixels, texWidth, texHeight);
            mTopMiddle.step(); 
            mTopBottom.step();
        }        

        if (bMiddleIsLeft) {
            left = mMiddleBottom; 
            right = mTopBottom;
        } 
        else {
            left = mTopBottom; 
            right = mMiddleBottom;
        }

        height = mMiddleBottom.mHeight;
        while (height-- > 0) {
            drawScanLine(id, pixels, zBuf, idBuf, w,
                         mGradients, left, right, 
                         texPixels, texWidth, texHeight);
            mMiddleBottom.step(); 
            mTopBottom.step();
        }
    }    
    
    /**
     *
     */
    void drawScanLine(byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth, 
                      TexturedGradients gradients, TexturedEdge left, TexturedEdge right, 
                      int[] texture, int textureWidth, int textureHeight) {
        
        int xStart = left.mX;
        int width = right.mX - xStart;

        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;
        float oneOverW = left.mOneOverW;
        float UOverW   = left.mUOverW;
        float VOverW   = left.mVOverW;                
        int maxU = textureWidth - 1;
        int maxV = textureHeight - 1;
        int pixel, texel;
        int red, green, blue;
        
        while (width-- > 0) {            
            if (Z < zBuf[offset]) {      
                float w = 1.0f / oneOverW;
                int u = (int)(UOverW * w * textureWidth);
                u = Math.max(Math.min(u, maxU), 0);
                int v = (int)(VOverW * w * textureHeight);
                v = Math.max(Math.min(v, maxV), 0);
            
                texel = texture[(v * textureWidth) + u];
                if (texel != 0xFFFFFFFF) {
                    pixel = pixels[offset];

                    red = (((pixel & 0x00FF0000) >> 16) * ((texel & 0x00FF0000) >> 16)) >> 8;
                    red = Math.max(red, 0);
                    green = (((pixel & 0x0000FF00) >> 8) * ((texel & 0x0000FF00) >> 8)) >> 8;
                    green = Math.max(green, 0);
                    blue = ((pixel & 0x000000FF) * (texel & 0x000000FF)) >> 8;
                    blue = Math.max(blue, 0);
                    
                    zBuf[offset]   = Z;
                    pixels[offset] = 0xFF000000 | (red << 16) | (green << 8) | blue;
                    idBuf[offset]  = id;
                }
            }
            offset++;
            Z        += gradients.mdZOverdX;
            oneOverW += gradients.mdOneOverWdX;
            UOverW   += gradients.mdUOverWdX;
            VOverW   += gradients.mdVOverWdX;
        }
    }    
    
}



