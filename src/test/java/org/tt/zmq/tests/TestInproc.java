package org.tt.zmq.tests;

import java.util.concurrent.Semaphore;

import org.junit.Test;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class TestInproc {

	@Test
	public void testInprocSocket() {
		ZContext context = new ZContext();
		
		// bind two sockets with inproc-transport
		ZMQ.Socket server = context.createSocket(ZMQ.ROUTER);
		server.bind("inproc://data");
		
		ZMQ.Socket client = context.createSocket(ZMQ.DEALER);
		client.connect("inproc://data");
		
		boolean ok = client.send("f95");
		assert(ok);
		ZMsg msg = ZMsg.recvMsg(server);
		String data = msg.getLast().toString();
		assert(data.equals("f95"));
		System.out.println("RECV:"+msg);
		
		// send a msg back
		msg.removeLast();
		msg.addLast("tommy".getBytes());
		msg.send(server);
		
		ZMsg clientIncomingMsg = ZMsg.recvMsg(client);
		System.out.println("Client Received:"+clientIncomingMsg);
		data = clientIncomingMsg.getLast().toString();
		assert(data.equals("tommy"));
		
		context.close();
	}
	
	@Test
	public void testInprocSocketThreaded() {
		// same logic as before, but this time in two threads
		
		final ZContext context = new ZContext();
		
		final Semaphore s = new Semaphore(-1);
		final Semaphore waitForCreation = new Semaphore(0);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// bind two sockets with inproc-transport
				ZMQ.Socket server = context.createSocket(ZMQ.ROUTER);
				server.bind("inproc://data");
				waitForCreation.release();
				ZMsg msg = ZMsg.recvMsg(server);
				String data = msg.getLast().toString();
				assert(data.equals("f95"));
				System.out.println("RECV:"+msg);
				// send a msg back
				msg.removeLast();
				msg.addLast("tommy".getBytes());
				msg.send(server);	
				s.release();
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// bind two sockets with inproc-transport
				try {
					waitForCreation.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ZMQ.Socket client = context.createSocket(ZMQ.DEALER);
				client.connect("inproc://data");
				
				boolean ok = client.send("f95");
				assert(ok);
				
				ZMsg clientIncomingMsg = ZMsg.recvMsg(client);
				System.out.println("Client Received:"+clientIncomingMsg);
				String data = clientIncomingMsg.getLast().toString();
				assert(data.equals("tommy"));
				s.release();
			}
		}).start();
		
		try {
			s.acquire(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		context.close();
		System.out.println("Context-Closed");
	}
	
}
