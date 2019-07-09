package com.funnyai.data;


public class Treap<T> {
    //private Random rndPriority = new Random();

    private int intCount;
    // random priority to keep the treap balanced
    // the number of key-and-value pairs contained in the treap
    // used for quick comparisons
    //private int intHashCode = rndPriority.Next();
    private TreapNode<T> treapTree;
    private boolean boolKeyFound;
    public int KeyMaxLen;
    // identIfies the owner of the treap
    // the treap

    //代表Key的字符串最长为多少，一般不用
    public Treap() {
        //this.strIdentIfier = strIdentIfier;
    }

//    public Treap Add_Treap_IfNone(IComparable key) {
//        Treap pTreap;
//        pTreap = (Treap) this.find(key);
//        if (pTreap == null) {
//            pTreap = new Treap();
//            this.insert(key, pTreap);
//        }
//        return pTreap;
//    }

    /**
     * 添加节点
     * @param key
     * @param data
     * @return 
     */
    public synchronized  TreapNode<T> insert(IComparable key, T data) {
        //S_File_Text.Write("Treap.txt", this.Size()+"\r\n",true);
        if ((key == null || data == null)) {
            return null;
            //throw new TreapException("Treap key and data must not be Nothing");
        }

        // create New node
        TreapNode<T> node = new TreapNode();

        node.Key = key;
        node.Data = data;
        // generate random priority
        node.height = 1;
        //node.priority = rndPriority.Next();
        boolKeyFound = false;

        // Insert node into treapTree
        treapTree = InsertNode(node, treapTree);

        if (boolKeyFound) {
            //Throw New TreapException("A Node with the same key already exists")
        } else {
            intCount = intCount + 1;
        }

        return treapTree;
    }

    ///<summary>
    /// InsertNode
    /// inserts a node into the tree - note recursive method
    /// this method rebalances the tree using the priorities
    ///
    /// Note: The lower the number, the higher the priority
    ///<summary>
    private synchronized TreapNode<T> InsertNode(TreapNode<T> node, TreapNode tree) {

        if ((tree == null)) {
            return node;
        }

        int result = node.Key.compareTo(tree.Key);

        if (result < 0) {
            tree.left = InsertNode(node, tree.left);
            if (tree.right != null) {
                if (tree.left.height - tree.right.height > 1) {
                    tree = tree.RotateRight();
                }
            } else {
                tree = tree.RotateRight();
            }
        } else if (result > 0) {
            tree.right = InsertNode(node, tree.right);
            if (tree.left != null) {
                if (tree.right.height - tree.left.height > 1) {
                    tree = tree.RotateLeft();
                }
            } else {
                tree = tree.RotateLeft();
            }
        } else {
            boolKeyFound = true;
            tree.Data = node.Data;
        }

        tree.calculateHeight();

        return tree;

    }

    
    /**
     * 查找节点
     * Gets the data associated with the specified key
     * @param key
     * @return 
     */
    public T find(IComparable key) {

        TreapNode<T> treeNode = treapTree;
        int result;

        //Dim treeNode_old As TreapNode
        while (treeNode != null) {
            result = key.compareTo(treeNode.Key);
            if (result == 0) {
                return treeNode.Data;
            }

            //treeNode_old = treeNode
            if (result < 0) {
                treeNode = treeNode.left;
            } else {
                treeNode = treeNode.right;
            }
        }
        return null;
    }
    
    /**
     * GetMinKey
     * Returns the minimum key value
     * @return 
     */
    public IComparable GetMinKey() {

        TreapNode<T> treeNode = treapTree;

        if (treeNode == null) {
            return null;
        }

        while (treeNode.left != null) {
            treeNode = treeNode.left;
        }

        return treeNode.Key;
    }
    
    /**
     * 查找最大的Key
     * Returns the maximum key value
     * @return 
     */
    public IComparable GetMaxKey() {

        TreapNode<T> treeNode = treapTree;

        if (treeNode == null){
            return null;
        }

        while (treeNode.right != null) {
            treeNode = treeNode.right;
        }

        return treeNode.Key;

    }
    
    
    ///<summary>
    /// GetMinValue
    /// Returns the object having the minimum key value
    ///<summary>
    public Object GetMinValue() {
        return find(GetMinKey());
    }
    
    
    ///<summary>
    /// GetMaxValue
    /// Returns the object having the maximum key
    ///<summary>
    public Object GetMaxValue() {
        return find(GetMaxKey());
    }
    ///<summary>
    /// GetEnumerator
    ///<summary>

