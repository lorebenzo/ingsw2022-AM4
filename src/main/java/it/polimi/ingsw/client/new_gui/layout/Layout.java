package it.polimi.ingsw.client.new_gui.layout;

import it.polimi.ingsw.client.new_gui.GUI;

public class Layout {
    /********************************* Player view ************************************/
    // Achipelago panel (relative)
    public static final double archRelX = 25;
    public static final double archRelY = 0;
    public static final double archRelW = 55;
    public static final double archRelH = 70;
    public static final GUI.Rectangle archRect = new GUI.Rectangle(
            archRelX, archRelY, archRelW, archRelH
    );

    // SchoolBoard panel (relative)
    public static final double schoolRelX = 0;
    public static final double schoolRelY = 0;
    public static final double schoolRelW = 25;
    public static final double schoolRelH = 100;
    public static final GUI.Rectangle schoolRect = new GUI.Rectangle(
            schoolRelX, schoolRelY, schoolRelW, schoolRelH
    );

    // Prompt panel
    public static final double promptRelX = 28;
    public static final double promptRelY = 75;
    public static final double promptRelW = 12;
    public static final double promptRelH = 25;
    public static final GUI.Rectangle promptRect = new GUI.Rectangle(
            promptRelX, promptRelY, promptRelW, promptRelH
    );

    // Cloud panel
    public static final double cloudRelX = 40;
    public static final double cloudRelY = 75;
    public static final double cloudRelW = 30;
    public static final double cloudRelH = 16;
    public static final GUI.Rectangle cloudRect = new GUI.Rectangle(
            cloudRelX, cloudRelY, cloudRelW, cloudRelH
    );
    public static final double cloudDefaultBrightness = -0.2;
    public static final double cloudHGapRelative = 5;

    // Cards panel
    public static final double cardsRelX = 40;
    public static final double cardsRelY = 95;
    public static final double cardsRelW = 35;
    public static final double cardsRelH = 10;
    public static final GUI.Rectangle cardsRect = new GUI.Rectangle(
        cardsRelX, cardsRelY, cardsRelW, cardsRelH
    );

    // Chat panel
    public static final double chatRelX = 75;
    public static final double chatRelY = 0;
    public static final double chatRelW = 25;
    public static final double chatRelH = 65;
    public static final GUI.Rectangle chatRect = new GUI.Rectangle(
            chatRelX, chatRelY, chatRelW, chatRelH
    );

    public static final GUI.Rectangle chatInputBoxRect = chatRect.relativeToThis(
            0, 110, 100, 10
    );

    // Switch view button to enemy
    public static final double switchButtonToEnemyRelX = 75;
    public static final double switchButtonToEnemyRelY = 75;
    public static final double switchButtonToEnemyRelW = 25;
    public static final double switchButtonToEnemyRelH = 5;
    public static final GUI.Rectangle switchButtonToEnemyRect = new GUI.Rectangle(
            switchButtonToEnemyRelX, switchButtonToEnemyRelY, switchButtonToEnemyRelW, switchButtonToEnemyRelH
    );

    /************************* Enemies view **********************************/

    // Choose schoolboard button
    public static final double chooseButtonRelX = 35;
    public static final double chooseButtonRelY = 0;
    public static final double chooseButtonRelW = 10;
    public static final double chooseButtonRelH = 10;
    public static final GUI.Rectangle chooseButtonRect = new GUI.Rectangle(
            chooseButtonRelX, chooseButtonRelY, chooseButtonRelW, chooseButtonRelH
    );

    // Last card played
    public static final double cardPlayedRelX = 25;
    public static final double cardPlayedRelY = 0;
    public static final double cardPlayedRelW = 10;
    public static final double cardPlayedRelH = 30;
    public static final GUI.Rectangle cardPlayedRect = new GUI.Rectangle(
            cardPlayedRelX, cardPlayedRelY, cardPlayedRelW, cardPlayedRelH
    );

    // Switch view button to player
    public static final double switchButtonToPlayerRelX = 35;
    public static final double switchButtonToPlayerRelY = 10;
    public static final double switchButtonToPlayerRelW = 10;
    public static final double switchButtonToPlayerRelH = 10;
    public static final GUI.Rectangle switchButtonToPlayerRect = new GUI.Rectangle(
            switchButtonToPlayerRelX, switchButtonToPlayerRelY, switchButtonToPlayerRelW, switchButtonToPlayerRelH
    );

    // First character card rect
    public static final double firstCharacterRelX = 75;
    public static final double firstCharacterRelY = 80;
    public static final double firstCharacterRelW = 6;
    public static final double firstCharacterRelH = 17.25;
    public static final GUI.Rectangle firstCharacterRect = new GUI.Rectangle(
            firstCharacterRelX, firstCharacterRelY, firstCharacterRelW, firstCharacterRelH
    );
}
