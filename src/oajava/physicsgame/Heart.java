package oajava.physicsgame;

import oajava.physicsgame.net.PacketChangeHealth;
import oajava.util.Util;

public class Heart {
	
	
	
	public int heartCountp1 = 3;
	public int heartCountp2 = 3;
	
	public void removeHeartp1()
	{
		heartCountp1--;
		Util.netSendPacket(PhysicsGame.socket, new PacketChangeHealth(heartCountp1, heartCountp2));
	}
	
	public void removeheartp2()
	{
		heartCountp2--;
		Util.netSendPacket(PhysicsGame.socket, new PacketChangeHealth(heartCountp1, heartCountp2));
	}

	public void displayHeart()
	{
		
	}
}
