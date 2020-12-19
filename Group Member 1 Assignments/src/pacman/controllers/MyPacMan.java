/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers;

import java.util.EnumMap;
import pacman.controllers.Controller;
import pacman.game.Constants.*;
import pacman.game.Game;

/**
 *
 * @author alvin
 */
public class MyPacMan extends Controller<MOVE> {
    private final Controller<EnumMap<GHOST, MOVE>> gCon;
    private final int MIN_DISTANCE = 20;
    private int deepest;
    private final int NUM_TRIALS = 10;
    private static final MOVE[] moves;

    static {
        moves = new MOVE[]{MOVE.UP, MOVE.RIGHT, MOVE.DOWN, MOVE.LEFT};
    }


    public MyPacMan(int deepest,Controller<EnumMap<GHOST,MOVE>> gCon ) {
        this.deepest = deepest;
        this.gCon = gCon;
    }

   

    
    @Override
    public MOVE getMove(Game game, long timeDue) {
        int[] weights = new int[moves.length];
        
        for (int i=0; i< moves.length;i++){
            weights[i]=Integer.MIN_VALUE;
        }
        
        for (int i=0; i<moves.length;++i){
            Game dupe = game.copy();
            
            for (int j = 0; j < NUM_TRIALS; j++) {
                dupe.advanceGame(moves[i], this.gCon.getMove(dupe, -1));
            }

            if (dupe.getPacmanCurrentNodeIndex() != game.getPacmanCurrentNodeIndex()) {
                weights[i] = this.minmax((MOVE) null, 0, dupe);
            }
        }
        int MaxIndex = 0;
        int MaxWeight = weights[0];
        for (int i=0; i<weights.length;i++){
            if(weights[i]>MaxWeight){
                MaxIndex = i;
                MaxWeight = weights[i];
            }
        }
        
        return moves[MaxIndex];
    }
    
    private int evaluate(Game game){
        int score = game.getScore();
        int distToGhost = Integer.MAX_VALUE;
        int feastTime = 0;
        int result;
        GHOST[] ghosts = GHOST.values();
        
        for(GHOST ghost: ghosts){
            int ghostDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
            int ghostTime = game.getGhostEdibleTime(ghost);
            if(ghostDist<=distToGhost){
                distToGhost = ghostDist;
            
            }
            feastTime = ghostTime;
        }
        
        if (distToGhost<=MIN_DISTANCE && feastTime==0){
            result = score - distToGhost;
            
        }
        else{
            result = score + distToGhost;
        }
        return result;
  
        }
    
    private int minmax(MOVE move, int deep, Game game){
        if (game.gameOver() || deep == this.deepest)
            return evaluate(game);

        if (move == null) {
            int maxScore = Integer.MIN_VALUE;

            for (MOVE currMove : moves) {
                int currentScore = this.minmax(currMove, deep + 1, game);
                maxScore = Math.max(currentScore, maxScore);
            }

            return maxScore;
        } 
        
        else {
            Game copy = game.copy();
            for (int i = 0; i < NUM_TRIALS; i++)
                copy.advanceGame(move, this.gCon.getMove(copy, -1));

            return this.minmax(null, deep, copy);
        
    }
    
    }
    
}
