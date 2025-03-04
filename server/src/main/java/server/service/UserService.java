package server.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
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

//    public LoginResult login(LoginRequest loginRequest) {}
//
//    public LogoutResult logout(LogoutRequest logoutRequest) {}

    public void clearUserData(){
        memoryUserDAO = new MemoryUserDAO();
    }

    public void clearAuthData(){
        memoryAuthDAO = new MemoryAuthDAO();
    }
}
