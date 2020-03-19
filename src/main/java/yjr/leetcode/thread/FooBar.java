package yjr.leetcode.thread;

public class FooBar {
	int n =10;
	boolean flag = true;
	public synchronized void foo() throws InterruptedException {
	    for (int i = 0; i < n; i++) {
	      while(!flag) {
		     wait();
	      }
	      System.out.print("foo");
	      flag = false;
	      notify();
	    }
	  }

	  public synchronized void bar() throws InterruptedException {
	    for (int i = 0; i < n; i++) {
	    	while(flag) {
			     wait();
		     }
		     System.out.print("bar");
		     flag = true;
		     notify();
	    }
	  }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FooBar fooBar = new FooBar();
		Thread a = new Thread(new Runnable() {
				@Override
				public void run() {
					
					try {
						fooBar.foo();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		});
		Thread b = new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					fooBar.bar();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	});
     a.start();
     b.start();
	}

}
