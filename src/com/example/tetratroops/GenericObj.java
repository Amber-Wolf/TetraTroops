package com.example.tetratroops;

import javax.microedition.khronos.opengles.GL10;

public class GenericObj extends RenderObject{
	
	int sizeX;
	int sizeY;

	public GenericObj(float radius) {
		super(radius);
		// TODO Auto-generated constructor stub
		sizeX = 10;
		sizeY = 10;
		initModel();
		this.translation.x = 0;
		this.translation.y = 0;
	}
	
	public GenericObj(float radius, float posX, float posY) {
		super(radius);
		// TODO Auto-generated constructor stub
		sizeX = 10;
		sizeY = 10;
		initModel();
		this.translation.x = posX;
		this.translation.y = posY;
	}

		@Override
	protected void initModel() {
		// TODO Auto-generated method stub
		this.initGenericObject(sizeX, sizeY);
		this.setBitmap(GameActivity.getTextureOutside(R.drawable.battleground));
	}
	
	public void doRender(GL10 gl){
		super.doRender(gl);
	}
		
}
