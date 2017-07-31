package com.hacked.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broadcaster implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	public interface BroadcastListener {
		void receiveBroadcast(String message);
	}

	private static HashMap<String, List<BroadcastListener>> mapOfListeners = new HashMap<String, List<BroadcastListener>>();

	public static synchronized void register(String domain, BroadcastListener listener) {
		List<BroadcastListener> listeners = mapOfListeners.get(domain);
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		listeners.add(listener);
		mapOfListeners.put(domain, listeners);
	}

	public static synchronized void unregister(String domain, BroadcastListener listener) {
		List<BroadcastListener> listeners = mapOfListeners.get(domain);
		if (listeners == null) {
			mapOfListeners.remove(domain);
			return;
		}
		listeners.remove(listener);
		mapOfListeners.put(domain, listeners);
	}

	public static synchronized void broadcast(String domain, final String message) {
		if (mapOfListeners.get(domain) != null) {
			for (final BroadcastListener listener : mapOfListeners.get(domain))
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						listener.receiveBroadcast(message);
					}
				});
		}
	}
}
