package org.tt.zmq.thrift;

import java.util.Iterator;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

public class ServiceBroker implements Runnable {
	
	private long pollTimeout = 1000;
	
	private String identity;
	private String backendEndpoint;
	private String frontendEndpoint;
	private boolean bind;
	private ZContext context;
	private boolean isRunning = true;

	private Socket backend;

	private Socket frontend;
	
	public ServiceBroker(ZContext context,String identity,String frontendEndpoint,boolean bind) {
		this(context,identity,frontendEndpoint,"inproc://"+identity,bind);
	}

	
	public ServiceBroker(ZContext context,String identity,String frontendEndpoint,String backendEndpoint,boolean bind) {
		this.identity = identity;
		this.backendEndpoint = backendEndpoint;
		this.frontendEndpoint = frontendEndpoint;
		this.bind = bind;
		this.context = context;
		// create the frontend-socket distribute messages to the right client
        frontend = context.createSocket(ZMQ.DEALER);
        frontend.setIdentity(identity.getBytes());
        if (bind) {
            frontend.bind(frontendEndpoint);
        } else {
            frontend.connect(frontendEndpoint);
        }
        // create the backend-socket as router to 
        backend  = context.createSocket(ZMQ.ROUTER);
        backend.setIdentity(backendEndpoint.getBytes());
        backend.bind(backendEndpoint);		
	}
	
	public void setPollTimeout(long timeout) {
		this.pollTimeout = timeout;
	}
	
	public void stop() {
		isRunning = false;
	}
	
	@Override
	public void run() {



//        System.out.println("launch and connect broker.");

        //  Initialize poll set
        Poller items = new Poller (2);
        items.register(frontend, Poller.POLLIN);
        items.register(backend, Poller.POLLIN);

        boolean more = false;
        byte[] message;

        //  Switch messages between sockets
        while (!Thread.currentThread().isInterrupted() && isRunning) {            
            //  poll and memorize multipart detection
            items.poll();

            if (items.pollin(0)) {
                while (true) {
                    // receive message
                    message = frontend.recv(0);
//                    System.out.println("msg from frontend:"+new String(message));
                    more = frontend.hasReceiveMore();

                    // Broker it
                    backend.send(message, more ? ZMQ.SNDMORE : 0);
                    if(!more){
                        break;
                    }
                }
            }
            if (items.pollin(1)) {
            	// this is actually a bit hacky
            	ZMsg msg = ZMsg.recvMsg(backend);
            	// check if this marked as reply and discard the last routing-information
            	Iterator<ZFrame> iter = msg.iterator();
            	iter.next();
            	if (iter.next().toString().equals("reply")){
            		// it is a reply, throw aways the first two frames of the ZMsg, otherwise the message would be sent back
            		// immediately
            		msg.pop();
            	}
        		msg.send(frontend);
            }
        }
    }

}
