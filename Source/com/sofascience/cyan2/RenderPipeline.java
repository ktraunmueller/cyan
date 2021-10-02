/*
 * RenderPipeline.java
 *
 * Created on February 23, 2002, 3:22 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.MemoryImageSource;

/**
 * The render pipeline: transform - clip - rasterize.
 *
 * @author  Karl Traunmueller
 */
class RenderPipeline {

    static final int   MAX_NR_CLIP_VERTS               = 200;
    static final float MODEL_TO_PIPELINE_VERTS_FACT    = 2.5f;
    static final int   NR_ADD_PIPELINE_VERTS           = 100;
    
    SceneGraphRenderer    mRenderer;
    WireframeRasterizer   mWireframeRasterizer;
    FlatRasterizer        mFlatRasterizer;
    GouraudRasterizer     mGouraudRasterizer;
    TexturedRasterizer    mTexturedRasterizer;
    ShadowRasterizer      mShadowRasterizer;
    TriangleRasterizer    mPreferredRasterizer;
    PipelineVertex[]      mPipelineVerts;
    int                   mNrPipelineVerts;
    int[]                 mVisibleTris;
    int                   mNrVisibleVerts;
    PipelineVertex[]      mClipVerts;
    PipelineVertex[]      mClipVertsNew;    
    Matrix4f              mHvt;
    Matrix4f              mHvtInv;
    Matrix4f              mHmvt;
    Matrix3f              mHmvtRot;
    Matrix4f              mHproj;        
    int                   mViewportWidth, mViewportHeight;
    
    /** 
     * Default constructor. 
     */
    RenderPipeline(SceneGraphRenderer renderer) {
        mRenderer = renderer;               
        
        mHvt = new Matrix4f();
        mHmvt = new Matrix4f();
        mHvtInv = new Matrix4f();
        mHmvtRot = new Matrix3f();
        mHproj = new Matrix4f();
        
        mClipVerts = new PipelineVertex[MAX_NR_CLIP_VERTS];
        mClipVertsNew = new PipelineVertex[MAX_NR_CLIP_VERTS];
        for (int v = 0; v < mClipVerts.length; v++) {
            mClipVerts[v] = new PipelineVertex();
            mClipVertsNew[v] = new PipelineVertex();
        }
        
        mWireframeRasterizer = (WireframeRasterizer)TriangleRasterizer.getRasterizer(TriangleRasterizer.WIREFRAME, mRenderer);
        mFlatRasterizer = (FlatRasterizer)TriangleRasterizer.getRasterizer(TriangleRasterizer.FLAT, mRenderer);
        mGouraudRasterizer = (GouraudRasterizer)TriangleRasterizer.getRasterizer(TriangleRasterizer.GOURAUD, mRenderer);
        mTexturedRasterizer = (TexturedRasterizer)TriangleRasterizer.getRasterizer(TriangleRasterizer.TEXTURED, mRenderer);
        mShadowRasterizer = (ShadowRasterizer)TriangleRasterizer.getRasterizer(TriangleRasterizer.SHADOW, mRenderer);
        mPreferredRasterizer = mTexturedRasterizer;
        
        int nrPipelineVerts = 20;
        mPipelineVerts = new PipelineVertex[nrPipelineVerts];
        for (int i = 0; i < nrPipelineVerts; i++)
            mPipelineVerts[i] = new PipelineVertex();
        
        mVisibleTris = new int[nrPipelineVerts * 3];
    }    
    
    /**
     *
     */
    final void setPreferredRasterizer(int type) {
        switch (type) {
            case TriangleRasterizer.WIREFRAME:
                mPreferredRasterizer = mWireframeRasterizer;
                break;
            case TriangleRasterizer.FLAT:
                mPreferredRasterizer = mFlatRasterizer;
                break;
            case TriangleRasterizer.GOURAUD:
                mPreferredRasterizer = mGouraudRasterizer;
                break;
            case TriangleRasterizer.TEXTURED:
                mPreferredRasterizer = mTexturedRasterizer;
                break;
            default:
                break;
        }
    }
    
    /**
     *
     */
    final void setProjectionMatrix(Matrix4f proj) {
        mHproj.set(proj);
    }
    
