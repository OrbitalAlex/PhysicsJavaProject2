package oajava.physicsgame.net;

import oajava.physicsgame.PhysicsGame;

import static oajava.util.Util.*;

import oajava.util.Util.NetReplyPacket;

public class PacketSynchronizeTanks implements NetReplyPacket {

	private static final long serialVersionUID = -2249495288569443090L;
	private String UUID;
	
	@Override
	public void genNewUUID() {
		UUID = ioCreateUUID(10);
	}

	@Override
	public String getUUID() {
		return UUID;
	}

	@Override
	public Object reply(int arg0) {
		return PhysicsGame.tanks;
	}

}
