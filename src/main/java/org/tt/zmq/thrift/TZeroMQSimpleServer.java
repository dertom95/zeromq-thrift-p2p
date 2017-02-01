package org.tt.zmq.thrift;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;


public class TZeroMQSimpleServer {

    public static class Args {
        final ZContext context;
        final String endpoint;
        final boolean bind;
        
        String identity=null;
        int socketType=ZMQ.DEALER;
        TProcessorFactory processorFactory;
        TProtocolFactory inputProtocolFactory = new TBinaryProtocol.Factory();
        TProtocolFactory outputProtocolFactory = new TBinaryProtocol.Factory();

        public Args(ZContext context, String endpoint,boolean bind) {
            this.context = context;
            this.endpoint = endpoint;
            this.bind = bind;
        }

        public Args identity(String identity) {
        	this.identity=identity;
        	return this;
        }
        
        public Args socketType(int socketType) {
        	this.socketType = socketType;
        	return this;
        }
        
        public Args processorFactory(TProcessorFactory factory) {
            this.processorFactory = factory;
            return this;
        }

        public Args processor(TProcessor processor) {
            this.processorFactory = new TProcessorFactory(processor);
            return this;
        }

        public Args protocolFactory(TProtocolFactory factory) {
            this.inputProtocolFactory = factory;
            this.outputProtocolFactory = factory;
            return this;
        }

        public Args inputProtocolFactory(TProtocolFactory factory) {
            this.inputProtocolFactory = factory;
            return this;
        }

        public Args outputProtocolFactory(TProtocolFactory factory) {
            this.outputProtocolFactory = factory;
            return this;
        }
    }

    private final ZMQ.Socket socket;
    private volatile boolean running = false;
    private final TProcessorFactory processorFactory;
    private final TProtocolFactory inputProtocolFactory;
    private final TProtocolFactory outputProtocolFactory;
    private final CountDownLatch stopLatch=new CountDownLatch(1);
    private ZMsg currentMessage = null;

    public TZeroMQSimpleServer(Args args) {
        socket = args.context.createSocket(args.socketType);
        socket.setLinger(0);
        if (args.identity!=null) {
//        	System.out.println("Set identity to "+args.identity);
        	socket.setIdentity(args.identity.getBytes());
        }
//        socket.setReceiveTimeOut(1000);
        if (args.bind){
            socket.bind(args.endpoint);
        } else {
        	socket.connect(args.endpoint);
        }
        processorFactory=args.processorFactory;
        inputProtocolFactory=args.inputProtocolFactory;
        outputProtocolFactory=args.outputProtocolFactory;
    }

    public Socket getSocket() {
    	return socket;
    }
    
    public String getRoute(String serviceName) {
    	if (currentMessage==null){
    		return null;
    	}
    	
    	StringBuilder b = new StringBuilder();
    	ZMsg copy = currentMessage.duplicate();
    	copy.removeLast();
    	copy.addLast(serviceName);
    	Iterator<ZFrame> frames = copy.iterator();
    	while (frames.hasNext()){
    		ZFrame frame = frames.next();
    		b.append('.').append(frame.toString());
    	}
    	return b.substring(1);
    }
    
    public void serve() {
        running=true;
        while(running){
        	currentMessage = ZMsg.recvMsg(socket);
//        	String peek = msg.peekFirst().toString();
//        	if (peek.equals(MainServer.MAIN_CLIENT)){
//        		msg.pop();
//        		msg.send(socket);
//        		continue;
//        	}
//        	System.out.println("Received:"+msg);
        	
//        	ZFrame address = null;
//        	if (socket.getType()==ZMQ.ROUTER) {
//        		address = msg.unwrap();
//        	}
        	byte[] data = currentMessage.getLast().getData();
        	currentMessage.removeLast();
        	
        	if(data==null){
                continue;
            }

            ByteArrayInputStream input=new ByteArrayInputStream(data);
            ByteArrayOutputStream output=new ByteArrayOutputStream(256);
            TTransport transport=new TIOStreamTransport(input, output);
            TProcessor processor=processorFactory.getProcessor(transport);
            TProtocol inpro=inputProtocolFactory.getProtocol(transport);
            TProtocol outpro=outputProtocolFactory.getProtocol(transport);
            try {
                processor.process(inpro, outpro);
            } catch (TException e) {
                e.printStackTrace();
            }
            byte[] result=output.toByteArray();
            
            if (result.length!=0){
            	// is this NOT a oneway method
            	currentMessage.addLast(result);
            	currentMessage.push("reply");
            	currentMessage.send(socket);
            }
        	currentMessage = null;
//            ZMsg outMsg = new ZMsg();
//            outMsg.push(result);
//            if (address!=null) {
//                outMsg.push(address);
//            }
//            outMsg.send(socket);
            
//            socket.send(result,0);
        }
        stopLatch.countDown();

    }

    public void stop(){
        running=false;
        try {
            stopLatch.await();
        } catch (InterruptedException e) {
        }
        socket.close();
    }

}
