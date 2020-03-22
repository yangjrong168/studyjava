package yjr.leetcode.link;


public class HebingLianbiao {
	   
	
	public ListNode mergeKLists(ListNode[] lists) {
		ListNode head = lists[0];
		ListNode pre = null;
		pre = head;
		ListNode curr = null;
        for(int i = 1;i<lists.length;i++) {
            curr = lists[i];
            if(curr.val > pre.val) {
            	
            }
        	
        }
		
		return null;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HebingLianbiao hebin = new HebingLianbiao();
		ListNode head = hebin.arrToListNode("1,4,5");
		hebin.printListNode(head);
		ListNode[] lists = new ListNode[3];
		lists[0] = head;
		lists[1] = hebin.arrToListNode("1,3,4");
		lists[2] = hebin.arrToListNode("2,6");
		
		hebin.mergeKLists(lists);
		
	}	
	public ListNode arrToListNode(String vals) {
		String[] arr = vals.split(",");
		ListNode head = null;
		ListNode pre = null;
		for(int i =0 ;i<arr.length;i++) {
			if(i==0) {
				int v = Integer.parseInt(arr[i]);
				ListNode node = new ListNode(v);
				head = node;
				pre = head;
			}else {
				int v = Integer.parseInt(arr[i]);
				ListNode node = new ListNode(v);
				pre.next = node;
				pre = node;
			}			
		}
		return head;
	}
	
	public void printListNode(ListNode head) {
		ListNode curr = head;
		while(curr != null) {	
			System.out.print("->"+curr.val);
			curr = curr.next;
		}
	}
}
