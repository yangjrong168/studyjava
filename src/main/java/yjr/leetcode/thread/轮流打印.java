package yjr.leetcode.thread;
/*
 * 深刻理解  synchronized   wait()  sleep()  notify() notifyAll()    
 */
public class 轮流打印 {
	public 轮流打印() {

	}

	public  int flag = 1; //  这里不需要volatile(可见性)

	public synchronized void one() {
		try {
			while (flag != 1) { // 竞态条件   用while 每次进来都循环判断条件是否满足    不能用if
				wait(); // 交还锁	 下次进来从该位置开始执行
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("one-"+flag);
		flag++;
		notifyAll(); // 通知等待的队列获取锁

	}

	public synchronized void two() {
		try {
			while (flag != 2) { // 竞态条件
				wait(); // 交还锁	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("two-"+flag);
		flag++;
		notifyAll(); // 通知等待的队列获取锁
		

	}

	public synchronized void three() {
		try {
			while (flag != 3) { // 竞态条件
				wait(); // 交还锁	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("three-"+flag);
		flag++;
		notifyAll(); // 通知等待的队列获取锁

	}

	public static void main(String[] args) {
		轮流打印 foo = new 轮流打印();
		Thread a = new Thread(new Runnable() {
			@Override
			public void run() {			
				foo.one();
			}

		});
		Thread b = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				foo.two();
			}

		});
		Thread c = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				foo.three();
			}
		});

		c.start();
		b.start();
		a.start();
	}
}
