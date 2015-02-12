package Oving2;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AdditionSolver extends Agent{
	
	private Agent myAgent = this; 
	
	
	public void setup() {
		System.out.println("Starting adder agent");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Adder");
		sd.setName("JADE-adder");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		addBehaviour(new BidServer());
		addBehaviour(new InformServer());
		
	}
	
	public void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		System.out.println("Adder agent terminating");
	}
	
	private class BidServer extends CyclicBehaviour {
		
		private Integer bid;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();

				bid = 1;
				if (bid != null) {
					// The requested book is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(bid.intValue()));
				}
				else {
					// Can't bid
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
			
		}
		
	}
	
	private class InformServer extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				System.out.println("");
				String problem = msg.getContent();
				ACLMessage reply = msg.createReply();

				System.out.println("Solving : " + problem);
				Integer solution = (Integer) solveProblem(problem);
				if (solution != null) {
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(String.valueOf(solution.intValue()));
					System.out.println(solution+" solved");
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
			
		}
		
		private Integer solveProblem(String problem) {
			try {
				String[] parts = problem.split("\\+");
				int left = Integer.parseInt(parts[0]);
				int right = Integer.parseInt(parts[1]);
				return ((Integer) left+right);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -999;
		}
		
	}

}
