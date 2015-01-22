package Oving1;

import java.util.List;

/*
 * Always defect player
 */
public class DefectPlayer extends Player implements Agent {

	@Override
	public Action dilemma(List<Action> opponentPreviousActions) {
		currentAction = Agent.Action.DEFECT;
		return currentAction;
	}

}
