package com.example.tetratroops;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import tv.ouya.console.api.OuyaController;

import Texample.GLText;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.KeyEvent;

public class TetrisBoard extends RenderObject{

	Bitmap bitmap;
	int[] textures;
	private FloatBuffer mTextureBuffer;
	final int BOARDSIZE = 10;
	TetrisBlock[][] board = new TetrisBlock[BOARDSIZE][BOARDSIZE];
	int[] buttonActivated = new int[100];
	GLText text;
	
	
	public int p1points = 0;  //move these elsewhere.
	public int p2points = 0;
	public boolean endGame1 = false;
	public boolean endGame2 = false;
	
	ArrayList<CoolDown> place =  new ArrayList<CoolDown>();
	
	int game1winner = 0;
	int game2winner = 0;
	final int SCORE = 40;
	final int SCOREPLUS = 80;
	int countdown = 10;
	
	final float ONSCREENSIZE =  40.0f;
	ArrayList<TetrisPiece> tetrisPieces =  new ArrayList<TetrisPiece>();
	ArrayList<TetrisPiece>playerPieces = new ArrayList<TetrisPiece>();
	
	ArrayList<OuyaController> ouyaControllers = new ArrayList<OuyaController>();
	OuyaController ouyaController;
	
	final int TEXTURENUM = 1;
	
	public TetrisBoard(float radius) {
		super(radius);
		for(int x = 0; x<BOARDSIZE; x++){
			for(int y = 0; y<BOARDSIZE; y++){
				board[x][y]= new TetrisBlock();  //TODO causes error.
			}
		}
		ouyaControllers.add(OuyaController.getControllerByPlayer(0));
		ouyaControllers.add(OuyaController.getControllerByPlayer(1));
		for(int x=0; x<2; x++){
			playerPieces.add(new TetrisPiece(1,0,x));
		}
		setBitmap(GameActivity.getTextureOutside(R.drawable.grid));
		for(int x = 0; x<2; x++){
			place.add(new CoolDown(25));
			place.get(x).reset();
		}
	}

