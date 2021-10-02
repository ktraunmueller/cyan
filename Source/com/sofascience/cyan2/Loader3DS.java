/*
 * Importer3DS.java
 *
 * Created on February 23, 2002, 3:20 PM
 */

package com.sofascience.cyan2;

import java.lang.String;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;
import java.util.zip.*;

import com.sofascience.cyan2.vecmath.*;

/**
 * Loader for Autodesk 3DS files.
 *
 * @author Karl Traunmueller
 */
class Loader3DS {

    /** 3DS file format magic numbers */    
    static final int NULL_CHUNK             = 0x0000;
    static final int M3DMAGIC               = 0x4D4D;    /*3DS file*/
    static final int SMAGIC                 = 0x2D2D;
    static final int LMAGIC                 = 0x2D3D;
    static final int MLIBMAGIC              = 0x3DAA;    /*MLI file*/
    static final int MATMAGIC               = 0x3DFF;
    static final int CMAGIC                 = 0xC23D;    /*PRJ file*/
    static final int M3D_VERSION            = 0x0002;
    static final int M3D_KFVERSION          = 0x0005;
    
    static final int COLOR_F                = 0x0010;
    static final int COLOR_24               = 0x0011;
    static final int LIN_COLOR_24           = 0x0012;
    static final int LIN_COLOR_F            = 0x0013;
    static final int INT_PERCENTAGE         = 0x0030;
    static final int FLOAT_PERCENTAGE       = 0x0031;
    
    static final int MDATA                  = 0x3D3D;
    static final int MESH_VERSION           = 0x3D3E;
    static final int MASTER_SCALE           = 0x0100;
    static final int LO_SHADOW_BIAS         = 0x1400;
    static final int HI_SHADOW_BIAS         = 0x1410;
    static final int SHADOW_MAP_SIZE        = 0x1420;
    static final int SHADOW_SAMPLES         = 0x1430;
    static final int SHADOW_RANGE           = 0x1440;
    static final int SHADOW_FILTER          = 0x1450;
    static final int RAY_BIAS               = 0x1460;
    static final int O_CONSTS               = 0x1500;
    static final int AMBIENT_LIGHT          = 0x2100;
    static final int BIT_MAP                = 0x1100;
    static final int SOLID_BGND             = 0x1200;
    static final int V_GRADIENT             = 0x1300;
    static final int USE_BIT_MAP            = 0x1101;
    static final int USE_SOLID_BGND         = 0x1201;
    static final int USE_V_GRADIENT         = 0x1301;
    static final int FOG                    = 0x2200;
    static final int FOG_BGND               = 0x2210;
    static final int LAYER_FOG              = 0x2302;
    static final int DISTANCE_CUE           = 0x2300;
    static final int DCUE_BGND              = 0x2310;
    static final int USE_FOG                = 0x2201;
    static final int USE_LAYER_FOG          = 0x2303;
    static final int USE_DISTANCE_CUE       = 0x2301;
    
    static final int MAT_ENTRY              = 0xAFFF;
    static final int MAT_NAME               = 0xA000;
    static final int MAT_AMBIENT            = 0xA010;
    static final int MAT_DIFFUSE            = 0xA020;
    static final int MAT_SPECULAR           = 0xA030;
    static final int MAT_SHININESS          = 0xA040;
    static final int MAT_SHIN2PCT           = 0xA041;
    static final int MAT_TRANSPARENCY       = 0xA050;
    static final int MAT_XPFALL             = 0xA052;
    static final int MAT_USE_XPFALL         = 0xA240;
    static final int MAT_REFBLUR            = 0xA053;
    static final int MAT_SHADING            = 0xA100;
    static final int MAT_USE_REFBLUR        = 0xA250;
    static final int MAT_SELF_ILLUM         = 0xA080;
    static final int MAT_TWO_SIDE           = 0xA081;
    static final int MAT_DECAL              = 0xA082;
    static final int MAT_ADDITIVE           = 0xA083;
    static final int MAT_WIRE               = 0xA085;
    static final int MAT_FACEMAP            = 0xA088;
    static final int MAT_PHONGSOFT          = 0xA08C;
    static final int MAT_WIREABS            = 0xA08E;
    static final int MAT_WIRE_SIZE          = 0xA087;
    static final int MAT_TEXMAP             = 0xA200;
    static final int MAT_SXP_TEXT_DATA      = 0xA320;
    static final int MAT_TEXMASK            = 0xA33E;
    static final int MAT_SXP_TEXTMASK_DATA  = 0xA32A;
    static final int MAT_TEX2MAP            = 0xA33A;
    static final int MAT_SXP_TEXT2_DATA     = 0xA321;
    static final int MAT_TEX2MASK           = 0xA340;
    static final int MAT_SXP_TEXT2MASK_DATA = 0xA32C;
    static final int MAT_OPACMAP            = 0xA210;
    static final int MAT_SXP_OPAC_DATA      = 0xA322;
    static final int MAT_OPACMASK           = 0xA342;
    static final int MAT_SXP_OPACMASK_DATA  = 0xA32E;
    static final int MAT_BUMPMAP            = 0xA230;
    static final int MAT_SXP_BUMP_DATA      = 0xA324;
    static final int MAT_BUMPMASK           = 0xA344;
    static final int MAT_SXP_BUMPMASK_DATA  = 0xA330;
    static final int MAT_SPECMAP            = 0xA204;
    static final int MAT_SXP_SPEC_DATA      = 0xA325;
    static final int MAT_SPECMASK           = 0xA348;
    static final int MAT_SXP_SPECMASK_DATA  = 0xA332;
    static final int MAT_SHINMAP            = 0xA33C;
    static final int MAT_SXP_SHIN_DATA      = 0xA326;
    static final int MAT_SHINMASK           = 0xA346;
    static final int MAT_SXP_SHINMASK_DATA  = 0xA334;
    static final int MAT_SELFIMAP           = 0xA33D;
    static final int MAT_SXP_SELFI_DATA     = 0xA328;
    static final int MAT_SELFIMASK          = 0xA34A;
    static final int MAT_SXP_SELFIMASK_DATA = 0xA336;
    static final int MAT_REFLMAP            = 0xA220;
    static final int MAT_REFLMASK           = 0xA34C;
    static final int MAT_SXP_REFLMASK_DATA  = 0xA338;
    static final int MAT_ACUBIC             = 0xA310;
    static final int MAT_MAPNAME            = 0xA300;
    static final int MAT_MAP_TILING         = 0xA351;
    static final int MAT_MAP_TEXBLUR        = 0xA353;
    static final int MAT_MAP_USCALE         = 0xA354;
    static final int MAT_MAP_VSCALE         = 0xA356;
    static final int MAT_MAP_UOFFSET        = 0xA358;
    static final int MAT_MAP_VOFFSET        = 0xA35A;
    static final int MAT_MAP_ANG            = 0xA35C;
    static final int MAT_MAP_COL1           = 0xA360;
    static final int MAT_MAP_COL2           = 0xA362;
    static final int MAT_MAP_RCOL           = 0xA364;
    static final int MAT_MAP_GCOL           = 0xA366;
    static final int MAT_MAP_BCOL           = 0xA368;
    
    static final int NAMED_OBJECT           = 0x4000;
    static final int N_DIRECT_LIGHT         = 0x4600;
    static final int DL_OFF                 = 0x4620;
    static final int DL_OUTER_RANGE         = 0x465A;
    static final int DL_INNER_RANGE         = 0x4659;
    static final int DL_MULTIPLIER          = 0x465B;
    static final int DL_EXCLUDE             = 0x4654;
    static final int DL_ATTENUATE           = 0x4625;
    static final int DL_SPOTLIGHT           = 0x4610;
    static final int DL_SPOT_ROLL           = 0x4656;
    static final int DL_SHADOWED            = 0x4630;
    static final int DL_LOCAL_SHADOW2       = 0x4641;
    static final int DL_SEE_CONE            = 0x4650;
    static final int DL_SPOT_RECTANGULAR    = 0x4651;
    static final int DL_SPOT_ASPECT         = 0x4657;
    static final int DL_SPOT_PROJECTOR      = 0x4653;
    static final int DL_SPOT_OVERSHOOT      = 0x4652;
    static final int DL_RAY_BIAS            = 0x4658;
    static final int DL_RAYSHAD             = 0x4627;
    static final int N_CAMERA               = 0x4700;
    static final int CAM_SEE_CONE           = 0x4710;
    static final int CAM_RANGES             = 0x4720;
    static final int OBJ_HIDDEN             = 0x4010;
    static final int OBJ_VIS_LOFTER         = 0x4011;
    static final int OBJ_DOESNT_CAST        = 0x4012;
    static final int OBJ_DONT_RECVSHADOW    = 0x4017;
    static final int OBJ_MATTE              = 0x4013;
    static final int OBJ_FAST               = 0x4014;
    static final int OBJ_PROCEDURAL         = 0x4015;
    static final int OBJ_FROZEN             = 0x4016;
    static final int N_TRI_OBJECT           = 0x4100;
    static final int POINT_ARRAY            = 0x4110;
    static final int POINT_FLAG_ARRAY       = 0x4111;
    static final int FACE_ARRAY             = 0x4120;
    static final int MSH_MAT_GROUP          = 0x4130;
    static final int SMOOTH_GROUP           = 0x4150;
    static final int MSH_BOXMAP             = 0x4190;
    static final int TEX_VERTS              = 0x4140;
    static final int MESH_MATRIX            = 0x4160;
    static final int MESH_COLOR             = 0x4165;
    static final int MESH_TEXTURE_INFO      = 0x4170;
    
