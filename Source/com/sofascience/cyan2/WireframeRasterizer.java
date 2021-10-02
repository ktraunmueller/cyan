/*
 * WireframeRasterizer.java
 *
 * Created on June 3, 2002, 12:27 AM
 */

package com.sofascience.cyan2;

import java.awt.Dimension;

/**
 * A wireframe triangle rasterizer.
 *
 * @author  Karl Traunmueller
 */
class WireframeRasterizer extends TriangleRasterizer {
    
    GouraudGradients    mGradients;
    GouraudEdge         mTopBottom, mTopMiddle, mMiddleBottom;
    
    /** 
     * Default constructor.
     */
    WireframeRasterizer(SceneGraphRenderer renderer) {
        super(renderer);
        mGradients    = new GouraudGradients();
        mTopBottom    = new GouraudEdge();
        mTopMiddle    = new GouraudEdge();
        mMiddleBottom = new GouraudEdge();
    }
    
    /**
     * Returns the rasterizer type (WIREFRAME).
     */
    final int getType() {
        return TriangleRasterizer.WIREFRAME;
    }
    
    /**
     *
     */
    void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2, TextureMap envmap) {
    }
    
    /**
     * 
     */
    void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2) {        
        sortAndStore(p0, p1, p2);
                
        /*
        mGradients.init(mRenderer.mScene.mAmbient, mP0, mP1, mP2, false);
        mTopBottom.init(mGradients, mP0, mP2, 0);
        mTopMiddle.init(mGradients, mP0, mP1, 0);
        mMiddleBottom.init(mGradients, mP1, mP2, 1);
         */
        
        Dimension pixelDimension = mRenderer.getPixelDimension();
        int w = pixelDimension.width;
        int h = pixelDimension.height;
        
        float[] zBuf = mRenderer.getZBuffer();
        int[] pixels = mRenderer.getPixels();
        byte[] idBuf = mRenderer.getIdBuffer();

        drawLine(pixels, w, h, mP0.mX >> 4, mP0.mY >> 4, mP1.mX >> 4, mP1.mY >> 4);
        drawLine(pixels, w, h, mP1.mX >> 4, mP1.mY >> 4, mP2.mX >> 4, mP2.mY >> 4);
        drawLine(pixels, w, h, mP2.mX >> 4, mP2.mY >> 4, mP0.mX >> 4, mP0.mY >> 4);
        
        /*
        boolean updateZBuffer = true;
        boolean updateColorBuffer = true;
        boolean updateIdBuffer = false;
        byte id = 0; //shape.getId();
        
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
            left = (GouraudEdge)mTopBottom; 
            right = (GouraudEdge)mTopMiddle;
        } 
        else {
            bMiddleIsLeft = true;
            left = (GouraudEdge)mTopMiddle; 
            right = (GouraudEdge)mTopBottom;
        }

        GouraudGradients gradients = (GouraudGradients)mGradients;
        
        int height = mTopMiddle.mHeight;
        while (height-- > 0) {
            drawScanLine(id, pixels, zBuf, idBuf, w,
                         gradients, left, right, 
                         updateZBuffer, updateColorBuffer, updateIdBuffer);
            mTopMiddle.step(); 
            mTopBottom.step();
        }        

        if (bMiddleIsLeft) {
            left = (GouraudEdge)mMiddleBottom; 
            right = (GouraudEdge)mTopBottom;
        } 
        else {
            left = (GouraudEdge)mTopBottom; 
            right = (GouraudEdge)mMiddleBottom;
        }

        height = mMiddleBottom.mHeight;
        while (height-- > 0) {
            drawScanLine(id, pixels, zBuf, idBuf, w,
                         gradients, left, right, 
                         updateZBuffer, updateColorBuffer, updateIdBuffer);
            mMiddleBottom.step(); 
            mTopBottom.step();
        }
         */
    }
    
    /**
     *
     */
    final void drawScanLine(byte id, int[] pixels, float[] zBuf, byte[] idBuf, 
                                      int scanLineWidth, TriangleGradients gradients, 
                                      GouraudEdge left, GouraudEdge right, 
                                      boolean updateZBuffer, boolean updateColorBuffer, boolean updateIdBuffer) {

        int xStart = left.mX;
        int width = right.mX - xStart;

        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;
        
        while (width-- > 0) {
            if (Z < zBuf[offset]) {
                zBuf[offset]   = updateZBuffer     ? Z : zBuf[offset];
                pixels[offset] = updateColorBuffer ? Color.BLACK : pixels[offset];
                idBuf[offset]  = updateIdBuffer    ? id : idBuf[offset];
            }
            offset++;
            Z += gradients.mdZOverdX;
        }
    }
    
    /**
     * Eberly, p. 115
     */
    void drawLine(int[] pixels, int w, int h, int x1, int y1, int x2, int y2) {
        if (y1 > y2) {
            int x = x1;
            x1 = x2;
            x2 = x;
            int y = y1;
            y1 = y2;
            y2 = y;
        }
        
        int dX = x2 - x1;
        int dY = y2 - y1;
        
        int sX;
        if (dX > 0) {
            sX = 1;
        } 
        else if (dX < 0) {
            sX = -1;
            dX = -dX;
        }
        else {
            sX = 0;
        }
        
        int x = x1;
        int y = y1;
        
        int aX = 2 * dX;
        int aY = 2 * dY;
        
        if (dY <= dX) {
            for (int decY = aY - dX; ; x += sX, decY += aY) {
                int offset = y * w + x;
                pixels[offset] = 0xFF000000; 
                if (x == x2)
                    break;
                if (decY >= 0) {
                    decY -= aX;
                    y += 1;
                }
            }
        }
        else {
            for (int decX = aX - dY; ; y += 1, decX += aX) {
                pixels[y * w + x] = 0xFF000000;
                if (y == y2)
                    break;
                if (decX >= 0) {
                    decX -= aY;
                    x += sX;
                }
            }
        }
    }
    
}
