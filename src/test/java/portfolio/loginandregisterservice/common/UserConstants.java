package portfolio.loginandregisterservice.common;

import portfolio.loginandregisterservice.model.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserConstants {

    public static final User USER = new User("user_name", "user@email.com", "Password@1", "uniqueToken");

    public static final User INVALID_USER = new User("", "", "", "");

    public static final User EMPTY_USER = new User();
    public static final List<User> USER_LIST = new ArrayList<>() {
        {
            add(new User("user1", "email1", "Password@2", "uniquetoken1"));
            add(new User("user2", "email2", "Password@3", "uniquetoken2"));
            add(new User("user3", "email3", "Password@4", "uniquetoken3"));
        }
    };

}