    /**
     *
     */
    void setViewingTransform(Matrix4f viewingTransform) {
        mHvt.set(viewingTransform);                
    }
    
    /**
     *
     */
    void renderMesh(Mesh mesh, Matrix4f localToWorld) {
        if (mesh == null || localToWorld == null)
            return;               

        Dimension viewportDimension = mRenderer.getPixelDimension();
        mViewportWidth = viewportDimension.width;
        mViewportHeight = viewportDimension.height;
        
        mHmvt.mul(localToWorld, mHvt);        
        mHvtInv.transpose(mHmvt);

        Light light = mRenderer.getScene().getLight(0);
        if (light == null)
            return;
        light.setViewMatrix(mHvtInv);
        
        Face[] faces = mesh.mFaces;
        for (int f = faces.length - 1; f >= 0; f--) {            
            Face face = faces[f];
            if (face.mMaterial != null && face.mMaterial.mTransparency == 0.0f) {
                if (transformAndClip(face)) {
                    lightAndRasterizePipelineTriangles(light);
                }
            }                
        }
    }        
    
    /**
     *
     */
    void renderShadow(Mesh mesh, Matrix4f localToWorld) {
        if (mesh == null || localToWorld == null)
            return;               

        Dimension viewportDimension = mRenderer.getPixelDimension();
        mViewportWidth = viewportDimension.width;
        mViewportHeight = viewportDimension.height;
        
        mHmvt.mul(localToWorld, mHvt);        
        mHvtInv.transpose(mHmvt);

        Light light = mRenderer.getScene().getLight(0);
        if (light == null)
            return;
        light.setViewMatrix(mHvtInv);
        
        Face[] faces = mesh.mFaces;
        for (int f = faces.length - 1; f >= 0; f--) {            
            Face face = faces[f];
            if (transformAndClip(face)) {
                lightAndRasterizeShadow(light);
            }
        }
    }
        
    /**
     * Takes a triangle array and a local-to-world transform matrix,
     * transforms all the vertices in the triangle array into view space,
     * performs clipping, projection, and rasterization.
     */
    void renderMeshTransparentFaces(Mesh mesh, Matrix4f localToWorld) {
        if (mesh == null || localToWorld == null)
            return;               

        Dimension viewportDimension = mRenderer.getPixelDimension();
        mViewportWidth = viewportDimension.width;
        mViewportHeight = viewportDimension.height;
        
        mHmvt.mul(localToWorld, mHvt);        
        mHvtInv.transpose(mHmvt);

        Light light = mRenderer.getScene().getLight(0);
        if (light == null)
            return;
        light.setViewMatrix(mHvtInv);
        
        Face[] faces = mesh.mFaces;
        for (int f = faces.length - 1; f >= 0; f--) {            
            Face face = faces[f];
            if (face.mMaterial != null && face.mMaterial.mTransparency > 0.0f) {
                if (transformAndClip(face)) {
                    lightAndRasterizePipelineTriangles(light);
                }
            }                
        }
    }        
    
    /**
     * Transforms all vertices into screen space and performs triangle clipping.
     */     
    final boolean transformAndClip(Face face) {
        mNrVisibleVerts = 0;
        mNrPipelineVerts = 0;
        
        if (face == null)
            return false;
        
        // transform verts into view space and project into screen space
        mPipelineVerts[0].init(face, 0, mHmvt, mHproj, mViewportWidth, mViewportHeight);
        mPipelineVerts[1].init(face, 1, mHmvt, mHproj, mViewportWidth, mViewportHeight);
        mPipelineVerts[2].init(face, 2, mHmvt, mHproj, mViewportWidth, mViewportHeight);

        // all vertex clip flags nonzero and identical -> all verts outside of same clipping plane
        if (0 < (mPipelineVerts[0].mClipFlags & mPipelineVerts[1].mClipFlags & mPipelineVerts[2].mClipFlags))
            return false;

        // screen space test for backfacing triangles
        float xa = mPipelineVerts[0].mScreenCoords.x;
        float ya = mPipelineVerts[0].mScreenCoords.y;
        float xb = mPipelineVerts[1].mScreenCoords.x;            
        float yb = mPipelineVerts[1].mScreenCoords.y;
        float xc = mPipelineVerts[2].mScreenCoords.x;
        float yc = mPipelineVerts[2].mScreenCoords.y;

        float area = xa * (yc - yb) + xb * (ya - yc) + xc * (yb - ya);
        if (area > 0.0f)
            return false;   // backfacing

        mNrPipelineVerts = 3;
        
        if (0 == (mPipelineVerts[0].mClipFlags | mPipelineVerts[1].mClipFlags | mPipelineVerts[2].mClipFlags)) {
            // all vertex clip flags zero -> tri fully visible
            mVisibleTris[mNrVisibleVerts++] = 0;
            mVisibleTris[mNrVisibleVerts++] = 1;
            mVisibleTris[mNrVisibleVerts++] = 2;                                               
        } 
        else {
            // clip triangle (a,b,c) and add new vertices to pipeline vertex array
            clip(0, 1, 2);
        }
        return true;
    }
    
