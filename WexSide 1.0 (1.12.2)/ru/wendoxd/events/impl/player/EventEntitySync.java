package ru.wendoxd.events.impl.player;

import ru.wendoxd.events.Event;
import ru.wendoxd.events.IEventCancelable;

public class EventEntitySync extends Event implements IEventCancelable {

	private boolean canceled;
	private float yaw, pitch, yawDelta, pitchDelta;
	private double posX, posY, posZ;
	private boolean onGround;

	public EventEntitySync(float yaw, float pitch, double posX, double posY, double posZ, boolean onGround) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.onGround = onGround;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public double getPosZ() {
		return posZ;
	}

	public void setPosZ(double posZ) {
		this.posZ = posZ;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	@Override
	public void setCanceled() {
		canceled = true;
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}
}
