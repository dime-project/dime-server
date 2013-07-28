/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.ps.semantic;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BroadcastManager {
	
	private final CopyOnWriteArrayList<BroadcastReceiver> receivers = new CopyOnWriteArrayList<BroadcastReceiver>();

    private ThreadPoolExecutor threadPool = null;
 
    private final int poolSize = 2;
    private final int maxPoolSize = 8;
    private final long keepAliveTime = 60;
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

	private static BroadcastManager INSTANCE = null;
	
	public static BroadcastManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BroadcastManager();
		}
		return INSTANCE;
	}

	private BroadcastManager() {
		threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime,
				TimeUnit.SECONDS, queue);
	}

	/**
	 * Register a receive for any broadcasts.
	 * @param receiver the BroadcastReceiver to be registered
	 */
	public void registerReceiver(BroadcastReceiver receiver) {
		this.receivers.add(receiver);
	}

	/**
	 * Unregister a previously registered BroadcastReceiver.
	 * @param receiver the BroadcastReceiver to be unregistered
	 */
	public void unregisterReceiver(BroadcastReceiver receiver) {
		this.receivers.remove(receiver);
	}
	
	/**
	 * Broadcast asynchronously the given event to all interested BroadcastReceivers.
	 * @param event
	 * @return
	 */
	public boolean sendBroadcast(Event event) {
		new BroadcastTask(this.receivers, event);
		return true;
	}
	
	/**
	 * Like sendBroadcast(Intent), but if there are any receivers for the
	 * Intent this function will block and immediately dispatch them before
	 * returning.
	 * 
	 * @param event
	 */
	public void sendBroadcastSync(Event event) {
		for (BroadcastReceiver receiver : receivers) {
			receiver.onReceive(event);
		}
	}

	private class BroadcastTask implements Runnable {
//		private Thread thread; 
		private List<BroadcastReceiver> receivers;
		private Event event;
		
		public BroadcastTask(List<BroadcastReceiver> receivers, Event event) {
			this.receivers = receivers;
			this.event = event;
			
			threadPool.execute(this);
//			thread = new Thread(this); 
//			thread.start();
		}
		
		@Override
		public void run() {
			for (BroadcastReceiver receiver : receivers) {
				receiver.onReceive(event);
			}
		}
	}

}