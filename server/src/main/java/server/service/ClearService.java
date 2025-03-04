package server.service;

import server.service.result.ClearResult;

public class ClearService {
    private void clearUserData(){}
    private void clearGameData(){}
    private void clearAuthData(){}
    public ClearResult clearAllData(){
        clearUserData();
        clearGameData();
        clearAuthData();

        return new ClearResult();
    }
}
