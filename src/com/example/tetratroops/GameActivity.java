package com.example.tetratroops;

import tv.ouya.console.api.OuyaController;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

@SuppressLint("NewApi")
public class GameActivity extends Activity {
	
	  static GameActivity main;
	
	  protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        main = this;  
	        OuyaController.init(this);
	        setContentView(R.layout.game_renderer);
	        GameRenderer.s_instance.loadTexture(BitmapFactory.decodeResource(getResources(),
                R.drawable.board_start));
	  }
	  
	  
	  public Bitmap getSmileyBitmap(){
		return BitmapFactory.decodeResource(getResources(),
                R.drawable.smiley);
	  }
	  
	  @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	      boolean handled = OuyaController.onKeyDown(keyCode, event);
	      if (keyCode == OuyaController.BUTTON_A) {
	            finish();
	       }
	      return handled || super.onKeyDown(keyCode, event);
	  }
	  
	  @Override
	  public boolean onGenericMotionEvent(MotionEvent event) {
	      boolean handled = OuyaController.onGenericMotionEvent(event);
	      return handled || super.onGenericMotionEvent(event);
	  }

	  @Override
	  public boolean onKeyUp(int keyCode, KeyEvent event) {
	      boolean handled = OuyaController.onKeyUp(keyCode, event);
	      return handled || super.onKeyUp(keyCode, event);
	  }
	    
	    public static Bitmap getTextureOutside(int num){
			return main.getTexture(num);
	    }
	    
	    public static Context getContext(){
	    	return main;
	    }
	    
	    public Bitmap getTexture(int num){
			return BitmapFactory.decodeResource(getResources(),num);
	    }
	  

}
