package oajava.physicsgame.net;

import oajava.physicsgame.PhysicsGame;
import oajava.physicsgame.Tank;
import oajava.util.Util.Angle;
import oajava.util.Util.NetPacket;

public class PacketTankChanged implements NetPacket {

	private static final long serialVersionUID = 1469100607177096453L;

	private int tank_side;
	private byte health;
	private Angle angle;
	
	
	@Override
	public void packetRecieved(int arg0) {
		for (Tank t : PhysicsGame.tanks) {
			if (t.side == tank_side) {
				t.health = health;
				t.angle.setAngRadians(angle.angRadians());
			}
		}
	}

}
