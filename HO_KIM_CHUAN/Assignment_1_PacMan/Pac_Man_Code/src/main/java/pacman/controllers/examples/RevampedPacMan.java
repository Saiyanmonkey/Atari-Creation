package pacman.controllers.examples;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.Random;

public class RevampedPacMan extends PacmanController {

    private final int THRESHOLD_DISTANCE;

    public RevampedPacMan(int threshold_distance) { THRESHOLD_DISTANCE = threshold_distance; }

    /**
     * Container to store 2 return types
     * @param <T1> Item 1
     * @param <T2> Item 2
     */
    static class Container<T1, T2> {
        T1 item1;
        T2 item2;
        Container(T1 item1, T2 item2) {
            this.item1 = item1;
            this.item2 = item2;
        }
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        return inferenceNextMove(game);
    }

    /**
     * Inference next move
     * @param game: pacman.game.Game
     * @return MOVE next move
     */
    private MOVE inferenceNextMove(Game game) {
        int pacMan = game.getPacmanCurrentNodeIndex();
        // 1st policy: Away from nearest ghost
        Container<GHOST, Integer> ghostAndDistance = getNearestGhostAndDistance(game);
        if (ghostAndDistance.item2 < THRESHOLD_DISTANCE) {
            int ghost = game.getGhostCurrentNodeIndex(ghostAndDistance.item1);
            return game.getNextMoveAwayFromTarget(pacMan, ghost, DM.PATH);
        }
        // 2nd policy: Chase edible ghost
        GHOST edibleGhost = getNearestEdibleGhost(game);
        if (edibleGhost != null) {
            int ghost = game.getGhostCurrentNodeIndex(edibleGhost);
            return game.getNextMoveTowardsTarget(pacMan, ghost, DM.PATH);
        }
        // 3rd policy: Eat nearest pill
        int[] pills = getPills(game);
        if (pills.length > 0) {
            int nearestPill = game.getClosestNodeIndexFromNodeIndex(pacMan, pills, DM.PATH);
            return game.getNextMoveTowardsTarget(pacMan, nearestPill, DM.PATH);
        }
        // 4th policy: Search for best move
        MOVE lastMove = game.getPacmanLastMoveMade();
        MOVE[] moves = game.getPossibleMoves(pacMan, lastMove);
        return (moves.length > 0) ? moves[new Random().nextInt(moves.length)] : lastMove.opposite();
    }

    /**
     * Compute the nearest ghost from the PacMan and its distance.
     * @param game pacman.game.Game
     * @return {item1: GHOST, item2: Integer}
     */
    private Container<GHOST, Integer> getNearestGhostAndDistance(Game game) {
        int pacManPos = game.getPacmanCurrentNodeIndex();
        int shortestDistance = Integer.MAX_VALUE;
        GHOST nearestGhost = null;
        int ghostPos, distance;

        for (GHOST ghost: GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                ghostPos = game.getGhostCurrentNodeIndex(ghost);
                if (ghostPos != -1) {
                    distance = game.getShortestPathDistance(ghostPos, pacManPos);
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        nearestGhost = ghost;
                    }
                }
            }
        }
        return new Container<>(nearestGhost, shortestDistance);
    }

    /**
     * Compute nearest edible ghost from the PacMan
     * @param game pacman.game.Game
     * @return GHOST nearest ghost from PacMan
     */
    private GHOST getNearestEdibleGhost(Game game) {
        int pacManPos = game.getPacmanCurrentNodeIndex();
        int shortestDistance = Integer.MAX_VALUE;
        GHOST nearestGhost = null;
        int ghostPos, distance;

        for (GHOST ghost: GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) > 0) {
                ghostPos = game.getGhostCurrentNodeIndex(ghost);
                distance = game.getShortestPathDistance(ghostPos, pacManPos);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestGhost = ghost;
                }
            }
        }
        return nearestGhost;
    }

    /**
     * Get the indices of all active pills
     * @param game pacman.game.Game
     * @return int[] indices of active pills or power pills
     */
    private int[] getPills(Game game) {
        int[] a = game.getActivePillsIndices();
        int[] b = game.getActivePowerPillsIndices();
        int[] pills = new int[a.length + b.length];

        for (int i = 0; i < a.length + b.length; i++)
            pills[i] = (i < a.length) ? a[i] : b[i - a.length];

        return pills;
    }
}