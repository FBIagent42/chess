import dataaccess.*;
import server.Server;
import service.servieimplimentation.Service;

public class Main {
    public static void main(String[] args) {
        try {
            Service service = new Service();
            service.setDaos(new SQLUserDAO(), new SQLAuthDAO(), new SQLGameDAO());
            Server server = new Server();
            server.run(8080);

            System.out.println("♕ 240 Chess Server");
        }catch (Throwable ex){
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