    static final int KFDATA                 = 0xB000;
    static final int KFHDR                  = 0xB00A;
    static final int KFSEG                  = 0xB008;
    static final int KFCURTIME              = 0xB009;
    static final int AMBIENT_NODE_TAG       = 0xB001;
    static final int OBJECT_NODE_TAG        = 0xB002;
    static final int CAMERA_NODE_TAG        = 0xB003;
    static final int TARGET_NODE_TAG        = 0xB004;
    static final int LIGHT_NODE_TAG         = 0xB005;
    static final int L_TARGET_NODE_TAG      = 0xB006;
    static final int SPOTLIGHT_NODE_TAG     = 0xB007;
    static final int NODE_ID                = 0xB030;
    static final int NODE_HDR               = 0xB010;
    static final int PIVOT                  = 0xB013;
    static final int INSTANCE_NAME          = 0xB011;
    static final int MORPH_SMOOTH           = 0xB015;
    static final int BOUNDBOX               = 0xB014;
    static final int POS_TRACK_TAG          = 0xB020;
    static final int COL_TRACK_TAG          = 0xB025;
    static final int ROT_TRACK_TAG          = 0xB021;
    static final int SCL_TRACK_TAG          = 0xB022;
    static final int MORPH_TRACK_TAG        = 0xB026;
    static final int FOV_TRACK_TAG          = 0xB023;
    static final int ROLL_TRACK_TAG         = 0xB024;
    static final int HOT_TRACK_TAG          = 0xB027;
    static final int FALL_TRACK_TAG         = 0xB028;
    static final int HIDE_TRACK_TAG         = 0xB029;
    
    static final int POLY_2D                = 0x5000;
    static final int SHAPE_OK               = 0x5010;
    static final int SHAPE_NOT_OK           = 0x5011;
    static final int SHAPE_HOOK             = 0x5020;
    static final int PATH_3D                = 0x6000;
    static final int PATH_MATRIX            = 0x6005;
    static final int SHAPE_2D               = 0x6010;
    static final int M_SCALE                = 0x6020;
    static final int M_TWIST                = 0x6030;
    static final int M_TEETER               = 0x6040;
    static final int M_FIT                  = 0x6050;
    static final int M_BEVEL                = 0x6060;
    static final int XZ_CURVE               = 0x6070;
    static final int YZ_CURVE               = 0x6080;
    static final int INTERPCT               = 0x6090;
    static final int DEFORM_LIMIT           = 0x60A0;
    
    static final int USE_CONTOUR            = 0x6100;
    static final int USE_TWEEN              = 0x6110;
    static final int USE_SCALE              = 0x6120;
    static final int USE_TWIST              = 0x6130;
    static final int USE_TEETER             = 0x6140;
    static final int USE_FIT                = 0x6150;
    static final int USE_BEVEL              = 0x6160;
    
    static final int DEFAULT_VIEW           = 0x3000;
    static final int VIEW_TOP               = 0x3010;
    static final int VIEW_BOTTOM            = 0x3020;
    static final int VIEW_LEFT              = 0x3030;
    static final int VIEW_RIGHT             = 0x3040;
    static final int VIEW_FRONT             = 0x3050;
    static final int VIEW_BACK              = 0x3060;
    static final int VIEW_USER              = 0x3070;
    static final int VIEW_CAMERA            = 0x3080;
    static final int VIEW_WINDOW            = 0x3090;
    
    static final int VIEWPORT_LAYOUT_OLD    = 0x7000;
    static final int VIEWPORT_DATA_OLD      = 0x7010;
    static final int VIEWPORT_LAYOUT        = 0x7001;
    static final int VIEWPORT_DATA          = 0x7011;
    static final int VIEWPORT_DATA_3        = 0x7012;
    static final int VIEWPORT_SIZE          = 0x7020;
    static final int NETWORK_VIEW           = 0x7030;
    
    static final float EPSILON              = 1e-8f;

    Canvas3D    mCanvas;
    URL         mCodebase;
    boolean     mEoS;    
    Scene       mScene;
    String      mObjName;
    
    /**
     *
     */
    Loader3DS(Canvas3D canvas, URL codebase) {
        mCanvas = canvas;
        mCodebase = codebase;
    }
    
    /**
     * Resets the loader's internal state.
     */
    private final void reset() {
    }
    
    /**
     * Loads a scene from an InputStream.
     */
    final void load(InputStream stream, Scene scene) throws java.io.IOException {               
        if (scene == null)
            return;
        
        reset();
        BufferedInputStream in = new BufferedInputStream(stream);
        
        mScene = scene;
        Chunk mainChunk = readChunkHeader(in, null);
        
        switch (mainChunk.id()) {
            case MDATA:
                readMeshData(in, mainChunk);
                break;            
            case M3DMAGIC:
            case MLIBMAGIC:
            case CMAGIC:
                Chunk subChunk = readChunkHeader(in, mainChunk);
                while (!mainChunk.complete()) {
                    switch (subChunk.id()) {
                        case M3D_VERSION:
                            mScene.mMeshVersion = readInt(in, subChunk);
                            break;
                        case MDATA:
                            readMeshData(in, subChunk);
                            break;
                        case KFDATA:
                            readKeyframeData(in, subChunk);
                            
                            mCanvas.startRenderer();
                            mCanvas.showLoadingScreen(false);
                            mScene.loadTextureMaps(mCanvas);
                            break;
                        default:
                            unknownChunk(in, subChunk);
                    }
                    subChunk = readChunkHeader(in, mainChunk);
                }
                break;
            default:
                unknownChunk(in, mainChunk);
                break;
        }
        in.close();
        
        /*
        int nrObjects = 0;
        int nrVertices = 0;
        int nrFaces = 0;
        for (int i = 0; i < mScene.getNrMeshes(); i++) {
            Mesh m = mScene.getMesh(i);
            nrObjects++;
            nrVertices += m.getNrVertices();
            nrFaces += m.getNrFaces();
        }
        System.out.println("Cyan.Loader3DS: read " + nrObjects + " objects, " + nrFaces + " faces, " + nrVertices + " vertices");
         */
        
        mCanvas.modelLoaded();        
        mCanvas.stopTimer();
    }
    
    /**
     * Loads a scene from an URL.
     */
    final void load(URL codebase, URL file, Scene scene) throws java.io.IOException {
        mCodebase = codebase;
        try {
            InputStream in = file.openStream();
            load(in, scene);
        }
        catch (java.io.IOException ex) {
            System.out.println(ex.getMessage());
        }        
    }
    
    /**
     *
     */
    final void loadZip(URL codebase, URL file, Scene scene) throws java.io.IOException {
        mCodebase = codebase;
        
        URLConnection connection = file.openConnection(); 
        connection.setDoInput(true); 
        connection.setDoOutput(false); 
        connection.setUseCaches(false); 
        connection.setDefaultUseCaches(false); 
        connection.connect(); 
        
        ZipInputStream zipIn = new ZipInputStream(connection.getInputStream()); 
        ZipEntry entry = zipIn.getNextEntry(); 
        if (entry != null) 
        { 
            long size = entry.getSize(); 
            byte[] buf = new byte[(int)size]; 
            int nrBytes = 0;
            int overall = 0;
            while (nrBytes != -1) {
                nrBytes = zipIn.read(buf, overall, (int)size - overall);
                if (nrBytes == 0)
                    nrBytes = -1;
                if (nrBytes != -1)
                    overall += nrBytes;
            }
            
            ByteArrayInputStream in = new ByteArrayInputStream(buf);
            load(in, scene);
        }         
        zipIn.close(); 
    }
    /**
     * Reads the next Chunk header (ChunkId + offset to next Chunk).
     */
    private final Chunk readChunkHeader(InputStream in, Chunk parentChunk) throws java.io.IOException {
        if (parentChunk != null && parentChunk.complete())
            return null;
        
        int id = readShort(in, parentChunk);
        int size = readInt(in, parentChunk);
        Chunk chunk = new Chunk(parentChunk, id, size);
        
        Object[] args = {getChunkName(chunk.id()), new Integer(chunk.id()), new Integer(chunk.length())};
        //String sOutput = new PrintfFormat("Cyan.Loader3DS: found Chunk %s (%hx), length is %d").sprintf(args);
        //System.out.println(sOutput);                
        
        return chunk;
    }    
        
    /**
     * Skips an unknown Chunk.
     */
    private final void skipChunk(InputStream in, Chunk chunk) {
        if (chunk == null)
            return;
        boolean eos = false;
        while (!chunk.complete() && !eos) {
            try {
                eos = in.read() < 0;
            }
            catch (java.io.IOException ex) {
            }
            if (!eos)
                chunk.crunch(1);
        }
    }
    
