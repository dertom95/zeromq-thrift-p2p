package org.tt.zmq.securityExamples;

import java.nio.charset.Charset;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * This is the java version of zmq-security-samples: http://hintjens.com/blog:49#toc2
 * 
 * @author ttrocha
 *
 */

//The Grasslands Pattern
//
//The Classic ZeroMQ model, plain text with no protection at all.
public class Grasslands {

	public static void main(String[] args){
	    //  Create context
	    ZContext ctx = new ZContext();
	    
	    //  Create and bind server socket
	    ZMQ.Socket server = ctx.createSocket(ZMQ.PUSH);
	    server.bind("tcp://*:9000");
	    
	    //  Create and connect client socket
	    ZMQ.Socket client = ctx.createSocket(ZMQ.PULL);
	    client.connect("tcp://127.0.0.1:9000");
	    
	    //  Send a single message from server to client
	    server.send("Hello");
	    String message = client.recvStr(0,Charset.defaultCharset());
	    
	    if (message.equals("Hello")) {
	    	System.out.println("Grasslangs test OK");
	    }
	}
	
}
