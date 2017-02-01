# zeromq-thrift-p2p
ZeroMQ-based thrift "peer" that is server (multiple services) and client in one. Using one router for punshing out.

- What is it good for?

Ever used thrift but wanted a way the server could push calls to the client (and of course the client to the server). This version here is basically based on [https://github.com/kdlan/thrift-zeromq](https://github.com/kdlan/thrift-zeromq) but it implements this two-way communication as described using [ZeroMQ](https://github.com/zeromq/jzmq)

1) Here a simple overview:  
  
![](http://thomas.trocha.com/pebble/images/0012_zeromq_thrift_overview.png)

Local you have multiple ZeroMQ-Sockets that are connected to the Backend(0MQ-Router). You can implement multiple Thrift-Services and add those to the TZeroMQPeer. Each of which will reside in a Thread of its own. All of those are listening if any other peer want to call one of those services. Central point in this setup is the Router which will handle all calls for you and that must be reachable from each peer. 

## How does it work?
1) Start an instance of Router (Router.java) somewhere. Let's assume we have it running locally on 127.0.0.1:9001(default-port)

2) Create one peer with a service(as seen in TestZeroMQThriftServer.java):
```java
		// create the peer that is Thrift Server and Client in one and connect to the Router which must be connectable for all other
		// clients
		final TZeroMQPeer peer = new TZeroMQPeer("server","tcp://127.0.0.1:9001");		
		
		// the implementation of the ServerService that will be handled by this server
		// so any other server (and actually me as well) can call this service via their Thrift-Client
		ServerService.Iface serverServiceImpl = new ServerService.Iface(){
			// some testdata
      List<User> users=new ArrayList<>();
			{
				users.add(new User("mixi", "mixi@tom.com"));
				users.add(new User("moxi", "moxi@tom.com"));
			}
			
			@Override
			public List<User> getOnlineUsers() throws TException {
				// with this call you can get the routing to the calling peer's ClientService
        String caller =  peer.getLocalService("ServerService").getCurrentCallerRoute("ClientService");
				clientTarget = caller;
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
```

In order to call something on a connected peer:
```java
    // Prepare a client-stub for ClientService-Calls on remote peers
		ClientService.Client client = new ClientService.Client(peer.getClientProtocol());

    // here happens the magic! You need to set the address to the client before sending via
		// peer. You need to know the path before. It is a dot-separated path like this "servername.servicename". Plz see that all servers need to have unique names, otherwise the Router won't do it's job properly and won't even complain. (The router has some glitches...). You can get this path also by using *peer.getLocalService("ServerService").getCurrentCallerRoute("ClientService")* inside of a service-call. With this call you get the routing-target for the calling server's service.		
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
```


On the other hand you need a Client-Peer that have a ClientService implemented and registered (as seen in TestZeroMQThriftClient). This is basically the same procedure as before, just ClientService instead of ServerService:
```java
		final User currentUser = new User("Tommy", "tom@tomtom.com");

		// the implementation of the ClientService that will be handled by this
		// Client so that any other server (and actually me as well) can call this service
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

		TZeroMQPeer peer = new TZeroMQPeer("client", "tcp://127.0.0.1:9001");
		peer.addService("ClientService", new ClientService.Processor<>(clientServiceImpl));

		ServerService.Client server = new ServerService.Client(peer.getClientProtocol());
		// the name of the server should be known and the name of the service as well
    peer.setClientTarget("server.ServerService");
		long start = System.currentTimeMillis();
		try {
			List<User> users = server.getOnlineUsers();
			for (User u : users) {
				System.out.println("User:" + u);
			}
		} catch (Exception te) {
			te.printStackTrace();
		}
```
