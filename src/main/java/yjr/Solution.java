package yjr;

public class Solution {
	//输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
//	输出：7 -> 0 -> 8
	//原因：342 + 465 = 807
	 public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
		 
		 ListNode node1  = l1;
		 ListNode node2 = l2;
		 ListNode headResult = null;
		 ListNode result = null;
		 int carry = 0;// 进位
		 boolean run = true;
		 while(run) {
			 int a = 0;
			 int b = 0;	
			 run = false;
			 System.out.println("---");
			 if(node1!=null) {
				 a = node1.val;
				 node1 = node1.next;
				 run = true;
			 }
			 if(node2!=null) {
				b = node2.val;
			    node2 = node2.next;
				 run = true;
			}
			 if(!run) {
				break; 
			 }
			 int c = a + b + carry;
			 if(c>=10) {
				 carry = 1;
				 c = c - 10;
			 }else {
				 carry = 0;
			 }
			 if(result == null) {
				 result = new ListNode(c);
				 headResult = result;
			 }else {
				 ListNode temp = new ListNode(c);
				 result.next = temp;
				 result = temp;
			 }
		 }
		 if(carry==1 && result!=null) {
			 System.out.println("-------");
			 ListNode temp = new ListNode(1);
			 result.next = temp;
		 }
		 
		 
		 return headResult;

	 }
	 public static void addTwoNumbers() {
		 ListNode l1 = new ListNode(2);
			ListNode l1_1 = new ListNode(4);
			ListNode l1_2 = new ListNode(3);
	        l1.next = l1_1;
	        l1_1.next = l1_2;
	       
	        
	        
	        ListNode l2 = new ListNode(5);
			ListNode l2_1 = new ListNode(6);
			ListNode l2_2 = new ListNode(4);
	        l2.next = l2_1;
	        l2_1.next = l2_2;       
	        ListNode headResult = new Solution().addTwoNumbers(l1, l2);
	        
	        ListNode curr = headResult;
	        while(curr!=null) {
	        	System.out.print("->"+curr.val);
	        	curr = curr.next;
	        }
	 }
	 public ListNode reverseBetween(ListNode head, int m, int n) {
		 
		 int i = 1;
		 ListNode node = head;
		 int left = m;
		 int right = n;
		 ListNode leftNode = null;ListNode rightNode = null;
		 while(node !=null) {
			 if(i==left) {
				 leftNode =  node;
			 }
			 if(i==right) {
				 rightNode =  node;
			 }			 
			 node = node.next;
			 i++;
		 }
		 // 调转 leftNode 和 rightNode
		 int tempVal = leftNode.val;
		 leftNode.val = rightNode.val;
		 rightNode.val = tempVal;
		 left = left+1;right = right -1;
		 if(left<right) {
			 reverseBetween(head,left,right);
		 }
         
		 return head;
	 }
	 
	 public static void reverseBetween() {
		 //输入: 1->2->3->4->5->NULL, m = 2, n = 4
		    ListNode l1 = new ListNode(1);
			ListNode l1_1 = new ListNode(2);
			ListNode l1_2 = new ListNode(3);
			ListNode l1_3 = new ListNode(4);
			ListNode l1_4 = new ListNode(5);
			ListNode l1_5 = new ListNode(6);
			ListNode l1_6 = new ListNode(7);


	        l1.next = l1_1;
	        l1_1.next = l1_2;
	        l1_2.next = l1_3;
	        l1_3.next = l1_4;
	        l1_4.next = l1_5;
	        l1_5.next = l1_6;
	        l1_6.next = null;
	        ListNode listResult = new  Solution().reverseBetween(l1, 2, 6);
	        ListNode curr = listResult;
	        while(curr!=null) {
	        	System.out.print("->"+curr.val);
	        	curr = curr.next;
	        }
	 }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//reverseBetween();
		addTwoNumbers();	
	}
	

}
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
}