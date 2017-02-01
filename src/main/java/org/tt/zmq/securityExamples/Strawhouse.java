package org.tt.zmq.securityExamples;

import java.nio.charset.Charset;

import org.zeromq.ZAuth;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * This is the java version of zmq-security-samples: http://hintjens.com/blog:49#toc3
 * 
 * @author ttrocha
 *
 */

//The Strawhouse Pattern
//
//We allow or deny clients according to their IP address. It may keep
//spammers and idiots away, but won't stop a real attacker for more
//than a heartbeat.
public class Strawhouse {

	public static void main(String[] args) {
	    //  Create context
		ZContext ctx = new ZContext();
		
	    //  Start an authentication engine for this context. This engine
	    //  allows or denies incoming connections (talking to the libzmq
	    //  core over a protocol called ZAP).
		ZAuth auth = new ZAuth(ctx);
	    //  Get some indication of what the authenticator is deciding
	    auth.setVerbose(true);
	    //  Whitelist our address; any other address will be rejected
	    auth.allow("127.0.0.2");
	        
	    //  Create and bind server socket
	    ZMQ.Socket server = ctx.createSocket(ZMQ.PUSH);
	    server.setZAPDomain("global".getBytes());
	    server.bind("tcp://*:9000");
	    
	    //  Create and connect client socket
	    ZMQ.Socket client = ctx.createSocket(ZMQ.PULL);
	    client.connect("tcp://127.0.0.1:9000");
	    
	    //  Send a single message from server to client
	    server.send("Hello");
	    String message = client.recvStr(0,Charset.defaultCharset());
	    
	    if (message.equals("Hello")) {
	    	System.out.println("Strawhouse test OK");
	    }
	}

}
