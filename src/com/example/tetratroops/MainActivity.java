package com.example.tetratroops;

import tv.ouya.console.api.OuyaController;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

    private VideoView myVideoView;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myVideoView = (VideoView)findViewById(R.id.myvideoview);
        myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() +"/"+R.raw.open));
        myVideoView.start();
        OuyaController c = OuyaController.getControllerByPlayer(0);
        Button newGame = (Button) findViewById(R.id.battle_button);
        newGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        handled = OuyaController.onKeyDown(keyCode, event);
        if (keyCode == OuyaController.BUTTON_O) {
        	startActivity(new Intent(MainActivity.this, GameActivity.class));
        }
        if (keyCode == OuyaController.BUTTON_A) {
            finish();
        }
        return handled;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean handled = false;
        handled = OuyaController.onKeyUp(keyCode, event);
        return handled;
    }

    
}
