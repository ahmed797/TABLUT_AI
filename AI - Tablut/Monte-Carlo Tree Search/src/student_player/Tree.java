package student_player;

import java.util.*;
import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutPlayer;
import student_player.node;



public class Tree {
node root;

	public Tree(){
		root=new node();
	}
	
	public Tree( node root){
		 this.root = root;
	}
	
	//set method
	public void setRoot(node root){
	        this.root = root;
    }
	
	//get method
	public node getRoot(){
		return this.root; 
	}
	
	//adding a child to a node of the tree
	public void addChild( node parent, node child){
		parent.getChildArray().add(child);
		child.setParent(parent);
	}
}
