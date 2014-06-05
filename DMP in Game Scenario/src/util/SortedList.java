package util;

import model.RRTNode;

public class SortedList {
	private Node head = null;
	private int size = 0;
	
	
	/*sorted list is only for getNearestNodes function use.
	 * To increase the efficiency, we only save the 20 smallest value of nodes*/
	public SortedList(){
		
	}
	
	public void insert(RRTNode treeNode, float value){
		Node newNode = new Node(treeNode, value);
		if(head == null){
			head = newNode;
			size++;
		}
		else if(head.value > newNode.value){
			newNode.next = head;
			head = newNode;
			size++;
		}
		else{
			Node tmp = head;
			int i;
			for(i = 0; tmp.next != null && i<20; i++){
				if(tmp.next.value > newNode.value){
					newNode.next = tmp.next;
					tmp.next = newNode;
					size++;
					return;
				}
				else
					tmp = tmp.next;
			}
			if(i>=20)
				return;
			tmp.next = newNode;
			size++;
		}
	}
	
	public RRTNode getNode(int index){
		Node tmp = head;
		for(int i=0; i<index; i++){
			if(tmp != null){
				tmp = tmp.next;
			}
			else
				return null;
		}
		
		if(tmp == null){
			return null;
		}
		return tmp.treeNode;
	}
	
	public int getSize(){
		return size;
	}
	
	private class Node{
		private RRTNode treeNode = null;
		private float value = 0;
		private Node next = null;
		
		public Node(RRTNode treeNode, float value){
			this.treeNode = treeNode;
			this.value = value;
		}
	}
}
