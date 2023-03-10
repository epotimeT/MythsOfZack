package com.game.utilities;

/**
 * <h1>Semaphore</h1> This class controls the custom semaphore - it allows for
 * it to be used as a lock until a condition has been met, regardless of the
 * order in which the threads arrive
 */
public class Semaphore {

	private boolean requirementMet = false;
	private int timeframe = 5000;

	public Semaphore(int time) {
		timeframe = time;
	}

	public Semaphore() {
		// Uses default timeframe
	}

	/**
	 * Releases the semaphore so threads can continue past it If a thread is
	 * currently waiting for the semaphore, said thread is also woken up so it can
	 * continue. Otherwise, any threads that come across the semaphore will be
	 * allowed to pass without waiting
	 */
	public synchronized void take() {
		requirementMet = true;
		this.notify();
	}

	/**
	 * Forces thread to sleep until take() is called If a thread calls this method,
	 * it is forced to sleep until a different thread calls take(). If the take()
	 * has already been called, it will continue as normal
	 * 
	 * @return - boolean that represents if the timer has finished or not
	 */
	public synchronized boolean release() {
		if (!requirementMet) {
			try {
				wait(timeframe);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (requirementMet == false) {
			return false;
		}
		requirementMet = false;

		return true;
	}
}