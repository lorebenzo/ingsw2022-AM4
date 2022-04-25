package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.exceptions.UnhandledMessageAtLowestLayerException;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.exceptions.*;


import java.util.*;
import java.util.stream.Collectors;

class Controller extends SugarMessageProcessor {
    private final GameState gameState;

    private Map<Peer,Integer> peersToSchoolBoardIdsMap;
    private List<Integer> roundOrder;
    private Iterator<Integer> roundIterator;
    //private boolean gameOver;

    public Controller(List<Peer> peers) throws GameStateInitializationFailureException {
        this.gameState = new GameState(peers.size());

        Iterator<Integer> schoolBoardIdsSetIterator = this.getSchoolBoardIds().iterator();

        for (int i = 0; i < this.getNumberOfPlayers(); i++){
            this.peersToSchoolBoardIdsMap = new HashMap<>();

            this.peersToSchoolBoardIdsMap.put(peers.get(i), schoolBoardIdsSetIterator.next());
        }

        this.roundOrder = new ArrayList<>(this.peersToSchoolBoardIdsMap.values());

        //startGame();

    }

    private Peer getPeerFromSchoolBoardId(int schoolBoardId){
        return this.peersToSchoolBoardIdsMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == schoolBoardId)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }

    private int getSchoolBoardIdFromPeer(Peer peer){
        return this.peersToSchoolBoardIdsMap.get(peer);
    }

    private boolean isMessageFromCurrentPlayer(Peer peer){
        return this.getSchoolBoardIdFromPeer(peer) == this.getCurrentPlayerSchoolBoardId();
    }

    private void setCurrentPhase(GameState.Phase phase){
        this.gameState.setCurrentPhase(phase);
    }

    private void setCurrentPlayerSchoolBoardId(int currentPlayerSchoolBoardId){
        this.gameState.setCurrentPlayerSchoolBoardId(currentPlayerSchoolBoardId);
    }

    private void fillClouds() throws FullCloudException, EmptyStudentSupplyException {
        this.gameState.fillClouds();
    }

    private void grabStudentsFromCloud(int cloudIndex) throws InvalidSchoolBoardIdException, EmptyCloudException {
        this.gameState.grabStudentsFromCloud(cloudIndex);
    }


    private void playCard(Card card) throws CardIsNotInTheDeckException, InvalidSchoolBoardIdException, InvalidCardPlayedException {
        if(!this.getSchoolBoardIdsToCardsPlayedThisRound().containsValue(card) || this.getDeck().size() == 1)
            this.gameState.playCard(card);
        else {
            throw new InvalidCardPlayedException();
        }
    }


    private void moveStudentFromEntranceToDiningRoom(Color student) throws InvalidSchoolBoardIdException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        this.gameState.moveStudentFromEntranceToDiningRoom(student);
        //assignProfessor() verifies on its own if the player should get the professor and does nothing if not
        this.assignProfessor(student);
    }


    private void moveStudentFromEntranceToArchipelago(Color student, List<Integer> archipelagoIslandCodes) throws InvalidSchoolBoardIdException, StudentNotInTheEntranceException {
        this.gameState.moveStudentFromEntranceToArchipelago(student, archipelagoIslandCodes);
    }

    private List<Integer> getMotherNaturePositionIslandCodes(){
        return this.gameState.getMotherNaturePositionIslandCodes();
    }

    private Optional<Integer> getMostInfluentSchoolBoardId(){
        List<Map.Entry<Integer, Integer>> orderedPlayersInfluences = this.getInfluence(this.getMotherNaturePositionIslandCodes()).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) //sorts the map using the influences in descending order
                .collect(Collectors.toList());

        if(orderedPlayersInfluences.get(0).getValue() > orderedPlayersInfluences.get(1).getValue())
            return Optional.of(orderedPlayersInfluences.get(0).getKey());
        else
            return Optional.empty();

    }


    public void moveMotherNature(int nSteps) throws InvalidNumberOfStepsException, InvalidSchoolBoardIdException {
        this.gameState.moveMotherNatureNStepsClockwise(nSteps);

        //If there is a player that is the most influent on an archipelago, he will conquest the archipelago
        if(this.getMostInfluentSchoolBoardId().isPresent())
            this.conquestArchipelago(this.getMostInfluentSchoolBoardId().get());

    }


    private void mergeLeft() throws NonMergeableArchipelagosException {
        this.gameState.mergeLeft();
    }


    private void mergeRight() throws NonMergeableArchipelagosException {
        this.gameState.mergeRight();
    }



    private int getNextPlayerSchoolBoardId() {
        return this.roundIterator.next();
    }


    private void conquestArchipelago(int schoolBoardId) throws InvalidSchoolBoardIdException {
        this.gameState.conquestArchipelago(schoolBoardId);
    }

    /**
     * This method gets an archipelago in input and returns a map where every entry links a schoolBoard with its influence on the inputed archipelago
     * @param archipelagoIslandCodes is a List<Integer> uniquely identifying an archipelago
     * @return a Map<Integer, Integer> where the key is the schoolBoardId and the value is the influence on the inputed archipelago
     */
    private Map<Integer, Integer> getInfluence(List<Integer> archipelagoIslandCodes){
        return this.gameState.getInfluence(archipelagoIslandCodes);
    }

    //TODO - aggiungere versione parametrizzata di getInfluence


    private List<List<Color>> getClouds() {
        return this.gameState.getClouds();
    }

    private int getNumberOfPlayers(){
        return this.gameState.getNumberOfPlayers();
    }

    private Set<Integer> getSchoolBoardIds(){
        return this.gameState.getSchoolBoardIds();
    }

    private Map<Integer, Card> getSchoolBoardIdsToCardsPlayedThisRound() {
        return this.gameState.getSchoolBoardIdsToCardsPlayedThisRound();
    }

    private List<Card> getDeck() throws InvalidSchoolBoardIdException {
        return this.gameState.getDeck();
    }

    private int getCurrentPlayerSchoolBoardId() {
        return this.gameState.getCurrentPlayerSchoolBoardId();
    }

    private void setMotherNaturePosition(Archipelago motherNaturePosition) {
        this.gameState.setMotherNaturePosition(motherNaturePosition);
    }

