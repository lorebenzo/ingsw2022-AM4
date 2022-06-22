package it.polimi.ingsw.server.repository.interfaces;

import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

public interface UsersRepositoryInterface {
    String getUserHashedPassword(String username) throws DBQueryException;

    void signUpUser(String username, String password) throws DBQueryException;
}
