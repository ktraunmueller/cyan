/*
 * FlatRasterizer.java
 *
 * Created on June 3, 2002, 9:35 PM
 */

package com.sofascience.cyan2;

import java.awt.Dimension;

/**
 *
 * @author  Karl Traunmueller
 */
class FlatRasterizer extends TriangleRasterizer {
    
    FlatGradients    mGradients;
    FlatEdge         mTopBottom, mTopMiddle, mMiddleBottom;
    
    /** 
     * Default constructor.
     */
    FlatRasterizer(SceneGraphRenderer renderer) {
        super(renderer);
        mGradients    = new FlatGradients();
        mTopBottom    = new FlatEdge();
        mTopMiddle    = new FlatEdge();
        mMiddleBottom = new FlatEdge();
    }
    
    /**
     * Returns the rasterizer type (FLAT).
     */
    final int getType() {
        return TriangleRasterizer.FLAT;
    }
    
    /**
     * Rasterizes the given triangle.
     */
    void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2) {
        sortAndStore(p0, p1, p2);
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
        
        FlatEdge left, right;
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
            drawScanLine(id, pixels, zBuf, idBuf, w, mGradients, left, right);
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
            drawScanLine(id, pixels, zBuf, idBuf, w, mGradients, left, right);
            mMiddleBottom.step(); 
            mTopBottom.step();
        }
    }    

    /**
     *
     */
    final void drawScanLine(byte id, int[] pixels, float[] zBuf, byte[] idBuf, int scanLineWidth,
                                      FlatGradients gradients, FlatEdge left, FlatEdge right) {

        int xStart = left.mX;
        int width = right.mX - xStart;

        int offset = left.mY * scanLineWidth + xStart;
        
        float Z = left.mZ;
        float red = left.mRed;
        float green = left.mGreen;
        float blue = left.mBlue;
        int color = 0xFF000000 | (((int)red) << 16) | (((int)green) << 8) | (int)blue;
        
        while (width-- > 0) {
            if (Z < zBuf[offset]) {
                zBuf[offset]   = Z;
                pixels[offset] = color;
                idBuf[offset]  = id;
            }
            offset++;
            Z += gradients.mdZOverdX;
        }
    }
}