    /**
     *
     */
    private final void unknownChunk(InputStream in, Chunk chunk) {
        //Object[] args = {new Integer(chunk.id()), new Integer(chunk.length())};
        //String sOutput = new PrintfFormat("Cyan.Loader3DS: unkown Chunk %hx, length is %d").sprintf(args);
        //System.out.println(sOutput);
        skipChunk(in, chunk);
    }
    
    /**
     *
     */
    private final void readMeshData(InputStream in, Chunk parentChunk) throws java.io.IOException {
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {  
            switch (subChunk.id()) {
                case MESH_VERSION:
                    mScene.mMeshVersion = readInt(in, subChunk);
                    break;                    
                case MASTER_SCALE:
                    mScene.mMasterScale = readFloat(in, subChunk);
                    break;
                case SHADOW_MAP_SIZE:
                case LO_SHADOW_BIAS:
                case HI_SHADOW_BIAS:
                case SHADOW_SAMPLES:
                case SHADOW_RANGE:
                case SHADOW_FILTER:
                case RAY_BIAS:
                    readShadowMap(in, subChunk);
                    break;
                case VIEWPORT_LAYOUT:
                case DEFAULT_VIEW:
                    readViewport(in, subChunk);
                    break;
                case O_CONSTS:
                    readTuple(mScene.mConstructionPlane, in, subChunk);
                    break;
                case AMBIENT_LIGHT:
                    readAmbientLight(in, subChunk);
                    break;
                case BIT_MAP:
                case SOLID_BGND:
                case V_GRADIENT:
                case USE_BIT_MAP:
                case USE_SOLID_BGND:
                case USE_V_GRADIENT:
                    readBackground(in, subChunk);
                    break;
                case FOG:
                case LAYER_FOG:
                case DISTANCE_CUE:
                case USE_FOG:
                case USE_LAYER_FOG:
                case USE_DISTANCE_CUE:
                    readAtmosphere(in, subChunk);
                    break;
                case MAT_ENTRY:
                    readMaterial(in, subChunk);
                    break;
                case NAMED_OBJECT:
                    readNamedObject(in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
        mScene.meshDataComplete();
    }
    
    /**
     *
     */
    private final void readKeyframeData(InputStream in, Chunk parentChunk) throws java.io.IOException {
        Chunk subChunk = readChunkHeader(in, parentChunk);
        
        Node node = null;
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case KFHDR:
                    mScene.mKeyfRevision = readShort(in, subChunk);
                    mScene.mName = readString(in, subChunk);
                    mScene.mFrames = readInt(in, subChunk);
                    break;
                case KFSEG:
                    mScene.mSegmentFrom = readInt(in, subChunk);
                    mScene.mSegmentTo = readInt(in, subChunk);
                    break;
                case KFCURTIME:
                    mScene.mCurrentFrame = readInt(in, subChunk);
                    break;
                case VIEWPORT_LAYOUT:
                case DEFAULT_VIEW:
                    readViewport(in, subChunk);
                    break;
                case AMBIENT_NODE_TAG:
                    node = new AmbientNode("");
                    readNode(node, in, subChunk);
                    break;
                case OBJECT_NODE_TAG:
                    node = new ObjectNode("");
                    readNode(node, in, subChunk);
                    break;
                case CAMERA_NODE_TAG:
                    node = new CameraNode("");
                    readNode(node, in, subChunk);
                    break;
                case TARGET_NODE_TAG:
                    node = new TargetNode("");
                    readNode(node, in, subChunk);
                    break;
                case LIGHT_NODE_TAG:
                case SPOTLIGHT_NODE_TAG:
                    node = new LightNode("");
                    readNode(node, in, subChunk);
                    break;
                case L_TARGET_NODE_TAG:
                    node = new SpotNode("");
                    readNode(node, in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
    }

    /**
     *
     */
    private final void readNode(Node node, InputStream in, Chunk parentChunk) throws java.io.IOException {
        switch (parentChunk.id()) {
            case AMBIENT_NODE_TAG:
            case OBJECT_NODE_TAG:
            case CAMERA_NODE_TAG:
            case TARGET_NODE_TAG:
            case LIGHT_NODE_TAG:
            case SPOTLIGHT_NODE_TAG:
            case L_TARGET_NODE_TAG:
                break;
            default:
                skipChunk(in, parentChunk);
                return;
        }

        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case NODE_ID:
                    node.mId = readShort(in, subChunk);
                    break;
                case NODE_HDR:
                    node.mName = readString(in, subChunk);
                    node.mFlags1 = readShort(in, subChunk);
                    node.mFlags2 = readShort(in, subChunk);
                    node.mParentId = readShort(in, subChunk);
                    break;
                case PIVOT:
                    if (node.mType == Node.OBJECT_NODE) {
                        node.mNodeData.mObjectData.mPivot.x = -readFloat(in, subChunk);
                        node.mNodeData.mObjectData.mPivot.y = -readFloat(in, subChunk);
                        node.mNodeData.mObjectData.mPivot.z = -readFloat(in, subChunk);
                    }
                    else
                        unknownChunk(in, subChunk);
                    break;
                case INSTANCE_NAME:
                    if (node.mType == Node.OBJECT_NODE)
                        node.mNodeData.mObjectData.mInstance = readString(in, subChunk);
                    else
                        unknownChunk(in, subChunk);
                    break;
                case BOUNDBOX:
                    if (node.mType == Node.OBJECT_NODE) {
                        readTuple(node.mNodeData.mObjectData.mBboxMin, in, subChunk);
                        readTuple(node.mNodeData.mObjectData.mBboxMax, in, subChunk);
                    }
                    else
                        unknownChunk(in, subChunk);
                    break;
                case COL_TRACK_TAG:
                    switch (node.mType) {
                        case Node.AMBIENT_NODE:
                            readLin3Track(node.mNodeData.mAmbientData.mColorTrack, in, subChunk);
                            break;
                        case Node.LIGHT_NODE:
                            readLin3Track(node.mNodeData.mLightData.mColorTrack, in, subChunk);
                            break;
                        default:
                            unknownChunk(in, subChunk);
                    }
                    break;
                case POS_TRACK_TAG:
                    switch (node.mType) {
                        case Node.OBJECT_NODE:
                            readLin3Track(node.mNodeData.mObjectData.mPosTrack, in, subChunk);
                            break;
                        case Node.CAMERA_NODE:
                            readLin3Track(node.mNodeData.mCameraData.mPosTrack, in, subChunk);
                            break;
                        case Node.TARGET_NODE:
                            readLin3Track(node.mNodeData.mTargetData.mPosTrack, in, subChunk);
                            break;
                        case Node.LIGHT_NODE:
                            readLin3Track(node.mNodeData.mLightData.mPosTrack, in, subChunk);
                            break;
                        case Node.SPOT_NODE:
                            readLin3Track(node.mNodeData.mSpotData.mPosTrack, in, subChunk);
                            break;
                        default:
                            unknownChunk(in, subChunk);
                    }
                    break;
                case ROT_TRACK_TAG:
                    if (node.mType == Node.OBJECT_NODE)
                        readQuatTrack(node.mNodeData.mObjectData.mRotTrack, in, subChunk);
                    else
                        unknownChunk(in, subChunk);
                    break;
                case SCL_TRACK_TAG:
                    if (node.mType == Node.OBJECT_NODE)
                        readLin3Track(node.mNodeData.mObjectData.mSclTrack, in, subChunk);
                    else
                        unknownChunk(in, subChunk);
                    break;
                case FOV_TRACK_TAG:
                    if (node.mType == Node.CAMERA_NODE)
                        readLin1Track(node.mNodeData.mCameraData.mFovTrack, in, subChunk);
                    else
                        unknownChunk(in, subChunk);
                    break;
                case HOT_TRACK_TAG:
                    if (node.mType == Node.LIGHT_NODE)
                        readLin1Track(node.mNodeData.mLightData.mHotspotTrack, in, subChunk);
                    else
                        unknownChunk(in, subChunk);
                    break;
                case FALL_TRACK_TAG:
                    if (node.mType == Node.LIGHT_NODE)
                        readLin1Track(node.mNodeData.mLightData.mFalloffTrack, in, subChunk);
                    else
                        unknownChunk(in, subChunk);
                    break;
                case ROLL_TRACK_TAG:
                    switch (node.mType) {
                        case Node.CAMERA_NODE:
                            readLin1Track(node.mNodeData.mCameraData.mRollTrack, in, subChunk);
                            break;
                        case Node.LIGHT_NODE:
                            readLin1Track(node.mNodeData.mLightData.mRollTrack, in, subChunk);
                            break;
                        default:
                            unknownChunk(in, subChunk);
                    }
                    break;
                case HIDE_TRACK_TAG:
                    if (node.mType == Node.OBJECT_NODE)
                        readBoolTrack(node.mNodeData.mObjectData.mHideTrack, in, subChunk);
                    else
                        unknownChunk(in, subChunk);
                    break;
                case MORPH_SMOOTH:
                    if (node.mType == Node.OBJECT_NODE)
                        node.mNodeData.mObjectData.mMorphSmooth = readFloat(in, subChunk);
                    else
                        unknownChunk(in, subChunk);
                    break;
                case MORPH_TRACK_TAG:
                    skipChunk(in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
        mScene.addNode(node);
    }

    /**
     *
     */
    private final void readShadowMap(InputStream in, Chunk parentChunk) throws java.io.IOException {
        Chunk subChunk = readChunkHeader(in, parentChunk);        
        switch (subChunk.id()) {
            case SHADOW_MAP_SIZE:
                readShort(in, subChunk);
                break;
            case LO_SHADOW_BIAS:
                readFloat(in, subChunk);
                break;
            case HI_SHADOW_BIAS:
                readFloat(in, subChunk);
                break;
            case SHADOW_SAMPLES:
                readShort(in, subChunk);
                break;
            case SHADOW_RANGE:
                readInt(in, subChunk);
                break;
            case SHADOW_FILTER:
                readFloat(in, subChunk);
                break;
            case RAY_BIAS:
                readFloat(in, subChunk);
                break;
        }
    }
    
    /**
     *
     */
    private final void readViewport(InputStream in, Chunk parentChunk) throws java.io.IOException {
        skipChunk(in, parentChunk);
    }
    
    /**
     *
     */
    private final void readAmbientLight(InputStream in, Chunk parentChunk) throws java.io.IOException {        
        boolean haveLin = false;
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case LIN_COLOR_F:
                    readTuple(mScene.mAmbient, in, subChunk);
                    haveLin = true;
                    break;
                case COLOR_F:
                    // gamma corrected color chunk, replaced in 3ds R3 by LIN_COLOR_24
                    if (!haveLin)
                        readTuple(mScene.mAmbient, in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
    }
    
    /**
     *
     */
    private final void readBackground(InputStream in, Chunk parentChunk) throws java.io.IOException {
        skipChunk(in, parentChunk);
        /*
        Chunk subChunk = readChunkHeader(in, parentChunk);
        switch (subChunk.id()) {
            case BIT_MAP:
                //mScene.mBackground.mBitmap.mName = readString(in, subChunk);
                break;
            case SOLID_BGND:
                readSolidBg(in, subChunk);
                break;
            case V_GRADIENT:
                readGradientBg(in, subChunk);
                break;
            case USE_BIT_MAP:
                break;
            case USE_SOLID_BGND:
                break;
            case USE_V_GRADIENT:
                break;
        }
         */
    }
    
    /**
     *
     */
    private final void readSolidBg(InputStream in, Chunk parentChunk) throws java.io.IOException {
        skipChunk(in, parentChunk);
        /*
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case LIN_COLOR_F:
                    readColor3b(mScene.mBackground.mSolid.mColor, in, subChunk);
                    break;
                case COLOR_F:
                    readColor3b(mScene.mBackground.mSolid.mColor, in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
         */
    }
    
    /**
     *
     */
    private final void readGradientBg(InputStream in, Chunk parentChunk) throws java.io.IOException {        
        skipChunk(in, parentChunk);
        /*
        readFloat(in, parentChunk);
        Chunk subChunk = readChunkHeader(in, parentChunk);
        
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case COLOR_F:
                case LIN_COLOR_F:
                    readColor3b(color[index++], in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
        mScene.mBackground.mGradient.mTop = color[0];
        mScene.mBackground.mGradient.mMiddle = color[1];
        mScene.mBackground.mGradient.mBottom = color[2];
         */
    }
    
    /**
     *
     */
    private final void readAtmosphere(InputStream in, Chunk parentChunk) throws java.io.IOException {
        skipChunk(in, parentChunk);
    }
        
    /**
     *
     */
    private final void readMaterial(InputStream in, Chunk parentChunk) throws java.io.IOException {
        Material material = new Material("");
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case MAT_NAME:
                    material.mName = readString(in, subChunk);
                    //System.out.println(material.mName);
                    break;
                case MAT_AMBIENT:
                    readColor(material.mAmbient, in, subChunk);
                    break;
                case MAT_DIFFUSE:
                    readColor(material.mDiffuse, in, subChunk);
                    break;
                case MAT_SPECULAR:
                    readColor(material.mSpecular, in, subChunk);
                    break;
                case MAT_SHININESS:
                    material.mShininess = readIntPercentage(in, subChunk);
                    break;
                case MAT_SHIN2PCT:
                    material.mShinStrength = readIntPercentage(in, subChunk);
                    break;
                case MAT_TRANSPARENCY:
                    material.mTransparency = readIntPercentage(in, subChunk);
                    break;
                case MAT_XPFALL:
                    material.mFalloff = readIntPercentage(in, subChunk);
                    break;
                case MAT_USE_XPFALL:
                    material.mUseFalloff = true;
                    break;
                case MAT_REFBLUR:
                    material.mBlur = readIntPercentage(in, subChunk);
                    break;
                case MAT_USE_REFBLUR:
                    material.mUseBlur = true;
                    break;
                case MAT_SHADING:
                    material.mShading = readShort(in, subChunk);
                    break;
                case MAT_SELF_ILLUM:
                    material.mSelfIllum = true;
                    break;
                case MAT_TWO_SIDE:
                    material.mTwoSided = true;
                    break;
                case MAT_DECAL:
                    material.mMapDecal = true;
                    break;
                case MAT_ADDITIVE:
                    material.mAdditive = true;
                    break;
                case MAT_FACEMAP:
                    material.mFaceMap = true;
                    break;
                case MAT_PHONGSOFT:
                    material.mSoften = true;
                    break;
                case MAT_WIRE:
                    material.mUseWire = true;
                    break;
                case MAT_WIREABS:
                    material.mUseWireAbs = true;
                    break;
                case MAT_WIRE_SIZE:
                    material.mWireSize = readFloat(in, subChunk);
                    break;
                case MAT_TEXMAP:
                    if (material.mTexture1Map == null)
                        material.mTexture1Map = new TextureMap();
                    readTextureMap(material.mTexture1Map, in, subChunk);
                    //System.out.println("Cyan.Loader3DS: Read texture1 map");
                    break;
                case MAT_TEXMASK:
                    if (material.mTexture1Mask == null)
                        material.mTexture1Mask = new TextureMap();
                    readTextureMap(material.mTexture1Mask, in, subChunk);                    
                    break;
                case MAT_TEX2MAP:
                    if (material.mTexture2Map == null)
                        material.mTexture2Map = new TextureMap();
                    readTextureMap(material.mTexture2Map, in, subChunk);
                    //System.out.println("Cyan.Loader3DS: Read texture2 map");
                    break;
                case MAT_TEX2MASK:
                    if (material.mTexture2Mask == null)
                        material.mTexture2Mask = new TextureMap();
                    readTextureMap(material.mTexture2Mask, in, subChunk);
                    break;
                case MAT_OPACMAP:
                    if (material.mOpacityMap == null)
                        material.mOpacityMap = new TextureMap();
                    readTextureMap(material.mOpacityMap, in, subChunk);
                    //System.out.println("Cyan.Loader3DS: Read opacity map");
                    break;
                case MAT_OPACMASK:
                    if (material.mOpacityMask == null)
                        material.mOpacityMask = new TextureMap();
                    readTextureMap(material.mOpacityMask, in, subChunk);
                    break;
                case MAT_BUMPMAP:
                    if (material.mBumpMap == null)
                        material.mBumpMap = new TextureMap();
                    readTextureMap(material.mBumpMap, in, subChunk);
                    //System.out.println("Cyan.Loader3DS: Read bump map");
                    break;
                case MAT_BUMPMASK:
                    if (material.mBumpMask == null)
                        material.mBumpMask = new TextureMap();
                    readTextureMap(material.mBumpMask, in, subChunk);                    
                    break;
                case MAT_SPECMAP:
                    if (material.mSpecularMap == null)
                        material.mSpecularMap = new TextureMap();
                    readTextureMap(material.mSpecularMap, in, subChunk);
                    System.out.println("Cyan.Loader3DS: Read specular map");
                    break;
                case MAT_SPECMASK:
                    if (material.mSpecularMask == null)
                        material.mSpecularMask = new TextureMap();
                    readTextureMap(material.mSpecularMask, in, subChunk);
                    break;
                case MAT_SHINMAP:
                    if (material.mShininessMap == null)
                        material.mShininessMap = new TextureMap();
                    readTextureMap(material.mShininessMap, in, subChunk);
                    //System.out.println("Cyan.Loader3DS: Read shininess map");
                    break;
                case MAT_SHINMASK:
                    if (material.mShininessMask == null)
                        material.mShininessMask = new TextureMap();
                    readTextureMap(material.mShininessMask, in, subChunk);
                    break;
                case MAT_SELFIMAP:
                    if (material.mSelfIllumMap == null)
                        material.mSelfIllumMap = new TextureMap();
                    readTextureMap(material.mSelfIllumMap, in, subChunk);
                    //System.out.println("Cyan.Loader3DS: Read selfillum map");
                    break;
                case MAT_SELFIMASK:
                    if (material.mSelfIllumMask == null)
                        material.mSelfIllumMask = new TextureMap();
                    readTextureMap(material.mSelfIllumMask, in, subChunk);
                    break;
                case MAT_REFLMAP:
                    if (material.mReflectionMap == null)
                        material.mReflectionMap = new TextureMap();
                    readTextureMap(material.mReflectionMap, in, subChunk);
                    //System.out.println("Cyan.Loader3DS: Read reflection map");
                    break;
                case MAT_REFLMASK:
                    if (material.mReflectionMask == null)
                        material.mReflectionMask = new TextureMap();
                    readTextureMap(material.mReflectionMask, in, subChunk);
                    break;
                case MAT_ACUBIC:
                    readByte(in, subChunk);
                    readByte(in, subChunk);
                    readShort(in, subChunk);
                    readInt(in, subChunk);
                    readInt(in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
        mScene.addMaterial(material);
    }
    
    /**
     *
     */
    private final void readColor(Color4f color, InputStream in, Chunk parentChunk) throws java.io.IOException {
        boolean haveLin = false;
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case LIN_COLOR_24:
                    readColor3b(color, in, subChunk);
                    haveLin = true;
                    break;
                case COLOR_24:
                    /* gamma corrected color chunk,  replaced in 3ds R3 by LIN_COLOR_24 */
                    if (!haveLin)
                        readColor3b(color, in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
    }
    
    /**
     *
     */
    private final void readColor3b(Color3f color, InputStream in, Chunk parentChunk) throws java.io.IOException {
        color.x = (float)readByte(in, parentChunk) / 255.0f;
        color.y = (float)readByte(in, parentChunk) / 255.0f;
        color.z = (float)readByte(in, parentChunk) / 255.0f;
    }
    
    /**
     *
     */
    private final void readColor3b(Color4f color, InputStream in, Chunk parentChunk) throws java.io.IOException {
        color.x = (float)readByte(in, parentChunk) / 255.0f;
        color.y = (float)readByte(in, parentChunk) / 255.0f;
        color.z = (float)readByte(in, parentChunk) / 255.0f;
        color.w = 1.0f;
    }
    
    /**
     *
     */
    private final void readColor3f(Color3f color, InputStream in, Chunk parentChunk) throws java.io.IOException {
        color.x = (float)readFloat(in, parentChunk);
        color.y = (float)readFloat(in, parentChunk);
        color.z = (float)readFloat(in, parentChunk);
    }
        
    /**
     *
     */
    private final float readIntPercentage(InputStream in, Chunk parentChunk) throws java.io.IOException {
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case INT_PERCENTAGE:
                    int i = readShort(in, subChunk);
                    return ((float)i / 100.0f);
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
        return 0.0f;
    }
    
    /**
     *
     */
    private final void readTextureMap(TextureMap map, InputStream in, Chunk parentChunk) throws java.io.IOException {
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case INT_PERCENTAGE:
                    map.mPercent = (float)readShort(in, subChunk) / 100.0f;
                    //System.out.println("map percent: " + map.mPercent);
                    break;
                case MAT_MAPNAME:                    
                    map.mMapName = readString(in, subChunk);
                    map.mMapName = map.mMapName.toLowerCase();
                    map.mUrl = new URL(mCodebase.getProtocol(), mCodebase.getHost(), mCodebase.getPort(), mCodebase.getFile() + map.mMapName);
                    mScene.addTextureMap(map.mMapName, map);
                    //System.out.println("map name: " + map.mMapName);
                    break;
                case MAT_MAP_TILING:
                    map.mFlags = readShort(in, subChunk);
                    //System.out.println("map tiling/flags: " + map.mFlags);
                    break;
                case MAT_MAP_TEXBLUR:
                    map.mBlur = readFloat(in, subChunk);
                    //System.out.println("map blur: " + map.mBlur);
                    break;
                case MAT_MAP_USCALE:
                    map.mScale[0] = readFloat(in, subChunk);
                    //System.out.println("map u-scale: " + map.mScale[0]);
                    break;
                case MAT_MAP_VSCALE:
                    map.mScale[1] = readFloat(in, subChunk);
                    //System.out.println("map v-scale: " + map.mScale[1]);
                    break;
                case MAT_MAP_UOFFSET:
                    map.mOffset[0] = readFloat(in, subChunk);
                    //System.out.println("map u offset: " + map.mOffset[0]);
                    break;
                case MAT_MAP_VOFFSET:
                    map.mOffset[1] = readFloat(in, subChunk);
                    //System.out.println("map v-offset: " + map.mOffset[1]);
                    break;
                case MAT_MAP_ANG:
                    map.mRotation = readFloat(in, subChunk);
                    //System.out.println("map rotation: " + map.mRotation);
                    break;
                case MAT_MAP_COL1:
                    readColor3b(map.mTint1, in, subChunk);
                    break;
                case MAT_MAP_COL2:
                    readColor3b(map.mTint2, in, subChunk);
                    break;
                case MAT_MAP_RCOL:
                    readColor3b(map.mTintR, in, subChunk);
                    break;
                case MAT_MAP_GCOL:
                    readColor3b(map.mTintG, in, subChunk);
                    break;
                case MAT_MAP_BCOL:
                    readColor3b(map.mTintB, in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
    }
    
    /**
     *
     */
    private final void readNamedObject(InputStream in, Chunk parentChunk) throws java.io.IOException {
        String name = readString(in, parentChunk);
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case N_TRI_OBJECT:
                    readMesh(name, in, subChunk);
                    break;
                case N_CAMERA:
                    readCamera(name, in, subChunk);
                    break;
                case N_DIRECT_LIGHT:
                    readLight(name, in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
    }

    /**
     *
     */
    private final void readMesh(String name, InputStream in, Chunk parentChunk) throws java.io.IOException {
        Mesh mesh = new Mesh(name);        
        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case MESH_MATRIX:
                    readMatrix(mesh.mMatrix, in, subChunk);
                    mesh.meshMatrixValid();
                    break;
                case MESH_COLOR:
                    mesh.mColor = readByte(in, subChunk);
                    break;
                case POINT_ARRAY:
                    int nrVertices = readShort(in, subChunk);
                    if (nrVertices > 0) {
                        mesh.setNrVertices(nrVertices);
                        for (int i = 0; i < nrVertices; i++) {
                            float x = readFloat(in, subChunk); 
                            float y = readFloat(in, subChunk); 
                            float z = readFloat(in, subChunk); 
                            mesh.setVertex(i, x, y, z);
                        }
                    }
                    break;
                case POINT_FLAG_ARRAY:
                    int nrFlags = readShort(in, subChunk);
                    if (nrFlags > 0) {
                        for (int i = 0; i < nrFlags; i++)
                            mesh.setFlag(i, readShort(in, subChunk));
                    }
                    break;
                case FACE_ARRAY:
                    readFaceList(mesh, in, subChunk);
                    break;
                case MESH_TEXTURE_INFO:
                    readTuple(mesh.mMapData.mTile, in, subChunk);
                    //System.out.println("map tiling: " + mesh.mMapData.mTile.x + "/" + mesh.mMapData.mTile.y);
                    readTuple(mesh.mMapData.mPos, in, subChunk);
                    mesh.mMapData.mScale = readFloat(in, subChunk);
                    readMatrix(mesh.mMapData.mMatrix, in, subChunk);
                    readTuple(mesh.mMapData.mPlanarSize, in, subChunk);
                    mesh.mMapData.mCylinderHeight = readFloat(in, subChunk);
                    break;
                case TEX_VERTS:
                    int nrTexCoords = readShort(in, subChunk);
                    if (nrTexCoords > 0) {
                        for (int i = 0; i < nrTexCoords; i++) {
                            float u = readFloat(in, subChunk);
                            float v = 1.0f - readFloat(in, subChunk);
                            mesh.setTexCoords(i, u, v);
                        }
                    }
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }        
        
        /*
        if (mCodebase != null) {
            String ttname = name + ".jpg";
            URL tooltipUrl = null;
            try {
                tooltipUrl = new URL(mCodebase.getProtocol(), mCodebase.getHost(), mCodebase.getPort(), 
                                     mCodebase.getFile() + ttname);
            }
            catch (java.net.MalformedURLException ex) {
            }

            System.out.println(tooltipUrl.toString());
            Texture tt = new Texture(tooltipUrl);
            mScene.addTooltip(tt);
        }
         */
        
        mScene.addMesh(mesh);
    }
    
    /**
     *
     */
    private final void readCamera(String name, InputStream in, Chunk parentChunk) throws java.io.IOException {
        Camera camera = new Camera(name);        
        float x = readFloat(in, parentChunk);
        float y = readFloat(in, parentChunk);
        float z = readFloat(in, parentChunk);
        camera.mPosition.set(x, y, z);
        
        x = readFloat(in, parentChunk);
        y = readFloat(in, parentChunk);
        z = readFloat(in, parentChunk);
        camera.mTarget.set(x, y, z);
        
        camera.mRoll = readFloat(in, parentChunk);
        float s = readFloat(in, parentChunk);
        if (Math.abs(s) < EPSILON)
          camera.mFov = 45.0f;
        else
          camera.mFov = 2400.0f / s;

        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case CAM_SEE_CONE:
                    camera.mSeeCone = true;
                    break;
                case CAM_RANGES:
                    camera.mNearRange = readFloat(in, subChunk);
                    camera.mFarRange = readFloat(in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
        mScene.addCamera(camera);
    }
    
    /**
     *
     */
    private final void readLight(String name, InputStream in, Chunk parentChunk) throws java.io.IOException {
        Light light = new Light(name);
        float x = readFloat(in, parentChunk);
        float y = readFloat(in, parentChunk);
        float z = readFloat(in, parentChunk);
        light.mPosition.set(x, y, z);

        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case COLOR_F:
                    readColor3f(light.mColor, in, subChunk);
                    break;
                case DL_OFF:
                    light.mIsOff = true;
                    break;
                case DL_OUTER_RANGE:
                    light.mOuterRange = readFloat(in, subChunk);
                    break;
                case DL_INNER_RANGE:
                    light.mInnerRange = readFloat(in, subChunk);
                    break;
                case DL_MULTIPLIER:
                    light.mMultiplier = readFloat(in, subChunk);
                    break;
                case DL_EXCLUDE:
                    /* FIXME: */
                    unknownChunk(in, subChunk);
                    break;
                case DL_ATTENUATE:
                    light.mAttenuation = readFloat(in, subChunk);
                    break;
                case DL_SPOTLIGHT:
                    readSpotLight(name, in, subChunk);
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
        mScene.addLight(light);
    }
    
    /**
     *
     */
    private final void readSpotLight(String name, InputStream in, Chunk parentChunk) throws java.io.IOException {
        Light light = new Light(name);
        light.mIsSpotLight = true;
        float x = readFloat(in, parentChunk);
        float y = readFloat(in, parentChunk);
        float z = readFloat(in, parentChunk);
        light.mSpot.set(x, y, z);
        light.mSpot.normalize();

        light.mHotSpot = readFloat(in, parentChunk);
        light.mFallOff = readFloat(in, parentChunk);

        Chunk subChunk = readChunkHeader(in, parentChunk);
        while (!parentChunk.complete()) {
            switch (subChunk.id()) {
                case DL_SPOT_ROLL:
                    light.mRoll = readFloat(in, subChunk);
                    break;
                case DL_SHADOWED:
                    light.mIsShadowed = true;
                    break;
                case DL_LOCAL_SHADOW2:
                    light.mShadowBias = readFloat(in, subChunk);
                    light.mShadowFilter = readFloat(in, subChunk);
                    light.mShadowSize = readShort(in, subChunk);
                    break;
                case DL_SEE_CONE:
                    light.mSeeCone = true;
                    break;
                case DL_SPOT_RECTANGULAR:
                    light.mIsRectangularSpot = true;
                    break;
                case DL_SPOT_ASPECT:
                    light.mSpotAspect = readFloat(in, subChunk);
                    break;
                case DL_SPOT_PROJECTOR:
                    light.mUseProjector = true;
                    light.mProjector = readString(in, subChunk);
                    break;
                case DL_SPOT_OVERSHOOT:
                    light.mSpotOvershoot = true;
                    break;
                case DL_RAY_BIAS:
                    light.mRayBias = readFloat(in, subChunk);
                    break;
                case DL_RAYSHAD:
                    light.mRayShadows = true;
                    break;
                default:
                    unknownChunk(in, subChunk);
            }
            subChunk = readChunkHeader(in, parentChunk);
        }
        mScene.addLight(light);
    }
    
    /**
     *
     */
    private final void readLin1Track(Track track, InputStream in, Chunk parentChunk) throws java.io.IOException {
        track.mFlags = readShort(in, parentChunk);
        readInt(in, parentChunk);
        readInt(in, parentChunk);
        int nrKeys = readInt(in, parentChunk);

        for (int i = 0; i < nrKeys; i++) {
            Lin1Key k = new Lin1Key();
            readTcb(k.mTcb, in, parentChunk);
            k.mValue.mFloatVal = readFloat(in, parentChunk);
            track.insertKey(k);
        }
        track.setup();
    }
    
    /**
     *
     */
    private final void readLin3Track(Track track, InputStream in, Chunk parentChunk) throws java.io.IOException {
        track.mFlags = readShort(in, parentChunk) + Track.REPEAT;
        readInt(in, parentChunk);
        readInt(in, parentChunk);
        int nrKeys = readInt(in, parentChunk);
        
        for (int i = 0; i < nrKeys; i++) {
            Lin3Key key = new Lin3Key();
            readTcb(key.mTcb, in, parentChunk);
            key.mValue.x = readFloat(in, parentChunk);
            key.mValue.y = readFloat(in, parentChunk);
            key.mValue.z = readFloat(in, parentChunk);
            track.insertKey(key);            
        }
        track.setup();
    }
    
    /**
     *
     */
    private final void readQuatTrack(Track track, InputStream in, Chunk parentChunk) throws java.io.IOException {
        track.mFlags = readShort(in, parentChunk) + Track.REPEAT;
        readInt(in, parentChunk);
        readInt(in, parentChunk);
        int nrKeys = readInt(in, parentChunk);
        
        for (int i = 0; i < nrKeys; i++) {
            QuatKey k = new QuatKey();
            readTcb(k.mTcb, in, parentChunk);
            k.mAngle = readFloat(in, parentChunk);
            k.mAxis.x = readFloat(in, parentChunk);
            k.mAxis.y = readFloat(in, parentChunk);
            k.mAxis.z = readFloat(in, parentChunk);
            track.insertKey(k);
        }
        track.setup();
    }
    
    /**
     *
     */
    private final void readMorphTrack(Track track, InputStream in, Chunk parentChunk) throws java.io.IOException {
        skipChunk(in, parentChunk);
    }
    
    /**
     *
     */
    private final void readBoolTrack(Track track, InputStream in, Chunk parentChunk) throws java.io.IOException {        
        track.mFlags = readShort(in, parentChunk);
        readInt(in, parentChunk);
        readInt(in, parentChunk);
        int nrKeys = readInt(in, parentChunk);

        for (int i = 0; i < nrKeys; i++) {
            BoolKey key = new BoolKey();
            readTcb(key.mTcb, in, parentChunk);
            track.insertKey(key);
        }
    }
    
    /**
     *
     */
    private final void readTcb(TcbSpline tcb, InputStream in, Chunk parentChunk) throws java.io.IOException {
        tcb.mFrame = readInt(in, parentChunk);
        tcb.mFlags = readShort(in, parentChunk);
        if ((tcb.mFlags & TcbSpline.USE_TENSION) != 0)
            tcb.mTension = readFloat(in, parentChunk);
        if ((tcb.mFlags & TcbSpline.USE_CONTINUITY) != 0)
            tcb.mContinuity = readFloat(in, parentChunk);
        if ((tcb.mFlags & TcbSpline.USE_BIAS) != 0)
            tcb.mBias = readFloat(in, parentChunk);
        if ((tcb.mFlags & TcbSpline.USE_EASE_TO) != 0)
            tcb.mEaseTo = readFloat(in, parentChunk);
        if ((tcb.mFlags & TcbSpline.USE_EASE_FROM) != 0)
            tcb.mEaseFrom=readFloat(in, parentChunk);
    }
        
    /**
     * Reads the indices into the vertex list.
     */
    private final void readFaceList(Mesh mesh, InputStream in, Chunk parentChunk) throws java.io.IOException {
        int nrFaces = readShort(in, parentChunk);
        if (nrFaces > 0) {
            mesh.setNrFaces(nrFaces);
            for (int i = 0; i < nrFaces; i++) {
                Face face = mesh.getFace(i);
                int a = readShort(in, parentChunk);
                int b = readShort(in, parentChunk);
                int c = readShort(in, parentChunk);
                face.setVertexIndices(a, b, c);
                face.setMesh(mesh);
                
                int flags = readShort(in, parentChunk);
                face.setFlags(flags);
            }

            Chunk subChunk = readChunkHeader(in, parentChunk);
            while (!parentChunk.complete()) {
                switch (subChunk.id()) {
                    case SMOOTH_GROUP:
                        //System.out.println("reading smoothing groups for mesh " + mesh.getName());
                        for (int j = 0; j < mesh.getNrFaces(); j++) {            
                            mesh.getFace(j).setSmoothingGroup(readInt(in, subChunk));
                            //System.out.println("face " + j + " has smoothing group " + mesh.getFace(j).getSmoothingGroup());
                        }
                        break;
                    case MSH_MAT_GROUP:
                        String materialName = readString(in, subChunk);
                        Material material = mScene.getMaterialByName(materialName);
                        nrFaces = readShort(in, subChunk);
                        for (int j = 0; j < nrFaces; j++) {
                            int index = readShort(in, subChunk);
                            mesh.getFace(index).setMaterial(material);
                        }
                        break;
                    case MSH_BOXMAP:                    
                        mesh.mBoxMap.mFront  = readString(in, subChunk);
                        mesh.mBoxMap.mBack   = readString(in, subChunk);
                        mesh.mBoxMap.mLeft   = readString(in, subChunk);
                        mesh.mBoxMap.mRight  = readString(in, subChunk);
                        mesh.mBoxMap.mTop    = readString(in, subChunk);
                        mesh.mBoxMap.mBottom = readString(in, subChunk);
                        break;
                    default:
                        unknownChunk(in, subChunk);
                }
                subChunk = readChunkHeader(in, parentChunk);
            }
        }
    }
    
    /**
     *
     */
    private final void readMatrix(Matrix4f m, InputStream in, Chunk parentChunk) throws java.io.IOException {
        m.setIdentity();
        m.m00 = readFloat(in, parentChunk);
        m.m10 = readFloat(in, parentChunk);
        m.m20 = readFloat(in, parentChunk);
        
        m.m01 = readFloat(in, parentChunk);
        m.m11 = readFloat(in, parentChunk);
        m.m21 = readFloat(in, parentChunk);
        
        m.m02 = readFloat(in, parentChunk);
        m.m12 = readFloat(in, parentChunk);
        m.m22 = readFloat(in, parentChunk);
        
        m.m03 = readFloat(in, parentChunk);
        m.m13 = readFloat(in, parentChunk);
        m.m23 = readFloat(in, parentChunk);
    }
    
    /**
     * Reads a bounding box.
     */
    private final void readBoundingBox(InputStream in, Chunk parentChunk) throws java.io.IOException {
        float llx = readFloat(in, parentChunk);
        float lly = readFloat(in, parentChunk);
        float llz = readFloat(in, parentChunk);
        
        float urx = readFloat(in, parentChunk);
        float ury = readFloat(in, parentChunk);
        float urz = readFloat(in, parentChunk);
    }
    
    /**
     * Reads a string.
     */
    private final String readString(InputStream in, Chunk parentChunk) throws java.io.IOException {
        String str = new String();
        byte b = (byte)in.read();
        if (parentChunk != null)
            parentChunk.crunch(1);
        while (b != 0) {
            str += (char)b;
            b = (byte)in.read();
            if (parentChunk != null)
                parentChunk.crunch(1);
        }
        return str;
    }
    
    /**
     * Reads an integer (32 bit).
     */
    private final int readInt(InputStream in, Chunk parentChunk) throws java.io.IOException {
        int i = (in.read() | (in.read() <<  8) | (in.read() << 16) | (in.read() << 24));
        if (parentChunk != null)
            parentChunk.crunch(4);
        return i;
    }
    
    /**
     * Reads a byte (8 bit).
     */
    private final int readByte(InputStream in, Chunk parentChunk) throws java.io.IOException {
        int b = in.read();
        if (parentChunk != null)
            parentChunk.crunch(1);
        return b;
    }
    
    /**
     * Reads a short (16 bit).
     */
    private final int readShort(InputStream in, Chunk parentChunk) throws java.io.IOException {
        int s = (in.read() | (in.read() << 8));
        if (parentChunk != null)
            parentChunk.crunch(2);
        return s;
    }
    
    /**
     * Reads a float (32 bit).
     */
    private final float readFloat(InputStream in, Chunk parentChunk) throws java.io.IOException {
        float f = java.lang.Float.intBitsToFloat(readInt(in, parentChunk));
        return f;
    }
    
    /**
     *
     */
    private final void readTuple(Tuple2f v, InputStream in, Chunk parentChunk) throws java.io.IOException {
        float x = readFloat(in, parentChunk);
        float y = readFloat(in, parentChunk);
        v.set(x, y);
    }
    
    /**
     *
     */
    private final void readTuple(Tuple3f v, InputStream in, Chunk parentChunk) throws java.io.IOException {
        float x = readFloat(in, parentChunk);
        float y = readFloat(in, parentChunk);
        float z = readFloat(in, parentChunk);
        v.set(x, y, z);
    }
    
    /**
     * Returns a name corresponding to the given Chunk id.
     */    
    private final String getChunkName(int chunkId) {
        return "";
        /*
        switch (chunkId) {
            case NULL_CHUNK: return "NULL_CHUNK";
            case M3DMAGIC: return "M3DMAGIC";
            case SMAGIC: return "SMAGIC";
            case LMAGIC: return "LMAGIC";
            case MLIBMAGIC: return "MLIBMAGIC";
            case MATMAGIC: return "MATMAGIC";
            case CMAGIC: return "CMAGIC";
            case M3D_VERSION: return "M3D_VERSION";
            case M3D_KFVERSION: return "M3D_KFVERSION";
            case COLOR_F: return "COLOR_F";
            case COLOR_24: return "COLOR_24";
            case LIN_COLOR_24: return "LIN_COLOR_24";
            case LIN_COLOR_F: return "LIN_COLOR_F";
            case INT_PERCENTAGE: return "INT_PERCENTAGE";
            case FLOAT_PERCENTAGE: return "FLOAT_PERCENTAGE";
            case MDATA: return "MDATA";
            case MESH_VERSION: return "MESH_VERSION";
            case MASTER_SCALE: return "MASTER_SCALE";
            case LO_SHADOW_BIAS: return "LO_SHADOW_BIAS";
            case HI_SHADOW_BIAS: return "HI_SHADOW_BIAS";
            case SHADOW_MAP_SIZE: return "SHADOW_MAP_SIZE";
            case SHADOW_SAMPLES: return "SHADOW_SAMPLES";
            case SHADOW_RANGE: return "SHADOW_RANGE";
            case SHADOW_FILTER: return "SHADOW_FILTER";
            case RAY_BIAS: return "RAY_BIAS";
            case O_CONSTS: return "O_CONSTS";
            case AMBIENT_LIGHT: return "AMBIENT_LIGHT";
            case BIT_MAP: return "BIT_MAP";
            case SOLID_BGND: return "SOLID_BGND";
            case V_GRADIENT: return "V_GRADIENT";
            case USE_BIT_MAP: return "USE_BIT_MAP";
            case USE_SOLID_BGND: return "USE_SOLID_BGND";
            case USE_V_GRADIENT: return "USE_V_GRADIENT";
            case FOG: return "FOG";
            case FOG_BGND: return "FOG_BGND";
            case LAYER_FOG: return "LAYER_FOG";
            case DISTANCE_CUE: return "DISTANCE_CUE";
            case DCUE_BGND: return "DCUE_BGND";
            case USE_FOG: return "USE_FOG";
            case USE_LAYER_FOG: return "USE_LAYER_FOG";
            case USE_DISTANCE_CUE: return "USE_DISTANCE_CUE";
            case MAT_ENTRY: return "MAT_ENTRY";
            case MAT_NAME: return "MAT_NAME";
            case MAT_AMBIENT: return "MAT_AMBIENT";
            case MAT_DIFFUSE: return "MAT_DIFFUSE";
            case MAT_SPECULAR: return "MAT_SPECULAR";
            case MAT_SHININESS: return "MAT_SHININESS";
            case MAT_SHIN2PCT: return "MAT_SHIN2PCT";
            case MAT_TRANSPARENCY: return "MAT_TRANSPARENCY";
            case MAT_XPFALL: return "MAT_XPFALL";
            case MAT_USE_XPFALL: return "MAT_USE_XPFALL";
            case MAT_REFBLUR: return "MAT_REFBLUR";
            case MAT_SHADING: return "MAT_SHADING";
            case MAT_USE_REFBLUR: return "MAT_USE_REFBLUR";
            case MAT_SELF_ILLUM: return "MAT_SELF_ILLUM";
            case MAT_TWO_SIDE: return "MAT_TWO_SIDE";
            case MAT_DECAL: return "MAT_DECAL";
            case MAT_ADDITIVE: return "MAT_ADDITIVE";
            case MAT_WIRE: return "MAT_WIRE";
            case MAT_FACEMAP: return "MAT_FACEMAP";
            case MAT_PHONGSOFT: return "MAT_PHONGSOFT";
            case MAT_WIREABS: return "MAT_WIREABS";
            case MAT_WIRE_SIZE: return "MAT_WIRE_SIZE";
            case MAT_TEXMAP: return "MAT_TEXMAP";
            case MAT_SXP_TEXT_DATA: return "MAT_SXP_TEXT_DATA";
            case MAT_TEXMASK: return "MAT_TEXMASK";
            case MAT_SXP_TEXTMASK_DATA: return "MAT_SXP_TEXTMASK_DATA";
            case MAT_TEX2MAP: return "MAT_TEX2MAP";
            case MAT_SXP_TEXT2_DATA: return "MAT_SXP_TEXT2_DATA";
            case MAT_TEX2MASK: return "MAT_TEX2MASK";
            case MAT_SXP_TEXT2MASK_DATA: return "MAT_SXP_TEXT2MASK_DATA";
            case MAT_OPACMAP: return "MAT_OPACMAP";
            case MAT_SXP_OPAC_DATA: return "MAT_SXP_OPAC_DATA";
            case MAT_OPACMASK: return "MAT_OPACMASK";
            case MAT_SXP_OPACMASK_DATA: return "MAT_SXP_OPACMASK_DATA";
            case MAT_BUMPMAP: return "MAT_BUMPMAP";
            case MAT_SXP_BUMP_DATA: return "MAT_SXP_BUMP_DATA";
            case MAT_BUMPMASK: return "MAT_BUMPMASK";
            case MAT_SXP_BUMPMASK_DATA: return "MAT_SXP_BUMPMASK_DATA";
            case MAT_SPECMAP: return "MAT_SPECMAP";
            case MAT_SXP_SPEC_DATA: return "MAT_SXP_SPEC_DATA";
            case MAT_SPECMASK: return "MAT_SPECMASK";
            case MAT_SXP_SPECMASK_DATA: return "MAT_SXP_SPECMASK_DATA";
            case MAT_SHINMAP: return "MAT_SHINMAP";
            case MAT_SXP_SHIN_DATA: return "MAT_SXP_SHIN_DATA";
            case MAT_SHINMASK: return "MAT_SHINMASK";
            case MAT_SXP_SHINMASK_DATA: return "MAT_SXP_SHINMASK_DATA";
            case MAT_SELFIMAP: return "MAT_SELFIMAP";
            case MAT_SXP_SELFI_DATA: return "MAT_SXP_SELFI_DATA";
            case MAT_SELFIMASK: return "MAT_SELFIMASK";
            case MAT_SXP_SELFIMASK_DATA: return "MAT_SXP_SELFIMASK_DATA";
            case MAT_REFLMAP: return "MAT_REFLMAP";
            case MAT_REFLMASK: return "MAT_REFLMASK";
            case MAT_SXP_REFLMASK_DATA: return "MAT_SXP_REFLMASK_DATA";
            case MAT_ACUBIC: return "MAT_ACUBIC";
            case MAT_MAPNAME: return "MAT_MAPNAME";
            case MAT_MAP_TILING: return "MAT_MAP_TILING";
            case MAT_MAP_TEXBLUR: return "MAT_MAP_TEXBLUR";
            case MAT_MAP_USCALE: return "MAT_MAP_USCALE";
            case MAT_MAP_VSCALE: return "MAT_MAP_VSCALE";
            case MAT_MAP_UOFFSET: return "MAT_MAP_UOFFSET";
            case MAT_MAP_VOFFSET: return "MAT_MAP_VOFFSET";
            case MAT_MAP_ANG: return "MAT_MAP_ANG";
            case MAT_MAP_COL1: return "MAT_MAP_COL1";
            case MAT_MAP_COL2: return "MAT_MAP_COL2";
            case MAT_MAP_RCOL: return "MAT_MAP_RCOL";
            case MAT_MAP_GCOL: return "MAT_MAP_GCOL";
            case MAT_MAP_BCOL: return "MAT_MAP_BCOL";
            case NAMED_OBJECT: return "NAMED_OBJECT";
            case N_DIRECT_LIGHT: return "N_DIRECT_LIGHT";
            case DL_OFF: return "DL_OFF";
            case DL_OUTER_RANGE: return "DL_OUTER_RANGE";
            case DL_INNER_RANGE: return "DL_INNER_RANGE";
            case DL_MULTIPLIER: return "DL_MULTIPLIER";
            case DL_EXCLUDE: return "DL_EXCLUDE";
            case DL_ATTENUATE: return "DL_ATTENUATE";
            case DL_SPOTLIGHT: return "DL_SPOTLIGHT";
            case DL_SPOT_ROLL: return "DL_SPOT_ROLL";
            case DL_SHADOWED: return "DL_SHADOWED";
            case DL_LOCAL_SHADOW2: return "DL_LOCAL_SHADOW2";
            case DL_SEE_CONE: return "DL_SEE_CONE";
            case DL_SPOT_RECTANGULAR: return "DL_SPOT_RECTANGULAR";
            case DL_SPOT_ASPECT: return "DL_SPOT_ASPECT";
            case DL_SPOT_PROJECTOR: return "DL_SPOT_PROJECTOR";
            case DL_SPOT_OVERSHOOT: return "DL_SPOT_OVERSHOOT";
            case DL_RAY_BIAS: return "DL_RAY_BIAS";
            case DL_RAYSHAD: return "DL_RAYSHAD";
            case N_CAMERA: return "N_CAMERA";
            case CAM_SEE_CONE: return "CAM_SEE_CONE";
            case CAM_RANGES: return "CAM_RANGES";
            case OBJ_HIDDEN: return "OBJ_HIDDEN";
            case OBJ_VIS_LOFTER: return "OBJ_VIS_LOFTER";
            case OBJ_DOESNT_CAST: return "OBJ_DOESNT_CAST";
            case OBJ_DONT_RECVSHADOW: return "OBJ_DONT_RECVSHADOW";
            case OBJ_MATTE: return "OBJ_MATTE";
            case OBJ_FAST: return "OBJ_FAST";
            case OBJ_PROCEDURAL: return "OBJ_PROCEDURAL";
            case OBJ_FROZEN: return "OBJ_FROZEN";
            case N_TRI_OBJECT: return "N_TRI_OBJECT";
            case POINT_ARRAY: return "POINT_ARRAY";
            case POINT_FLAG_ARRAY: return "POINT_FLAG_ARRAY";
            case FACE_ARRAY: return "FACE_ARRAY";
            case MSH_MAT_GROUP: return "MSH_MAT_GROUP";
            case SMOOTH_GROUP: return "SMOOTH_GROUP";
            case MSH_BOXMAP: return "MSH_BOXMAP";
            case TEX_VERTS: return "TEX_VERTS";
            case MESH_MATRIX: return "MESH_MATRIX";
            case MESH_COLOR: return "MESH_COLOR";
            case MESH_TEXTURE_INFO: return "MESH_TEXTURE_INFO";
            case KFDATA: return "KFDATA";
            case KFHDR: return "KFHDR";
            case KFSEG: return "KFSEG";
            case KFCURTIME: return "KFCURTIME";
            case AMBIENT_NODE_TAG: return "AMBIENT_NODE_TAG";
            case OBJECT_NODE_TAG: return "Node.OBJECT_NODE_TAG";
            case CAMERA_NODE_TAG: return "CAMERA_NODE_TAG";
            case TARGET_NODE_TAG: return "TARGET_NODE_TAG";
            case LIGHT_NODE_TAG: return "LIGHT_NODE_TAG";
            case L_TARGET_NODE_TAG: return "L_TARGET_NODE_TAG";
            case SPOTLIGHT_NODE_TAG: return "SPOTLIGHT_NODE_TAG";
            case NODE_ID: return "NODE_ID";
            case NODE_HDR: return "NODE_HDR";
            case PIVOT: return "PIVOT";
            case INSTANCE_NAME: return "INSTANCE_NAME";
            case MORPH_SMOOTH: return "MORPH_SMOOTH";
            case BOUNDBOX: return "BOUNDBOX";
            case POS_TRACK_TAG: return "POS_TRACK_TAG";
            case COL_TRACK_TAG: return "COL_TRACK_TAG";
            case ROT_TRACK_TAG: return "ROT_TRACK_TAG";
            case SCL_TRACK_TAG: return "SCL_TRACK_TAG";
            case MORPH_TRACK_TAG: return "MORPH_TRACK_TAG";
            case FOV_TRACK_TAG: return "FOV_TRACK_TAG";
            case ROLL_TRACK_TAG: return "ROLL_TRACK_TAG";
            case HOT_TRACK_TAG: return "HOT_TRACK_TAG";
            case FALL_TRACK_TAG: return "FALL_TRACK_TAG";
            case HIDE_TRACK_TAG: return "HIDE_TRACK_TAG";
            case POLY_2D: return "POLY_2D";
            case SHAPE_OK: return "SHAPE_OK";
            case SHAPE_NOT_OK: return "SHAPE_NOT_OK";
            case SHAPE_HOOK: return "SHAPE_HOOK";
            case PATH_3D: return "PATH_3D";
            case PATH_MATRIX: return "PATH_MATRIX";
            case SHAPE_2D: return "SHAPE_2D";
            case M_SCALE: return "M_SCALE";
            case M_TWIST: return "M_TWIST";
            case M_TEETER: return "M_TEETER";
            case M_FIT: return "M_FIT";
            case M_BEVEL: return "M_BEVEL";
            case XZ_CURVE: return "XZ_CURVE";
            case YZ_CURVE: return "YZ_CURVE";
            case INTERPCT: return "INTERPCT";
            case DEFORM_LIMIT: return "DEFORM_LIMIT";
            case USE_CONTOUR: return "USE_CONTOUR";
            case USE_TWEEN: return "USE_TWEEN";
            case USE_SCALE: return "USE_SCALE";
            case USE_TWIST: return "USE_TWIST";
            case USE_TEETER: return "USE_TEETER";
            case USE_FIT: return "USE_FIT";
            case USE_BEVEL: return "USE_BEVEL";
            case DEFAULT_VIEW: return "DEFAULT_VIEW";
            case VIEW_TOP: return "VIEW_TOP";
            case VIEW_BOTTOM: return "VIEW_BOTTOM";
            case VIEW_LEFT: return "VIEW_LEFT";
            case VIEW_RIGHT: return "VIEW_RIGHT";
            case VIEW_FRONT: return "VIEW_FRONT";
            case VIEW_BACK: return "VIEW_BACK";
            case VIEW_USER: return "VIEW_USER";
            case VIEW_CAMERA: return "VIEW_CAMERA";
            case VIEW_WINDOW: return "VIEW_WINDOW";
            case VIEWPORT_LAYOUT_OLD: return "VIEWPORT_LAYOUT_OLD";
            case VIEWPORT_DATA_OLD: return "VIEWPORT_DATA_OLD";
            case VIEWPORT_LAYOUT: return "VIEWPORT_LAYOUT";
            case VIEWPORT_DATA: return "VIEWPORT_DATA";
            case VIEWPORT_DATA_3: return "VIEWPORT_DATA_3";
            case VIEWPORT_SIZE: return "VIEWPORT_SIZE";
            case NETWORK_VIEW: return "NETWORK_VIEW";
            default: return "*** UNKNOWN ***";
        }
         */
    }
}

