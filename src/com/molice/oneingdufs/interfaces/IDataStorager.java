package com.molice.oneingdufs.interfaces;

public interface IDataStorager {
	/**
	 * ���ݴ����key����ȡ��Ӧ��ֵ
	 * @param key ����
	 * @param defValue get������Ĭ��ֵ��defValueͨ��ΪString/Boolean/Integer�е�һ�֣�����get()���ص�Ҳ����ͬ���͵�ֵ���������������֣���ֱ�ӽ�Ĭ��ֵ����
	 * @return ��ȡ���Ķ�Ӧkey��value����������ڸ�key�򷵻�Ĭ��ֵ
	 */
	public String get(String key, String defValue);
	public Boolean get(String key, Boolean defValue);
	public int get(String key, int defValue);
	/**
	 * ����key-value���д洢��֧����ʽ���ã����洢�����Ҫ����save()�������������ݽ���ʧ
	 * @param key ����
	 * @param value ֵ������ΪString/Boolean/Integer�е�һ�֣���������������洢��û���κδ�����ʾ
	 * @return IDataStorager���󣬷�����ʽ����
	 */
	public IDataStorager set(String key, String value);
	public IDataStorager set(String key, Boolean value);
	public IDataStorager set(String key, int value);
	
	/**
	 * ɾ���洢��key��Ŀ���������ڸ�key��ʲô��������ִ����Ϻ���Ҫ����save()�Ż���Ч
	 * @param key Ҫɾ������Ŀ��
	 * @return IDataStorager���󣬷�����ʽ����
	 */
	public IDataStorager del(String key);
	/**
	 * ��set()��������ã�����ӵ����ݱ���������������ӵ����ݽ���ʧ
	 */
	public void save();
	/**
	 * �ж��Ƿ���ڸ�key
	 * @param key
	 * @return �����򷵻�true�����򷵻�false
	 */
	public Boolean has(String key);
}
