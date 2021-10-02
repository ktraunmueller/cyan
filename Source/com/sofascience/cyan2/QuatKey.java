/*
 * QuatKey.java
 *
 * Created on September 27, 2002, 12:10 PM
 */

package com.sofascience.cyan2;

import com.sofascience.cyan2.vecmath.Vector3f;

/**
 *
 * @author  Karl Traunmueller
 */
class QuatKey extends Key {
        
    Vector3f  mAxis;
    float     mAngle;
    Quat4fEx  mQ;
    Quat4fEx  mDd;
    Quat4fEx  mDs;

    /** Creates a new instance of QuatKey */
    QuatKey() {
        mAxis = new Vector3f();
        mQ = new Quat4fEx(0.0f, 0.0f, 0.0f, 1.0f);
        mDd = new Quat4fEx(0.0f, 0.0f, 0.0f, 1.0f);
        mDs = new Quat4fEx(0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    /**
     *
     */
    static void setup(QuatKey p, QuatKey cp, QuatKey c, QuatKey cn, QuatKey n) {
        /*
        Quat4fEx q = null;
        Quat4fEx qp = null;
        Quat4fEx qn = null;
        Quat4fEx qa = null;
        Quat4fEx qb = null;

        if (cp == null)
            cp = c;
        if (cn == null)
            cn = c;

        if (p == null || n == null) {
            c.mDs.set(c.mQ);
            c.mDd.set(c.mQ);
            return;
        }

        if (p != null) {
            if (p.mAngle > Constants.TWOPI - Constants.EPSILON) {
                qp.setAxisAngle(p.mAxis, 0.0f);
                qp.ln();
            }
            else {
              q.set(p.mQ);
              if (q.dot(c.mQ) < 0.0f)
                  q.scale(-1.0f);
              qp.lnDif(c.mQ, q);
            }
        }
        if (n != null) {
            if (n.mAngle > Constants.TWOPI - Constants.EPSILON) {                
                qn.setAxisAngle(n.mAxis, 0.0f);
                qn.ln();
            }
            else {
                q.set(n.mQ);
                if (q.dot(c.mQ) < 0.0f)
                    q.scale(-1.0f);
                qn.lnDif(c.mQ, q);
            }
        }

        TcbHelper helper = new TcbHelper();
        if (n != null && p != null) {
            helper.init(p.mTcb, cp.mTcb, c.mTcb, cn.mTcb, n.mTcb);
            qa.x = -0.5f * (helper.kdm * qn.x + helper.kdp * qp.x);
            qb.x = -0.5f * (helper.ksm * qn.x + helper.ksp * qp.x);
            qa.y = -0.5f * (helper.kdm * qn.y + helper.kdp * qp.y);
            qb.y = -0.5f * (helper.ksm * qn.y + helper.ksp * qp.y);
            qa.z = -0.5f * (helper.kdm * qn.z + helper.kdp * qp.z);
            qb.z = -0.5f * (helper.ksm * qn.z + helper.ksp * qp.z);
            qa.w = -0.5f * (helper.kdm * qn.w + helper.kdp * qp.w);
            qb.w = -0.5f * (helper.ksm * qn.w + helper.ksp * qp.w);
            qa.exp();
            qb.exp();

            c.mDs.mul(c.mQ, qa);
            c.mDd.mul(c.mQ, qb);
        }
        else {
            if (p != null) {
                qp.exp();
                c.mDs.mul(c.mQ, qp);
                c.mDd.mul(c.mQ, qp);
            }
            if (n != null) {
                qn.exp();
                c.mDs.mul(c.mQ, qn);
                c.mDd.mul(c.mQ, qn);
            }
        }
         */
    }          
    
    /**
     *
     */
    /*
    void dump(int level) {
        indent(level); System.out.println("---- QUATKEY");
        mTcb.dump(level + 1);
        indent(level + 1); System.out.print("axis: "); dumpTuple(0, mAxis);
        indent(level + 1); System.out.println("angle: " + mAngle);
        indent(level + 1); System.out.print("q: "); dumpQuat(0, mQ);
        indent(level + 1); System.out.print("dd: "); dumpQuat(0, mDd);
        indent(level + 1); System.out.print("ds: "); dumpQuat(0, mDs);
    }
     */
    
}
