package Oving1;

import java.util.List;
import java.util.Random;

/*
 * Random player that chooses 50/50 between cooperate or defect.
 */
public class RandomPlayer extends Player implements Agent {

	public static final int RANDOMUPTO = 2;
	
	private Random random;
	
	public RandomPlayer() {
		super();
		random = new Random();
	}
	
	@Override
	public Action dilemma(List<Action> opponentPreviousActions) {
		int strategyNumber = random.nextInt(RANDOMUPTO); // 0 or 1
		if (strategyNumber==0) {
			currentAction = Agent.Action.COOPERATE;
		} else {
			currentAction = Agent.Action.DEFECT;
		}
		return currentAction;
	}
	

}
