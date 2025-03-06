package server.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import server.DataInputException;
import server.service.request.LoginRequest;
import server.service.request.LogoutRequest;
import server.service.request.RegisterRequest;
import server.service.result.LoginResult;
import server.service.result.LogoutResult;
import server.service.result.RegisterResult;

public class UserService {
    MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        memoryUserDAO.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
        //return RegisterResult
        String token = memoryAuthDAO.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(), token);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException, DataInputException {
        //verify credentials
        UserData userData = memoryUserDAO.getUser(loginRequest.username()); //throws DataAccessException if user doesn't exist
        if(!userData.password().equals(loginRequest.password())){ //if password doesn't match throw exception
            throw new DataInputException("unauthorized");
        }
        //create new authData for valid login
        String authToken = memoryAuthDAO.createAuth(userData.username());

        return new LoginResult(userData.username(), authToken);
    }

    public boolean authenticate(String authToken){
        try{
            memoryAuthDAO.getAuth(authToken);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException{
        memoryAuthDAO.deleteAuth(memoryAuthDAO.getAuth(logoutRequest.authToken()));

        return new LogoutResult();
    }

    public void clearUserData(){
        memoryUserDAO = new MemoryUserDAO();
    }

    public void clearAuthData(){
        memoryAuthDAO = new MemoryAuthDAO();
    }
}
