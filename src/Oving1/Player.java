package Oving1;

import java.util.ArrayList;
import java.util.List;

import Oving1.Agent.Action;

public class Player {
	
	protected List<Action> previousActions;
	protected Action currentAction;
	protected int sumPayoff;
	
	public Player() {
		previousActions = new ArrayList<Action>();
		currentAction = null;
		sumPayoff = 0;
	}
	
	public List<Action> getPreviousActions() {
		return previousActions;
	}
	
	public Action getCurrentAction() {
		return currentAction;
	}
	
	public void addCurrentActionToPrevious() {
		previousActions.add(currentAction);
	}
	
	public void updateSumPayoff(Action opponentAction) {
		if(opponentAction==Agent.Action.DEFECT && currentAction==Agent.Action.DEFECT) {
			sumPayoff += 2;
		} else if (opponentAction==Agent.Action.DEFECT && currentAction==Agent.Action.COOPERATE) {
			sumPayoff += 0;
		} else if (opponentAction==Agent.Action.COOPERATE && currentAction==Agent.Action.DEFECT) {
			sumPayoff += 5;
		} else if (opponentAction==Agent.Action.COOPERATE && currentAction==Agent.Action.COOPERATE) {
			sumPayoff += 3;
		}
	}
	
	public void resetSumPayoff() {
		sumPayoff = 0;
	}
	
	public double calcFScore(int n) {
		return ((double)sumPayoff)/(((double)n)*((double)5));
	}

}
