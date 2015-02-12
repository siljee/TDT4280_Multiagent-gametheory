package Oving2;


import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class GeneralSolver extends Agent {
	
	protected String name;
	protected String type;
	protected Agent myAgent;
	
	public void setup() {
		System.out.println("Agent " + getName() + " started");
		registerToDF();
		addBehaviours();
	}
	
	private void registerToDF() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
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
		
	}
	
	protected abstract Integer solveProblem(String problem);
	
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
