package com.github.sylphlike.framework.adapt.cache;


import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.StampedLock;


/**
 * 简单缓存，无超时实现，默认使用{@link WeakHashMap}实现缓存自动清理
 * <p>  K 键类型 V 值类型              </p>
 * <p>  time 17:56 2019/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class SimpleCache<K, V> implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 池*/
	private final Map<K, V> cache;
	// 乐观读写锁
	private final StampedLock lock = new StampedLock();

	/**
	 * 构造，默认使用{@link WeakHashMap}实现缓存自动清理
	 */
	public SimpleCache() {
		this(new WeakHashMap<>());
	}

	/**
	 * 构造
	 *
	 * <p> 通过自定义Map初始化，可以自定义缓存实现。比如使用{@link WeakHashMap}则会自动清理key，使用HashMap则不会清理，同时，传入的Map对象也可以自带初始化的键值对，防止在get时创建
	 * @param initMap 初始Map，用于定义Map类型
	 */

	/**
	 * 构造
	 * <p> 通过自定义Map初始化，可以自定义缓存实现。比如使用{@link WeakHashMap}则会自动清理key，使用HashMap则不会清理，同时，传入的Map对象也可以自带初始化的键值对，防止在get时创建 </p>
	 * @param initMap   初始Map，用于定义Map类型
	 * @author  Gopal.pan
	 */
	public SimpleCache(Map<K, V> initMap) {
		this.cache = initMap;
	}


	public V get(K key) {
		long stamp = lock.readLock();
		try {
			return cache.get(key);
		} finally {
			lock.unlockRead(stamp);
		}
	}




	public V put(K key, V value) {
		// 独占写锁
		final long stamp = lock.writeLock();
		try {
			cache.put(key, value);
		} finally {
			lock.unlockWrite(stamp);
		}
		return value;
	}


	public V remove(K key) {
		// 独占写锁
		final long stamp = lock.writeLock();
		try {
			return cache.remove(key);
		} finally {
			lock.unlockWrite(stamp);
		}
	}


	public void clear() {
		// 独占写锁
		final long stamp = lock.writeLock();
		try {
			this.cache.clear();
		} finally {
			lock.unlockWrite(stamp);
		}
	}
}
