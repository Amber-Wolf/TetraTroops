package com.example.tetratroops;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GameView extends GLSurfaceView {
	private com.example.tetratroops.GameRenderer _renderer;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public GameView(Context context) {
	    super(context);
	    init();
	}
	
	private void init() {
        _renderer = new GameRenderer();
        setRenderer(_renderer);
    }

}
