/*
 * Copyright (C) 2012 OUYA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tetratroops;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class RenderObject {
	
	protected Bitmap bitmap;
	protected final int TEXTURENUM = 1;
	protected FloatBuffer mTextureBuffer;
    protected ShortBuffer indexBuffer;
    protected FloatBuffer vertexBuffer;
    private int mTextureId = -1;
    boolean mShouldLoadTexture = false;

    protected float rotation = 0.0f;    // rotation about the Z-axis
    protected PointF translation;

    protected float radius = 1.0f;

    protected CollisionListener collisionListener;

    public interface CollisionListener {
        // Return false if the move should fail
        public void onCollide(PointF prev, RenderObject me, RenderObject other);
    }

    public RenderObject(float radius) {
        this.radius = radius;
        translation = new PointF();
        initModel();
        GameRenderer.s_instance.addRenderObject(this);
    }

    public void setCollisionListener(CollisionListener collisionListener) {
        this.collisionListener = collisionListener;
    }

    public void setRotate(float degrees) {
        float delta =  degrees - rotation;
        rotate(delta);
    }

    public void rotate(float degreeDelta) {
        rotation += degreeDelta;
        rotation %= 360.0f;
    }

    public PointF getForwardVector() {
        float fwdX = (float) Math.sin(Math.toRadians(-rotation));
        float fwdY = (float) Math.cos(Math.toRadians(-rotation));
        return new PointF(fwdX, fwdY);
    }

    public void goForward(float amount) {
        final PointF prev = new PointF(translation.x, translation.y);

        PointF newPos = getForwardVector();

        translation.x += newPos.x * amount;
        translation.y += newPos.y * amount;

        if (translation.x < 0.0f) translation.x += GameRenderer.BOARD_WIDTH;
        if (translation.x > 10.0f) translation.x %= GameRenderer.BOARD_WIDTH;
        if (translation.y < 0.0f) translation.y += GameRenderer.BOARD_HEIGHT;
        if (translation.y > 10.0f) translation.y %= GameRenderer.BOARD_HEIGHT;

        if (collisionListener != null) {
            final RenderObject collidingObject = GameRenderer.s_instance.getCollidingObject(this);
            if (collidingObject != null) {
                collisionListener.onCollide(prev, this, collidingObject);
            }
        }
    }

    protected abstract void initModel();

    protected void update() {
    }

    protected void setColor(GL10 gl, int color) {
        gl.glColor4f(
                Color.red(color) / 255.0f,
                Color.green(color) / 255.0f,
                Color.blue(color) / 255.0f,
                0.75f);
    }

    protected void doRender(GL10 gl) {
    	gl.glEnable(gl.GL_BLEND);  //TODO Move elsewhere, like the super.doRender
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
    	if (mShouldLoadTexture) {
            loadGLTexture(gl);
            mShouldLoadTexture = false;
        }
        if (mTextureId != -1 && mTextureBuffer != null) {
            gl.glEnable(GL10.GL_TEXTURE_2D);
            // Enable the texture state
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
     
            // Point to our buffers
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
        }
    	
        gl.glPushMatrix();

        gl.glTranslatef(translation.x, translation.y, 5.0f);
        gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, indexBuffer.limit(), GL10.GL_UNSIGNED_SHORT, indexBuffer);

        gl.glPopMatrix();
		
		if (mTextureId != -1 && mTextureBuffer != null) {
	        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    }
    }

    public float getRadius() {
        return radius;
    }

    public boolean doesCollide(RenderObject other) {
        float deltaX = translation.x - other.translation.x;
        float deltaY = translation.y - other.translation.y;
        float distSq = deltaX * deltaX + deltaY * deltaY;
        float radiiSq = getRadius() + other.getRadius();
        radiiSq *= radiiSq;
        if (distSq <= radiiSq) {
            return true;
        }
        return false;
    }
    
    protected void setTextureCoordinates(float[] textureCoords) {
        // float is 4 bytes, therefore we multiply the number if
            // vertices with 4.
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(
                                               textureCoords.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuf.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);
    }

    
	protected void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
		System.out.println("textureSet");
		mShouldLoadTexture = true;
	}
	
	private void loadGLTexture(GL10 gl) {
	    // Generate one texture pointer...
	    int[] textures = new int[1];
	    gl.glGenTextures(1, textures, 0);
	    System.out.println("texture" + textures[0]);
	    mTextureId = textures[0];
	 
	    // ...and bind it to our array
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
	 
	    // Create Nearest Filtered Texture
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
	            GL10.GL_LINEAR);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
	            GL10.GL_LINEAR);
	 
	    // Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
	            GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
	            GL10.GL_REPEAT);
	 
	    // Use the Android GLUtils to specify a two-dimensional texture image
	    // from our bitmap
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	}
	
	protected void initGenericObject(float sizeX, float sizeY){
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
                0.0f, sizeY, 0.0f, // 0
                 sizeX, sizeY, 0.0f, // 1
                 0.0f,  0.0f, 0.0f, // 2
                 sizeX, 0.0f, 0.0f, // 3
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
	
}
