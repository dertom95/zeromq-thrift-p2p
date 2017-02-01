package org.tt.zmq.tests;

public class TestUtils {
	
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

}
