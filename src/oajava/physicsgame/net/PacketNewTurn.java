package oajava.physicsgame.net;

import oajava.physicsgame.PhysicsGame;
import oajava.util.Util.NetPacket;

public class PacketNewTurn implements NetPacket {

	private static final long serialVersionUID = -8985586724426140185L;

	private int turn;
	
	public PacketNewTurn() {}
	
	public PacketNewTurn(int turn) {
		super();
		this.turn = turn;
	}

	@Override
	public void packetRecieved(int arg0) {
		PhysicsGame.turn = turn;
	}

}
