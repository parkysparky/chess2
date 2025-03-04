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
        }
        throw new DataAccessException("User already exists");
    }

    @Override
    public String getUser(String username) throws DataAccessException{
        for(var user : userData){
            if (user.username().equals(username)){
                return username;
            }
        }
        throw new DataAccessException("User Not Found");
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        userData.remove(getUser(username));
    }
}
