package Oving2;

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

public class TaskAdmin extends Agent {
	
	private String problem = "";
	private ArrayList<String> splittedProblem;
	private String subProblemToSolve = "";
	private String currentMathOperator = "";
	private AID[] taskAgents;
	private String agentType = "";
	private int index=0; // index of where the solution is going to be.
	private Agent myAgent = this;
	
	public void setup() {
		System.out.println("Starting TaskAdmin");
		splittedProblem = new ArrayList<String>();
		
		// To get enough time to setup the sniffer agent
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Get problem
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			problem = (String) args[0];
			String[] splitProb = problem.split(" ");
			for (String str: splitProb) {
				splittedProblem.add(str);
				System.out.println(str);
			}
			
			System.out.println();
			addBehaviour(new ProblemHandler());
			
		} else {
			System.out.println("Could not get input");
			doDelete();
		}
		
		
		
	}
	

	
	private class ProblemHandler extends CyclicBehaviour {
		
		@Override
		public void action() {
			
			if (!splittedProblem.get(index).equals("S") && splittedProblem.size()>1) {
				taskAgents = null;
			
				System.out.println("ProblemHandler cycle");
				System.out.println(splittedProblem);
				subProblemToSolve = getSubProblem();
				if (subProblemToSolve==null) {
					System.out.println("subproblem error");
					doDelete();
				}
				agentType = getAgentType();
				if (agentType==null) {
					System.out.println("agenttype error");
					doDelete();
				}
				
				// Searching
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
				System.out.println("Starting requests");
				myAgent.addBehaviour(new RequestHandler2());
				System.out.println("behaviour added");
			}
		
			
		}
		
		private String getAgentType() {
			if (currentMathOperator.equals("+")) {
				return "Adder";
			} else if (currentMathOperator.equals("-")) {
				return "Subtracter";
			} else if (currentMathOperator.equals("/")) {
				return "Divider";
			} else if (currentMathOperator.equals("*")) {
				return "Multiplier";
			}
			return null;
		}
		
		private String getSubProblem() {
			String subProblem = "";
			for (int i = 0; i < splittedProblem.size(); i++) {
				System.out.println(isMathOperator(splittedProblem.get(i)));
				if (isMathOperator(splittedProblem.get(i))) {
					subProblem += subProblem + splittedProblem.get(i-2) + " " + splittedProblem.get(i-1);
					currentMathOperator = splittedProblem.get(i);
					splittedProblem.set(i, "S");
					splittedProblem.remove(i-1);
					splittedProblem.remove(i-2);
					// Setting index to where the solution is going to be
					index = i - 2;
					System.out.println("Suproblem to solve " + subProblem);
					return subProblem;
				}
			}
			return null;
		}
		
		private boolean isMathOperator(String element) {
			if (element.equals("+") || element.equals("-") || element.equals("/") || element.equals("*")) {
				return true;
			}
			return false;
		}
		
	}
	
	private class RequestHandler2 extends Behaviour {
		private AID bestAgent;
		private int bestBid;
		private int repliesCnt = 0;
		private MessageTemplate mt;
		private int step = 0;
	

		@Override
		public void action() {
			System.out.println("Requsting...");
			switch (step) {
			case 0:
				// Send cfp to agents.
				System.out.println("Sending cfp");
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < taskAgents.length; ++i) {
					cfp.addReceiver(taskAgents[i]);
				} 
				cfp.setContent(subProblemToSolve);
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
				order.setContent(subProblemToSolve);
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
						System.out.println(subProblemToSolve +" successfully solved from agent "+reply.getSender().getName());
						System.out.println("Solution: " + reply.getContent());
						for (int i = 0; i < splittedProblem.size(); i++) {
							if (splittedProblem.get(i).equals("S")) {
								splittedProblem.set(i, reply.getContent().toString());
								break;
							}
							if(splittedProblem.size()==1) {
								System.out.println("Final solution: " + splittedProblem.get(0));
								doDelete();
							}
						}
//						myAgent.doDelete();
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
				System.out.println("Attempt failed: "+subProblemToSolve+" not solvable");
			}
			return ((step == 2 && bestAgent == null) || step == 4);
		}
	}
	
	

}