	@Override
	protected void initModel() {
		final short[] _indicesArray = {2, 1, 0, 1, 2, 3};

        // float has 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(_indicesArray.length * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();

        // short has 2 bytes
        ByteBuffer ibb = ByteBuffer.allocateDirect(_indicesArray.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();

        final float[] coords = {
                0.0f, ONSCREENSIZE/2, 0.0f, // 0
                ONSCREENSIZE, ONSCREENSIZE/2, 0.0f, // 1
                0.0f,  0.0f, 0.0f, // 2
                ONSCREENSIZE, 0.0f, 0.0f, // 3
        };

        vertexBuffer.put(coords);
        indexBuffer.put(_indicesArray);

        vertexBuffer.position(0);
        indexBuffer.position(0);
        
        float textureCoords[] = {0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f };
        setTextureCoordinates(textureCoords);
		
	}
	
	public void update(){
		for(int x = 0; x<2; x++){
			if( (playerPieces.get(x) == null) && (place.get(x).ready())) {
				playerPieces.set(x, generatePiece(x));
				Log.d("Piece", "Piece Reset");
			}
		}
		if(!endGame2){
			updateControllers();
		}
		if(!endGame1){
			if((p1points >= 40) && (p2points >=40)){
				game1winner = -1;
				endGame1 = true;
			}else if(p1points >= 40){
				game1winner = 1;
				endGame1 = true;
			}else if(p2points >= 40){
				game1winner = 2;
				endGame1 = true;
			}
		}
		if(!endGame2){
			if((p1points >= 80) && (p2points >=80)){
				game2winner = -1;
				endGame2 = true;
			}else if(p1points >= 80){
				game2winner = 1;
				endGame2 = true;
			}else if(p2points >= 80){
				game2winner = 2;
				endGame2 = true;
			}
		}
	}
	
	
	
	public void updateControllers(){
		for(int x=0; x<ouyaControllers.size(); x++){
			ouyaController = ouyaControllers.get(x);
			TetrisPiece piece = playerPieces.get(x);
			if(piece == null){
				
			}else if(ouyaController != null){
				if(ouyaController.buttonPressedThisFrame(OuyaController.BUTTON_DPAD_RIGHT)){
					movePiece(piece, 1,0);
				}
				if(ouyaController.getButton(OuyaController.BUTTON_DPAD_RIGHT)){
					buttonActivated[OuyaController.BUTTON_DPAD_RIGHT] += 1;
					if(buttonActivated[OuyaController.BUTTON_DPAD_RIGHT] > 10){
						buttonActivated[OuyaController.BUTTON_DPAD_RIGHT] = 0;
						movePiece(piece, 1,0);
					}
				} else {
					buttonActivated[OuyaController.BUTTON_DPAD_RIGHT] = 0;
				}
				if(ouyaController.buttonPressedThisFrame(OuyaController.BUTTON_DPAD_DOWN)){
					movePiece(piece, 0,1);
				}
				if(ouyaController.getButton(OuyaController.BUTTON_DPAD_DOWN)){
					buttonActivated[OuyaController.BUTTON_DPAD_DOWN] += 1;
					if(buttonActivated[OuyaController.BUTTON_DPAD_DOWN] > 10){
						buttonActivated[OuyaController.BUTTON_DPAD_DOWN] = 0;
						movePiece(piece, 0,1);
					}
				} else {
					buttonActivated[OuyaController.BUTTON_DPAD_DOWN] = 0;
				}
				if(ouyaController.buttonPressedThisFrame(OuyaController.BUTTON_DPAD_LEFT)){
					movePiece(piece, -1,0);
				}
				if(ouyaController.getButton(OuyaController.BUTTON_DPAD_LEFT)){
					buttonActivated[OuyaController.BUTTON_DPAD_LEFT] += 1;
					if(buttonActivated[OuyaController.BUTTON_DPAD_LEFT] > 10){
						buttonActivated[OuyaController.BUTTON_DPAD_LEFT] = 0;
						movePiece(piece, -1,0);
					}
				} else {
					buttonActivated[OuyaController.BUTTON_DPAD_LEFT] = 0;
				}
				if(ouyaController.buttonPressedThisFrame(OuyaController.BUTTON_DPAD_UP)){
					movePiece(piece, 0,-1);
				}
				if(ouyaController.getButton(OuyaController.BUTTON_DPAD_UP)){
					buttonActivated[OuyaController.BUTTON_DPAD_UP] += 1;
					if(buttonActivated[OuyaController.BUTTON_DPAD_UP] > 10){
						buttonActivated[OuyaController.BUTTON_DPAD_UP] = 0;
						movePiece(piece, 0,-1);
					}
				} else {
					buttonActivated[OuyaController.BUTTON_DPAD_UP] = 0;
				}
				if(ouyaController.buttonPressedThisFrame(OuyaController.BUTTON_O)){
					if(placePiece(piece)){
						//playerPieces.set(x, generatePiece(x));
						playerPieces.set(x, null);
						place.get(x).reset();
						checkFilled();
					}
				}
				if(ouyaController.buttonPressedThisFrame(OuyaController.BUTTON_U)){
					piece.rotateLeft();
					shiftPiece(piece);
				}
				if(ouyaController.buttonPressedThisFrame(OuyaController.BUTTON_Y)){
					piece.rotateRight();
					shiftPiece(piece);
				}
				if(ouyaController.buttonPressedThisFrame(OuyaController.BUTTON_L1)){
					playerPieces.set(x, null);
					place.get(x).reset();
				}
			}
			if(ouyaController == null){
				//System.out.println("Ouya Controller " + x + " is NULL");
			}
		}
	}
	
	protected void checkFilled(){
		for(int x=0;x<BOARDSIZE;x++ ){
			if(checkRow(x)){
				clearRow(x);
			}
			if(checkColumn(x)){
				clearColumn(x);
			}
		}
		printBoardState();
	}
	
	protected void printBoardState(){
		for(int x = 0; x<BOARDSIZE; x++){
			for(int y = 0; y<BOARDSIZE; y++){
				System.out.print(board[y][x].filled() + " ");
			}
			System.out.println();
		}
	}
	
	protected boolean checkRow(int x){
		boolean filled = true;
		for(int r=0; r< BOARDSIZE; r++){
			if(!board[r][x].filled()){
				filled = false;
				r = BOARDSIZE;
			}
		}
		return filled;
	}
	
	protected boolean checkColumn(int x){
		boolean filled = true;
		for(int r=0; r< BOARDSIZE; r++){
			if(!board[x][r].filled()){
				filled = false;
				r = BOARDSIZE;
			}
		}
		return filled;
	}
	
	protected void clearRow(int x){
		for(int r=0; r< BOARDSIZE; r++){
			ArrayList<TetrisPiece> pieces = board[r][x].getPiece().breakPiece(r,x);
			TetrisPiece clearPiece  = board[r][x].getPiece();
			if(clearPiece.getTeam() == 0){
				p1points++;
			}else{
				p2points++;
			}
			board[r][x].removePiece();
			tetrisPieces.remove(clearPiece);
			for(int rr=0; rr<pieces.size(); rr++){
				TetrisPiece piece = pieces.get(rr);
				putOnBoard(piece,piece.getX(), piece.getY());
				tetrisPieces.add(piece);
			}
			
		}
	}
	
	protected void clearColumn(int x){
		for(int r=0; r< BOARDSIZE; r++){
			ArrayList<TetrisPiece> pieces = board[x][r].getPiece().breakPiece(x,r);
			TetrisPiece clearPiece  = board[x][r].getPiece();
			if(clearPiece.getTeam() == 0){
				p1points++;
			}else{
				p2points++;
			}
			board[x][r].removePiece();
			tetrisPieces.remove(clearPiece);
			for(int rr=0; rr<pieces.size(); rr++){
				TetrisPiece piece = pieces.get(rr);
				putOnBoard(piece,piece.getX(), piece.getY());
				tetrisPieces.add(piece);
			}
			
		}
	}
	
	
	
	public boolean placePiece(TetrisPiece piece){
		if(isLegalMove(piece, piece.getX(), piece.getY(), true)){
			tetrisPieces.add(piece);
			putOnBoard(piece,piece.getX(),piece.getY());
			return true;
		}else{
			return false;
		}
	}
	
	protected boolean isLegalMove(TetrisPiece piece, int posX, int posY, boolean collide){
		boolean bool = true;
		int[][] pieceShape = piece.getShape();
		int pX = posX;
		int pY = posY;
		for(int x = 0; x < 4; x++){
			for(int y=0; y < 4; y++){
				if(pieceShape[x][y] == 1){
					if((x+pX >= BOARDSIZE) || (x + pX < 0) || (y+pY < 0)
							|| (y + pY >= BOARDSIZE)){
						bool = false;
					} else if( collide && (board[x + pX][y + pY].filled())){
						bool = false;
					}
				}
			}
		}
		return bool;
	}
	
	protected void shiftPiece(TetrisPiece piece){
		int[][] pieceShape = piece.getShape();
		int pX = piece.getX();
		int pY = piece.getY();
		boolean reset = false;
		for(int x = 0; x < 4; x++){
			for(int y=0; y < 4; y++){
				if(pieceShape[x][y] == 1){
					if(x+pX >= BOARDSIZE){
						forceMovePiece(piece, -1,0);
						x=10;
						y=10;
						reset = true;
					}else if(x + pX < 0){
						forceMovePiece(piece, 1, 0);
						x=10;
						y=10;
						reset = true;
					}else if(y+pY < 0){
						forceMovePiece(piece, 0, 1);
						x=10;
						y=10;
						reset = true;
					}else if(y + pY >= BOARDSIZE){
						forceMovePiece(piece, 0, -1);
						x=10;
						y=10;
						reset = true;
					}
				}
			}
		}
		if(reset){
			shiftPiece(piece);
		}
	}
	
	protected void putOnBoard(TetrisPiece piece, int posX, int posY){
		int[][] pieceShape = piece.getShape();
		for(int x = 0; x < 4; x++){
			for(int y=0; y < 4; y++){
				if(pieceShape[x][y] == 1){
					board[x + posX][y + posY].setPiece(piece);
				}
			}
		}
		piece.setPos(posX, posY);
	}
	
	protected TetrisPiece generatePiece(int playerNum){
		return new TetrisPiece(1,0,playerNum);
	}
	
	public void movePiece(TetrisPiece piece, int changeX, int changeY){
		if(isLegalMove(piece, piece.getX() + changeX, piece.getY() + changeY, false)){
			piece.setPos(piece.getX() + changeX, piece.getY() + changeY);
		}
		piece.setTranslation(piece.getX()*4, piece.getY() * 2);
	}
	
	private void forceMovePiece(TetrisPiece piece, int changeX, int changeY){
		piece.setPos(piece.getX() + changeX, piece.getY() + changeY);
		piece.setTranslation(piece.getX()*4, piece.getY() * 2);
	}
	
	protected void doRender(GL10 gl) {
		if(text == null){
			text = new GLText(gl, GameActivity.getContext().getAssets());
			text.load( "Roboto-Regular.ttf", 12, 1, 1);
			text.setScale(text.getScaleX()/10.0f, -text.getScaleY()/10.0f);
		}
		super.doRender(gl);
		for(int x=0; x<tetrisPieces.size(); x++){
			tetrisPieces.get(x).doRender(gl);
		}
		for(int x=0; x<2; x++){
			TetrisPiece piece = playerPieces.get(x);
			if(piece != null){
				piece.doRender(gl,1);
			}
		}
		
	}
	
	
}
