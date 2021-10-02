/*
 * SceneGraphObject.java
 *
 * Created on February 23, 2002, 2:55 PM
 */

package com.sofascience.cyan2;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import com.sofascience.cyan2.vecmath.*;

/**
 * SceneGraphObject is the base class for all objects (nodes) in a scene graph.
 *
 * @author  Karl Traunmueller
 */
abstract class SceneGraphObject {
    
    /**
     * Default constructor.
     */
    SceneGraphObject() {
    }

    /**
     *
     */
    void indent(int level) {
        for (int i = 0; i < level; i++)
            System.out.print("  ");
    }
    
    /*
    abstract void dump(int level);
     */
    
    /*
    void dumpMatrix(int level, Matrix4f m) {
        if (m == null) {
            indent(level); System.out.println("*** Matrix is null ***");
            return;
        }
        indent(level); System.out.println(m.m00 + " " + m.m01 + " " + m.m02 + " " + m.m03);
        indent(level); System.out.println(m.m10 + " " + m.m11 + " " + m.m12 + " " + m.m13);
        indent(level); System.out.println(m.m20 + " " + m.m21 + " " + m.m22 + " " + m.m23);
        indent(level); System.out.println(m.m30 + " " + m.m31 + " " + m.m32 + " " + m.m33);
    }
    
    void dumpTuple(int level, Tuple2f v) {
        if (v == null) {
            indent(level); System.out.println("*** Vector is null ***");
            return;
        }
        indent(level); System.out.println("(" + v.x + ", " + v.y + ")");
    }
    
    void dumpTuple(int level, Tuple3f v) {
        if (v == null) {
            indent(level); System.out.println("*** Vector is null ***");
            return;
        }
        indent(level); System.out.println("(" + v.x + ", " + v.y + ", " + v.z + ")");
    }
    
    void dumpQuat(int level, Quat4f q) {
        if (q == null) {
            indent(level); System.out.println("*** Quaternion is null ***");
            return;
        }
        indent(level); System.out.println("(" + q.x + ", " + q.y + ", " + q.z + ", " + q.w + ")");
    }
        
    void dumpColor(int level, Color3f color) {
        if (color == null) {
            indent(level); System.out.println("*** Color is null ***");
            return;
        }
        indent(level); System.out.println("(" + color.x + ", " + color.y + ", " + color.z + ")");
    }
    
    void dumpObjects(int level, Vector objects) {
        if (objects == null) {
            indent(level); System.out.println("*** Scene graph object vector is null ***");
            return;
        }
        for (int i = 0; i < objects.size(); i++) {
            SceneGraphObject object = (SceneGraphObject)(objects.elementAt(i));
            object.dump(level);
        }
    }
    
    void dumpObjects(int level, Hashtable objects) {
        if (objects == null) {
            indent(level); System.out.println("*** Scene graph object hashtable is null ***");
            return;
        }
        Enumeration keys = objects.keys();
        while (keys.hasMoreElements()) {
            SceneGraphObject object = (SceneGraphObject)(keys.nextElement());
            object.dump(level);
        }
    }
    
    void dumpObjects(int level, SceneGraphObject[] objects) {
        if (objects == null) {
            indent(level); System.out.println("*** Scene graph object array is null ***");
            return;
        }
        for (int i = 0; i < objects.length; i++) {            
            objects[i].dump(level);
        }
    }
     */
}