    /**
     * Clips the triangle (a,b,c) with the Sutherland-Hodgeman polygon clipping algorithm.
     * First, the triangle is clipped against all 6 planes, and then the resulting
     * polygon is retesselated back to triangles. The new vertices produced during
     * clipping are appended to mPipelineVerts, and the resulting triangle indices
     * are appended to mVisibleTris.
     */
    final void clip(int a, int b, int c/*, int w, int h*/) {
        int w = mViewportWidth;
        int h = mViewportHeight;
        
        mClipVerts[0].set(mPipelineVerts[a]);
        mClipVerts[1].set(mPipelineVerts[b]);
        mClipVerts[2].set(mPipelineVerts[c]);
        int nrClipVerts = 3;

        // clip against all 6 planes
        for (int clipPlane = 0; clipPlane < 6; clipPlane++) {                        
            if (PipelineVertex.CLIP_PLANES[clipPlane] == PipelineVertex.CLIP_FAR)
                continue;   // no clipping against far plane
            
            int nrClipVertsNew = 0;            
            for (int v = 0; v < nrClipVerts; v++) {                
                PipelineVertex v1 = mClipVerts[v];
                PipelineVertex v2 = mClipVerts[(v + 1) % nrClipVerts];
                
                int clip1 = v1.mClipFlags & PipelineVertex.CLIP_PLANES[clipPlane];
                int clip2 = v2.mClipFlags & PipelineVertex.CLIP_PLANES[clipPlane];
                
                if (clip1 > 0 && clip2 == 0) {         // out -> in
                    clipLine(clipPlane, v1, v2, mClipVertsNew[nrClipVertsNew++], w, h);   // save intersection point
                    mClipVertsNew[nrClipVertsNew++].set(v2);                        // save internal point
                }
                else if (clip1 == 0 && clip2 == 0) {    // in -> in
                    mClipVertsNew[nrClipVertsNew++].set(v2);                        // save second point
                }
                else if (clip1 == 0 && clip2 > 0) {    // in -> out
                    clipLine(clipPlane, v1, v2, mClipVertsNew[nrClipVertsNew++], w, h);   // save intersection point
                }
                // else out -> out; save none
            }            
            // swap mClipVerts and mClipVertsNew
            PipelineVertex[] swap = mClipVerts;
            mClipVerts = mClipVertsNew;
            mClipVertsNew = swap;
            nrClipVerts = nrClipVertsNew;
        }
        
        // retesselate into triangles     
        int firstVert = mNrPipelineVerts;
        for (int v = 0; v < nrClipVerts; v++) {
            mPipelineVerts[mNrPipelineVerts++].set(mClipVerts[v]);
        }
        for (int offset = 1; offset < nrClipVerts - 1; offset++) {
            mVisibleTris[mNrVisibleVerts++] = firstVert;
            mVisibleTris[mNrVisibleVerts++] = firstVert + offset;
            mVisibleTris[mNrVisibleVerts++] = firstVert + offset + 1;
        }
    }
    
