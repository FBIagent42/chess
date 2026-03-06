package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthDAOTests implements BaseSQLTests{

    static AuthDAO AUTH_DAO;

    static {
        try {
            AUTH_DAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void clear() throws DataAccessException {
        AUTH_DAO.clear();
    }

    @Test
    @Order(1)
    public void createAuthPositive() throws DataAccessException {
        AUTH_DAO.createAuth(newAuth);

        AuthData dbAuth = AUTH_DAO.getAuth(newAuth.authToken());
        Assertions.assertEquals(newAuth.authToken(), dbAuth.authToken());
        Assertions.assertEquals(newAuth.username(), dbAuth.username());
    }

    @Test
    @Order(2)
    public void createAuthNegative(){
        Assertions.assertThrows(DataAccessException.class,
                () -> AUTH_DAO.createAuth(newAuth));
        Assertions.assertThrows(DataAccessException.class,
                () -> AUTH_DAO.createAuth(new AuthData(null, "user")));
        Assertions.assertThrows(DataAccessException.class,
                () -> AUTH_DAO.createAuth(new AuthData("auth", null)));
    }

    @Test
    @Order(3)
    public void getAuthPositive() throws DataAccessException {
        AuthData dbAuth = AUTH_DAO.getAuth(newAuth.authToken());
        Assertions.assertEquals(newAuth.authToken(), dbAuth.authToken());
        Assertions.assertEquals(newAuth.username(), dbAuth.username());
    }

    @Test
    @Order(4)
    public void getAuthNegative() throws DataAccessException {
        Assertions.assertNull(AUTH_DAO.getAuth("NoAuth"));
    }

    @Test
    @Order(5)
    public void deleteAuthPositive() throws DataAccessException {
        AUTH_DAO.deleteAuth(newAuth.authToken());

        Assertions.assertNull(AUTH_DAO.getAuth(newAuth.authToken()));
    }

    @Test
    @Order(6)
    public void deleteAuthNegative(){
        Assertions.assertThrows(DataAccessException.class,
                () -> AUTH_DAO.deleteAuth(newAuth.authToken()));
    }

    @Test
    @Order(7)
    public void clearPositive() throws DataAccessException {
        AUTH_DAO.createAuth(newAuth);
        AUTH_DAO.clear();

        Assertions.assertNull(AUTH_DAO.getAuth(newAuth.authToken()));
    }

}
