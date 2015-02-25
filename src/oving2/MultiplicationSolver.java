package oving2;

import jade.core.Agent;

public class MultiplicationSolver extends GeneralSolver {
	
	public MultiplicationSolver() {
		//this.myAgent = this;
		this.type = SolverType.MULTIPLIER;
		this.name = "JADE-multiplier";
	}

	@Override
	protected Integer solveProblem(String problem) {
		String[] parts = numberSplitter(problem);
		int left = Integer.parseInt(parts[0]);
		int right = Integer.parseInt(parts[1]);
		return ((Integer) left*right);
	}
}