    /**
     * Clips the line (v1,v2) against clipPlane and stores the intersection point in vi.
     */
    final void clipLine(int clipPlane, PipelineVertex v1, PipelineVertex v2, PipelineVertex vi,
                               int w, int h) {
        float bound = PipelineVertex.CLIP_BOUNDS[clipPlane];
        float t = 0.0f;                
        
        switch (PipelineVertex.CLIP_PLANES[clipPlane]) {
            case PipelineVertex.CLIP_LEFT:
            case PipelineVertex.CLIP_RIGHT:
                t = (bound - v1.mScreenCoords.x) / (v2.mScreenCoords.x - v1.mScreenCoords.x);                
                vi.mScreenCoords.x = bound;
                vi.mScreenCoords.y = v1.mScreenCoords.y + t * (v2.mScreenCoords.y - v1.mScreenCoords.y);
                vi.mScreenCoords.z = v1.mScreenCoords.z + t * (v2.mScreenCoords.z - v1.mScreenCoords.z);
                break;
                
            case PipelineVertex.CLIP_TOP:
            case PipelineVertex.CLIP_BOTTOM:
                t = (bound - v1.mScreenCoords.y) / (v2.mScreenCoords.y - v1.mScreenCoords.y);
                vi.mScreenCoords.y = bound;
                vi.mScreenCoords.x = v1.mScreenCoords.x + t * (v2.mScreenCoords.x - v1.mScreenCoords.x);
                vi.mScreenCoords.z = v1.mScreenCoords.z + t * (v2.mScreenCoords.z - v1.mScreenCoords.z);
                break;
                
            case PipelineVertex.CLIP_NEAR:
            case PipelineVertex.CLIP_FAR:
                t = (bound - v1.mScreenCoords.z) / (v2.mScreenCoords.z - v1.mScreenCoords.z);
                vi.mScreenCoords.x = v1.mScreenCoords.x + t * (v2.mScreenCoords.x - v1.mScreenCoords.x);
                vi.mScreenCoords.y = v1.mScreenCoords.y + t * (v2.mScreenCoords.y - v1.mScreenCoords.y);
                vi.mScreenCoords.z = bound;
                break;
        }

        vi.mFace = v1.mFace;
        
        // interpolate vertex normal
        vi.mNormal.interpolate(v1.mNormal, v2.mNormal, t);
        
        // calculate clip flags for intersection vertex
        vi.clip();
        
        // perform rational linear interpolation of texture coordinates
        float Di = v1.mOneOverW + t * (v2.mOneOverW - v1.mOneOverW);
        float N1 = v1.mU * v1.mOneOverW;
        float N2 = v2.mU * v2.mOneOverW;
        float Ni = N1 + t * (N2 - N1);        
        vi.mU = Ni / Di;        
        
        N1 = v1.mV * v1.mOneOverW;        
        N2 = v2.mV * v2.mOneOverW;        
        Ni = N1 + t * (N2 - N1);
        vi.mV = Ni / Di;        
        vi.mOneOverW = Di;
        
        // transform to viewport
        vi.transformToViewport(w, h);
    }

    /**
     * Rasterizes all visible triangles.
     */
    final void lightAndRasterizePipelineTriangles(Light light) {
        for (int v = 0; v < mNrVisibleVerts; v += 3) {
            PipelineVertex p0 = mPipelineVerts[mVisibleTris[v]];            
            PipelineVertex p1 = mPipelineVerts[mVisibleTris[v + 1]];
            PipelineVertex p2 = mPipelineVerts[mVisibleTris[v + 2]];
            light.light(p0, p1, p2);
            
            if (p0.mFace.mMaterial.mTexture1Map != null &&
                p0.mFace.mMaterial.mTexture1Map.getTexture() != null ) {
                mPreferredRasterizer.rasterize(p0, p1, p2);
            }
            else {
                if (mPreferredRasterizer != mTexturedRasterizer)
                    mPreferredRasterizer.rasterize(p0, p1, p2);
                else
                    mGouraudRasterizer.rasterize(p0, p1, p2);
            }
        }
    }
    
    /**
     * 
     */
    final void lightAndRasterizeShadow(Light light) {
        for (int v = 0; v < mNrVisibleVerts; v += 3) {
            PipelineVertex p0 = mPipelineVerts[mVisibleTris[v]];            
            PipelineVertex p1 = mPipelineVerts[mVisibleTris[v + 1]];
            PipelineVertex p2 = mPipelineVerts[mVisibleTris[v + 2]];
            light.light(p0, p1, p2);
            
            mShadowRasterizer.rasterize(p0, p1, p2);
        }
    }
}
