package org.tt.zmq.thrift;

import org.apache.thrift.TProcessor;
import org.tt.thrift.ClientService.Iface;
import org.zeromq.ZContext;

public class LocalService<T extends TProcessor> implements Runnable {

	private ZContext context;
	private String connectTo;
	private Iface handler;
	private T processor;
	private String identity;
	private TZeroMQSimpleServer server;

	public LocalService(ZContext context, String identity, String connectTo, T processor) {
		this.context = context;
		this.connectTo = connectTo;
		this.processor = processor;
		this.identity = identity;
	}

	@Override
	public void run() {
		TZeroMQSimpleServer.Args args = new TZeroMQSimpleServer.Args(context, connectTo, false).identity(identity);
		args.processor(processor);
		server = new TZeroMQSimpleServer(args);
		server.serve();
	}
	
	public void stop() {
		server.stop();
	}
	
	public String getCurrentCallerRoute(String service){
		return server.getRoute(service);
	}

}
