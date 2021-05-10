package student_player;


import java.util.*;
import boardgame.Board;
import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;
import student_player.node;
import student_player.Tree;
import coordinates.Coord;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

	montyCarlo mcts= new montyCarlo();
	
	
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260676547");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
    	
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
    	long start = System.currentTimeMillis();
    	long end; //30 seconds
    	if (boardState.getTurnNumber() == 0) {
        	end = start + 29*1000; //30 seconds
    	}
    	
    	else {
        	end = start + 2*900; //just under 2 seconds
    	}
    	

    	if(this.mcts.getTree()==null) {
    		node root=new node(boardState);
    		this.mcts=new montyCarlo(root);
    	}else {
    		boolean treeExist=false;
    		node potentialRoot= new node(boardState);
    		if(this.mcts.getTree().getRoot().getChildArray().size()!=0) {
    			for(node child : this.mcts.getTree().getRoot().getChildArray()) {
    				if(child.equal(potentialRoot)) {
    					this.mcts.getTree().setRoot(potentialRoot);
    					treeExist = true; 
    				} 
    			}
    			if(!treeExist) {
    				this.mcts=new montyCarlo(potentialRoot);
    			}
    		}
    	}  
    	
    	node rootNode = mcts.getTree().getRoot();
    	
   
    	while (System.currentTimeMillis() < end) {   
    		node potentialNode = mcts.selection(rootNode); 
      		
      			List<TablutMove> moves = null;
      			if(boardState.getTurnPlayer()== potentialNode.getState().getTurnPlayer()) {
      				moves = potentialNode.bestMoves();
      			}else {
      				moves = potentialNode.getState().getAllLegalMoves();
      			}
      			
      			if(potentialNode.getState().getWinner() == Board.NOBODY){
      				for( TablutMove move : moves) {
      					mcts.expantion(potentialNode, move);
      					int gameWinner = mcts.simulation(potentialNode);
      					mcts.backPropagation(potentialNode, gameWinner);
      				}
      			}
    	}
    	
    	node bestNode = rootNode.getChildWithMaxScore();
    	mcts.getTree().setRoot(bestNode);

    	System.out.println(bestNode.getWins() + "/" + bestNode.getVisits());
        // Return your move to be processed by the server.
        return bestNode.getMove();
    }
}