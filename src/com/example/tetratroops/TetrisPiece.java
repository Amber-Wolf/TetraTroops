package com.example.tetratroops;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

public class TetrisPiece extends RenderObject {

	protected int pieceType;
	int[][] squares;
	int posX = 0;
	int posY = 0;
	int rot = 0;
	int player;
	int backstep;
	int team = 0;
	
	TetrisDrawBlock darwHelper;
	
	static Bitmap redBlock, redBlockT;
	static Bitmap blueBlock, blueBlockT;
	
	static final int SQUARE = 1;
	static final int I_BLOCK = 2;
	static final int JANK = 3;
	static final int R_JANK = 4;
	static final int L_BLOCK = 5;
	static final int R_L_BLOCK = 6;
	static final int T_BLOCK = 7;
	
	Bitmap[] bitmaps = new Bitmap[4];
	
	public TetrisPiece(float radius, int pieceNum, int team) {
		super(radius);
		if(redBlock == null){
			redBlock = GameActivity.getTextureOutside(R.drawable.redblock);
			redBlockT = GameActivity.getTextureOutside(R.drawable.redblockt);
		}
		if(blueBlock == null){
			blueBlock = GameActivity.getTextureOutside(R.drawable.blueblock);
			blueBlockT = GameActivity.getTextureOutside(R.drawable.blueblockt);
		}
		this.team = team;
		if(pieceNum == 0){
			pieceNum = (int)(Math.floor(Math.random() * 7)) + 1;
		}
		pieceType = pieceNum;
		this.initModel();
		switch(pieceType){
		case SQUARE:
			squares =  getSquareShape();
			//setBitmap(GameActivity.getTextureOutside(R.drawable.face));
			break;
		case I_BLOCK:
			squares = TetrisPiece.getIShape();
			break;
		case JANK:
			squares = TetrisPiece.getJankShape();
			break;
		case R_JANK:
			squares = TetrisPiece.getReverseJankShape();
			break;
		case L_BLOCK:
			squares = TetrisPiece.getLBlock();
			break;
		case R_L_BLOCK:
			squares = TetrisPiece.getReverseLBlock();
			break;
		case T_BLOCK:
			squares = TetrisPiece.getTShape();
			break;
		}
		darwHelper = new TetrisDrawBlock(1.0f, team);
	}
	
	//TODO readjust this to make the texture finding far easier.
	public TetrisPiece(float radius, int[][] squares, int posX, int posY, int team){  
		super(radius);
		this.team = team;
		this.squares = squares;
		this.posX = posX;
		this.posY = posY;
		this.rot = rot;
		this.initModel();
		//printSquares();
		darwHelper = new TetrisDrawBlock(1.0f, team);
		
	}

	@Override
	protected void initModel() {
		initGenericObject(8.0f,4.0f);
	}
	
	public void setTranslation(float x, float y){
		this.translation.set(x, y);
	}
	
	public void setPos(int x, int y){
		posX = x;
		posY = y;
	}
	
	public int getTeam(){
		return team;
	}
	
	public void doRender(GL10 gl) {
		doRender(gl,0);
	}
	
	public void doRender(GL10 gl, int setTran) {
		//mShouldLoadTexture = true;
		//super.doRender(gl);
		darwHelper.setTexture(setTran);
		for(int x = 0; x < 4; x++){
			for(int y=0; y < 4; y++){
				if(squares[x][y]  == 1){
					darwHelper.setTranslation((this.posX + x) * 4.0f, (this.posY + y) * 2.0f);
					darwHelper.doRender(gl);
				}
			}
		}
	}
	
	public int getX(){
		return posX;
	}
	
	public int getY(){
		return posY;
	}
	
	public int[][] getShape(){
		return squares;
	}
	
	void rotateRight(){
		int[][] newSquares = new int[4][4];
		for(int x = 0; x < 4; x++){
			for(int y=0; y < 4; y++){
				newSquares[3-y][x]=squares[x][y];
			}
		}
		squares = newSquares;
		rot++;
		if(rot>3){
			rot=0;
		}
		//TODO set new bitmap
	}
	
	void rotateLeft(){
		int[][] newSquares = new int[4][4];
		for(int x = 0; x < 4; x++){
			for(int y=0; y < 4; y++){
				newSquares[y][3-x]=squares[x][y];
			}
		}
		squares = newSquares;
		if(rot<0){
			rot=3;
		}
		//TODO set new bitmap
	}
	
	public ArrayList<TetrisPiece> breakPiece(int posX, int posY){
		int relPosX = posX - this.posX;
		int relPosY = posY - this.posY;
		squares[relPosX][relPosY] = 0;
		printSquares();
		ArrayList<TetrisPiece> pieces = new ArrayList<TetrisPiece>();
		findPart(pieces, 0, 0);
		return pieces;
	}
	
