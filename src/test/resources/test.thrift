namespace java org.tt.thrift

struct User{
    1:string name;
    2:string email;
}

struct Group {
    1:string groupName="default";
    2:list<User> users={}
}

// to be called from the server
service ClientService {
    User getCurrentClientUser();
    oneway void pushMessageToClient(1:string msg,2:i32 msgCode);
}

// to be called from the client
service ServerService { 
    User addUser(1:string username;2:string email);
    list<User> getOnlineUsers();
    oneway void printOnServer(1:string st);
}