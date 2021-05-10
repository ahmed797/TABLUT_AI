package student_player;
import java.util.*;
import boardgame.Move;
import tablut.TablutMove;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutPlayer;
import tablut.TablutBoardState.Piece;

//constructor 1 
public class node {
 TablutBoardState state; 
 node parent;
 List<node> childArray;
 int wins;
 int visits;
 Move move; 
 int depth;
 
//constructor 1 
public node (TablutBoardState state){
	this.state = (TablutBoardState) state.clone();;
	this.parent= null;
	this.childArray= new ArrayList<node>();
	this.wins=0;
	this.visits=0;
	this.move=null; 
	this.depth=0;
}

//constructor 2 
public node (){
	this.state = new TablutBoardState();
	this.parent= null;
	this.childArray= new ArrayList<node>();
	this.wins=0;
	this.visits=0;
	this.move=null; 
	this.depth=0;
}

//constructor 3 
public node(node node) {
    this.childArray = new ArrayList<>();
    this.state = (TablutBoardState) node.getState().clone();
    if (node.getParent() != null)
        this.parent = node.getParent();
    List<node> childArray = node.getChildArray();
    for (node child : childArray) {
        this.childArray.add(new node(child));
    }
    this.move=node.move; 
	this.depth=node.depth;
}

//set methods
public void setState(TablutBoardState state){
	this.state= state ; 
}
public void setParent(node p){
	this.parent= p ; 
}
public void setchildArray( List<node> childArray){
	this.childArray=childArray; 
}
public void setWins(int wins){
	this.wins=wins ; 
}
public void setVisits(int visits){
	this.visits=visits ; 
}
public void setMove(Move move){
	this.move=move ; 
}
public void setDepth(int depth){
	this.depth=depth ; 
}

//get methods
public TablutBoardState getState(){
	return this.state ; 
}
public node getParent(){
	return this.parent; 
}
public List<node> getChildArray(){
	return this.childArray;
}
public int getWins(){
	return this.wins; 
}
public int getVisits(){
	return this.visits; 
}
public Move getMove(){
	return this.move; 
}
public int getDepth(){
	return this.depth; 
}

//Increment methods
public void IncWins(){
	this.wins++; 
}
public void IncVisits(){
	this.visits++; 
}

public node randomNodeChild(){
	if (childArray.size()==0){
		return null;
	}
	
	return this.childArray.get((int)(Math.random()*childArray.size()));
}

//adding child to a node 
public void addChild(node child){
	this.getChildArray().add(child);
	child.setParent(this);
}

//UCT
public double uctValue(double childWins, double childVisits) {
	if(childVisits==0){
		return 0; 
	} else{
		return ((double)childWins/childVisits) + Math.sqrt(2)*Math.sqrt(Math.log(this.visits)/childVisits);
	}
}

public node bestChild(){
	

	node bestChild = childArray.get(0);
	double max=bestChild.uctValue(bestChild.getWins(), bestChild.getVisits());
	for(node child:this.childArray){
		double temp=uctValue( child.getWins(), child.getVisits());
		if(temp > max) {
			max=temp;
			bestChild=child;
		}
	}
	return bestChild;
}

public node getChildWithMaxScore() {
	if(childArray.size() == 0) {
		return this;
	}
	node maxScoreNode = childArray.get(0);
	
	for(node child : childArray) {
		if(child.getVisits() > maxScoreNode.getVisits()) {
			maxScoreNode = child;
		}
	}
	return maxScoreNode;
}


//////Best moves 
public List<TablutMove> bestMoves(){
	TablutBoardState bs = this.getState();
	List<TablutMove> bestMoves = new ArrayList<TablutMove>();
	int player = bs.getTurnPlayer();
	int opponent = bs.getOpponent();
	List<TablutMove> allMoves = bs.getAllLegalMoves();
	int originalNumberOfOpponentPieces = bs.getNumberPlayerPieces(opponent);
	
	boolean moveCaptures = false;
	for(TablutMove move : allMoves) {
		TablutBoardState cloneBoardState = (TablutBoardState) bs.clone();
		cloneBoardState.processMove(move);
		
		// CAPTURE HEURISTIC
		int newNumberOfOpponentPieces = cloneBoardState.getNumberPlayerPieces(opponent);
		if(newNumberOfOpponentPieces < originalNumberOfOpponentPieces) {
			moveCaptures = true;
			bestMoves.add(move);
		}
		
		// WIN HEURISTIC - IF MOVE LEADS TO OUR WIN, RETURN JUST THIS MOVE
		if(cloneBoardState.getWinner() == player) {
			List<TablutMove> bestMove = new ArrayList<TablutMove>();
			bestMove.add(move);
			return bestMove;
		}
	}
	
	//
	if(player == TablutBoardState.SWEDE && bs.getKingPosition() != null) {
		if(!moveCaptures) {
		for(TablutMove move : allMoves) {
			boolean check=true;
			for(Coord pos : Coordinates.getNeighbors(move.getEndPosition())) {
				if(bs.getOpponentPieceCoordinates().contains(pos)) {
					check=false;
				}
			}

			if(check) {
				int count = 0;
				if(lineEmpty(move.getEndPosition(), Coordinates.get(move.getEndPosition().x, 0), bs)) {
					count++;
				}
				if(lineEmpty(move.getEndPosition(), Coordinates.get(move.getEndPosition().x, 8), bs)) {
					count++;
				}
				if(lineEmpty(move.getEndPosition(), Coordinates.get(0, move.getEndPosition().y), bs)) {
					count++;
				}
				if(lineEmpty(move.getEndPosition(), Coordinates.get(8, move.getEndPosition().y), bs)) {
					count++;
				}
				if(count >= 2) {
					bestMoves.add(move);
				}
			}
		}
		
		}
		
		// TEST HEURISTIC - ALL KING MOVES THAT LEAD TO EDGE
		Coord kingPosition = bs.getKingPosition();
		//int minDistance = Coordinates.distanceToClosestCorner(kingPosition);
		List<TablutMove> specialList = new ArrayList<TablutMove>();
		for(TablutMove kingMove : bs.getLegalMovesForPosition(kingPosition)) {
			List<Coord> twoCorners = new ArrayList<Coord>();
			if(!moveCaptures) {
			 bestMoves.add(kingMove);
			}
			
			if(kingMove.getEndPosition().x == 0) {
				for(Coord corner : Coordinates.getCorners()) {
					if(corner.x == 0) {
						twoCorners.add(corner);
					}
				}
			}
			if(kingMove.getEndPosition().x == 8) {
				for(Coord corner : Coordinates.getCorners()) {
					if(corner.x == 8) {
						twoCorners.add(corner);
					}
				}
			}
			if(kingMove.getEndPosition().y == 0) {
				for(Coord corner : Coordinates.getCorners()) {
					if(corner.y == 0) {
						twoCorners.add(corner);
					}
				}
			}
			if(kingMove.getEndPosition().y == 8) {
				for(Coord corner : Coordinates.getCorners()) {
					if(corner.y == 8) {
						twoCorners.add(corner);
					}
				}
			}
			
			if(twoCorners.size() == 2) {
				if(lineEmpty(kingMove.getEndPosition(), twoCorners.get(0), bs) && lineEmpty(kingMove.getEndPosition(), twoCorners.get(1), bs)) {
  				System.out.println("-------------------------------------------");
					List<TablutMove> bestMove = new ArrayList<TablutMove>();
					bestMove.add(kingMove);
					return bestMove;
				}
			}
			
			// PARTIAL
			boolean check =true; 
			for(Coord pos : Coordinates.getNeighbors(kingMove.getEndPosition())) {
				if(bs.getOpponentPieceCoordinates().contains(pos) || Coordinates.distanceToClosestCorner(kingMove.getEndPosition()) == 1) {
					check=false;
				}
				if (bs.getOpponentPieceCoordinates().contains(pos) && Coordinates.distanceToClosestCorner(kingMove.getEndPosition()) == 1) {
					check=true;
				}
			}
		
			// KNOW NO BLACKS
			if(twoCorners.size() == 2 && check) {
				if(lineEmpty(kingMove.getEndPosition(), twoCorners.get(0), bs) || lineEmpty(kingMove.getEndPosition(), twoCorners.get(1), bs)) {
					specialList.add(kingMove);
				}			
			}
			
		}
		if(specialList.size() != 0) {
			return specialList;
		}
		
	}
	
	if(bestMoves.size() == 0) {
		return bs.getAllLegalMoves();
	}
	return bestMoves; 
}

public boolean lineEmpty(Coord first, Coord second, TablutBoardState board) {
	List<Coord> coordsBetween = first.getCoordsBetween(second);
	for(Coord position : coordsBetween) {
		if(board.getOpponentPieceCoordinates().contains(position)||board.getPlayerPieceCoordinates().contains(position)) {
			return false;
		}
	}
	
	return true;
}

public boolean line(Coord first, Coord second, TablutBoardState board) {
	List<Coord> coordsBetween = first.getCoordsBetween(second);
	for(Coord position : coordsBetween) {
		if(board.getOpponentPieceCoordinates().contains(position)) {
			return false;
		}
	}
	
	return true;
}

public boolean coordinateEqual(Coord first, Coord second) {
	return (first.x == second.x && first.y == second.y);
}

// comparing 2 node 

public boolean equal (node node) {
	HashSet<Coord> player0 = this.getState().getPlayerPieceCoordinates();
	HashSet<Coord> player1 = node.getState().getPlayerPieceCoordinates();
	HashSet<Coord> op0 = this.getState().getOpponentPieceCoordinates();
	HashSet<Coord> op1 = node.getState().getOpponentPieceCoordinates();
		return player0.equals(player1) && op0.equals(op1);
	}
}

