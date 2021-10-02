/*
 * GouraudRasterizer.java
 *
 * Created on June 3, 2002, 9:02 PM
 */

package com.sofascience.cyan2;

import java.awt.Dimension;

/**
 * A Gouraud-shading triangle rasterizer.s
 *
 * @author  Karl Traunmueller
 */
class GouraudRasterizer extends TriangleRasterizer {
    
    GouraudGradients    mGradients;
    GouraudEdge         mTopBottom, mTopMiddle, mMiddleBottom;
    
    /** 
     * Default constructor.
     */
    GouraudRasterizer(SceneGraphRenderer renderer) {
        super(renderer);
        mGradients    = new GouraudGradients();
        mTopBottom    = new GouraudEdge();
        mTopMiddle    = new GouraudEdge();
        mMiddleBottom = new GouraudEdge();
    }
    
    /**
     * Returns the rasterizer type (GOURAUD).
     */
    int getType() {
        return TriangleRasterizer.GOURAUD;
    }
    
    /**
     * Rasterizes and texture-maps the given triangle.
     */
    void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2) {
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

        Dimension viewportDimension = mRenderer.getPixelDimension();
        int w = viewportDimension.width;
        int h = viewportDimension.height;
        
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
        
        GouraudEdge left, right;
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
                drawScanLine(id, pixels, zBuf, idBuf, w, mGradients, left, right);
                mTopMiddle.step(); 
                mTopBottom.step();
            }        
        }
        else {
            while (height-- > 0) {
                drawScanLineTransparent(alpha, id, pixels, zBuf, idBuf, w, mGradients, left, right);
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
                drawScanLine(id, pixels, zBuf, idBuf, w, mGradients, left, right);
                mMiddleBottom.step(); 
                mTopBottom.step();
            }
        }
        else {
            while (height-- > 0) {
                drawScanLineTransparent(alpha, id, pixels, zBuf, idBuf, w, mGradients, left, right);
                mMiddleBottom.step(); 
                mTopBottom.step();
            }
        }
    }    

    /**
     * Rasterizes and texture-maps the given triangle.
     */
    void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2, TextureMap envmap) {        
        int[] envPixels = envmap.mTexture.getPixels();
        int envWidth = envmap.mTexture.getWidth();
        int envHeight = envmap.mTexture.getHeight();
        
        mGradients.init(mRenderer.mScene.mAmbient, mP0, mP1, mP2, true);
        mTopBottom.init(mGradients, mP0, mP2, 0, true);
        mTopMiddle.init(mGradients, mP0, mP1, 0, true);
        mMiddleBottom.init(mGradients, mP1, mP2, 1, true);

        Dimension viewportDimension = mRenderer.getPixelDimension();
        int w = viewportDimension.width;
        int h = viewportDimension.height;                
        
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
        
        GouraudEdge left, right;
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
                drawScanLineEnvMapped(id, pixels, zBuf, idBuf, w, mGradients, left, right, 
                                      envPixels, envWidth, envHeight);
                mTopMiddle.step(); 
                mTopBottom.step();
            }
        }
        else {
            while (height-- > 0) {
                drawScanLineEnvMappedTransparent(alpha, id, pixels, zBuf, idBuf, w, mGradients, left, right, 
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
                drawScanLineEnvMapped(id, pixels, zBuf, idBuf, w, mGradients, left, right, 
                                      envPixels, envWidth, envHeight);
                mMiddleBottom.step(); 
                mTopBottom.step();
            }
        }
        else {            
            while (height-- > 0) {
                drawScanLineEnvMappedTransparent(alpha, id, pixels, zBuf, idBuf, w, mGradients, left, right, 
                                                 envPixels, envWidth, envHeight);
                mMiddleBottom.step(); 
                mTopBottom.step();
            }
        }
    } 
    
    /**
     *
     */
    final void drawScanLine(byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth, 
                            GouraudGradients gradients, GouraudEdge left, GouraudEdge right) {

        int xStart = left.mX;
        int width = right.mX - xStart;
        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;                
        float red = left.mRed;
        float green = left.mGreen;
        float blue = left.mBlue;
        int color = 0;
        
        while (width-- > 0) {            
            if (Z < zBuf[offset]) {
                zBuf[offset] = Z;
                color = 0xFF000000 | (((int)red) << 16) | (((int)green) << 8) | (int)blue;
                pixels[offset] = color;
                idBuf[offset] = id;
            }
            offset++;
            Z += gradients.mdZOverdX;
            red += gradients.mdRedOverdX;            
            green += gradients.mdGreenOverdX;
            blue += gradients.mdBlueOverdX;
        }
    }        
    
    /**
     *
     */
    final void drawScanLineTransparent(int alpha, byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth, 
                                       GouraudGradients gradients, GouraudEdge left, GouraudEdge right) {

        int xStart = left.mX;
        int width = right.mX - xStart;
        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;                
        float red = left.mRed;
        float green = left.mGreen;
        float blue = left.mBlue;
        int color = 0;
        
        int oneMinusAlpha = 255 - alpha;
        int screenColor, screenRed, screenGreen, screenBlue;
        
        while (width-- > 0) {            
            if (Z < zBuf[offset]) {
                screenColor = pixels[offset];
                screenRed = oneMinusAlpha * ((screenColor & 0x00FF0000) >> 16);
                screenRed = ((int)red * alpha + screenRed) >> 8;
                screenGreen = oneMinusAlpha * ((screenColor & 0x0000FF00) >> 8);
                screenGreen = ((int)green * alpha + screenGreen) >> 8;
                screenBlue = oneMinusAlpha * (screenColor & 0x000000FF);                
                screenBlue = ((int)blue * alpha + screenBlue) >> 8;
                color = 0xFF000000 | (screenRed << 16) | (screenGreen << 8) | (screenBlue);
                pixels[offset] = color;
                idBuf[offset] = id;
            }
            offset++;
            Z += gradients.mdZOverdX;
            red += gradients.mdRedOverdX;            
            green += gradients.mdGreenOverdX;
            blue += gradients.mdBlueOverdX;
        }
    }        

    /**
     *
     */
    final void drawScanLineEnvMapped(byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth, 
                                     GouraudGradients gradients, GouraudEdge left, GouraudEdge right,
                                     int[] envPixels, int envWidth, int envHeight) {

        int xStart = left.mX;
        int width = right.mX - xStart;
        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;        
        
        float red = left.mRed;
        float green = left.mGreen;
        float blue = left.mBlue;
        int color = 0;
        
        float oneOverW = left.mOneOverW;
        float envUOverW = left.mEnvUOverW;
        float envVOverW = left.mEnvVOverW;       
        int texel;
        
        float w;
        int u;
        int v;
        int maxU = envWidth - 1;
        int maxV = envHeight - 1;
        int envred, envgreen, envblue;
        
        while (width-- > 0) {            
            if (Z < zBuf[offset]) {
                w = 1.0f / oneOverW;
                u = (int)(envUOverW * w * envWidth);
                u = Math.max(Math.min(u, maxU), 0);
                v = (int)(envVOverW * w * envHeight);
                v = Math.max(Math.min(v, maxV), 0);
                            
                texel = envPixels[(v * envWidth) + u];
                envred = ((int)red + (((texel & 0x00FF0000) >> 16) >> 3));
                envred = Math.max(Math.min(envred, 255), 0);
                envgreen = ((int)green + (((texel & 0x0000FF00) >> 8) >> 3));
                envgreen = Math.max(Math.min(envgreen, 255), 0);
                envblue = ((int)blue + ((texel & 0x000000FF) >> 3));
                envblue = Math.max(Math.min(envblue, 255), 0);
                
                color = 0xFF000000 | (envred << 16) | (envgreen << 8) | envblue;                
                
                zBuf[offset] = Z;
                pixels[offset] = color;
                idBuf[offset] = id;
            }
            offset++;
            Z += gradients.mdZOverdX;
            oneOverW += gradients.mdOneOverWdX;
            red += gradients.mdRedOverdX;            
            green += gradients.mdGreenOverdX;
            blue += gradients.mdBlueOverdX;
            envUOverW += gradients.mdEnvUOverWdX;
            envVOverW += gradients.mdEnvVOverWdX;
        }
    }
    
    /**
     *
     */
    final void drawScanLineEnvMappedTransparent(int alpha, byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth, 
                                                GouraudGradients gradients, GouraudEdge left, GouraudEdge right,
                                                int[] envPixels, int envWidth, int envHeight) {

        int xStart = left.mX;
        int width = right.mX - xStart;
        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;        
        
        float red = left.mRed;
        float green = left.mGreen;
        float blue = left.mBlue;
        int color = 0;
        
        float oneOverW = left.mOneOverW;
        float envUOverW = left.mEnvUOverW;
        float envVOverW = left.mEnvVOverW;       
        int texel;
        
        float w;
        int u;
        int v;
        int maxU = envWidth - 1;
        int maxV = envHeight - 1;
        
        int envred, envgreen, envblue;        
        int oneMinusAlpha = 255 - alpha;
        int screenColor, screenRed, screenGreen, screenBlue;
        
        while (width-- > 0) {            
            if (Z < zBuf[offset]) {
                    zBuf[offset] = Z;
                
                w = 1.0f / oneOverW;
                u = (int)(envUOverW * w * envWidth);
                u = Math.max(Math.min(u, maxU), 0);
                v = (int)(envVOverW * w * envHeight);
                v = Math.max(Math.min(v, maxV), 0);
            
                texel = envPixels[(v * envWidth) + u];
                envred = ((int)red + (((texel & 0x00FF0000) >> 16) >> 3));
                envred = Math.max(Math.min(envred, 255), 0);
                envgreen = ((int)green + (((texel & 0x0000FF00) >> 8) >> 3));
                envgreen = Math.max(Math.min(envgreen, 255), 0);
                envblue = ((int)blue + ((texel & 0x000000FF) >> 3));
                envblue = Math.max(Math.min(envblue, 255), 0);
                
                screenColor = pixels[offset];
                screenRed = oneMinusAlpha * ((screenColor & 0x00FF0000) >> 16);
                screenRed = (envred * alpha + screenRed) >> 8;
                screenGreen = oneMinusAlpha * ((screenColor & 0x0000FF00) >> 8);
                screenGreen = (envgreen * alpha + screenGreen) >> 8;
                screenBlue = oneMinusAlpha * (screenColor & 0x000000FF);                
                screenBlue = (envblue * alpha + screenBlue) >> 8;
                color = 0xFF000000 | (screenRed << 16) | (screenGreen << 8) | (screenBlue);

                pixels[offset] = color;
                idBuf[offset] = id;
            }
            offset++;
            Z += gradients.mdZOverdX;
            oneOverW += gradients.mdOneOverWdX;
            red += gradients.mdRedOverdX;            
            green += gradients.mdGreenOverdX;
            blue += gradients.mdBlueOverdX;
            envUOverW += gradients.mdEnvUOverWdX;
            envVOverW += gradients.mdEnvVOverWdX;
        }
    }
    
}
