package it.polimi.ingsw.server.controller.auth_controller;
import it.polimi.ingsw.server.repository.interfaces.UsersRepositoryInterface;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class UsersRepositoryMock implements UsersRepositoryInterface {
    @Override
    public void saveUserSchoolBardMap(UUID gameUUID, String username, int schoolBoardID) {

    }

    @Override
    public String getUserHashedPassword(String username) {
        return DigestUtils.sha256Hex("password");
    }

    @Override
    public void signUpUser(String username, String password) {
    }
}
