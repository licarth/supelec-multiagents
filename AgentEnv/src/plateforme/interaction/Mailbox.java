package plateforme.interaction;

import java.util.List;

import plateforme.agent.Agent;

public interface Mailbox<A extends Agent> {
	void deposer(Message m);
	List<Message> relever();
}
