package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    HashSet<UserData> userData = new HashSet<>();

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException{
        try{
            getUser(username);
        } catch (DataAccessException e) {
            UserData newUser = new UserData(username, password, email);
            userData.add(newUser);
            return;
        }
        throw new DataAccessException("already taken");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        for(var user : userData){
            if (user.username().equals(username)){
                return user;
            }
        }
        throw new DataAccessException("User Not Found");
    }

    @Override
    public void deleteUser(UserData user) throws DataAccessException {
        userData.remove(user);
    }
}
