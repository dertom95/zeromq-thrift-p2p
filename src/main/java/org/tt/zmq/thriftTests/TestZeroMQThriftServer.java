package org.tt.zmq.thriftTests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.apache.thrift.TException;
import org.tt.thrift.ClientService;
import org.tt.thrift.ServerService;
import org.tt.thrift.User;
import org.tt.zmq.thrift.TZeroMQPeer;

public class TestZeroMQThriftServer {
	/**
	 * This Application represents a server that also call's the client's pushMessageToClient
	 * @param args
	 */
	
	private static String clientTarget = null;
	private static Semaphore s = new Semaphore(0);
	
	public static void main(String[] args) {
		// set the name of this server. this MUST be unique in the whole distributed system. The router is dumb and won't check this
		// and can actually be a security problem
		String servername = args.length>0?args[0]:UUID.randomUUID().toString().substring(0,5);
		
		// the target which will get client-calls
		// this can be changed before any call via:
		// peer.setClientTarget(target);
		String target = args.length>1?args[1]:"server.ServerService";
		
		// the routerEndpoint which distributes all calls. 
		String routerEndpoint = args.length>2?args[1]:"tcp://127.0.0.1:9001";
		
		// create the peer that is Thrift Server and Client in one and connect to the Router which must be connectable for all other
		// clients
		final TZeroMQPeer peer = new TZeroMQPeer(servername,routerEndpoint);		
		
		// the implementation of the ServerService that will be handled by this server
		// so any other server (and actually me as well) can call this service via their Thrift-Client
		ServerService.Iface serverServiceImpl = new ServerService.Iface(){
			List<User> users=new ArrayList<>();
			{
				users.add(new User("tom", "tom@tom.com"));
				users.add(new User("max", "max@tom.com"));
			}
			
			@Override
			public List<User> getOnlineUsers() throws TException {
				String caller =  peer.getLocalService("ServerService").getCurrentCallerRoute("ClientService");
				clientTarget = caller;
				System.out.println();
//				System.out.println("Client wants the onlineusers");
				if (s.availablePermits()==0){
					s.release();
				}
				return users;
			}

			@Override
			public User addUser(String username, String email) throws TException {
				User newUser = new User();
				users.add(newUser);
				return newUser;
			}
			@Override
			public void printOnServer(String st) throws TException {
				// this is a oneway method, so the client won't wait for a reply
				System.out.println("Print on server:"+st);
			}
		};
		

		// Add the service we just created and make it available under the servicename 'ServerService' (case-sensitive)
		// You can create multiple service to be available. Each will reside in its own thread.
		peer.addService("ServerService", new ServerService.Processor<>(serverServiceImpl));
		
		ClientService.Client client = new ClientService.Client(peer.getClientProtocol());
		try {
			s.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// here happens the magic! You need to set the address to the client before sending via
		// peer. You can on one hand know the path e.g. server.Service or you got a call from a client and
		// know the path because of that
		peer.setClientTarget(clientTarget);
		long start = System.currentTimeMillis();
		for (int i=0;i<10000;i++) {
			try {
				int sleepTime = 3000 + (int)(Math.random()*10000);
				Thread.sleep(sleepTime);
				System.out.println(servername+":send "+i);
				client.pushMessageToClient(servername+" is calling! "+i,95);
			} 
			catch (Exception te) {
				te.printStackTrace();
			}
		}
		long dif = System.currentTimeMillis() - start;
		System.out.println("Time:"+dif);
		
	}

}
