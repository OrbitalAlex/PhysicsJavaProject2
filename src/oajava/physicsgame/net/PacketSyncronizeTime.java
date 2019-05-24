package oajava.physicsgame.net;

import static oajava.util.Util.*;

import oajava.physicsgame.PhysicsGame;
import oajava.util.Util.NetReplyPacket;

public class PacketSyncronizeTime implements NetReplyPacket {

	private static final long serialVersionUID = -8438113218324449987L;

	private String uuid;
	
	@Override
	public void genNewUUID() {
		uuid = ioCreateUUID(10);
	}

	@Override
	public String getUUID() {
		return uuid;
	}

	@Override
	public Object reply(int arg0) {
		return new Long(PhysicsGame.time0);
	}

}
