package yjr.leetcode.thread;

public class ZeroEvenOdd {
	int n;
	public ZeroEvenOdd(int n) {
		this.n = n;
	}
	int printNumber = 1;
	int i = 1;  // 1  打0 2 其他
	boolean  even = true;// true  奇数    odd 偶数
	// 构造函数
	public synchronized void zero() {
		while(printNumber<n) {
			while(i!=1) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.print(0);
			i=2;
			notifyAll();
		}
		
	} // 仅打印出 0

	public synchronized void even() {
		while(printNumber<n) {
			while(i!=2) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(even) {
				System.out.print("e"+printNumber);
		        i = 1;
				printNumber++;
				even = !even;
			}else {
				i = 2;
			}
			
	        notifyAll();
		}
	} // 仅打印出 偶数

	public synchronized void odd() {
		while(printNumber<n) {
			while(i!=2) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!even) {// 打印
				System.out.print("o"+printNumber);
		        i = 1;
				printNumber++;
				even = !even;
			}else {
		        i = 2;
			}
			
	        notifyAll();	
		}
		
	} // 仅打印出 奇数

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ZeroEvenOdd fooBar = new ZeroEvenOdd(5);
		Thread a = new Thread(new Runnable() {
			@Override
			public void run() {
				fooBar.zero();
			}
		});
		Thread b = new Thread(new Runnable() {
			@Override
			public void run() {
				fooBar.even();
			}
		});
		Thread c = new Thread(new Runnable() {
			@Override
			public void run() {
				fooBar.odd();
			}
		});

		a.start();
		b.start();
		c.start();
	}

}
