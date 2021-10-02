/*
 * TriangleRasterizer.java
 *
 * Created on June 2, 2002, 1:25 PM
 */

package com.sofascience.cyan2;

import java.util.Vector;

/**
 * Base class for all triangle rasterizers.
 *
 * @author  Karl Traunmueller
 */
abstract class TriangleRasterizer {

    static final int WIREFRAME      = 0;
    static final int FLAT           = 1;
    static final int GOURAUD        = 2;
    static final int TEXTURED       = 3;
    static final int SHADOW         = 4;
    
    SceneGraphRenderer    mRenderer;
    /** triangle vertices (mP0 is topmost, mP2 is bottommost) */
    PipelineVertex        mP0, mP1, mP2;    
    
    /**
     * Triangle rasterizer factory.
     */
    static final TriangleRasterizer getRasterizer(int type, SceneGraphRenderer renderer) {
        switch (type) {
            case WIREFRAME:
                return new WireframeRasterizer(renderer);
            case FLAT:
                return new FlatRasterizer(renderer);
            case GOURAUD:
                return new GouraudRasterizer(renderer);
            case TEXTURED:
                return new TexturedRasterizer(renderer);
            case SHADOW:
                return new ShadowRasterizer(renderer);
        }
        return null;
    }
    
    /** 
     * Constructs a new triangle rasterizer for the given renderer.
     */
    TriangleRasterizer(SceneGraphRenderer renderer) {
        mRenderer = renderer;                
    }

    /**
     * Returns the rasterizer's type.
     */
    abstract int getType();
    
    /**
     * Sorts the pipeline vertices in y order and stores them in mP0..mP2.
     */
    void sortAndStore(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2) {
        // sort vertices by y (p0 topmost, p1 middle, p2 bottommost)
        if (p0.mScreenCoords.y > p1.mScreenCoords.y) {
            PipelineVertex swap = p0;
            p0 = p1;
            p1 = swap;
        }
        if (p0.mScreenCoords.y > p2.mScreenCoords.y) {
            PipelineVertex swap = p0;
            p0 = p2;
            p2 = swap;
        }
        if (p1.mScreenCoords.y > p2.mScreenCoords.y) {
            PipelineVertex swap = p1;
            p1 = p2;
            p2 = swap;
        }
        mP0 = p0;
        mP1 = p1;
        mP2 = p2;
    }
    
    /**
     * Sorts the three vertices in y, and lights the vertices.
     */
    abstract void rasterize(PipelineVertex p0, PipelineVertex p1, PipelineVertex p2);
                                        
}
