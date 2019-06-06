package oajava.physicsgame.net;

import oajava.physicsgame.PhysicsGame;
import oajava.physicsgame.Tank;
import oajava.util.Util.NetPacket;

public class PacketChangeHealth implements NetPacket 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 873650183087439L;
	int healthp1 = 0;
	int healthp2 = 0;
	
	
	
	public PacketChangeHealth(int healthp1, int healthp2) {
		super();
		this.healthp1 = healthp1;
		this.healthp2 = healthp2;
	}



	@Override
	public void packetRecieved(int arg0) 
	{
		 PhysicsGame.heart.heartCountp1 = healthp1;
		 PhysicsGame.heart.heartCountp2 = healthp2;
	}

}
