package com.example.tetratroops;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import Texample.GLText;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.opengl.GLU;
import android.util.Log;

public class GameMaster {
	
	static GLText text;
	TetrisBoard board;
	BanterManager bm;
	GenericObj god1;
	int god2Val = BanterManager.HADES;
	CoolDown god1Speak = new CoolDown(1100);
	CoolDown god1Speaking = new CoolDown(150);
	int god1quote;
	CoolDown god2Speak = new CoolDown(1000);
	CoolDown god2Speaking = new CoolDown(140);
	int god2quote;
	GenericObj god2;
	int god1Val = BanterManager.HERA;
	MediaPlayer m;
	
	GameMaster(){
		board = new TetrisBoard(1);
		bm = new BanterManager();
		god2 = new GenericObj(1,40,2);
		god2.setBitmap(GameActivity.getTextureOutside(R.drawable.hades1));
		god1 = new GenericObj(1, -10,2);
		god1.setBitmap(GameActivity.getTextureOutside(R.drawable.herahat1));
		try {
			bm.loadBanter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		m = new MediaPlayer();
		playMusic("bmusic2.mp3");
		god1Speak.reset();
		god2Speak.reset();
	}
	
	void update(){
		board.update();
		CoolDown.downTickAll();
	}
	
	void playMusic(String fileName){
		try {

	        if (m.isPlaying()) {
	            m.stop();
	            m.release();
	            m = new MediaPlayer();
	        }
	        AssetFileDescriptor descriptor = GameActivity.getContext().getAssets().openFd(fileName);
	        m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
	        descriptor.close();

	        m.prepare();
	        m.setVolume(1f, 1f);
	        m.setLooping(true);
	        m.start();
	    } catch (Exception e) {
	    	Log.d("Music Error","Error loading music!");
	    }
	}
	
	void doRender(GL10 gl){
		board.doRender(gl);
		if(text == null){
			text = new GLText(gl, GameActivity.getContext().getAssets());
			text.load( "Roboto-Regular.ttf", 12, 1, 1);
			text.setScale(text.getScaleX()/10.0f, -text.getScaleY()/10.0f);
		}
		text.begin( 0.0f, 0.0f, 0.0f, 1.0f );         // Begin Text Rendering (Set Color WHITE)
	    text.draw( "Player 1 Score: " + board.p1points, 0, 30 );
	    text.draw( "Player 2 Score: " + board.p2points, 10, 30);// Draw Test String
	    if(board.endGame1){
	    	text.draw("Player " + board.game1winner + " wins to 40 points!", 0, 35);
	    }
	    if(board.endGame2){
	    	text.draw("Player " + board.game2winner + "wins to 80 points!", 0, 37);
	    }
	    text.end();
	    //bm.doRenderText(gl, 0, 40, 0);
	    god1.doRender(gl);
	    god2.doRender(gl);
	    if(god1Speak.ready()){
	    	god1Speak.reset();
	    	god1Speaking.reset();
	    	if(board.p1points > board.p2points){
	    		god1quote = bm.findQuote(god1Val, god2Val, bm.WINNING);
	    	}else{
	    		god1quote = bm.findQuote(god1Val, god2Val, bm.LOSING);
	    	}
	    }
	    if(!god1Speaking.ready()){
	    	bm.doRenderText(gl, -12, 20, god1quote);
	    }
	    if(god2Speak.ready()){
	    	god2Speak.reset();
	    	god2Speaking.reset();
	    	if(board.p2points > board.p1points){
	    		god2quote = bm.findQuote(god2Val, god1Val, bm.WINNING);
	    	}else{
	    		god2quote = bm.findQuote(god2Val, god1Val, bm.LOSING);
	    	}
	    }
	    if(!god2Speaking.ready()){
	    	bm.doRenderText(gl, 40, 20, god2quote);
	    }
	}
	
	
	
	class BanterManager {
		
		ArrayList<String> banter = new ArrayList<String>();
		final int CONSTSTRIDE = 1;
		final int STRIDE = 41;
		final int MINISTRIDE = 5;
		boolean loaded = false;
		static final int ARES = 0;
		static final int ATHENA = 1;
		static final int HADES = 2;
		static final int HERA = 3;
		static final int MARTONIS = 4;
		static final int POSIEDON = 5;
		static final int STYX = 6;
		static final int ZUES = 7;
		
		static final int VICTORY = 1;
		static final int WINNING = 2;
		static final int LOSING = 3;
		static final int LOSS = 4;
		
		void loadBanter() throws IOException{
			AssetManager am = GameActivity.getContext().getAssets();
			InputStream is = am.open("GodBanter.txt");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String line;

			while ((line = br.readLine()) != null) {
				banter.add(line);
			}	
			loaded = true; 
		}
		
		void doRenderText(GL10 gl, float posX, float posY, int god, int enemy, int situation){
			if(loaded){
				text.begin( 0.0f, 0.0f, 0.0f, 1.0f );
				int pureVal = findQuote(god, enemy, situation);
				text.draw(banter.get(pureVal),posX,posY);
				text.end();
			}
		}
		
		void doRenderText(GL10 gl, float posX, float posY, int pureVal){
			if(loaded){
				text.begin( 0.0f, 0.0f, 0.0f, 1.0f );
				text.draw(banter.get(pureVal),posX,posY);
				text.end();
			}
		}
		
		int findQuote(int god, int enemy, int situation){
			return CONSTSTRIDE + god * STRIDE + enemy * MINISTRIDE + situation;
			
		}
	}
}
