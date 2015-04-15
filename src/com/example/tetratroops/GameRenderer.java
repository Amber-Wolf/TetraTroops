package com.example.tetratroops;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import tv.ouya.console.api.OuyaController;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameRenderer implements GLSurfaceView.Renderer {
	
	 static public GameRenderer s_instance = null;
	 float colorVal = 0f;

	 static public final float BOARD_WIDTH = 40.0f;
	 static public final float BOARD_HEIGHT = 40.0f;
	 TetrisBoard board;
	 GameMaster gameMaster;
	 Background background;
	 
	 
	  private float _red = 0.9f;
	  private float _green = 0.2f;
	  private float _blue = 0.2f;
	  private float width = 1920;
	  private float height = 1080;
	
	 public GameRenderer() {
		 s_instance = this;
		 board = new TetrisBoard(1);
		 gameMaster = new GameMaster();
		 background = new Background(1);
		 background.initModel();
		
		 
	 }
	 
	public void loadTexture(Bitmap bitmap){
			board.setBitmap(bitmap);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glClearColor(_red, _green, colorVal, 1.0f);
		colorVal = colorVal + .001f;
		if(colorVal >= 1){
			colorVal = 0f;
		}
		background.doRender(gl);
		gameMaster.doRender(gl);
		gameMaster.update();
		OuyaController.startOfFrame();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
        width = w;
        height = h;
        gl.glViewport(0, 0, w, h);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		  gl.glMatrixMode(GL10.GL_PROJECTION);
	      gl.glLoadIdentity();

	      // orthographic
	      float ratio = width/height;
	      float extraSpacePerSide = ((BOARD_WIDTH * ratio) - BOARD_WIDTH ) / 2.0f;
	      float left = 0.0f - extraSpacePerSide;
	      float right = BOARD_WIDTH + extraSpacePerSide;
	      gl.glOrthof(left, right, BOARD_HEIGHT, 0, -10.0f, 10.0f);
	      //gl.glFrustumf(-10.0f, 10.0f, 10.0f, -10.0f, 0.1f, 100.0f);
	      gl.glViewport(0, 0, (int) width, (int) height);
	      gl.glMatrixMode(GL10.GL_MODELVIEW);
	      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	      gl.glClearColor(_red, _green, _blue, 1.0f);
	}
	
	

	public void addRenderObject(RenderObject renderObject) {
		// TODO Auto-generated method stub
		
	}

	public RenderObject getCollidingObject(RenderObject renderObject) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	public class Background extends RenderObject{

		public Background(float radius) {
			super(radius);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void initModel() {
			// TODO Auto-generated method stub
			this.initGenericObject(100, 50);
			this.setBitmap(GameActivity.getTextureOutside(R.drawable.battleground));
			this.translation.x = -20;
			this.translation.y = 0;
		}
		
		public void doRender(GL10 gl){
			super.doRender(gl);
		}
		
	}
}