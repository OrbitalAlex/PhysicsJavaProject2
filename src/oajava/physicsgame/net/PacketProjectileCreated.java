package oajava.physicsgame.net;

import oajava.physicsgame.PhysicsGame;
import oajava.physicsgame.Projectile;
import oajava.util.Util.NetPacket;

public class PacketProjectileCreated implements NetPacket {

	private static final long serialVersionUID = 5432379442700247189L;

	private Projectile projectile;
	
	public PacketProjectileCreated() {}
	
	public PacketProjectileCreated(Projectile projectile) {
		super();
		this.projectile = projectile;
	}

	@Override
	public void packetRecieved(int arg0) {
		PhysicsGame.projectiles.add(projectile);
	}

}
