package Oving1;

import java.util.*;

/*
 * Mix of tit for tat and tit for every other tat 
 */
public class MixPlayer extends Player implements Agent{
	
	public static final int RANDOMUPTO = 2; 
	
	private Random random;
	
	public MixPlayer() {
		super();
		random = new Random();
	}

	@Override
	public Action dilemma(List<Action> opponentPreviousActions) {
		int strategyNumber = random.nextInt(RANDOMUPTO); // 0 or 1
		if (strategyNumber==0) {
			titForTat(opponentPreviousActions);
		} else {
			titForEveryOtherTat(opponentPreviousActions);
		}
		
		return currentAction;
	}
	
	private void titForTat(List<Action> opponentPreviousActions) {
		if (opponentPreviousActions==null){
			currentAction = Agent.Action.COOPERATE;
		} else {
			int lastElementIndex = opponentPreviousActions.size() - 1;
			currentAction = opponentPreviousActions.get(lastElementIndex);
		}
	}

	private void titForEveryOtherTat(List<Action> opponentPreviousActions) {
		if(opponentPreviousActions.size()<2) {
			currentAction = Agent.Action.COOPERATE;
		} else {
			int lastElementIndex = opponentPreviousActions.size() - 1;
			int secondLastElementIndex = opponentPreviousActions.size() - 2;
			
			if(opponentPreviousActions.get(lastElementIndex)==Agent.Action.DEFECT && 
					opponentPreviousActions.get(secondLastElementIndex)==Agent.Action.DEFECT) {
				currentAction = Agent.Action.DEFECT;
			} else {
				currentAction = Agent.Action.COOPERATE;
			}
		}
	}

	

}