	protected void findPart(ArrayList<TetrisPiece> parts, int pX, int pY){
		boolean foundPart =false;
		boolean exit = false;
		int[][] newSquares = new int[4][4];
		while(!foundPart && !exit){
			if(pX > 3){
				pX = 0;
				pY++;
			}
			if(pY > 3){
				exit = true;
			}else if(squares[pX][pY] == 1){
				backstep = 0;
				newSquares[0][0]=1;
				squares[pX][pY] = 0;
				fillFoundPiece(newSquares, 0, 1, pX,pY + 1);
				fillFoundPiece(newSquares, 1, 0, pX + 1,pY);
				foundPart = true;
			}
			pX++;
		}
		if(foundPart){ //float radius, int[][] squares, int posX, int posY, int rot
			TetrisPiece part = new TetrisPiece(1.0f,newSquares,posX + pX - 1 - backstep,posY + pY,team);
			parts.add(part);
			findPart(parts,pX,pY);
		}
	}
	
	protected void fillFoundPiece(int[][] newSquares, int newX, int newY, int mastX, int mastY){
		if((mastX < 4) && (mastX >=0) && (mastY < 4) && (mastY>=0) && squares[mastX][mastY] == 1){
			if(newX < 0){
				for(int x = 2; x >= 0; x--){  //Shifts the new array to the right
					for(int y=0; y < 4; y++){
						newSquares[x+1][y] = newSquares[x][y]; //TODO
						newSquares[x][y] = 0;
					}
				}
				newX = 0;
				backstep ++;
			}
			newSquares[newX][newY] = 1;
			squares[mastX][mastY] = 0;
			fillFoundPiece(newSquares, newX + 1, newY, mastX + 1, mastY);
			fillFoundPiece(newSquares, newX, newY + 1, mastX, mastY + 1);
			fillFoundPiece(newSquares, newX - 1, newY, mastX - 1, mastY);
		}
	}
	
	protected static int[][] getSquareShape(){
		int[][] squares = new int[][]{{1, 1, 0, 0},{1, 1, 0, 0},{0,0,0,0},{0,0,0,0}};
		return squares;
	}
	
	protected static int[][] getIShape(){
		int[][] squares = new int[][]{{1, 0, 0, 0},{1, 0, 0, 0},{1,0,0,0},{1,0,0,0}};
		return squares;
	}
	
	protected static int[][] getTShape(){
		int[][] squares = new int[][]{{1, 0, 0, 0},{1, 1, 0, 0},{1,0,0,0},{0,0,0,0}};
		return squares;
	}
	
	protected static int[][] getJankShape(){
		int[][] squares = new int[][]{{1, 0, 0, 0},{1, 1, 0, 0},{0,1,0,0},{0,0,0,0}};
		return squares;
	}
	
	protected static int[][] getReverseJankShape(){
		int[][] squares = new int[][]{{0, 1, 0, 0},{1, 1, 0, 0},{1,0,0,0},{0,0,0,0}};
		return squares;
	}
	
	protected static int[][] getLBlock(){
		int[][] squares = new int[][]{{1, 0, 0, 0},{1, 0, 0, 0},{1,1,0,0},{0,0,0,0}};
		return squares;
	}
	
	protected static int[][] getReverseLBlock(){
		int[][] squares = new int[][]{{0, 1, 0, 0},{0, 1, 0, 0},{1,1,0,0},{0,0,0,0}};
		return squares;
	}
	
	protected void printSquares(){
		for(int r=0; r<4; r++){
			for(int s=0; s<4; s++){
				System.out.print(squares[s][r] + " ");
			}
			System.out.println();
		}
		System.out.println("-------------------");
	}
	
	public class TetrisDrawBlock extends RenderObject{
		
		Bitmap usedBitmap;
		int mode;
		
		public TetrisDrawBlock(float radius, int side) {
			super(radius);
			mode =-1;
			setTexture(0);
		}

		@Override
		protected void initModel() {
			initGenericObject(6.4f,3.2f);
		}
		
		public void doRender(GL10 gl) {
			//mShouldLoadTexture = true;
			super.doRender(gl);
		}
		
		public void setTranslation(float x, float y){
			this.translation.set(x, y);
		}
		
		public void setTexture(int mode){
			if(this.mode != mode){
				if(mode == 0){
					if(team == 0){
						setBitmap(redBlock);
					}else{
						setBitmap(blueBlock);
					}
				}else{
					if(team == 0){
						setBitmap(redBlockT);
					}else{
						setBitmap(blueBlockT);
					}
				}
				this.mode = mode;
			}
		}
		
		
		
	}

}
