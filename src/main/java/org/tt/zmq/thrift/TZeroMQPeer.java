package org.tt.zmq.thrift;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransportException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class TZeroMQPeer {

	private Map<String,LocalService<TProcessor>> serviceMap;
	private ServiceBroker broker;
	private ZContext context;
	private String servername;
	private TProtocol protocol;
	private TZeroMQTransport transport;
	/**
	 * Create the peer. The peer is essentially a Thrift Server and Client in one. Using only one connection outgoing to the internet.
 	 * Interally a ZeroMQ-Router distributes incoming calls to the services and the client replies. Any of the services and the client have
 	 * a unique ZeroMQ-Socket to the internal Router.
 	 *  
	 * CAUTION: The servername HAVE TO BE UNIQUE within the net. Otherwise ZeroMQ will only serve one. Not sure how this is handled. But 
	 *          for sure not as intended (as far as you don't want to get data that is usually not meant for you ;) )
	 * 
	 * @param servername the name this Peer will have in the network. 
	 * @param forwarderEndpoint The endpoint to the Main-Router which normally lies in the internet callable for any other client. For this use the {@link Router}
	 */
	public TZeroMQPeer(String servername,String forwarderEndpoint)  {
		serviceMap = new HashMap<>();
		this.servername = servername;
		
		context = new ZContext();
		Thread threadBroker = new Thread(broker = new ServiceBroker(context,servername, forwarderEndpoint, false));
		threadBroker.setName("LocalBroker");
		threadBroker.start();
		
		transport = new TZeroMQTransport(context, "inproc://"+servername, ZMQ.DEALER,false);
		transport.setIdentity("client-call");
		try {
			transport.open();
		} catch (TTransportException e1) {
			e1.printStackTrace();
		}
		
		protocol = new TBinaryProtocol(transport);
	}
	/**
	 * 
	 * @param serviceName The name this service should be available by the clients setTarget(...)-method
	 * @param processor The ThriftProcessor that wraps around the service's iface (e.g. peer.addService("ServerService", new ServerService.Processor<>(serverServiceImpl)); )
	 */
	public void addService(String serviceName,TProcessor processor){
		LocalService<TProcessor> localService = new LocalService<>(context, serviceName,"inproc://"+servername, processor);
		Thread thLocalService = new Thread(localService);
		thLocalService.setName(serviceName);
		thLocalService.start();		
		serviceMap.put(serviceName, localService);
	}
	
	public LocalService getLocalService(String name) {
		return serviceMap.get(name);
	}
	
	public TProtocol getClientProtocol() {
		return protocol;
	}

	/**
	 * Set's the recipient for this client-call. use this notation "servername.servicename".
	 * Let's say you have somewhere a server/peer with name:server1 and it has a thrift-service added under the name 'theservice' 
	 * you have to set target to 'server1.theservice'
	 * 
	 * @param outgoingRoute the dot seperated path 'server.service'
	 */
	public void setClientTarget(String outgoingRoute){
		String[] routes = outgoingRoute.trim().split("\\.");
		transport.clearRoute();
		for (int i=routes.length-1;i>=0;i--){
			String route = routes[i];
			if (!route.isEmpty()) {
				transport.addRoute(route);
			}
		}
	} 
	

}
