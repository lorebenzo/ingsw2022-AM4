package it.polimi.ingsw.server.repository.interfaces;

import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

import java.util.UUID;

public interface UsersRepositoryInterface {
    void saveUserSchoolBardMap(UUID gameUUID, String username, int schoolBoardID) throws DBQueryException;

    String getUserHashedPassword(String username) throws DBQueryException;

    void signUpUser(String username, String password) throws DBQueryException;
}
