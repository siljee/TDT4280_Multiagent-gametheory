package oving2;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class TaskAdmin extends Agent{	
	
	private String problem = ""; 		// Complete problem
	private String [] splittedProblem;	// Problem splitted as a list of string
	private ArrayList<Integer> subProblemIndices = new ArrayList<Integer>();
	private ArrayList<Integer> old_subProblemIndices = new ArrayList<Integer>();

	public void setup() {
		// Make the TaskAgent sleep for 5 seconds to make sure the other agents are started.
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("TaskAdmin: "+getAID().getName()+" is ready.");
		
		// Get problem and start behaviour. If no problem: terminate.
		if (getProblem(getArguments())) { 
			addBehaviour(new startBehaviour());
			System.out.println("Problem to solve: " + problem);
		} else {
			System.out.println("Could not solve this");
			doDelete();
		}
	}
	
	private class startBehaviour extends CyclicBehaviour {
		SolverType agentType;
		
		@Override
		public void action() {
			if (isProblemSolved()) {
				System.out.println("******************** Problem is solved! ********************");
				System.out.println("Solution: " + splittedProblem[splittedProblem.length-1]);
				doDelete();
			}
			subProblemIndices = findSubproblemIndeces();
			
			if (subProblemIndices.size() > 0) {
				// There is at least one possible subproblem. Define agentType and subProblem
				for (int i = 0; i < subProblemIndices.size(); i++ ) {
					int subProblemIndex = subProblemIndices.get(i);
					agentType = getAgentType(splittedProblem[subProblemIndex]);
					String subProblem = splittedProblem[findValidIndex(subProblemIndex-2)] + " "
						+ splittedProblem[findValidIndex(subProblemIndex-1)];
					
					// Add the subproblem to old_subProblemIndices so they will not be analysed again.
					old_subProblemIndices.add(subProblemIndices.get(i));
					myAgent.addBehaviour(new SearchForAgents(subProblemIndices.get(i), subProblem, agentType));					
				}
			}	
		}
	} 
	
	// Split the problem from the program arguments
	public boolean getProblem(Object [] args) {
		if (args != null && args.length > 0) {
			problem = (String) args[0];
			splittedProblem = problem.split(" ");
			return true;
		}
		return false;
	}
	
	// Find agentType based on an input string.
	private SolverType getAgentType(String string) {
		switch(string) {
		case "+": 
			return SolverType.ADDER;
		case "-": 
			return SolverType.SUBTRACTER;
		case "*": 
			return SolverType.MULTIPLIER;
		case "/": 
			return SolverType.DIVIDER;
		default: 
			return SolverType.NONE;
		}
	}
	
	// Checks if there are any operators left in the problem
	private boolean isProblemSolved() {
		for (int i = 0; i < splittedProblem.length; i++) {
			if (isOperator(splittedProblem[i])) {
				return false;
			}
		}
		return true;
	}
	
	// Jumps over indexes with null value to find the right arguments in the arithmetic.
	private int findValidIndex(int index) {
		while (splittedProblem[index] == null) {
			index--;
			if (index < 0) {
				System.out.println("ERROR. Indexen er: " + index);
			}
		}
		return index;
	}
	
	
	/**
	 * Returns a list of indices of the operators in subproblems of postfix type. Eg: 5 6 +
	 * The index is only added to the list if it is an operator and follows two numbers. 
	 * @param splittedProblem
	 * @return
	 * */
	private ArrayList<Integer> findSubproblemIndeces() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for (int i = 2; i < splittedProblem.length; i++) {
			if ( isSubProblem(i)) {
				// The operator belongs to a subproblem that can be executed immediately.
				indices.add(i);
			} 
		}
		//printProblem();
		return indices;
	}
	
