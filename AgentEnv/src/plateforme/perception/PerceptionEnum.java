package plateforme.perception;

import plateforme.agent.Agent;

/**
 * @author thomas
 *
 *	Une interface qui décrit un conteneur de Perception.
 *
 *	Cette interface a une méthode qui renvoie la Perception.
 *	Elle est particulièrement utile si on veut mettre les Perceptions dans une énumération Perceptions.
 *
 */
public interface PerceptionEnum<A extends Agent> {
	
	public <T> Perception<A,T> getPerception();
	
}
