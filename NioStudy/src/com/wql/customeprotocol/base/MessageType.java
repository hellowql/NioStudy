package com.wql.customeprotocol.base;


/**
 * @author wuqinglong
 * @date 2015年7月17日 下午9:36:55
 */
public enum MessageType {

	LOGIN_REQ((byte) 0), LOGIN_RESP((byte) 1), ONE_WAY((byte) 2), HANDSHAKE_REQ((byte) 3), HANDSHAKE_RESP((byte)4), HEARTBEAT_REQ((byte) 5),HEARTBEAT_RESP((byte)6);

	private byte type;

	private MessageType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte value() {
		return getType();
	}

}
