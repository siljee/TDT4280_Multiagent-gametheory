package Oving1;

import java.util.ArrayList;

public class Environment {
	
	public static final int NUMBEROFPLAYERS = 6;
	private ArrayList<Player> Players;
	
	public Environment() {
		Players = new ArrayList<Player>();
		Players.add(new CooperatePlayer());
		Players.add(new DefectPlayer());
		Players.add(new TitfortatPlayer());
		Players.add(new TitforeveryothertatPlayer());
		Players.add(new MixPlayer());
		Players.add(new RandomPlayer());
	}
		
	public void playNRounds(int n) {
		for(int i = 0; i<Players.size()-1; i++) {
			for(int j = i+1; j<Players.size();j++) {
				for(int roundNumber = 0; roundNumber < n; roundNumber++) {
					((Agent) Players.get(i)).dilemma(Players.get(j).getPreviousActions());
					((Agent) Players.get(j)).dilemma(Players.get(i).getPreviousActions());
					
					Players.get(i).updateSumPayoff(Players.get(j).getCurrentAction());
					Players.get(j).updateSumPayoff(Players.get(i).getCurrentAction());
					
					Players.get(i).addCurrentActionToPrevious();
					Players.get(j).addCurrentActionToPrevious();
					
				}
				Players.get(i).resetpreviousActions();
				Players.get(j).resetpreviousActions();
			}
		}
		
	}
	
	public void printFScores(int n) {
		System.out.println("The Players F-score:");
		for(int i = 0; i < NUMBEROFPLAYERS; i++) {
			System.out.println(Players.get(i).calcFScore(n));
		}
	}
	
	public void resetEnvironment() {
		for(int i = 0; i < NUMBEROFPLAYERS; i++) {
			Players.get(i).resetSumPayoff();
		}
	}

}
