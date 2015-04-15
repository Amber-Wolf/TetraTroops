package com.example.tetratroops;

import java.util.ArrayList;

import android.util.Log;

public class CoolDown {

	static ArrayList<CoolDown> cooldowns = new ArrayList<CoolDown>();
	
	boolean deleteMe = false;
	boolean offCooldown = true;
	
	int counter = 0;
	int maxCooldown = 0;
	
	public CoolDown(int maxCooldown){
		cooldowns.add(this);
		this.maxCooldown = maxCooldown;
	}
	
	protected void downTick(){
		counter--;
		
		if(counter < 0){
			counter = 0;
			offCooldown = true;
			//Log.d("","");
			//Log.d("DownTicking","HI");
		}
	}
	
	public void reset(){
		counter = maxCooldown;
		offCooldown = false;
	}
	
	public boolean ready(){
		return offCooldown;
	}
	
	public static void downTickAll(){
		for(int x = 0; x < cooldowns.size(); x++){
			CoolDown cl = cooldowns.get(x);
			if(!cl.ready()){
				cl.downTick();
			}
			//Log.d("DOWNTICK", "Happened");
		}
	}
	
	public void remove(){
		cooldowns.remove(this);
	}
}
