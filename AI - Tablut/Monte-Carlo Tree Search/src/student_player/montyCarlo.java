package student_player;

import java.util.*;

import boardgame.Board;
import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;
import student_player.node;
import student_player.Tree;

public class montyCarlo {

Tree tree;	

public montyCarlo(){
	this.tree=null; 
}
public montyCarlo(node root){
	this.tree=new Tree(root); 
}

public void setTree(Tree tree) {
	this.tree = tree;
}

public Tree getTree() {
	return this.tree;
}

public node selection(node treeRoot){
	node parent=treeRoot; 
	while(parent.getChildArray().size() != 0){
		parent = parent.bestChild();
	}
	return parent; 
}
 
public void expantion (node node, Move move){
	
	TablutBoardState boardState = node.getState();
    TablutBoardState cloneBoardState=(TablutBoardState) boardState.clone();
	
  //  TablutMove randMove =  (TablutMove) cloneBoardState.getRandomMove(); 
	cloneBoardState.processMove((TablutMove)move);
	node child = new node(cloneBoardState);
	child.setMove(move);
	child.setDepth(node.getDepth()+1);
	child.setParent(node);
	node.addChild(child);
	
}
	
public void backPropagation(node explored, int gameStatus){
	node temp = explored; 
	while(temp != null) {
		temp.IncVisits();
		if(temp.getState().getTurnPlayer() == gameStatus){
			temp.IncWins();
			}
		temp=temp.getParent();
	}
}
	
public int simulation (node node){
	node temp = new node(node); 
	TablutBoardState tempState =(TablutBoardState) temp.getState(); 
	int gameStatus= tempState.getWinner();
	int depth=node.getDepth();
	while(gameStatus == Board.NOBODY && depth<100){
        tempState.processMove((TablutMove)tempState.getRandomMove());
        gameStatus = tempState.getWinner();
        depth++;
	}
	return gameStatus;
}
	
	
	
}