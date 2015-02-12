package Oving2;

import jade.core.Agent;

public class SubtractionSolver extends GeneralSolver {
	

	
	public SubtractionSolver() {
		this.myAgent = this;
		this.type = "Subber";
		this.name = "JADE-subber";
	}
	

	@Override
	protected Integer solveProblem(String problem) {
		String[] parts = numberSplitter(problem);
		int left = Integer.parseInt(parts[0]);
		int right = Integer.parseInt(parts[1]);
		return ((Integer) left-right);
	}

}
