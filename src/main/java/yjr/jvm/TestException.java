package yjr.jvm;
public class TestException {
	void testEx2(){
		try {
			int b = 12;
			int c = 0;
			int d = b/c;
		} catch (ArithmeticException e) {
			System.out.println("testEx2, catch ArithmeticException");
		} catch(Exception e){
			System.out.println("exceptin");
		}finally {
			System.out.println("testEx2, finally; return value=");
		}
	}
}
