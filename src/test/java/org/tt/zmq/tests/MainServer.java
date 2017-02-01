package org.tt.zmq.tests;
//package org.tt.zmq.thrift;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import org.apache.thrift.TException;
//import org.apache.thrift.protocol.TBinaryProtocol;
//import org.apache.thrift.protocol.TProtocol;
//import org.apache.thrift.transport.TTransportException;
//import org.tt.thrift.ClientService;
//import org.tt.thrift.ServerService;
//import org.tt.thrift.Song;
//import org.tt.thrift.User;
//import org.zeromq.ZContext;
//
//public class MainServer {
//	
//	public static final String MAIN_SERVER = "ms_server";
//	public static final String MAIN_SERVER_ENDPOINT = "ipc://mainserver";
//	public static final String MAIN_CLIENT = "ms_client";
//	public static final String MAIN_CLIENT_ENDPOINT = "ipc://mainclient";
//	public static final String MAIN_BROKER_ENDPOINT = "tcp://*:9000";
//	
//	private HashSet<String> clients = new HashSet<>();
//	
//	public MainServer() {
//
//		
//		ServerService.Iface handler = new ServerService.Iface(){
//			List<User> users=new ArrayList<>();
//			{
//				users.add(new User("tom", "tom@tom.com"));
//				users.add(new User("max", "max@tom.com"));
//			}
//			
//			@Override
//			public List<User> getOnlineUsers() throws TException {
//				System.out.println("Client wants the onlineusers");
//				return users;
//			}
//			@Override
//			public void doit(String st) throws TException {
//				System.out.println("Client want me to DOIT! ==>"+st);
//				clients.add(st);
//			}
//		};
//		
//        final ZContext context= new ZContext();
//        TZeroMQSimpleServer.Args args=new TZeroMQSimpleServer.Args(context, "tcp://*:9000",true).identity(MAIN_SERVER);
////        args.socketType(ZMQ.ROUTER);
//        ServerService.Processor<ServerService.Iface> process = new ServerService.Processor<ServerService.Iface>(
//                handler);
//        args.processor(process);
//        final TZeroMQSimpleServer server=new TZeroMQSimpleServer(args);
//        Thread t=new Thread(){
//            @Override
//			public void run() {
//                server.serve();
//            };
//        };
//       t.start();	
//       
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
////				TZeroMQTransport transport = new TZeroMQTransport(context, "tcp://127.0.0.1:9000", ZMQ.DEALER, false);
//				TZeroMQTransport transport = new TZeroMQTransport(server.getSocket());
//				transport.setIdentity(MAIN_CLIENT);
//				try {
//					transport.open();
//				} catch (TTransportException e1) {
//					e1.printStackTrace();
//				}
//				TProtocol protocol = new TBinaryProtocol(transport);
//				ClientService.Client clientClient = new ClientService.Client(protocol);
//
//				while (true) {
//
//					if (clients.size()==0) {
//						try {
//							Thread.sleep(100);
//
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}				
//						continue;
//					}
//					transport.clearRoute();
//					transport.addRoute("lserver");
//					transport.addRoute(clients.iterator().next());
//					try {
//						List<Song> list = clientClient.getSongsFromClient();
//						Thread.sleep(1500);
//						System.out.println("GOT SONGS FROM CLIENT:");
//						for (Song s : list) {
//							System.out.println(s);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
//				}
//
////				transport.close();
////				server.stop();
//			}
//		}).start();
//       
//	}
//	
//	public static void main(String[] args) {
//		new MainServer();
//	}
//
//}
