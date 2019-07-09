package com.funnyai.data;


// Note that this class is not accessible outside
// of package DataStructures
public class TreapNode<T> {
    // Constructors

    TreapNode() {
        // IComparable theKey,Object pObj
        //this(theKey, pObj,null, null );
    }

    TreapNode(IComparable theKey, T pObj, TreapNode lt, TreapNode rt) {
        Key = theKey;
        Data = pObj;
        left = lt;
        right = rt;
        height = 0;
        //priority= randomObj.randomInt( );
    }
    // Friendly data; accessible by other package routines
    IComparable Key;      // The data in the node
    T Data;      // The data in the node
    TreapNode left;         // Left child
    TreapNode right;        // Right child
    //int        priority;     // Priority
    int height;//平衡树的高度

    public synchronized TreapNode RotateLeft() {
        TreapNode temp = right;
        right = right.left;
        temp.left = this;
        this.calculateHeight();
        temp.calculateHeight();
        return temp;
    }

    /// RotateRight
    /// Rebalance the tree by rotating the nodes to the right
    public synchronized TreapNode RotateRight() {

        TreapNode temp = left;
        left = left.right;
        temp.right = this;

        this.calculateHeight();
        temp.calculateHeight();
        return temp;
    }

    public void calculateHeight() {
        //计算高度
        int iLeft = 0;
        int iRight = 0;
        if (left != null) {
            iLeft = left.height;
        }
        if (right != null) {
            iRight = right.height;
        }
        this.height = Math.max(iLeft, iRight) + 1;
    }

    public TreapNode DeleteRoot() {
        TreapNode temp;
        if ((left == null)) {
            return right;
        }

        if ((right == null)) {
            return left;
        }

        if (left.height > right.height) {
            temp = RotateRight();
            temp.right = DeleteRoot();
        } else {
            temp = RotateLeft();
            if (temp!=null)
                temp.left = DeleteRoot();
        }

        //计算height
        temp.calculateHeight();

        return temp;

    }
}
