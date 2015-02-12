package Oving2;

import jade.core.Agent;

public class DivisionSolver extends GeneralSolver {
	
	public DivisionSolver() {
		this.myAgent = this;
		this.type = "Divisioner";
		this.name = "JADE-Divisioner";
	}

	@Override
	protected Integer solveProblem(String problem) {
		String[] parts = numberSplitter(problem);
		int left = Integer.parseInt(parts[0]);
		int right = Integer.parseInt(parts[1]);
		return ((Integer) left/right);
	}

}
