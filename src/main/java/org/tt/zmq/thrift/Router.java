package org.tt.zmq.thrift;

import java.util.Iterator;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class Router {

	/**
	 * Simple Router which distributes service-calls to their respective services and their replies back
	 * This will usually stand somewhere accessable in the internet for the services to connect to.
	 * It is not needed that the computer where the services run have to open ports for this to work.
	 *
	 *  The routing of the replies is a bit hacky.
	 *  
	 */
	public Router(String endpoint) {
		ZContext ctx = new ZContext();
		
		ZMQ.Socket forwardingSocket = ctx.createSocket(ZMQ.ROUTER);
		forwardingSocket.setIdentity("FORWARDER".getBytes());
		forwardingSocket.bind(endpoint);
		
		while (true) {
			try{
				ZMsg incoming = ZMsg.recvMsg(forwardingSocket); 
				System.out.println("FORWARD GOT:"+incoming);
				
	        	Iterator<ZFrame> iter = incoming.iterator();
	        	iter.next();
	        	String check = iter.next().toString();
//	        	if (check.equals("client-call")){
//	        		incoming.send(forward)
//	        	}
//	        	else 
        		if (check.equals("reply")){
	        		incoming.pop();
	        		incoming.pop();
	        		incoming.send(forwardingSocket);
	        		continue;
	        	}
				
				ZFrame fromAddress = incoming.pop();
				ZFrame fromService = incoming.pop();
				ZFrame toAddress = incoming.pop();
				ZFrame toService = incoming.pop();
				incoming.push(fromService);
				incoming.push(fromAddress);
				incoming.push(toService);
				incoming.push(toAddress);
				incoming.send(forwardingSocket);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		
		String port = args.length==0?"tcp://*:9001":args[0];
		new Router(port);
	}
	
	
}
