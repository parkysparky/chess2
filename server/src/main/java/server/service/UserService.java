package server.service;

import server.service.request.LoginRequest;
import server.service.request.RegisterRequest;
import server.service.result.RegisterResult;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {}

    public LoginResult login(LoginRequest loginRequest) {}

    public LogoutResult logout(LogoutRequest logoutRequest) {}
}
