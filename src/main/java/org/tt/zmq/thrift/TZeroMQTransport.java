package org.tt.zmq.thrift;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

public class TZeroMQTransport extends TTransport {

    private ZMQ.Socket socket;
    private final ZContext context;
    private final List<String> endpoints;
    private final int type;
    private final boolean bind;
    private String identity=null;
    private List<ZFrame> route = new ArrayList<>();

    private final TMemoryInputTransport is = new TMemoryInputTransport();
    private final ByteArrayOutputStream os=new ByteArrayOutputStream(1024);

    public TZeroMQTransport(Socket socket) {
    	this.socket = socket;
    	endpoints = new ArrayList<>();
    	context = null;
    	type = socket.getType();
    	bind = false;
    }

    
    public TZeroMQTransport(ZContext context, List<String> endpoints,
            int type, boolean bind) {
        super();
        this.context = context;
        this.endpoints = endpoints;
        this.type = type;
        this.bind = bind;
    }

    public TZeroMQTransport(ZContext context, String endpoint, int type,
            boolean bind) {
        this(context, Arrays.asList(endpoint), type, bind);
    }

    public void setSocket(ZMQ.Socket socket) {
    	this.socket = socket;
    }
    
    @Override
    public boolean isOpen() {
        return socket != null;
    }
    
    public void addRoute(String route){
    	this.route.add(new ZFrame(route));
    }
    
    public void clearRoute() {
    	this.route.clear();
    }

    @Override
    public void open() throws TTransportException {
        if (socket == null) {
            socket = context.createSocket(type);
            socket.setLinger(0);
            if (identity!=null) {
//            	System.out.println("Set transport identity:"+identity);
            	socket.setIdentity(identity.getBytes());
            }
            for (String endpoint : endpoints) {
                if (bind) {
                    socket.bind(endpoint);
                } else {
                    socket.connect(endpoint);
                }
            }
        }
    }

    @Override
    public void close() {
        socket.close();
        socket = null;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        if (isOpen()) {
            checkRead();
            return is.read(buf, off, len);
        } else {
            throw new IllegalStateException("transport not open");
        }
    }

    private void checkRead() {
        // TODO process zmq envelop
        if (is.getBuffer() == null||is.getBytesRemainingInBuffer()==0) {
            byte[] data = socket.recv();
            is.reset(data);
        }
    }




    @Override
    public void flush() throws TTransportException {
        byte[] data=os.toByteArray();
        os.reset();
        //TODO send zmq envelop;
        if (!route.isEmpty()) {
        	ZMsg msg = new ZMsg();
        	msg.push(data);
            for (ZFrame routeFrame : route) {
            	msg.push(routeFrame);
//            	socket.sendMore(routeFrame.toString());
            }
//            socket.send(data,0);
            msg.send(socket);
        } else {
            ZMsg msg = new ZMsg();
            // mark this msg to be a call that should be passed to a service
//            msg.push("service-call");
            msg.push(data);
            msg.send(socket);
        }
//        socket.send(data,0);
    }


    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        os.write(buf, off, len);
    }


	public void setIdentity(String identity) {
		this.identity = identity;
	}

}
