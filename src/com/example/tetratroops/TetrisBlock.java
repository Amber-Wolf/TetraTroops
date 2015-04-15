package com.example.tetratroops;

public class TetrisBlock {
	TetrisPiece piece = null;
	boolean filled = false;
	
	TetrisBlock(){	
	}
	
	TetrisPiece getPiece(){
		return piece;
	}
	
	public boolean filled(){
		return (piece != null);
	}
	
	public void removePiece(){
		piece = null;
	}
	
	public void setPiece(TetrisPiece p){
		piece = p;
	}
}
