package Oving2;

import java.util.ArrayList;

import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TaskAdministrator extends Agent{	
	
	private AID[] taskAgents;
	private String agentType = ""; // What operation to do. Ex: Adder, Subtracter, Divider, Multiplier
	private Agent myAgent = this;
	private String problem = ""; // Complete number
	private String subProblem = ""; // Two numbers with a space between
	
	public void setup() {
		System.out.println("TaskAdmin: "+getAID().getName()+" is ready.");
		
		// Get problem
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			problem = (String) args[0];
			System.out.println("Problem to solve: " + problem);
			subProblem = problem;
			//Decompose problem
		
			/*
			 * For each sub problem. Search for agents and start auction.
			 * Then solve each sub problem 
			 */
			
			
			
			
			
			addBehaviour(new TickerBehaviour(this, 10000) {
				
				@Override
				protected void onTick() {
					
					System.out.println("Searching for agent type: " + agentType);
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType(agentType);
					template.addServices(sd);
					System.out.println("Starting search");
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template); 
						System.out.println("Found the following agents:");
						taskAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							taskAgents[i] = result[i].getName();
							System.out.println(taskAgents[i].getName());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}
					
					// Perform the request
					System.out.println("Start request");
					myAgent.addBehaviour(new RequestPerformer());
					
				}
			});
			
			
		} else {
			System.out.println("Could not solve this");
			doDelete();
		}
		
		
	}
	
	public void takeDown() {
		System.out.println("Terminating TaskAdministrator");
	}
	
	private class RequestPerformer extends Behaviour {
		private AID bestAgent;
		private int bestBid;
		private int repliesCnt = 0;
		private MessageTemplate mt;
		private int step = 0;
	

		@Override
		public void action() {
			switch (step) {
			case 0:
				// Send cfp to agents.
				System.out.println("Sending cfp");
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < taskAgents.length; ++i) {
					cfp.addReceiver(taskAgents[i]);
				} 
				cfp.setContent(subProblem);
				cfp.setConversationId("problem-solver");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("problem-solver"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;

			case 1:
				// Receive all proposals/refusals from seller agents
				System.out.println("Receive proposal");
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					System.out.println("Reply received");
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int bid = Integer.parseInt(reply.getContent());
						System.out.println("New bid: " + bid);
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
				order.setConversationId("problem-solver");
				order.setReplyWith("solve"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("problem-solver"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case 3:
				// Receive the purchase order reply
				System.out.println("Getting solution");
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(subProblem +" successfully solved from agent "+reply.getSender().getName());
						System.out.println("Solution: " + reply.getContent());
						
						myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: requested book already sold.");
					}

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
	
	
	
	

}
