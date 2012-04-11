package com.molice.oneingdufs.interfaces;

public interface IDataStorager {
	/**
	 * 根据传入的key，获取对应的值
	 * @param key 键名
	 * @param defValue get动作的默认值，defValue通常为String/Boolean/Integer中的一种，这样get()返回的也是相同类型的值，若不是其中三种，则直接将默认值返回
	 * @return 获取到的对应key的value，如果不存在该key则返回默认值
	 */
	public String get(String key, String defValue);
	public Boolean get(String key, Boolean defValue);
	public int get(String key, int defValue);
	/**
	 * 根据key-value进行存储，支持链式调用，若存储完毕需要调用save()方法，否则数据将丢失
	 * @param key 键名
	 * @param value 值，必须为String/Boolean/Integer中的一种，否则将跳过而不会存储且没有任何错误提示
	 * @return IDataStorager对象，方便链式调用
	 */
	public IDataStorager set(String key, String value);
	public IDataStorager set(String key, Boolean value);
	public IDataStorager set(String key, int value);
	
	/**
	 * 删除存储的key条目，若不存在该key则什么都不做，执行完毕后需要调用save()才会生效
	 * @param key 要删除的条目名
	 * @return IDataStorager对象，方便链式调用
	 */
	public IDataStorager del(String key);
	/**
	 * 在set()方法后调用，将添加的数据保存起来，否则添加的数据将丢失
	 */
	public void save();
	/**
	 * 判断是否存在该key
	 * @param key
	 * @return 存在则返回true，否则返回false
	 */
	public Boolean has(String key);
}
