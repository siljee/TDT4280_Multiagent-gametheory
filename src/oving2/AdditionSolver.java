package oving2;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AdditionSolver extends GeneralSolver {
	

	
	public AdditionSolver() {
		//this.myAgent = this;
		this.type = SolverType.ADDER;
		this.name = "JADE-adder";
	}
	

	@Override
	protected Integer solveProblem(String problem) {
		String[] parts = numberSplitter(problem);
		int left = Integer.parseInt(parts[0]);
		int right = Integer.parseInt(parts[1]);
		return ((Integer) left+right);
	}

}