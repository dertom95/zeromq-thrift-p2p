package org.tt.zmq.thriftTests;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.thrift.TException;
import org.tt.thrift.ClientService;
import org.tt.thrift.ServerService;
import org.tt.thrift.User;
import org.tt.zmq.thrift.TZeroMQPeer;

public class TestZeroMQThriftClient {
	/**
	 * This Application represents a Client that also implements a
	 * Client-Service that can be called from the server
	 * 
	 * @param args
	 */

	private static String target = null;
	private static Semaphore s = new Semaphore(0);

	public static void main(String[] args) {
		// set the name of this server. this MUST be unique in the whole
		// distributed system. The router is dumb and won't check this
		// and can actually be a security problem
		// String servername =
		// args.length>0?args[0]:UUID.randomUUID().toString().substring(0,5);
		String servername = "client";

		// the target which will get client-calls
		// this can be changed before any call via:
		// peer.setClientTarget(target);
		String target = args.length > 1 ? args[1] : "server.ServerService";

		// the routerEndpoint which distributes all calls.
		String routerEndpoint = args.length > 2 ? args[1] : "tcp://127.0.0.1:9001";

		final User currentUser = new User("Tommy", "tom@tomtom.com");

		// the implementation of the ClientService that will be handled by this
		// Client
		// so any other server (and actually me as well) can call this service
		// via their Thrift-Client
		ClientService.Iface clientServiceImpl = new ClientService.Iface() {

			@Override
			public User getCurrentClientUser() throws TException {
				return currentUser;
			}

			@Override
			public void pushMessageToClient(String msg, int msgCode) throws TException {
				System.out.println("Message from Server:" + msg + "|" + msgCode);
			}
		};

		// create the peer that is Thrift Server and Client in one and connect
		// to the Router which must be connectable for all other
		// clients
		TZeroMQPeer peer = new TZeroMQPeer(servername, routerEndpoint);
		// Add the service we just created and make it available under the
		// servicename 'ClientService' (case-sensitive)
		// You can create multiple services to be available. Each will reside in
		// its own thread.
		peer.addService("ClientService", new ClientService.Processor<>(clientServiceImpl));

		ServerService.Client server = new ServerService.Client(peer.getClientProtocol());
		peer.setClientTarget(target);
		long start = System.currentTimeMillis();
		try {
//			int sleepTime = 3000 + (int) (Math.random() * 10000);
//			Thread.sleep(sleepTime);
			List<User> users = server.getOnlineUsers();
			for (User u : users) {
				System.out.println("User:" + u);
			}
		} catch (Exception te) {
			te.printStackTrace();
		}
		long dif = System.currentTimeMillis() - start;
		System.out.println("Time:" + dif);

	}

}
