package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTests {
    UserData newUser = new UserData("Bill", "myPass", "stuff@gmail.com");


    static UserDAO USER_DAO = new SQLUserDAO();

    @BeforeAll
    public static void clear() throws DataAccessException {
        USER_DAO.clear();
    }

    @Test
    @Order(1)
    public void createUserPositive() throws DataAccessException {
        USER_DAO.createUser(newUser);

        UserData dbUser = USER_DAO.getUser(newUser.username());
        Assertions.assertEquals(newUser.username(), dbUser.username());
        Assertions.assertEquals(newUser.password(), dbUser.password());
        Assertions.assertEquals(newUser.email(), dbUser.email());
    }

    @Test
    @Order(2)
    @DisplayName("Username Already Taken Error")
    public void createUserNegativeTaken(){
        Assertions.assertThrows(DataAccessException.class,
                () -> USER_DAO.createUser(newUser));
    }

    @Test
    @Order(3)
    @DisplayName("Inserting an empty user")
    public void createUserEmpty(){
        Assertions.assertThrows(DataAccessException.class,
                () -> USER_DAO.createUser(new UserData(null, "pass", "email")));
        Assertions.assertThrows(DataAccessException.class,
                () -> USER_DAO.createUser(new UserData("user", null, "email")));
        Assertions.assertThrows(DataAccessException.class,
                () -> USER_DAO.createUser(new UserData("user", "pass", null)));
    }

    @Test
    @Order(4)
    public void getUserPositive() throws DataAccessException {
        UserData dbUser = USER_DAO.getUser(newUser.username());
        Assertions.assertEquals(newUser.username(), dbUser.username());
        Assertions.assertEquals(newUser.password(), dbUser.password());
        Assertions.assertEquals(newUser.email(), dbUser.email());
    }

    @Test
    @Order(5)
    public void getUserNegative() throws DataAccessException {
        Assertions.assertNull(USER_DAO.getUser("NoUser"));
    }

    @Test
    @Order(6)
    public void clearPositive() throws DataAccessException {
        USER_DAO.clear();

        Assertions.assertNull(USER_DAO.getUser(newUser.username()));
    }
}