//	private void printProblem() {
//		for (int i = 0; i < splittedProblem.length; i++) {
//			System.out.println(splittedProblem[i]);
//		}
//	}
	
	private boolean isSubProblem(int i) {
		if (isOperator(splittedProblem[i])) {
			if (old_subProblemIndices.indexOf(i) == -1 && isNumber(splittedProblem[findValidIndex(i-1)]) && isNumber(splittedProblem[findValidIndex(i-2)])) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isOperator(String string) {
		if (string != null && (string.equals("+") || string.equals("-") || string.equals("*") || string.equals("/"))) {
			return true;
		}
		return false;
	}
	
	private boolean isNumber(String string) {
	    try { 
	        Integer.parseInt(string); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
		
	private class SearchForAgents extends OneShotBehaviour {
		private int subProblemIndex;			// The index of this subproblem in the list subProblemIndices
		private String subProblem;
		private SolverType agentType;
		private AID[] taskAgents;
		public SearchForAgents(int subProblemIndex, String subProblem, SolverType agentType) {
			super();
			this.subProblemIndex = subProblemIndex;
			this.subProblem = subProblem;
			this.agentType = agentType;
		}

		@Override
		public void action() {
			// Make a DF description with the preferred agentType as service description
			DFAgentDescription DFDescription = new DFAgentDescription();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType(agentType.name());
			DFDescription.addServices(serviceDescription);
			System.out.println("\nStarting search");
			try {
			//Search for all agents with the DF description
			DFAgentDescription[] result = DFService.search(myAgent, DFDescription);
			// Place the agents in the taskAgent array and print the result.
			System.out.println("Found the following agents:");
			taskAgents = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				taskAgents[i] = result[i].getName();
				System.out.println(taskAgents[i].getName());
			}
			if (taskAgents.length <= 0) {
				System.out.println("Not enough agents to solve this task! Terminate TA.");
				doDelete();
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Perform the request and start request behaviour
		System.out.println("Start request");
		myAgent.addBehaviour(new RequestPerformer(subProblemIndex, subProblem, taskAgents));
		}
	}
	
	private class RequestPerformer extends Behaviour {
		private AID bestAgent;				// The agent that will do the job fastest
		private int bestBid;				// The bidTime of this agent.
		private int repliesCnt = 0;			
		private MessageTemplate messageTemplate;
		private int step = 0;
		private int subProblemIndex;
		private String subProblem;
		private String uniqueConversationId;	// On the form: <index> <supProblem>
		private AID[] taskAgents;
		
		public RequestPerformer(int subProbleIndex, String subProblem, AID[] taskAgents) {
			super();
			this.subProblemIndex = subProbleIndex;
			this.subProblem = subProblem;
			this.taskAgents = taskAgents;
			
			// Make the unique conversation ID
			uniqueConversationId = Integer.toString(subProbleIndex) + " " + subProblem; 
		}

		@Override
		public void action() {
			switch (step) {
			case 0:
				// Send CFP (call for proposal) to agents in taskAgents.
				System.out.println("\n--------> Sending cfp");
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				// Set all agents of agentType as receivers
				for (int i = 0; i < taskAgents.length; ++i) {
					cfp.addReceiver(taskAgents[i]);
				} 
				cfp.setContent(subProblem);
				cfp.setConversationId(uniqueConversationId);
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				
				// Prepare the template to get proposals
				messageTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId(uniqueConversationId),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;

			case 1:
				// Receive all proposals/refusals from solver agents
				ACLMessage reply = myAgent.receive(messageTemplate);
				if (reply != null) {
					// Reply received
					System.out.println("\n<-------- Proposal received");
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is a bid
						int bid = Integer.parseInt(reply.getContent());
						System.out.println("New bid: " + bid + " from " + reply.getSender().getLocalName());
						if (bestAgent == null || bid > bestBid) {
							// This is the best bid at present
							bestBid = bid;
							bestAgent = reply.getSender();
						}
					}
					repliesCnt++;
					if (repliesCnt >= taskAgents.length) {
						// We received all replies
						step = 2; 
					}
				} 
				else {
					block();
					System.out.println("Blocking");
				}
				break;
				
			case 2:
				// Send the purchase order to the seller that provided the best offer
				System.out.println("--------> Send problem");
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestAgent);
				order.setContent(subProblem);
				order.setConversationId(uniqueConversationId);
				order.setReplyWith("solve"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				messageTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId(uniqueConversationId),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case 3:
				// Receive the purchase order reply
				//System.out.println("Getting solution");
				reply = myAgent.receive(messageTemplate);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Solution recieved sucessfully, update the problem.
						updateProblem(subProblemIndex, subProblem, reply.getContent());
						System.out.println(subProblem +" successfully solved by agent " + reply.getSender().getLocalName());
						System.out.println("Solution: " + reply.getContent() + "\n");
						return;
					}
					else {
						System.out.println("Attempt failed.");
					}
					System.out.println("END PREFORMER");
					step = 4;
				}
				else {
					block();
				}
				break;
			}	
		}

		@Override
		public boolean done() {
			if (step == 2 && bestAgent == null) {
				System.out.println("Attempt failed: "+subProblem+" not solvable");
			}
			return ((step == 2 && bestAgent == null) || step == 4);
		}
	}
	
	public void takeDown() {
		// Terminate TaskManager
	}
	
	// Set arguments of the subProblem to null and the operator to the answer.
	public void updateProblem(int sbIndex, String subProblem, String solution) {
		int element2Index = findValidIndex(sbIndex-1);
		splittedProblem[element2Index] = null;
		int element1Index = findValidIndex(sbIndex-2);
		splittedProblem[element1Index] = null;
		splittedProblem[sbIndex] = solution;
	}
}
