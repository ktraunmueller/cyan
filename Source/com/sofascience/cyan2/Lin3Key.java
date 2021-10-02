/*
 * Lin3Key.java
 *
 * Created on September 27, 2002, 11:52 AM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Vector3f;

/**
 *
 * @author  Karl Traunmueller
 */
class Lin3Key extends Key {

    Vector3f  mValue;
    Vector3f  mDd;
    Vector3f  mDs;

    /** 
     * Creates a new instance of Lin3Key.
     */
    Lin3Key() {        
        mValue = new Vector3f();
        mDd = new Vector3f();
        mDs = new Vector3f();
    }

    /**
     *
     */
    static void setup(Lin3Key p, Lin3Key cp, Lin3Key c, Lin3Key cn, Lin3Key n) {
        if (cp == null)
            cp = c;
        if (cn == null)
            cn = c;
        
        if (p == null && n == null) {
            c.mDs.set(0.0f, 0.0f, 0.0f);
            c.mDd.set(0.0f, 0.0f, 0.0f);
            return;
        }

        TcbHelper helper = new TcbHelper();
        Vector3f np = new Vector3f();
        Vector3f nn = new Vector3f();
            
        if (n != null && p != null) {
            helper.init(p.mTcb, cp.mTcb, c.mTcb, cn.mTcb, n.mTcb);            
            np.sub(c.mValue, p.mValue);
            nn.sub(n.mValue, c.mValue);

            c.mDs.x = helper.ksm * np.x + helper.ksp * nn.x;
            c.mDd.x = helper.kdm * np.x + helper.kdp * nn.x;
            c.mDs.y = helper.ksm * np.y + helper.ksp * nn.y;
            c.mDd.y = helper.kdm * np.y + helper.kdp * nn.y;
            c.mDs.z = helper.ksm * np.z + helper.ksp * nn.z;
            c.mDd.z = helper.kdm * np.z + helper.kdp * nn.z;
        }
        else {
            if (p != null) {
                np.sub(c.mValue, p.mValue);
                c.mDs.set(np);
                c.mDd.set(np);
            }
            if (n != null) {
                nn.sub(n.mValue, c.mValue);
                c.mDs.set(nn);
                c.mDd.set(nn);
            }
        }
    }
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- LIN3KEY");
        mTcb.dump(level + 1);
        indent(level + 1); System.out.print("value: "); dumpTuple(0, mValue);
        indent(level + 1); System.out.print("dd: "); dumpTuple(0, mDd);
        indent(level + 1); System.out.print("ds: "); dumpTuple(0, mDs);
    }
     */
}
