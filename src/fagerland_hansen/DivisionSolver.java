package fagerland_hansen;

import java.util.Random;


public class DivisionSolver extends GeneralSolver {
	
	public DivisionSolver() {
		this.type = SolverType.DIVIDER;
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
