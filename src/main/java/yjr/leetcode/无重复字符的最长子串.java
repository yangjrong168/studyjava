package yjr.leetcode;

public class 无重复字符的最长子串 {
    
	
	
	
	public static int getLength(String s) {
		if(s==null || s.equals("")) {
			return 0;
		}
		char[] chars = s.toCharArray();
		int[] arrLength = new int[chars.length];
		int totalChar = chars.length;
		for(int i=0;i<chars.length;i++) {
			int length = 1;//最小是1
			int j = i+1;   // j 不断右移
			while(j < totalChar) {
				// j->i 没有相等的 长度加1
				boolean eq = false;
				for(int k=i;k<j;k++) {
					if(chars[j]==chars[k]) {
						eq = true;
						break;
					}
				}
				j++;
				if(!eq) {
					length=length+1;
				}else {
					break;
				}
			}	
			arrLength[i] = length;
		}
		int max = 1;
		for(int c:arrLength) {
			System.out.print(c+" ");
			if(c>max) {
				max = c;
			}
		}
		return max;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
       String str ="pwwkew";
       getLength(str);
	}

}
