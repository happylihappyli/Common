package com.funnyai.data;

import java.util.LinkedList;
/**
 * 直接用 java.util.Queue即可
 * @author happyli
 */
public class C_Queue{

	private final LinkedList pList=new LinkedList();

	 public void put(Object o){
		 pList.addLast(o);
	 }
	 
	 public int Count(){
		 return pList.size();
	 }
	 
	 //使用removeFirst()方法，返回队列中第一个数据，然后将它从队列中删除
	 public Object get(){
		 if (pList.size()>0){
			 Object pObj=pList.get(0);
			 pList.removeFirst();
			 return pObj;
		 }else{
			 return null;
		 }
	 }
	 
	 public Object Dequeue(){
		 return get();
	 }
	 
	 public Object peek(){
		 return pList.get(0);
	}
	 
	 public boolean empty(){
		 return pList.isEmpty();
	 }
	 
	 public C_Queue Clone(){
            C_Queue pQueue=new C_Queue();

            for (int i=0;i<pList.size();i++){
               pQueue.put(pList.get(i));
            }
            return pQueue;
	 }

	public void Enqueue(Object o){
		 this.put(o);
	 }

}
