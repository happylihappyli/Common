package com.funnyai.data;

import java.util.Stack;

public class TreapEnumerator<T> {

    // the treap uses the stack to order the nodes 
    private Stack<TreapNode> pStack = null;
    // return the keys 
    private boolean keys;
    // return in ascending order (true) or descending (false) 
    private boolean ascending;
    // key 
    private IComparable ordKey;
    // the data or value associated with the key 
    private T objValue;

    public IComparable Key() {
        return ordKey;
    }
    ///<summary> 
    ///Data 
    ///</summary> 

    public T Value() {
        return objValue;
    }

    public TreapEnumerator() {
    }
    ///<summary> 
    /// Determine order, walk the tree and push the nodes onto the stack 
    ///</summary> 

    public TreapEnumerator(TreapNode tnode, boolean ascending) {//boolean keys,

        pStack = new Stack<>();
//        this.keys = keys;
        this.ascending = ascending;

        // find the lowest node 
        if ((ascending)) {
            while (tnode != null) {
                pStack.push(tnode);
                tnode = tnode.left;
            }
        } else {
            // find the highest or greatest node 
            while (tnode != null) {
                pStack.push(tnode);
                tnode = tnode.right;
            }
        }

    }
    
    
    ///<summary> 
    /// HasMoreElements 
    ///</summary> 
    public boolean HasMoreElements() {
        return (pStack.size() > 0);
    }
    
    
    public IComparable NextKey() {

        if (pStack.isEmpty()) {
            //throw new TreapException("Element not found"); 
            return null;
        }

        // the top of stack will always have the next item 
        // get top of stack but don't remove it as the next nodes in sequence 
        // may be pushed onto the top 
        // the stack will be popped after all the nodes have been returned 
        TreapNode<T> node = (TreapNode) pStack.peek();

        if (ascending) {
            // if right node is nothing, the stack top is the lowest node 
            // if left node is nothing, the stack top is the highest node 
            if (node.right == null) {
                // walk the tree 
                TreapNode<T> tn = (TreapNode) pStack.pop();
                while ((HasMoreElements()) && (tn == ((TreapNode) pStack.peek()).right)) {
                    tn = (TreapNode) pStack.pop();
                }
            } else {
                // find the next items in the sequence 
                // traverse to left; find lowest and push onto stack 
                TreapNode<T> tn = node.right;
                while ((tn != null)) {
                    pStack.push(tn);
                    tn = tn.left;
                }
            }
        } // descending
        else if (node.left == null) {
            // walk the tree 
            TreapNode tn = (TreapNode) pStack.pop();
            while ((HasMoreElements()) && (tn == ((TreapNode) pStack.peek()).left)) {
                tn = (TreapNode) pStack.pop();
            }
        } else {
            // find the next items in the sequence 
            // traverse to right; find highest and push onto stack 
            TreapNode<T> tn = node.left;
            while (tn != null) {
                pStack.push(tn);
                tn = tn.right;
            }
        }

        // the following is for .NET compatibility (see MoveNext()) 
        ordKey = node.Key;
        objValue = node.Data;

        //Object objValue = (keys == true ? node.key : node.element); 
        return node.Key; //objValue;
    }
    
    
    ///<summary> 
    /// NextElement 
    ///</summary> 

    public T NextElement() {

        if (pStack.isEmpty()) {
            //throw new TreapException("Element not found"); 
            return null;
        }

        // the top of stack will always have the next item 
        // get top of stack but don't remove it as the next nodes in sequence 
        // may be pushed onto the top 
        // the stack will be popped after all the nodes have been returned 
        TreapNode<T> node = (TreapNode) pStack.peek();

        if (ascending) {
            // if right node is nothing, the stack top is the lowest node 
            // if left node is nothing, the stack top is the highest node 
            if (node.right == null) {
                // walk the tree 
                TreapNode<T> tn = (TreapNode) pStack.pop();
                while ((HasMoreElements()) && (tn == ((TreapNode) pStack.peek()).right)) {
                    tn = (TreapNode) pStack.pop();
                }
            } else {
                // find the next items in the sequence 
                // traverse to left; find lowest and push onto stack 
                TreapNode<T> tn = node.right;
                while ((tn != null)) {
                    pStack.push(tn);
                    tn = tn.left;
                }
            }
        } // descending
        else if (node.left == null) {
            // walk the tree 
            TreapNode tn = (TreapNode) pStack.pop();
            while ((HasMoreElements()) && (tn == ((TreapNode) pStack.peek()).left)) {
                tn = (TreapNode) pStack.pop();
            }
        } else {
            // find the next items in the sequence 
            // traverse to right; find highest and push onto stack 
            TreapNode<T> tn = node.left;
            while (tn != null) {
                pStack.push(tn);
                tn = tn.right;
            }
        }

        ordKey = node.Key;
        objValue = node.Data;

        return node.Data;//(keys == true ? node.Key : node.Data); //objValue;
    }
    
    
    ///<summary> 
    /// MoveNext 
    /// For .NET compatibility 
    ///</summary> 

    public boolean MoveNext() {

        if (HasMoreElements()) {
            NextElement();
            return true;
        } else {
            return false;
        }

    }
}
