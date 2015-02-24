package fagerland_hansen;


import java.util.ArrayList;
import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class GeneralSolver extends Agent {
	
	protected String name;				// Name of the agent.
	protected SolverType type;				// Type of agent.
	protected int taskTime;				// Time to execute one task.
	protected ArrayList<String> qeue;	// Queue of problems assigned to the agent to solve
	
	public void setup() {
		System.out.println("Agent " + getName() + " started");
		registerToDF();
		Random random = new Random();
		taskTime = random.nextInt(10);
		addBehaviours();
	}
	
	private void registerToDF() {
		// Register agents ID to a new DF Description object.
		DFAgentDescription dfDescription = new DFAgentDescription();
		dfDescription.setName(getAID());
		
		// Register itself(local name and type) as a service to the DF Description object.  
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType(type.name());
		serviceDescription.setName(name);
		dfDescription.addServices(serviceDescription);
		
		// Register the DF description object at the DF service.
		try {
			DFService.register(this, dfDescription);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	
	private void addBehaviours() {
		addBehaviour(new BidServer());
		addBehaviour(new InformServer());
	}
	
	
	private class BidServer extends CyclicBehaviour {	
		private Integer bid;

		@Override
		public void action() {
			
			// Received messages of type CFP (Call for proposal) are placed in msg.
			MessageTemplate messageTemplateCFP = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(messageTemplateCFP);
			
			
			if (msg != null) {
				// CFP Message received. Get the content and create a reply
				String task = msg.getContent();
				ACLMessage reply = msg.createReply();
				
				// Calculate bid.
				int queueTime = 1;   // TODO: Time from adding times in array of queued tasks.
				bid = taskTime + queueTime;
				
				if (bid != null) {
					// Send a propose message with the bid back. 
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(bid.intValue()));
				}
				else {
					// Send a refuse if no bid.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				// If no CFP message is received, the behaviour is blocked. 
				block();
			}
		}
	}
	
	private class InformServer extends CyclicBehaviour {

		@Override
		public void action() {
			// Received messages of type ACCEPT_PROPOSAL are placed in msg.
			MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(messageTemplate);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Make a reply.
				String problem = msg.getContent();
				ACLMessage reply = msg.createReply();

				// Send the solution as an INFORM message
				System.out.println("Solving : " + problem);
				Integer solution = (Integer) solveProblem(problem);
				if (solution != null) {
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(String.valueOf(solution.intValue()));
					System.out.println(solution+" solved");
				}
				else {
					// The solution is not found. Send a FAILURE message
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				// If no CFP message is received, the behaviour is blocked.
				block();
			}
			
		}
		
	}
	
	protected abstract Integer solveProblem(String problem);
	
	// Split the problem in two strings with the two terms.
	protected String[] numberSplitter(String problem) {
		String[] parts = problem.split("\\s+");
		return parts;
	}
	
	
	public void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		System.out.println(type+" agent terminating");
	}

}
