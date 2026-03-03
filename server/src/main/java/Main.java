import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.Server;
import service.servieimplimentation.Service;

public class Main {
    public static void main(String[] args) {
        Service service = new Service();
        service.setDaos(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}
