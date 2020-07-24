package yjr.leetcode.array;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreeSum {
	 public List<List<Integer>> threeSum(int[] nums) {
		 List<List<Integer>> iList = new ArrayList<>();
         for(int i = 0;i< nums.length;i++) {
        	 int ii = nums[i];
        	 for(int j = i+1;j<nums.length;j++) {
        		    int jj = nums[j];
        		 for( int k = j+1;k<nums.length;k++) {
        			 int kk = nums[k];
        			 if(ii + jj + kk == 0) {
						 boolean notIn = true;
						 if(iList.size()>0){
							 for(List<Integer> tempList : iList) {
							 	 int h = 0;
								 for (int l = 0; l < 3 ; l++) {
									 int tt = tempList.get(l);
									 if(tt == ii || tt == jj || tt == kk){
                                        h++;
									 }
								 }
								 if(h>=2){
									 notIn = false;
									 break;
								 }
							 }
						 }
						 if(notIn){
							 List<Integer> list = new ArrayList<>();
							 list.add(ii);
							 list.add(jj);
							 list.add(kk);
							 iList.add(list);
						 }
        			 }
        		 }
        	 }
         }
		 
		 return iList;
	 }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ThreeSum obj = new ThreeSum();
		int[] nums = new int[] {0,3,0,1,1,-1,-5,-5,3,-3,-3,0};
		System.out.println("------------------");
		List<List<Integer>> iList = obj.threeSum(nums);
		iList.stream().forEach(s->{s.stream().forEach(k->System.out.println(k));});
		System.out.println("---------------------");
		boolean b = Arrays.stream(nums).anyMatch(s-> s == 7);
		System.out.println("b=="+b);
		 int [] tt = Arrays.stream(nums).sorted().toArray();
		System.out.println(tt);

	}

}