    public TreapEnumerator GetEnumerator() {
        return Elements(true);
    }
    
    /**
     * Keys
     * If ascending is True, the keys will be returned in ascending order, else
     * the keys will be returned in descending order.
     * @return TreapEnumerator
     */
    public TreapEnumerator Keys() {
        return Keys(true);
    }

    public TreapEnumerator Keys(boolean ascending) {
        return new TreapEnumerator(treapTree,ascending);
    }
    ///<summary>
    /// Values
    /// .NET compatibility
    ///<summary>

    public TreapEnumerator<T> Values() {
        return Elements(true);
    }
    ///<summary>
    /// Elements
    /// Returns an enumeration of the data objects.
    /// If ascending is true, the objects will be returned in ascending order,
    /// else the objects will be returned in descending order.
    ///<summary>

    public TreapEnumerator<T> Elements() {
        return Elements(true);
    }

    public TreapEnumerator<T> Elements(boolean ascending) {
        return new TreapEnumerator(treapTree, ascending);
    }
    ///<summary>
    /// IsEmpty
    ///<summary>

    public boolean IsEmpty() {
        return (treapTree == null);
    }
    
    /**
     * 删除节点
     * @param key
     * @return 
     */
    public synchronized  boolean remove(IComparable key) {

        boolKeyFound = false;

        treapTree = Delete(key, treapTree);

        if (boolKeyFound) {
            intCount = intCount - 1;
        }

        return boolKeyFound;
    }
    
    /**
     * RemoveMin
     * removes the node with the minimum key
     * @return 
     */
    public Object RemoveMin() {

        // start at top
        TreapNode<T> treeNode = treapTree;
        TreapNode<T> prevTreapNode;

        if (treeNode == null){
            return null;
        }

        if (treeNode.left == null){
            // remove top node by replacing with right
            treapTree = treeNode.right;
        } else {
            do {
                // find the minimum node
                prevTreapNode = treeNode;
                treeNode = treeNode.left;
            } while ((treeNode.left != null));
            // remove left node by replacing with right node
            prevTreapNode.left = treeNode.right;
        }

        intCount = intCount - 1;
        return treeNode.Data;
    }

    ///<summary>
    /// RemoveMax
    /// removes the node with the maximum key
    ///<summary>
    public Object RemoveMax(){
        // start at top
        TreapNode<T> treeNode = treapTree;
        TreapNode<T> prevTreapNode;

        if (treeNode == null){
            return null;
            //throw new TreapException("Treap is null");
        }

        if ((treeNode.right == null)) {
            // remove top node by replacing with left
            treapTree = treeNode.left;
        } else {
            do {
                // find the maximum node
                prevTreapNode = treeNode;
                treeNode = treeNode.right;
            } while ((treeNode.right != null));
            // remove right node by replacing with left node
            prevTreapNode.right = treeNode.left;
        }

        intCount = intCount - 1;

        return treeNode.Data;

    }
    
    ///<summary>
    /// Clear
    ///<summary>

    public void Clear() {
        treapTree = null;
        intCount = 0;
    }
    ///<summary>
    /// Size
    ///<summary>

    public int Size() {
        // number of keys
        return intCount;
    }
    ///<summary>
    /// Delete
    /// deletes a node - note recursive function
    /// Deletes works by "bubbling down" the node until it is a leaf, and then
    /// pruning it off the tree
    ///<summary>

    private TreapNode<T> Delete(IComparable key, TreapNode<T> tNode) {

        if ((tNode == null)) {
            return null;
        }

        int result = key.compareTo(tNode.Key);

        if ((result < 0)) {
            tNode.left = Delete(key, tNode.left);
            if (tNode.left != null) {
                tNode.left.calculateHeight();
            }
        } else if ((result > 0)) {
            tNode.right = Delete(key, tNode.right);
            if (tNode.right != null) {
                tNode.right.calculateHeight();
            }
        } else {
            boolKeyFound = true;
            tNode = tNode.DeleteRoot();
            if (tNode != null) {
                tNode.calculateHeight();
            }
        }
        return tNode;
    }
}
