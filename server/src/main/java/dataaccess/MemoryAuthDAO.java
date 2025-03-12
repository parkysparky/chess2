package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    HashSet<AuthData> authData = new HashSet<>();

    @Override
     public String createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData newAuthorization = new AuthData(token, username);
        authData.add(newAuthorization);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for(var authorization : authData){
            if (authorization.authToken().equals(authToken)){
                return authorization;
            }
        }
        throw new DataAccessException("unauthorized");
    }

    @Override
    public void deleteAuth(AuthData authRecord) {
        authData.remove(authRecord);
    }

    @Override
    public void clear() {
        authData = new HashSet<>();
    }

    @Override
    public boolean isEmpty() {
        return authData.isEmpty();
    }
}
