package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthDAOTests {
    AuthData newAuth = new AuthData("TestToken", "Bill");

    static AuthDAO authDAO;

    static {
        try {
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void clear() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    @Order(1)
    public void createAuthPositive() throws DataAccessException {
        authDAO.createAuth(newAuth);

        AuthData dbAuth = authDAO.getAuth(newAuth.authToken());
        Assertions.assertEquals(newAuth.authToken(), dbAuth.authToken());
        Assertions.assertEquals(newAuth.username(), dbAuth.username());
    }

    @Test
    @Order(2)
    public void createAuthNegative(){
        Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(newAuth));
        Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(new AuthData(null, "user")));
        Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(new AuthData("auth", null)));
    }

    @Test
    @Order(3)
    public void getAuthPositive() throws DataAccessException {
        AuthData dbAuth = authDAO.getAuth(newAuth.authToken());
        Assertions.assertEquals(newAuth.authToken(), dbAuth.authToken());
        Assertions.assertEquals(newAuth.username(), dbAuth.username());
    }

    @Test
    @Order(4)
    public void getAuthNegative() throws DataAccessException {
        Assertions.assertNull(authDAO.getAuth("NoAuth"));
    }

    @Test
    @Order(5)
    public void deleteAuthPositive() throws DataAccessException {
        authDAO.deleteAuth(newAuth.authToken());

        Assertions.assertNull(authDAO.getAuth(newAuth.authToken()));
    }

    @Test
    @Order(6)
    public void deleteAuthNegative(){
        Assertions.assertThrows(DataAccessException.class,
                () -> authDAO.deleteAuth(newAuth.authToken()));
    }

    @Test
    @Order(7)
    public void clearPositive() throws DataAccessException {
        authDAO.createAuth(newAuth);
        authDAO.clear();

        Assertions.assertNull(authDAO.getAuth(newAuth.authToken()));
    }

}
