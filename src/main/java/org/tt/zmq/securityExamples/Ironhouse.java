package org.tt.zmq.securityExamples;

import java.nio.charset.Charset;

import org.zeromq.ZAuth;
import org.zeromq.ZCert;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * This is the java version of zmq-security-samples: http://hintjens.com/blog:49#toc6
 * 
 * @converted by thomas (dot) trocha (at) gmail (dot) com
 *
 */

//The Ironhouse Pattern
//
//Security doesn't get any stronger than this. An attacker is going to
//have to break into your systems to see data before/after encryption.
public class Ironhouse {

	private static final String CERTIFICATE_FOLDER=".curve";
	
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
	    auth.allow("127.0.0.1");
	    //  Tell authenticator to use the certificate store in .curve
	    auth.configureCurve(CERTIFICATE_FOLDER);

	    //  We'll generate a new client certificate and save the public part
	    //  in the certificate store (in practice this would be done by hand
	    //  or some out-of-band process).
	    ZCert client_cert = new ZCert();
	    client_cert.setMeta("name", "Client test certificate");
	    client_cert.savePublic(CERTIFICATE_FOLDER+"/testcert.pub");
	    
	    ZCert server_cert = new ZCert();
	    
	    //  Create and bind server socket
	    ZMQ.Socket server = ctx.createSocket(ZMQ.PUSH);
	    server.setZAPDomain("global".getBytes());
	    server.setCurveServer(true);
	    server.setCurvePublicKey(server_cert.getPublicKey());
	    server.setCurveSecretKey(server_cert.getSecretKey());
	    server.bind("tcp://*:9000");
	    
	    //  Create and connect client socket
	    ZMQ.Socket client = ctx.createSocket(ZMQ.PULL);
	    client.setCurvePublicKey(client_cert.getPublicKey());
	    client.setCurveSecretKey(client_cert.getSecretKey());
	    client.setCurveServerKey(server_cert.getPublicKey());
	    client.connect("tcp://127.0.0.1:9000");
	    
	    //  Send a single message from server to client
	    server.send("Hello");
	    String message = client.recvStr(0,Charset.defaultCharset());
	    
	    if (message.equals("Hello")) {
	    	System.out.println("Strawhouse test OK");
	    }
	    ctx.close();
	}

}