/*    private void setCurrentPlayerProfessor(Color professor) throws InvalidSchoolBoardIdException, ProfessorNotPresentException {
        this.gameState.setCurrentPlayerProfessor(professor);
    }*/

    private void assignProfessor(Color student) throws InvalidSchoolBoardIdException {
        this.gameState.assignProfessor(student);
    }

    private void endTurn(){
        //TODO sicuramente mancano operazioni da svolgere per impedire rollback delle mosse e per avviare il prossimo turno
        this.nextTurn();
    }

    private void nextTurn(){
        this.setCurrentPlayerSchoolBoardId(this.getNextPlayerSchoolBoardId());
        this.setCurrentPhase(GameState.Phase.PLANNING);
    }

    public void defineRoundOrder(){

        List<Map.Entry<Integer, Card>> orderedSchoolBoardsToCardPlayed = this.gameState.getSchoolBoardIdsToCardsPlayedThisRound().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .collect(Collectors.toList());

        this.roundOrder = orderedSchoolBoardsToCardPlayed.stream().map(Map.Entry::getKey).collect(Collectors.toList());

        this.roundIterator = this.roundOrder.listIterator();

    }




/*
    public void nextTurn(){
        if(this.roundIterator.hasNext())
            this.setCurrentPlayerSchoolBoardId(this.roundIterator.next());

        setCurrentPhase(GameState.Phase.PLANNING);


    }*/

    //MessageHandlers

    @SugarMessageHandler
    public SugarMessage playCardMsg(SugarMessage message, Peer peer){

        if(this.isMessageFromCurrentPlayer(peer)){
            //TODO inserire controllo qui per verificare se la mossa è tra quelle giocabili
            var msg = (PlayCardMsg) message;

            try {
                this.playCard(msg.card);
                return new OKMsg();
            } catch (CardIsNotInTheDeckException e) {

                return new KOMsg("Card played is not available in the deck");
            } catch (InvalidSchoolBoardIdException e) {
                return new FatalErrorMsg(InvalidCardPlayedException.class.getName());
            } catch (InvalidCardPlayedException e) {
                return new KOMsg("Card played has already been played by another player in this round");
            }
        }
        else
            return new NotYourTurnMsg();


    }

    @SugarMessageHandler
    public SugarMessage moveStudentFromEntranceToDiningRoomMsg(SugarMessage message, Peer peer){
        if(isMessageFromCurrentPlayer(peer)){
            //TODO inserire controllo qui per verificare se la mossa è tra quelle giocabili
            var msg = (MoveStudentFromEntranceToDiningRoomMsg) message;

            try {
                this.moveStudentFromEntranceToDiningRoom(msg.student);
                return new OKMsg();
            } catch (InvalidSchoolBoardIdException e) {
                return new FatalErrorMsg(InvalidCardPlayedException.class.getName());
            } catch (StudentNotInTheEntranceException e) {
                return new KOMsg("The student you are trying to move is not in the entrance of your schoolboard");
            } catch (FullDiningRoomLaneException e) {
                return new KOMsg("The dining room lane corresponding to the color of the student you are trying to move is already full");
            }

        }
        else
            return new NotYourTurnMsg();
    }

    @SugarMessageHandler
    public SugarMessage moveStudentFromEntranceToArchipelagoMsg(SugarMessage message, Peer peer) {
        if (isMessageFromCurrentPlayer(peer)) {
            //TODO inserire controllo qui per verificare se la mossa è tra quelle giocabili
            var msg = (MoveStudentFromEntranceToArchipelagoMsg) message;

            try {
                this.moveStudentFromEntranceToArchipelago(msg.student, msg.archipelagoIslandCodes);
                return new OKMsg();
            } catch (InvalidSchoolBoardIdException e) {
                return new FatalErrorMsg(InvalidCardPlayedException.class.getName());
            } catch (StudentNotInTheEntranceException e) {
                return new KOMsg("The student you are trying to move is not in the entrance of your schoolboard");
            }
        }
        else
            return new NotYourTurnMsg();
    }

    @SugarMessageHandler
    public SugarMessage setPhaseToActionMsg(SugarMessage message, Peer peer){
        if (isMessageFromCurrentPlayer(peer)) {
            //TODO inserire controllo qui per verificare se la mossa è tra quelle giocabili

            this.setCurrentPhase(GameState.Phase.ACTION);
            return new OKMsg();
        }
        else
            return new NotYourTurnMsg();
    }


    @SugarMessageHandler
    public SugarMessage moveMotherNatureMsg(SugarMessage message, Peer peer) {
        if(isMessageFromCurrentPlayer(peer)){
            //TODO inserire controllo qui per verificare se la mossa è tra quelle giocabili
            var msg = (MoveMotherNatureMsg) message;

            try {
                this.moveMotherNature(msg.numberOfSteps);
                return new OKMsg();
            } catch (InvalidNumberOfStepsException e) {
                return new InvalidNumberOfStepsMsg();
            } catch (InvalidSchoolBoardIdException e) {
                return new FatalErrorMsg(InvalidCardPlayedException.class.getName());
            }


        }
        else
            return new NotYourTurnMsg();
    }

    @SugarMessageHandler
    public SugarMessage grabStudentsFromCloudMsg(SugarMessage message, Peer peer){
        if(isMessageFromCurrentPlayer(peer)){
            //TODO inserire controllo qui per verificare se la mossa è tra quelle giocabili
            var msg = (GrabStudentsFromCloudMsg) message;

            try {
                this.grabStudentsFromCloud(msg.cloudIndex);
                return new OKMsg();
            } catch (InvalidSchoolBoardIdException e) {
                return new FatalErrorMsg(InvalidCardPlayedException.class.getName());
            } catch (EmptyCloudException e) {
                return new KOMsg("The choosen cloud is empty, choose another one");
            }
        }
        else
            return new NotYourTurnMsg();
    }

    @SugarMessageHandler
    public SugarMessage endTurnMsg(SugarMessage message, Peer peer){
        if(isMessageFromCurrentPlayer(peer)){
            //TODO inserire controllo qui per verificare se la mossa è tra quelle giocabili
            this.endTurn();

            return new OKMsg();
        }
        else
            return new NotYourTurnMsg();
    }

    @SugarMessageHandler
    public void base(SugarMessage message, Peer peer) throws UnhandledMessageAtLowestLayerException {
        System.out.println("Dropping message : " + message.serialize());
        throw new UnhandledMessageAtLowestLayerException(message);
    }
}

