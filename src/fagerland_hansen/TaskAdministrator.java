package fagerland_hansen;

import java.awt.Window.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.ExtendedProperties;


// TODO: Clear taskAgents when subproblem is solved.

public class TaskAdministrator extends Agent{	
	
//	private AID[] taskAgents; 			// List of agents that can do the specified problem
//	private SolverType agentType; 		// What operation to do. Ex: Adder, Subtracter, Divider, Multiplier	
	private String problem = ""; 		// Complete problem
	private String [] splittedProblem;	// Problem splitted as a list of string
//	private ArrayList<Integer> ongoingSubProblemIndices = new ArrayList<Integer>();
	//private String subProblem = ""; 	// Subproblem: Two numbers with a space between
	private ArrayList<Integer> subProblemIndices = new ArrayList<Integer>();
//	private List<Problem> problems = new ArrayList<Problem>();
	private ArrayList<Integer> old_subProblemIndices = new ArrayList<Integer>();

		
	public void setup() {
		System.out.println("TaskAdmin: "+getAID().getName()+" is ready.");
		
		// Get problem
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			problem = (String) args[0];
			splittedProblem = problem.split(" ");
			System.out.println("Problem to solve: " + problem);
			
			System.out.println("START print");
			printProblem(splittedProblem);
			
			addBehaviour(new startBehaviour());
			//int subProblemIndex = 0;
			//subProblem = problem;   // TODO: remove
			//Decompose problem
		
			/*
			 * For each sub problem. Search for agents and start auction.
			 * Then solve each sub problem 
			 */
		//	agentType = SolverType.ADDER; // TODO: remove
			
			// TODO: fjerne kode inn i funksjoner.
			//while (isProblemUnsolved()) {
			//for (int yy = 0; yy < 2; yy++) {
				//waitForSubProblems();
			
				/*
				subProblemIndex = findNextSubProblem(splittedProblem, subProblemIndex+1);
				ongoingSubProblemIndices.add(subProblemIndex);
				SolverType agentType;
				String subProblem;
				
				if (subProblemIndex < 0) {
					// Problem is solved!
					System.out.println("******************** Problem is Solved! ****************** ");
					doDelete();
				}
				
				switch(splittedProblem[subProblemIndex].charAt(0)) {
				case '+': 
					agentType = SolverType.ADDER;
					break;
				case '-': 
					agentType = SolverType.SUBTRACTER;
					break;
				case '*': 
					agentType = SolverType.MULTIPLIER;
					break;
				case '/': 
					agentType = SolverType.DIVIDER;
					break;
				default: {
					System.out.println("Something wrong! Is this not an operator?");
					agentType = SolverType.NONE;
					doDelete();
					}
				}
				
				subProblem = splittedProblem[subProblemIndex-2] + " "
						+ splittedProblem[subProblemIndex-1];
				System.out.println(subProblem);
				*/
			//	this.addBehaviour(new SearchForAgents(this, 10000, subProblemIndex, subProblem, agentType));
			//} 
			
				
			//ArrayList<Integer> subProblemIndices; 
			
			
			
			/*
			
			subProblemIndices = findSubproblemIndeces(); 
			System.out.println("START! " + subProblemIndices.size()); */
			/*if (subProblemIndices.size() <= 0) {
				//Problem is  solved
				System.out.println("******************** Problem is Solved! ****************** ");
				doDelete();
			}*/
			
			/*
			if (subProblemIndices.size() > 0) {
				SolverType agentType;
				// There is at least one possible subproblem
				for (int i = 0; i < subProblemIndices.size(); i++ ) {
					switch(splittedProblem[subProblemIndices.get(i)].charAt(0)) {
					case '+': 
						agentType = SolverType.ADDER;
						break;
					case '-': 
						agentType = SolverType.SUBTRACTER;
						break;
					case '*': 
						agentType = SolverType.MULTIPLIER;
						break;
					case '/': 
						agentType = SolverType.DIVIDER;
						break;
					default: System.out.println("Something wrong! ");
						agentType = SolverType.NONE;
					}
					String subProblem = splittedProblem[subProblemIndices.get(i)-2] + " "
							+ splittedProblem[subProblemIndices.get(i)-1];
					System.out.println(subProblem);
					
					System.out.println("BEFORE BEHAVIOUR: " + subProblemIndices.size());
					System.out.println("BEFORE BEHAVIOUR print");
					printProblem(splittedProblem);
					this.addBehaviour(new SearchForAgents(this, 10000, subProblemIndices.get(i), subProblem, agentType));
					System.out.println("AFTER BEHAVIOR: " + subProblemIndices.size());
					System.out.println("AFTER BEHAVIOR print");
					printProblem(splittedProblem);
				}
				System.out.println("BEFORE WAIT: " + subProblemIndices.size());
				System.out.println("BEFORE WAIT print");
				printProblem(splittedProblem); */
				//synchronized (splittedProblem) {
					
				//}
				//waitForSubProblems(); 
				
				/*for (int i = 0; i < subProblemIndices.size(); i++) {
					subProblemIndices.remove(i);
				} */
			//}
			//}
			
			
			
			
		} else {
			System.out.println("Could not solve this");
			doDelete();
		}
	}
	
	private class startBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			//addProblems();
			subProblemIndices.addAll(findSubproblemIndeces());
			System.out.println("START! " + subProblemIndices.size());
			/*if (subProblemIndices.size() <= 0) {
				//Problem is  solved
				System.out.println("******************** Problem is Solved! ****************** ");
				doDelete();
			}*/
			
			if (subProblemIndices.size() > 0) {
				SolverType agentType;
				// There is at least one possible subproblem
				for (int i = 0; i < subProblemIndices.size(); i++ ) {
					if (subProblemIndices.get(i) != null) {
					switch(splittedProblem[subProblemIndices.get(i)].charAt(0)) {
					case '+': 
						agentType = SolverType.ADDER;
						break;
					case '-': 
						agentType = SolverType.SUBTRACTER;
						break;
					case '*': 
						agentType = SolverType.MULTIPLIER;
						break;
					case '/': 
						agentType = SolverType.DIVIDER;
						break;
					default: System.out.println("Something wrong! ");
						agentType = SolverType.NONE;
					}
					String subProblem = splittedProblem[subProblemIndices.get(i)-2] + " "
							+ splittedProblem[subProblemIndices.get(i)-1];
					System.out.println(subProblem);
					
					System.out.println("BEFORE BEHAVIOUR: " + subProblemIndices.size());
					System.out.println("BEFORE BEHAVIOUR print");
					printProblem(splittedProblem);
					System.out.println("her: " + subProblemIndices);
					myAgent.addBehaviour(new SearchForAgents(myAgent, 10000, subProblemIndices.get(i), subProblem, agentType));
					System.out.println("her: " + subProblemIndices);
					old_subProblemIndices.add(subProblemIndices.get(i));
					subProblemIndices.set(i, null);
					System.out.println("AFTER BEHAVIOR: " + subProblemIndices.size());
					System.out.println("AFTER BEHAVIOR print");
					printProblem(splittedProblem);
					}
				}
				System.out.println("BEFORE WAIT: " + subProblemIndices.size());
				System.out.println("BEFORE WAIT print");
				printProblem(splittedProblem);
				//waitForSubProblems();
			}
			
		}
		
	} 
	
	private boolean isProblemUnsolved() {
		for (int i = 0; i < splittedProblem.length; i++) {
			if (isOperator(splittedProblem[i])) {
				return true;
			}
		}
		return false;
	}
	
	private void waitForSubProblems() {
		System.out.println("does this work?" + subProblemIndices);
		System.out.println("does this work?" + subProblemIndices.size());
		while (subProblemIndices.size() != 0) {
			System.out.println("I am working on: " + subProblemIndices.size());
			updateSubProblemIndices();
			printProblem(splittedProblem);
		}
	}
	
	private void updateSubProblemIndices() {
		for (int i = 0; i < subProblemIndices.size(); i++) {
			System.out.println(splittedProblem[subProblemIndices.get(i)]);
			if (isNumber(splittedProblem[subProblemIndices.get(i)])) {
				subProblemIndices.remove(i);
			}
		}
	}
	
	private void printProblem() {
		for (int i = 0; i < splittedProblem.length; i++) {
			System.out.println(splittedProblem[i]);
		}
	} 
	
	private void printProblem(String [] splittedProblem) {
		for (int i = 0; i < splittedProblem.length; i++) {
			System.out.println(splittedProblem[i]);
		}
	}
	/*private int findNextSubProblem(String [] splittedProblem, int start) {
		int index = findNextOperator(splittedProblem, start);
		System.out.println("Indexen er: " + index);
		if (index < 0) {
			// wait to check if there are any operators left after this round of calculations
			System.out.println("wait for calculations");
			waitForCalculations();
			if (isProblemUnsolved()) {
				System.out.println("alle calcalations are finnished. and there excists a operator");
				// The operator must appear before start
				index = findNextOperator(splittedProblem, 0);
			}
		}
		return index;
	} */
	
	
	
	private int findNextOperator(int start){
		int elem1;
		int elem2;
		for (int i = start; i < splittedProblem.length; i++) {
			if (isOperator(splittedProblem[i])) {
				elem1 = findValidIndex(i-2);
				elem2 = findValidIndex(i-1);
				if (isNumber(splittedProblem[elem1]) && isNumber(splittedProblem[elem2])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private int findValidIndex(int index) {
		while (splittedProblem[index].equals(null)) {
			index--;
			if (index < 0) {
				System.out.println("ERROR. Indexen er: " + index);
			}
		}
		return index;
	}
	
	/*private void waitForCalculations() {
		while (ongoingSubProblemIndices.size() != 0) {
			System.out.println("I am working on: " + ongoingSubProblemIndices.size());
			updateOngoing();
		}
	}
	
	private void updateOngoing() {
		for (int i = 0; i < ongoingSubProblemIndices.size(); i++) {
			if (isNumber(splittedProblem[ongoingSubProblemIndices.get(i)])) {
				ongoingSubProblemIndices.remove(i);
			}
		}
	}
	*/
	
	/**
	 * Returns a list of indices of the operators in subproblems of postfix type. Eg: 5 6 +
	 * The index is only added to the list if it is an operator and follows two numbers. 
	 * @param splittedProblem
	 * @return
	 * */
	 
	private ArrayList<Integer> findSubproblemIndeces() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for (int i = 2; i < splittedProblem.length; i++) {
			if (	isOperator(splittedProblem[i]) 
					&& subProblemIndices.indexOf(i) == -1 
					&& old_subProblemIndices.indexOf(i) == -1
					&& isNumber(splittedProblem[i-1]) 
					&& isNumber(splittedProblem[i-2])) {
				// The operator belongs to a subproblem that can be executed immediately.
				indices.add(i);
			} 
		}
		return indices;
	}
	
	
	private boolean isOperator(String string) {
		if (string != null && (string.equals("+") || string.equals("-") || string.equals("*") || string.equals("/"))) {
			return true;
		}
		return false;
	}
	
	

	private boolean isNumber(String string) {
		int asciiValue = (int) string.charAt(0);
		// ASCII value of numbers is btw and including: 48-57
		if (asciiValue >=48 && asciiValue <= 57) {
			return true;
		}
		return false;
	}
	
	// Assumes that if the string start with a number character, then the whole
	/*// string are numbers. E.g 593.
	private boolean isNumber(String[] string, int index) {
		// Get the ASCII number of the character in the string
		while (string[index] == null ) {
			index -= 1;
			if (index < 0) {
				System.out.println("Problemet er på feil format. En operator er første element. Terminate!");
				doDelete();
			}
		}
		int asciiValue = (int) string[index].charAt(0);
		
		// ASCII value of numbers is btw and including: 48-57
		if (asciiValue >=48 && asciiValue <= 57) {
			return true;
		}
		return false;
	}*/
	
	
	private class SearchForAgents extends OneShotBehaviour {
		private int subProblemIndex;			// The index of this subproblem in the list subProblemIndices
		private String subProblem;
		private SolverType agentType;
		private AID[] taskAgents;
		//private ArrayList<Integer> subProblemIndices; 
		public SearchForAgents(Agent agent, int time, int subProblemIndex, String subProblem, SolverType agentType) {
			super();
			//super(agent, time);
			this.subProblemIndex = subProblemIndex;
			System.out.println("SUBPROBLEMINDEX: " + subProblemIndex);
			System.out.println(" The subproblem: " + subProblem);
			System.out.println("agent Type: " + agentType.name());
			this.subProblem = subProblem;
			this.agentType = agentType;
			System.out.println("SEARCHFORAGENTS print");
			printProblem(splittedProblem);
			//this.subProblemIndices = subProblemIndices;
			//System.out.println("BEGINNING SEARCHFORAGENT: " + subProblemIndices.size());
		}

		@Override
		public void action() {
			System.out.println("Searching for agent type: " + agentType);
		// Make a DF description with the preferred agentType as service description
		DFAgentDescription DFDescription = new DFAgentDescription();
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType(agentType.name());
		DFDescription.addServices(serviceDescription);
		System.out.println("Starting search");
		try {
			// Search for all agents with the DF description
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
		//System.out.println("BEFORE PROPORSAL: " + subProblemIndices.size());
		// Perform the request
		System.out.println("Start request");
		System.out.println("BEFORE PREFORMER print");
		printProblem(splittedProblem);
		myAgent.addBehaviour(new RequestPerformer(subProblemIndex, subProblem, taskAgents));
		return;
			
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
		//private ArrayList<Integer> subProblemIndices; 
		
		public RequestPerformer(int subProbleIndex, String subProblem, AID[] taskAgents) {
			super();
			System.out.println("*** PERFORMER ***");
			this.subProblemIndex = subProbleIndex;
			this.subProblem = subProblem;
			this.taskAgents = taskAgents;
			//this.subProblemIndices = subProblemIndices;
			
			// Make the unique conversation ID
			uniqueConversationId = Integer.toString(subProbleIndex) + " " + subProblem; 
		}

		@Override
		public void action() {
			switch (step) {
			case 0:
				// Send CFP (call for proposal) to agents in taskAgents.
				System.out.println("Sending cfp");
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
				// Receive all proposals/refusals from seller agents
				System.out.println("Receive proposal");
				ACLMessage reply = myAgent.receive(messageTemplate);
				if (reply != null) {
					// Reply received
					System.out.println("Reply received");
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int bid = Integer.parseInt(reply.getContent());
						System.out.println("New bid: " + bid + " from " + reply.getSender().getLocalName());
						if (bestAgent == null || bid > bestBid) {
							// This is the best offer at present
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
				System.out.println("Send solve");
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
				System.out.println("Getting solution");
				reply = myAgent.receive(messageTemplate);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println("BEFORE UPDATE ***");
						//System.out.println("index er: " + index);
						//System.out.println("subProblemSize: " + subProblemIndices.size());
						updateProblem(subProblemIndex, subProblem, reply.getContent());
						System.out.println(subProblem +" successfully solved from agent " + reply.getSender().getName());
						System.out.println("Solution: " + reply.getContent());
						return;
						//myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: requested book already sold.");
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
		
		public void updateProblem(int sbIndex, String subProblem, String solution) {
			System.out.println("it is: " + subProblemIndices);
			System.out.println("it is: " + subProblemIndices.get(sbIndex));
			System.out.println("*** UPDATE ***");
			System.out.println("UPDATE print");
			printProblem(splittedProblem);
			int element2Index = sbIndex-1;
			System.out.println(element2Index);
			while (splittedProblem[element2Index] == null) {
				element2Index -= 1;
				if (element2Index < 0) {
					System.out.println("index is smaller that 0 ele2");
					System.out.println("sbindex: " + sbIndex + " element2: " + element2Index);
					doDelete();
				}
			}
			System.out.println("element2Index rett før nulling: " + element2Index + " size er: " + splittedProblem.length);
			splittedProblem[element2Index] = null;
			
			int element1Index = sbIndex-2;
			while (splittedProblem[element1Index] == null) {
				element1Index -= 1;
				if (element1Index < 0) {
					System.out.println("index is smaller that 0 ele1");
					System.out.println("sbindex: " + sbIndex + " element1: " + element1Index);
					doDelete();
				}
			}
			System.out.println("element1Index rett før nulling: " + element1Index + " size er: " + splittedProblem.length);
			splittedProblem[element1Index] = null;
			
			System.out.println("sbIndex rett før nulling: " + sbIndex + " size er: " + splittedProblem.length);
			splittedProblem[sbIndex] = solution;
			
			System.out.println("AFTER UPDATE print");
			printProblem(splittedProblem);
			System.out.println("and");
			printProblem();
			System.out.println("new problem");
			for (int i = 0; i < splittedProblem.length; i++) {
				System.out.println(i + ":   " + splittedProblem[i]);
			}
			
			/*
			// Check if the problem is where it is supposed to be.
			if (!subProblem.split(" ")[0].equals(splittedProblem[sbIndex-2]) || 
					!subProblem.split(" ")[1].equals(splittedProblem[sbIndex-1])) {
				System.out.println("ERROR ERROR AN ERROR HAS OCCRED!! noe er feil med indexene. index er: " + index + "   suubproblem: " + subProblem );
				System.out.println("if " + subProblem.split(" ")[0] + " == " + splittedProblem[sbIndex-2] + "\n" +
						subProblem.split(" ")[1] + " == " + splittedProblem[sbIndex-1]);
			} else {
				System.out.println("NO ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
			}
			
			// Update problem
			splittedProblem[sbIndex] = solution;
			splittedProblem[sbIndex-1] = null;
			splittedProblem[sbIndex-2] = null;
			System.out.println("new problem: ");
			for (int i = 0; i< splittedProblem.length; i++) {
				System.out.println(splittedProblem[i]);
			} */
			System.out.println("END UPDATE");
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
		System.out.println("Terminating TaskAdministrator");
	}
}
