package server.service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.DataInputException;
import server.request.LoginRequest;
import server.request.LogoutRequest;
import server.request.RegisterRequest;
import server.result.LoginResult;
import server.result.LogoutResult;
import server.result.RegisterResult;

public class UserService {
    //these are public so that the unit tests can call to them. find a better solution
    public UserDAO userDAO;
    public AuthDAO authDAO;

    public UserService() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
    }

    public UserService(boolean useMySQL) throws DataAccessException {
        if(useMySQL){
            userDAO = new MySQLUserDAO();
            authDAO = new MySQLAuthDAO();
        }
        else{
            userDAO = new MemoryUserDAO();
            authDAO = new MemoryAuthDAO();
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataInputException {
        try{
            userDAO.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            //return RegisterResult
            String token = authDAO.createAuth(registerRequest.username());
            return new RegisterResult(registerRequest.username(), token);
        }
        catch (DataAccessException e){
            throw new DataInputException(e.getMessage());
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException, DataInputException {
        //verify credentials
        UserData userData = userDAO.getUser(loginRequest.username()); //throws DataAccessException if user doesn't exist
        if(!BCrypt.checkpw(loginRequest.password(), userData.password())){ //if password doesn't match throw exception
            throw new DataInputException("unauthorized");
        }
        //create new authData for valid login
        String authToken = authDAO.createAuth(userData.username());

        return new LoginResult(userData.username(), authToken);
    }

    public String authenticate(String authToken){
        try{
            AuthData authData = authDAO.getAuth(authToken);
            return authData.username();
        } catch (DataAccessException e) {
            return null;
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException{
        authDAO.deleteAuth(authDAO.getAuth(logoutRequest.authToken()));

        return new LogoutResult();
    }

    private void clearUserData() throws DataAccessException {
        userDAO.clear();
    }

    private void clearAuthData() throws DataAccessException {
        authDAO.clear();
    }

    public void clear() throws DataAccessException {
        clearAuthData();
        clearUserData();
    }
}
