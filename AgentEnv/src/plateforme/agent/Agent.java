package plateforme.agent;

import java.util.List;

import javax.security.auth.callback.Callback;

import philo.Main;
import plateforme.AMS;
import plateforme.Environnement;
import plateforme.action.Action;
import plateforme.action.ActionEnum;
import plateforme.action.UndefinedActionException;
import plateforme.action.WrongActionException;
import plateforme.interaction.Mailbox;
import plateforme.interaction.Message;
import plateforme.interaction.SendMessageException;
import plateforme.interaction.SimpleMailbox;
import plateforme.perception.Perception;
import plateforme.perception.PerceptionEnum;

/**
 * S est un type Enum dévrivant les états possibles pour cet agent.
 * 
 * @author thomas
 *
 * @param <E>
 * @param <StateEnumType>
 */
public abstract class Agent<E extends Environnement,
		StateEnumType extends Enum<? extends Etat<? extends Agent>>,
		PerceptionEnumType extends Enum<? extends PerceptionEnum>,
		ActionsEnumType extends Enum<? extends ActionEnum<? extends Agent>>> extends Thread implements AgentI{

	protected E env;
	protected Mailbox<Agent<E,?,?,?>> mailbox = new SimpleMailbox<>();
	private Class<? extends PerceptionEnum> perceptionContainer;
	private StateEnumType agentState;

	public Agent(E env) {
		this.setEnv(env);
		this.setAgentState(etatInitial());
	}

	//Abstrait
	public abstract StateEnumType etatInitial();
	/**
	 * Les perceptions sont possibles en même temps que la décision.
	 * 
	 * @return
	 */
	public abstract String percevoirEtDecider();
	protected abstract Class<? extends PerceptionEnum> perceptionContainerClass();
	protected abstract Class<? extends ActionEnum> actionContainerClass();
	abstract public Class<? extends Enum<? extends Etat>> getAgentStates();

	/**
	 * Retourne toutes les actions que l'agent est capable d'exécuter.
	 * 
	 * @return
	 */
	public abstract List<Action> getActions();

	
	
	@Override
	public final void run() {
		while(true){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//TODO Process messages if mailbox not empty
			List<Message> courrierReleve = mailbox.relever();
			for (Message message : courrierReleve) {
				//TODO Process message.
				//Recupérer la pile de courrier.
				lireCourrier(courrierReleve);
			}

			//Percept & decide
			StringBuilder out = new StringBuilder();
//			out.append(percevoir());
			out.append(percevoirEtDecider());
			System.out.println(out);
			Main.reporter.report();
		}
	}

	public abstract void lireCourrier(List<Message> courrierReleve);

	/**
	 * @param p
	 * @return
	 * 
	 * Un agent ne peut avoir de perception que de son point de vue. 
	 * L'environnement lui renvoie sa propre perception. Pour cela,
	 * l'environnement doit savoir qui percoit, d'où le this passé en paramètre.
	 * 
	 */
	public <T> T percept(PerceptionEnum p) {
		return (T) getEnv().getPerception(this, (Perception<Agent, T>) p.getPerception());
	}

	public <T> T act(ActionEnum ac) throws WrongActionException {
		try {
			if (ac.getAction() == null) throw new UndefinedActionException(String.format("L'action %s n'est pas implémentée.", ac));
			return (T) getEnv().executeAction(this, (Action<Agent, T>) ac.getAction());
		} catch (UndefinedActionException e) {
			throw new WrongActionException("Action invalide.", e);
		}
	}

	public Mailbox<Agent<E, ?, ?, ?>> getMailbox() {
		return mailbox;
	}

	public void setMailbox(Mailbox<Agent<E, ?, ?, ?>> mailbox) {
		this.mailbox = mailbox;
	}

	public E getEnv() {
		return env;
	}	

	public void setEnv(E env) {
		this.env = env;
	}

	@Override
	public AMS getAms() {
		return env.getAms();
	}

	@Override
	public void send(Message m) throws SendMessageException {
		getAms().send(m);
	}

	@Override
	public void sendAsync(Message m, Callback c) throws SendMessageException {
		getAms().send(m);
	}

	public StateEnumType getAgentState() {
		return agentState;
	}

	public void setAgentState(StateEnumType agentState) {
		this.agentState = agentState;
	}
	
}
