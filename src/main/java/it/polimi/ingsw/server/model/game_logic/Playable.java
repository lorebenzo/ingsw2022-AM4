package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.List;

public interface Playable {
    default void unLock(){ }
    default void useLock(){ }
    default boolean isLockAvailable(){ return false; }
    default List<Color> getStudents(){ return null; }
    default boolean removeStudent(Color student){ return false;}
    default void addStudent(Color student){}
    default boolean containsAllStudents(List<Color> students) {return false;}
    default int getInitialStudentsNumberOnCharacter(){return 0;}

}
