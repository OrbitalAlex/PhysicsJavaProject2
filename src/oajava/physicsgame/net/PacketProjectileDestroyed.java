package oajava.physicsgame.net;

import oajava.physicsgame.PhysicsGame;
import oajava.physicsgame.Projectile;
import oajava.util.Util.NetPacket;

public class PacketProjectileDestroyed implements NetPacket {


	private static final long serialVersionUID = 7576127494398168108L;
	public float projectileCreationTime;
	
	
	
	@Override
	public void packetRecieved(int arg0) {
		for (Projectile p : PhysicsGame.projectiles.toArray(new Projectile[] {})) {
			if (p.initialTime == projectileCreationTime) {
				PhysicsGame.projectiles.remove(p);
				return;
			}
		}
	}

}
