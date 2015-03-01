package oving3;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class GeneralAgent extends Agent implements Constants {
	private GlobalItems globalItems;
	private ArrayList<Item> inventoryList;
	private ArrayList<Item> desiredList;
	private int money;
	private String agentType = AGENT_TYPE;
	private AID [] agents;
	private int numberOfPropReject;
	private String prevDesItem;
	
	public void setup() {
		// Sleep for some time. This is to setup sniffer.
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		numberOfPropReject = 0;
		// The agent have started and will register to DF.
		System.out.println("Agent " + getLocalName() + " started");
		registerToDF();
		
		// Initialise member variables:
		globalItems = GlobalItems.getInstance();
		inventoryList = globalItems.makeInventoryList();
		money = START_MONEY;
		
		System.out.println(NUMBER_OF_AGENTS + " agents are needed to run the negotiations.");
		
		// Add behaviours
		addBehaviour(new DesiredItemBehaviour());
		addBehaviour(new sellerBehaviour());
	}
	
	/**
	 * Register agent at the DF. All agents in this project are and do the same and therefore 
	 * all agents have the same type defined in the constant AGENT_TYPE.
	 * Agents discover what other agents offer by communicating with them, so this should not be specified.
	 */
	private void registerToDF() {
		// Set Agents ID to a new DF Description object.
		DFAgentDescription dfDescription = new DFAgentDescription();
		dfDescription.setName(getAID());
		
		// Set the agents service, with type as AGENT_TYPE, and add it to dfDescription
		ServiceDescription serviceDescription  = new ServiceDescription();
		serviceDescription.setName(getLocalName());
		serviceDescription.setType(agentType);
		dfDescription.addServices(serviceDescription);
		
		// Register the dfDescription at the DF.
		try {
			DFService.register(this, dfDescription);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	/**
	 * Waits until all agents have made their inventory before it makes a list of desired items.
	 * This is to make sure the desired items exists in the environment.
	 * Then it adds a new BuyBehaviour for each item in the desired item list. 
	 */
	private class DesiredItemBehaviour extends Behaviour {
		boolean isDone = false;
	
		@Override
		public void action() {
			// When all agents have made their inventory, the desired list is made
			if (globalItems.getInventoryAgentsCount() >= NUMBER_OF_AGENTS) {
				desiredList = globalItems.makeDesiredList();
				
				// If one agent is a winner the actions stops.
				if (isWinner()) {
					System.out.println(myAgent.getLocalName() + " holds all the items! \n");
					isDone = true;
					// TODO: Terminate myAgent , terminate all agents
					
				} else {
				// For each desired item, make a new BuyerBehaviour, if the desired item is not in the agent inventory.
				for (int i = 0; i < desiredList.size(); i++) {
					if (! inventoryList.contains(desiredList.get(i))) {
						System.out.println("\n" + myAgent.getLocalName() + " is searching for " + desiredList.get(i).getName());
						addBehaviour(new BuyBehaviour(desiredList.get(i)));
					} else {
						System.out.println("\n" + myAgent.getLocalName() + " already own " + desiredList.get(i).getName());
					}
				}
				}
				isDone = true;
				
			} else {
				// Sleep for a short time to avoid running many times in vain.
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public boolean done() {
			return isDone;
		}
	}

	private class BuyBehaviour extends Behaviour {
		private int step = 0;
		private MessageTemplate messageTemplate;
		private Item desiredItem;
		
		int replyCounter = 0;
		int lowestPrice = Integer.MAX_VALUE;
		ACLMessage chosenConversation = null;
		
		public BuyBehaviour(Item desiredItem) {
			super();
			this.desiredItem = desiredItem;
		}
		
		@Override
		public void action() {
			
			MessageTemplate messageCancel = MessageTemplate.MatchPerformative(ACLMessage.CANCEL);
			ACLMessage receivedCancel = myAgent.receive(messageCancel);
			if (receivedCancel != null) {
				System.out.println(getLocalName() + ": Shutting down");
				doDelete();
			} else {
			
			switch (step) {
			
			/* Runs once. Finds all negotiation agents */ 
			case 0: 
				// Make a DF description with AGENT_TYPE as service description
				DFAgentDescription DFDescription = new DFAgentDescription();
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType(AGENT_TYPE);
				DFDescription.addServices(serviceDescription);
				
				try {
					// Find all agents with the DF description
					DFAgentDescription[] result = DFService.search(myAgent, DFDescription);
					
					// Place the agents in the agents array.
					agents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						agents[i] = result[i].getName();
					}
					
					// if there are no other agents: terminate. This should never happen.
					if (agents.length <= 0) {
						System.out.println("There are no other agents. Terminate!");
						doDelete();
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				step = 1;
				break;
				
			/* Runs once. Send CFP to all agents, asking for price of the desired item. */
			case 1:			
				// Create CFP (call for proposal) message.
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				
				// Set all negotiation agents as receivers except self.
				for (int i = 0; i < agents.length; ++i) {
					if (!myAgent.getAID().equals(agents[i])) {
						cfp.addReceiver(agents[i]);
					}
				} 
				
				// set content and conversationID to be the desired items name and send the CFP message.
				cfp.setContent(desiredItem.getName());
				cfp.setConversationId(desiredItem.getName());
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				
				// Prepare the template to get proposals. Must match conversationID and Replywith
				messageTemplate = MessageTemplate.and(	MessageTemplate.MatchConversationId(desiredItem.getName()),
														MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 2;
				break;
				
			/* Runs until all agents have answered. Receive all proposal/refusal from agents. Choose one */
			case 2:	
				
				// receiver reply with the same template as cfp
				ACLMessage receivedReply = myAgent.receive(messageTemplate);
				if (receivedReply != null) {
					// Reply received
					if (receivedReply.getPerformative() == ACLMessage.INFORM) {
						System.out.println("\n" + myAgent.getLocalName() + "<-------- Informed received (step2 buy) about " + desiredItem.getName() + " from " + receivedReply.getSender().getLocalName());
						// The other agent have the desired item.
						int receivedPrice = Integer.parseInt(receivedReply.getContent());
						System.out.println("New price: " + receivedPrice + " from " + receivedReply.getSender().getLocalName());
						
						// Choose the conversation with the lowest price
						if (receivedPrice < lowestPrice) {
							lowestPrice = receivedPrice;
							chosenConversation = receivedReply;
						}
					} else {
						System.out.println("\n" + myAgent.getLocalName() + "<-------- Refuse received (step2 buy) about " + desiredItem.getName() + " from " + receivedReply.getSender().getLocalName());
					}
					replyCounter++;
					
					// When all agents have answered the agent makes a new proposal and send it to the chosen conversation partner
					if (replyCounter >= agents.length - 1) {
						System.out.println(myAgent.getLocalName() + " have recieved informed/refuse for all agents about the item: " + desiredItem.getName());
						
						// TODO:!!! Make a proposal
						double desiredListLength = 1;
						if (desiredList.size()>0) {
							desiredListLength = desiredList.size();
						}
						int price = (int) (0.5*(((double)globalItems.getValueOfItem(desiredItem.getName())/((double)Constants.MAX_VALUE))*((double)money)/((double)desiredListLength)));	
						
						// send proposal to the chosen conversation.
						if (chosenConversation == null) {
							System.out.println("No agents hold the item: " + desiredItem.getName());
							step = 3;
							break;
						}
						
						// Prepare the reply
						ACLMessage reply = chosenConversation.createReply();
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(String.valueOf(price));
						
						// Send the reply
						System.out.println(myAgent.getLocalName() + " sent (from step2 buy) a propose to " + chosenConversation.getSender().getLocalName());
						myAgent.send(reply);
						
						// Give the conversation to a new negotiation behavior.
						addBehaviour(new NegotiationBehaviour(reply, true, desiredItem.getName()));

						// End this behhaviour
						step = 3;
						break;
					}
				} else {
					// If no message is received the behaviour is blocked.
					block();
				}
				break;
			}
			}
		}
	
		@Override
		public boolean done() {
			// Stop behaviour if no agent hold the desired item or if all steps are executed
			return (step == 3);
		}
		
	}

	/**
	 * Always listens for CFP messages from other agents trying to buy stuff. When receiving such 
	 * a message it will check if it has the item. If it has the item it will start a new
	 * negotiation behaviour. If it does not have the item it sends a refuse message back. 
	 * 
	 * and use it's negotiation powers to find a price. 
	 * It will then get either a new proposal, accept_proposal or refuse-proposal from the other agent.
	 * When receiving a proposal it can send a new proposal, accept_proposal or refuse-proposal aswell.
	 */
	private class sellerBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			// Make a template for CFP messages to receive. And receive a message. 
			MessageTemplate messageTemplateCFP = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage receivedCFPmessage = myAgent.receive(messageTemplateCFP);
			
			
			// Check to shutdown
			MessageTemplate messageCancel = MessageTemplate.MatchPerformative(ACLMessage.CANCEL);
			ACLMessage receivedCancel = myAgent.receive(messageCancel);
			if (receivedCancel != null) {
				System.out.println(getLocalName()+ ": Shutting down");
				doDelete();
			} else {
			
			if (receivedCFPmessage != null) {
				
				
				
				
				// CFP Message received. Get the item to consider and prepare a reply
				String item = receivedCFPmessage.getContent();
				ACLMessage reply = receivedCFPmessage.createReply();
			
				if (isInInventory(item)) {
					// TODO: Make a price!
					double desiredListLength = 1;
					if (desiredList.size()>0) {
						desiredListLength = desiredList.size();
					}
					int price = (int) (1.5*(((double)globalItems.getValueOfItem(item)/((double)Constants.MAX_VALUE))*((double)money)/((double)desiredListLength)));			//Dummy!
					
					// Send a propose message with the bid back. 
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(String.valueOf(price));
					
					// Make a negotiation behaviour that will take care of the negotiation
					// receiving and sending proposes with the right conversatio ID and reply with
					addBehaviour(new NegotiationBehaviour(reply, false, item));
					System.out.println(myAgent.getLocalName() + " sent (from seller) a INFORM about " + item + " to " + receivedCFPmessage.getSender().getLocalName());
				}
				else {
					// Send a refuse if no bid.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
					System.out.println(myAgent.getLocalName() + " sent (from seller) a REFUSE about " + item + " to " + receivedCFPmessage.getSender().getLocalName());

				}
				myAgent.send(reply);
			} else {
				// If no CFP message is received, the behaviour is blocked. 
				block();
			}
			
			
			}
			
		}
		
	}
	
	private class NegotiationBehaviour extends Behaviour {
		String item;
		String itemName;
		int price;
		boolean isBuyer;
		ACLMessage receivedReply;
		MessageTemplate messageTemplate;
		boolean stopNegotiate = false;
		
		public NegotiationBehaviour(ACLMessage receivedMessage, boolean isBuyer,String itemName) {
			item = receivedMessage.getContent();
			this.itemName = itemName;
			this.isBuyer = isBuyer;
			messageTemplate = MessageTemplate.and(	MessageTemplate.MatchConversationId(receivedMessage.getConversationId()),
													MessageTemplate.MatchInReplyTo(receivedMessage.getReplyWith()));
			
			// TODO: This might be before addBehaviour, depending on whether price is obvious 
			// or if a special algorithm is needed.
			if (isBuyer) {
				double desiredListLength = 1;
				if (desiredList.size()>0) {
					desiredListLength = desiredList.size();
				}
				price = (int) (0.5*(((double)globalItems.getValueOfItem(itemName)/((double)Constants.MAX_VALUE))*((double)money)/((double)desiredListLength)));	
			} else {
				double desiredListLength = 1;
				if (desiredList.size()>0) {
					desiredListLength = desiredList.size();
				}
				price = (int) (1.5*(((double)globalItems.getValueOfItem(itemName)/((double)Constants.MAX_VALUE))*((double)money)/((double)desiredListLength)));	
			}
		}
		
		@Override
		public void action() {
			// Check to shutdown
			MessageTemplate messageCancel = MessageTemplate.MatchPerformative(ACLMessage.CANCEL);
			ACLMessage receivedCancel = myAgent.receive(messageCancel);
			if (receivedCancel != null) {
				System.out.println(getLocalName() + ": Shutting down");
				doDelete();
			} else {
				
			
			
			receivedReply = myAgent.receive(messageTemplate);
			if (receivedReply != null) {
				if (isBuyer) {
					System.out.println("This is the old step 3!!");
					
				}
				
				if (receivedReply.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
					System.out.println("Proposal rejected");
					stopNegotiate = true;
					if (isBuyer && numberOfPropReject < 5) {
						Item desiredItem = new Item(itemName, globalItems.getValueOfItem(itemName));
						myAgent.addBehaviour(new BuyBehaviour(desiredItem));
						numberOfPropReject++;
					}
				}

				
				if (receivedReply.getPerformative() == ACLMessage.PROPOSE) {
					System.out.println("NEGOTIATION!");
					// Reply received
					System.out.println("\n" + myAgent.getLocalName() + "<-------- Proposal received in negotiation from " + receivedReply.getSender().getLocalName());
					
					// Agent received a new proposal in chosenConversaton
					int receivedPrice = Integer.parseInt(receivedReply.getContent());
					System.out.println("New bid: " + receivedPrice + " from " + receivedReply.getSender().getLocalName());
					
					// Evaluate receivedPrice
					
					double desiredListLength = 1;
					if (desiredList.size() != 0) {
						desiredListLength = desiredList.size();
					}
					
					double utilityOffered = 0;
					
					if (isBuyer) {
						 utilityOffered = (double)globalItems.getValueOfItem(itemName) - (double)receivedPrice + ((double)money)/desiredListLength;
						 if (money<receivedPrice) {
							 // Cannot afford the offered price.
							 System.out.println("Cannot afford item");
							 utilityOffered = -1;
						 }
					} else {
						utilityOffered = (double)receivedPrice - (double)globalItems.getValueOfItem(itemName)  + ((double)money)/desiredListLength;
					}
					
					
					if (utilityOffered>5900 || receivedPrice==price) {
						// Accept offered price
						stopNegotiate = true;
						System.out.println("Accepting offer");
						System.out.println("Stopping negotiation");
						if (receivedPrice!=price) {
							// send proposal.
							System.out.println("Sending back same price");
							ACLMessage sendReply = receivedReply.createReply();
							sendReply.setPerformative(ACLMessage.PROPOSE);
							sendReply.setContent(String.valueOf(receivedPrice));
							System.out.println(myAgent.getLocalName()
									+ " sent (from NEGO) proposal to "
									+ receivedReply.getSender().getLocalName());
							myAgent.send(sendReply);
							messageTemplate = MessageTemplate.and(
									MessageTemplate
											.MatchConversationId(sendReply
													.getConversationId()),
									MessageTemplate.MatchInReplyTo(sendReply
											.getReplyWith()));
						} 
						if (isBuyer) {
							// update inventory
							Item itemBought = new Item(item, globalItems.getValueOfItem(itemName));
							inventoryList.add(itemBought);
							for (int i = 0; i < desiredList.size(); i++) {
								if(desiredList.get(i).getName().equals(itemName)) {
									desiredList.remove(i);
									break;
								}
							}
							if (isWinner()) {
								// Check if new winner elsewhere
								receivedCancel = myAgent.receive(messageCancel);
								if (receivedCancel != null) {
									System.out.println(getLocalName() + ": Shutting down");
									doDelete();
								} else {
								
								System.out.println("We have a winner: " + getLocalName());
								// Need to send abort to other agents.
								
								DFAgentDescription DFDescription = new DFAgentDescription();
								ServiceDescription serviceDescription = new ServiceDescription();
								serviceDescription.setType(AGENT_TYPE);
								DFDescription.addServices(serviceDescription);
								//AID[] agents;
								
//								try {
//									// Find all agents with the DF description
//									DFAgentDescription[] result = DFService.search(myAgent, DFDescription);
//									
//									// Place the agents in the agents array.
//									agents = new AID[result.length];
//									for (int i = 0; i < result.length; ++i) {
//										agents[i] = result[i].getName();
//									}
//									
//									// if there are no other agents: terminate. This should never happen.
//									if (agents.length <= 0) {
//										System.out.println("There are no other agents. Terminate!");
//										doDelete();
//									}
									
									ACLMessage cancel = new ACLMessage(ACLMessage.CANCEL);
									
									// Set all negotiation agents as receivers except self.
									for (int i = 0; i < agents.length; ++i) {
										if (!myAgent.getAID().equals(agents[i])) {
											cancel.addReceiver(agents[i]);
										}
									} 
									
									System.out.println("Sending cancels");
									myAgent.send(cancel);
									
									System.out.println(getLocalName() + ": Shutting down");
									doDelete();
//									
								}
//								} catch (FIPAException fe) {
//									fe.printStackTrace();
//								}
								
								
								
								
								// set content and conversationID to be the desired items name and send the CFP message.
								
								
								
							}
						} else {
							// update inventory if agent is seller
							for (int i = 0; i < inventoryList.size(); i++) {
								if(inventoryList.get(i).getName().equals(itemName)) {
									inventoryList.remove(i);
									break;
								}
							}
						}
						
					} else {
						// Continue negotiation
							
						System.out.println("Continue negotiation");
						
						// Make a proposal
						if (isBuyer) {
							price = price + 100;
						} else {
							price = price - 100;
						}
						
						// Check if new proposal you are sending is acceptable for you.
						double utilityNewPrice = 0;
						if (isBuyer) {
							utilityNewPrice =  (double)globalItems.getValueOfItem(itemName) - (double)price + ((double)money)/desiredListLength;
						} else {
							utilityNewPrice =  (double)price - (double)globalItems.getValueOfItem(itemName) + ((double)money)/desiredListLength;
						}
						System.out.println(isBuyer + " " + utilityNewPrice);
						if (utilityNewPrice>6050) {
						
							
						// send proposal.
						ACLMessage sendReply = receivedReply.createReply();
						sendReply.setPerformative(ACLMessage.PROPOSE);
						sendReply.setContent(String.valueOf(price));
						System.out.println(myAgent.getLocalName() + " sent (from NEGO) proposal to " + receivedReply.getSender().getLocalName());
						myAgent.send(sendReply);
						
						// TODO: go forever.
						//step = 3;	
						messageTemplate = MessageTemplate.and(	MessageTemplate.MatchConversationId(sendReply.getConversationId()),
								MessageTemplate.MatchInReplyTo(sendReply.getReplyWith()));
						
						} else {
							// Cannot yield more, reject.
							stopNegotiate = true;
							System.out.println("Recting proposal");
							
							if (isBuyer && numberOfPropReject < 5) {
								Item desiredItem = new Item(itemName, globalItems.getValueOfItem(itemName));
								
								myAgent.addBehaviour(new BuyBehaviour(desiredItem));
								numberOfPropReject++;
							}
							
							ACLMessage sendReply = receivedReply.createReply();
							sendReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
							System.out.println(myAgent.getLocalName() + " sent (from NEGO) reject to " + receivedReply.getSender().getLocalName());
							myAgent.send(sendReply);
							
							// TODO: go forever.
							//step = 3;	
							messageTemplate = MessageTemplate.and(	MessageTemplate.MatchConversationId(sendReply.getConversationId()),
									MessageTemplate.MatchInReplyTo(sendReply.getReplyWith()));
						}
					}
				}
			} else {
				// If no message is received the behaviour is blocked.
				block();
			}
			
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return stopNegotiate;
		}
		
	}
	
	private boolean isWinner() {
		System.out.println("\nChecking winner");
		for (int i = 0; i < desiredList.size(); i++) {
			if (! inventoryList.contains(desiredList.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isInInventory(String item) {
		for (int i = 0; i < inventoryList.size(); i++) {
			if (inventoryList.get(i).getName().equals(item)) {
				return true;
			}
		}
		return false;
	}
	
	
	private void printList(ArrayList<Item> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getName() + " " + list.get(i).getValue());
		}
		System.out.println("\n");
	}
	
	private String agentsToString(AID [] agents) {
		String string = "";
		for (int i = 0; i < agents.length; i++) {
			string += agents[i].getLocalName() + " ";
		}
		return string;
	}
}
