package Oving1;

import java.util.List;

/*
 * Always cooperate player
 */
public class CooperatePlayer extends Player implements Agent{

	@Override
	public Action dilemma(List<Action> opponentPreviousActions) {
		currentAction = Agent.Action.COOPERATE;
		return currentAction;
	}

}
