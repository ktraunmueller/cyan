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
class TexturedRasterizer extends TriangleRasterizer {
    
    TexturedGradients    mGradients;
    TexturedEdge         mTopBottom, mTopMiddle, mMiddleBottom;
    
    /** 
     * Default constructor.
     */
    TexturedRasterizer(SceneGraphRenderer renderer) {
        super(renderer);                
        mGradients    = new TexturedGradients();
        mTopBottom    = new TexturedEdge();
        mTopMiddle    = new TexturedEdge();
        mMiddleBottom = new TexturedEdge();
    }
    
    /**
     * Returns the rasterizer type (GOURAUD_TEXTURED).
     */
    int getType() {
        return TriangleRasterizer.TEXTURED;
    }
    
    /**
     * Rasterizes and texture-maps the given triangle.
     */
    void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2) {
        if (p0.mFace.mMaterial.mTexture1Map == null)
            return; 
        
        sortAndStore(p0, p1, p2);
        
        if (p0.mFace.mMaterial != null) {
            TextureMap reflectionMap = p0.mFace.mMaterial.mReflectionMap;
            if (reflectionMap != null && reflectionMap.mTexture != null && 
                reflectionMap.mTexture.getPixels() != null) {
                rasterize(p0, p1, p2, reflectionMap);
                return;
            }
        }

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
        
        int alpha = (int)(255.0 * (1.0f - p0.mFace.mMaterial.mTransparency));
        
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
        if (alpha == 255) {
            while (height-- > 0) {
                drawScanLine(id, pixels, zBuf, idBuf, w,
                             mGradients, left, right, 
                             texPixels, texWidth, texHeight);
                mTopMiddle.step(); 
                mTopBottom.step();
            }        
        }
        else {
            while (height-- > 0) {
                drawScanLineTransparent(alpha, id, pixels, zBuf, idBuf, w,
                             mGradients, left, right, 
                             texPixels, texWidth, texHeight);
                mTopMiddle.step(); 
                mTopBottom.step();
            }
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
        if (alpha == 255) {
            while (height-- > 0) {
                drawScanLine(id, pixels, zBuf, idBuf, w,
                             mGradients, left, right, 
                             texPixels, texWidth, texHeight);
                mMiddleBottom.step(); 
                mTopBottom.step();
            }
        }
        else {
            while (height-- > 0) {
                drawScanLineTransparent(alpha, id, pixels, zBuf, idBuf, w,
                             mGradients, left, right, 
                             texPixels, texWidth, texHeight);
                mMiddleBottom.step(); 
                mTopBottom.step();
            }
        }
    }    
    
    /**
     *
     */
    void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2, TextureMap envmap) { 
        int[] envPixels = envmap.mTexture.getPixels();
        int envWidth = envmap.mTexture.getWidth();
        int envHeight = envmap.mTexture.getHeight();

        mGradients.init(mRenderer.mScene.mAmbient, mP0, mP1, mP2, true);
        mTopBottom.init(mGradients, mP0, mP2, 0, true);
        mTopMiddle.init(mGradients, mP0, mP1, 0, true);
        mMiddleBottom.init(mGradients, mP1, mP2, 1, true);

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
        
        int alpha = (int)(255.0 * (1.0f - p0.mFace.mMaterial.mTransparency));
        
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
        if (alpha == 255) {
            while (height-- > 0) {
                drawScanLineEnvMapped(id, pixels, zBuf, idBuf, w,
                                      mGradients, left, right, 
                                      texPixels, texWidth, texHeight,
                                      envPixels, envWidth, envHeight);
                mTopMiddle.step(); 
                mTopBottom.step();
            }        
        }
        else {
            while (height-- > 0) {
                drawScanLineEnvMappedTransparent(alpha, id, pixels, zBuf, idBuf, w,
                                                 mGradients, left, right, 
                                                 texPixels, texWidth, texHeight,
                                                 envPixels, envWidth, envHeight);
                mTopMiddle.step(); 
                mTopBottom.step();
            }
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
        if (alpha == 255) {
            while (height-- > 0) {
                drawScanLineEnvMapped(id, pixels, zBuf, idBuf, w,
                                      mGradients, left, right, 
                                      texPixels, texWidth, texHeight,
                                      envPixels, envWidth, envHeight);
                mMiddleBottom.step(); 
                mTopBottom.step();
            }
        }
        else {
            while (height-- > 0) {
                drawScanLineEnvMappedTransparent(alpha, id, pixels, zBuf, idBuf, w,
                                                 mGradients, left, right, 
                                                 texPixels, texWidth, texHeight,
                                                 envPixels, envWidth, envHeight);
                mMiddleBottom.step(); 
                mTopBottom.step();
            }
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
        float cosAlpha = left.mCosAlpha;
        float oneOverW = left.mOneOverW;
        float UOverW   = left.mUOverW;
        float VOverW   = left.mVOverW;                
        int maxU = textureWidth - 1;
        int maxV = textureHeight - 1;
        
        while (width-- > 0) {            
            if (Z < zBuf[offset]) {      
                float w = 1.0f / oneOverW;
                int u = (int)(UOverW * w * textureWidth);
                u = Math.max(Math.min(u, maxU), 0);
                int v = (int)(VOverW * w * textureHeight);
                v = Math.max(Math.min(v, maxV), 0);
            
                zBuf[offset]   = Z;
                int cosAlphaInt = (int)(cosAlpha * 255.0f);
                int texel = texture[(v * textureWidth) + u];
                int red = (((texel & 0x00FF0000) >> 16) * cosAlphaInt) >> 8;
                int green = (int)(((texel & 0x0000FF00) >> 8) * cosAlphaInt) >> 8;
                int blue = (int)((texel & 0x000000FF) * cosAlphaInt) >> 8;
                pixels[offset] = (0xFF000000 | (red << 16) | (green << 8) | blue);
                idBuf[offset]  = id;
            }
            offset++;
            Z           += gradients.mdZOverdX;
            cosAlpha    += gradients.mdCosAlphaOverdX;
            oneOverW    += gradients.mdOneOverWdX;
            UOverW      += gradients.mdUOverWdX;
            VOverW      += gradients.mdVOverWdX;
        }
    }    
    
    /**
     *
     */
    final void drawScanLineTransparent(int alpha, byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth, 
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
        
        int oneMinusAlpha = 255 - alpha;
        int texel, color;
        int screenColor, screenRed, screenGreen, screenBlue;
        
        while (width-- > 0) {            
            if (Z < zBuf[offset]) {      
                float w = 1.0f / oneOverW;
                int u = (int)(UOverW * w * textureWidth);
                u = Math.max(Math.min(u, maxU), 0);
                int v = (int)(VOverW * w * textureHeight);
                v = Math.max(Math.min(v, maxV), 0);
            
                texel = texture[(v * textureWidth) + u];
                
                screenColor = pixels[offset];
                screenRed = oneMinusAlpha * ((screenColor & 0x00FF0000) >> 16);
                screenRed = (((texel & 0x00FF0000) >> 16) * alpha + screenRed) >> 8;
                screenGreen = oneMinusAlpha * ((screenColor & 0x0000FF00) >> 8);
                screenGreen = (((texel & 0x0000FF00) >> 8) * alpha + screenGreen) >> 8;
                screenBlue = oneMinusAlpha * (screenColor & 0x000000FF);                
                screenBlue = ((texel & 0x000000FF) * alpha + screenBlue) >> 8;
                
                color = 0xFF000000 | (screenRed << 16) | (screenGreen << 8) | (screenBlue);
                
                zBuf[offset]   = Z;
                pixels[offset] = color;
                idBuf[offset]  = id;
            }
            offset++;
            Z        += gradients.mdZOverdX;
            oneOverW += gradients.mdOneOverWdX;
            UOverW   += gradients.mdUOverWdX;
            VOverW   += gradients.mdVOverWdX;
        }
    }
    
    /**
     *
     */
    final void drawScanLineEnvMapped(byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth, 
                                     TexturedGradients gradients, TexturedEdge left, TexturedEdge right, 
                                     int[] texture, int textureWidth, int textureHeight,
                                     int[] envPixels, int envWidth, int envHeight) {
        
        int xStart = left.mX;
        int width = right.mX - xStart;

        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;
        float cosAlpha = left.mCosAlpha;
        float oneOverW = left.mOneOverW;
        float UOverW   = left.mUOverW;
        float VOverW   = left.mVOverW;                       
        float envUOverW = left.mEnvUOverW;
        float envVOverW = left.mEnvVOverW;       
        
        int u, envU;
        int v, envV;
        int maxEnvU = envWidth - 1;
        int maxEnvV = envHeight - 1;
        int maxU = textureWidth - 1;
        int maxV = textureHeight - 1;
        int envTexel;
        int texel, color;
        int envred, envgreen, envblue;
        
        while (width-- > 0) {
            if (Z < zBuf[offset]) {                                                                
                float w = 1.0f / oneOverW;
                
                u = (int)(UOverW * w * textureWidth);
                u = Math.max(Math.min(u, maxU), 0);
                v = (int)(VOverW * w * textureHeight);
                v = Math.max(Math.min(v, maxV), 0);
                texel = texture[(v * textureWidth) + u];
                
                int cosAlphaInt = (int)(cosAlpha * 255.0f);
                int red = (((texel & 0x00FF0000) >> 16) * cosAlphaInt) >> 8;
                int green = (int)(((texel & 0x0000FF00) >> 8) * cosAlphaInt) >> 8;
                int blue = (int)((texel & 0x000000FF) * cosAlphaInt) >> 8;
                
                envU = (int)(envUOverW * w * envWidth);
                envU = Math.max(Math.min(u, maxEnvU), 0);
                envV = (int)(envVOverW * w * envHeight);
                envV = Math.max(Math.min(v, maxEnvV), 0);
                envTexel = envPixels[(envV * envWidth) + envU];
                                                
                envred = (red + (((envTexel & 0x00FF0000) >> 16) >> 3));
                envred = Math.max(Math.min(envred, 255), 0);
                envgreen = (green + (((envTexel & 0x0000FF00) >> 8) >> 3));
                envgreen = Math.max(Math.min(envgreen, 255), 0);
                envblue = (blue + ((envTexel & 0x000000FF) >> 2));
                envblue = Math.max(Math.min(envblue, 255), 0);
                
                color = 0xFF000000 | (envred << 16) | (envgreen << 8) | envblue;
                
                zBuf[offset]   = Z;
                pixels[offset] = color;
                idBuf[offset]  = id;
            }
            offset++;
            Z           += gradients.mdZOverdX;
            cosAlpha    += gradients.mdCosAlphaOverdX;
            oneOverW    += gradients.mdOneOverWdX;
            UOverW      += gradients.mdUOverWdX;
            VOverW      += gradients.mdVOverWdX;
            envUOverW   += gradients.mdEnvUOverWdX;
            envVOverW   += gradients.mdEnvVOverWdX;
        }
    }
    
    /**
     *
     */
    final void drawScanLineEnvMappedTransparent(int alpha, byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth, 
                                                TexturedGradients gradients, TexturedEdge left, TexturedEdge right, 
                                                int[] texture, int textureWidth, int textureHeight,
                                                int[] envPixels, int envWidth, int envHeight) {
        
        int xStart = left.mX;
        int width = right.mX - xStart;

        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;
        float oneOverW = left.mOneOverW;
        float UOverW   = left.mUOverW;
        float VOverW   = left.mVOverW;                       
        float envUOverW = left.mEnvUOverW;
        float envVOverW = left.mEnvVOverW;       
        
        int u, envU;
        int v, envV;
        int maxEnvU = envWidth - 1;
        int maxEnvV = envHeight - 1;
        int maxU = textureWidth - 1;
        int maxV = textureHeight - 1;
        
        int envTexel;
        int texel, color;
        
        int envred, envgreen, envblue;
        int oneMinusAlpha = 255 - alpha;
        int screenColor, screenRed, screenGreen, screenBlue;
        
        while (width-- > 0) {
            float w = 1.0f / oneOverW;                        

            if (Z < zBuf[offset]) {                                                                
                u = (int)(UOverW * w * textureWidth);
                u = Math.max(Math.min(u, maxU), 0);
                v = (int)(VOverW * w * textureHeight);
                v = Math.max(Math.min(v, maxV), 0);
                texel = texture[(v * textureWidth) + u];
                
                envU = (int)(envUOverW * w * envWidth);
                envU = Math.max(Math.min(u, maxEnvU), 0);
                envV = (int)(envVOverW * w * envHeight);
                envV = Math.max(Math.min(v, maxEnvV), 0);
                envTexel = envPixels[(envV * envWidth) + envU];
                                                
                envred = (((texel & 0x00FF0000) >> 16) + (((envTexel & 0x00FF0000) >> 16) >> 3));
                envred = Math.max(Math.min(envred, 255), 0);
                envgreen = (((texel & 0x0000FF00) >> 8) + (((envTexel & 0x0000FF00) >> 8) >> 3));
                envgreen = Math.max(Math.min(envgreen, 255), 0);
                envblue = ((texel & 0x000000FF) + ((envTexel & 0x000000FF) >> 3));
                envblue = Math.max(Math.min(envblue, 255), 0);
                
                screenColor = pixels[offset];
                screenRed = oneMinusAlpha * ((screenColor & 0x00FF0000) >> 16);
                screenRed = (envred * alpha + screenRed) >> 8;
                screenGreen = oneMinusAlpha * ((screenColor & 0x0000FF00) >> 8);
                screenGreen = (envgreen * alpha + screenGreen) >> 8;
                screenBlue = oneMinusAlpha * (screenColor & 0x000000FF);                
                screenBlue = (envblue * alpha + screenBlue) >> 8;
                color = 0xFF000000 | (screenRed << 16) | (screenGreen << 8) | (screenBlue);
                
                zBuf[offset]   = Z;
                pixels[offset] = color;
                idBuf[offset]  = id;
            }
            offset++;
            Z        += gradients.mdZOverdX;
            oneOverW += gradients.mdOneOverWdX;
            UOverW   += gradients.mdUOverWdX;
            VOverW   += gradients.mdVOverWdX;
            envUOverW += gradients.mdEnvUOverWdX;
            envVOverW += gradients.mdEnvVOverWdX;
        }
    }
}
