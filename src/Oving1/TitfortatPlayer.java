package Oving1;

import java.util.List;

/*
 * Tit for tat player.
 * Inherits currentAction and previousActions from Player.
 */
public class TitfortatPlayer extends Player implements Agent  {

	
	@Override
	public Action dilemma(List<Action> opponentPreviousActions) {
		if (opponentPreviousActions.size()==0){
			currentAction = Agent.Action.COOPERATE;
		} else {
			int lastElementIndex = opponentPreviousActions.size() - 1;
			currentAction = opponentPreviousActions.get(lastElementIndex);
		}
			
		return currentAction;
	}
	


}
