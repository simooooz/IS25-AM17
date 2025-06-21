package it.polimi.ingsw.common.dto;

import java.util.List;
import java.util.Map;

public class BoardDTO {
    public Map<Integer, ComponentDTO> mapIdComponents;
    public List<Integer> commonComponents;
    public List<PlayerPositionDTO> players;
    public List<PlayerDTO> startingDeck;
    public String cardPile;
    public int timeLeft;
    public int hourglassPos;

    public BoardDTO() {}

}