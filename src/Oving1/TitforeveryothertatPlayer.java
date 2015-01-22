package Oving1;

import java.util.List;

/*
 * Tit for every other tat player
 */
public class TitforeveryothertatPlayer extends Player implements Agent {

	@Override
	public Action dilemma(List<Action> opponentPreviousActions) {
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
		return currentAction;
	}

}
