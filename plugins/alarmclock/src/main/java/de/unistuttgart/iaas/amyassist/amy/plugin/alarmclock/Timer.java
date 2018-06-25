package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.util.Calendar;

/**
 * Class that defines alarm attributes and behaviour
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 *
 */
public class Timer {

	private int id;
	private Calendar timerDate;
	private boolean active;

	/**
	 * Constructor for a timer object
	 * 
	 * @param id
	 *            id of the timer
	 * @param hours
	 *            hour delay
	 * @param minutes
	 *            minute delay
	 * @param seconds
	 *            second delay
	 * @param active
	 *            timer active
	 */
	public Timer(int id, int hours, int minutes, int seconds, boolean active) {
		if (id < 1)
			throw new IllegalArgumentException();
		this.id = id;

		this.timerDate = Calendar.getInstance();
		this.timerDate.add(Calendar.HOUR, hours);
		this.timerDate.add(Calendar.MINUTE, minutes);
		this.timerDate.add(Calendar.SECOND, seconds);

		this.active = active;
	}

	/**
	 * Alternative constructor for a timer object
	 * 
	 * @param id
	 *            id of the timer
	 * @param timerDate
	 *            date the timer rings
	 * @param active
	 *            timer active
	 */
	public Timer(int id, Calendar timerDate, boolean active) {
		if (id < 1)
			throw new IllegalArgumentException();
		this.id = id;
		this.timerDate = timerDate;
		this.active = active;
	}

	/**
	 * Returns a string representation of this object
	 * 
	 * @see java.lang.Object#toString()
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return this.id + ":" + this.timerDate.getTimeInMillis() + ":" + this.active;
	}

	/**
	 * Construct an alarm object from the String that was made by the
	 * convertToString method
	 * 
	 * @param input
	 *            the String made by convertToString method
	 * @return the corresponding alarm object
	 */
	public static Timer reconstructObject(String input) {
		String[] params = input.split(":");
		if (params.length == 3) {
			Calendar timerDate = Calendar.getInstance();
			timerDate.setTimeInMillis(Long.parseLong(params[1]));
			return new Timer(Integer.parseInt(params[0]), timerDate, Boolean.parseBoolean(params[2]));
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Returns this timers delay until it goes off
	 * 
	 * @return hourDiff, minuteDiff, secondDiff
	 */
	public int[] getRemainingTime() {
		Calendar current = Calendar.getInstance();
		Calendar future = this.timerDate;

		long diff = future.getTimeInMillis() - current.getTimeInMillis();
		diff /= 1000;

		long hourDiff = diff / 3600;
		diff %= 3600;

		long minuteDiff = diff / 60;
		diff %= 60;

		long secondDiff = diff;

		return new int[] { (int) hourDiff, (int) minuteDiff, (int) secondDiff };
	}

	/**
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return timerDate
	 */
	public Calendar getTimerDate() {
		return this.timerDate;
	}

	/**
	 * @param timerDate
	 */
	public void setTimerDate(Calendar timerDate) {
		this.timerDate = timerDate;
	}

	/**
	 * @return active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
