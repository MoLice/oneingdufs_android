package com.molice.oneingdufs.layouts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.molice.oneingdufs.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * <strong>��ʱ����</strong>��ʹ��ʱ�ڻ����Ĺ����У�ѡ��ᷢ���Ķ�
 * 
 * @author MoLice (sf.molice@gmail.com)
 * @date 2012-3-29
 */
public class CheckBoxLister {
	private Activity activity;
	private ListView listView;
	// �洢����
	private String[] titles;
	// �洢���飬��Ϊnull�򴴽����Ⱥ�titleһ�µ�����
	private String[] details;
	private int maxSelect;
	private ArrayList<Integer> selectedItem;
	
	public CheckBoxLister(Activity activity, String[] titles, String[] details, int maxSelect) {
		init(activity, maxSelect);
		this.titles = titles;
		this.details = initDetails(details);
	}
	
	public CheckBoxLister(Activity activity, int resTitles, int resDetails, int maxSelect) {
		init(activity, maxSelect);
		this.titles = activity.getResources().getStringArray(resTitles);
		this.details = activity.getResources().getStringArray(resDetails);
	}
	
	public Object[] getSelectedItemIndex() {
		return this.selectedItem.toArray();
	}
	
	public void setSelected(String[] tags) {
		Log.d("!!!", tags[0]);
		// �����ѡ��item��Ϣ
		for(int i=0; i<selectedItem.size(); i++) {
			((CheckBox) listView.getChildAt(new Integer(String.valueOf(selectedItem.get(i)))).findViewById(R.id.checkbox_checkbox)).setChecked(false);
		}
		this.selectedItem.clear();
		for(int i=0; i<titles.length; i++) {
			for(int j=0; j<tags.length; j++) {
				if(titles[i].equals(tags[j])) {
					// ����tag��������ӵ�selectedItem
					selectedItem.add(i);
					((CheckBox) listView.getChildAt(i).findViewById(R.id.checkbox_checkbox)).setChecked(true);
				}
			}
		}
	}
	
	private void init(Activity activity, int maxSelect) {
		this.activity = activity;
		this.maxSelect = maxSelect;
		listView = new ListView(activity);
		selectedItem = new ArrayList<Integer>();
	}
	
	private ListView createListView() {
		if(listView.getCount() == 0) {
			listView.setAdapter(createAdapter());
			listView.setOnItemClickListener(listener);
			return listView;
		}
		return listView;
	}
	
	public void refreshListView() {
		listView.setAdapter(createAdapter());
	}
	
	private SimpleAdapter createAdapter() {
		SimpleAdapter adapter = new SimpleAdapter(activity,
				createData(),
				R.layout.checkbox_list,
				new String[] {"title","detail"},
				new int[] {R.id.checkbox_title, R.id.checkbox_details});
		return adapter;
	}
	
	private List<Map<String, Object>> createData() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int length = titles.length;
		for(int i=0; i<length; i++) {
			list.add(getItemData(titles[i], details[i]));
		}
		return list;
	}
	public AlertDialog createDialog() {
		return new AlertDialog.Builder(activity)
			.setTitle("��ѡ��")
			.setView(createListView())
			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.create();
	}
	private String[] initDetails(String[] details) {
		if(details == null || details.length != titles.length) {
			int length = titles.length;
			String[] result = new String[length];
			for(int i=0; i<length; i++) {
				result[i] = "";
			}
			return result;
		}
		return details;
	}
	private Map<String, Object> getItemData(String title, String detail) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("detail", detail);
		return map;
	}
	private OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d("Item���", "position=" + String.valueOf(position));
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_checkbox);
			if(checkBox.isChecked()) {
				checkBox.setChecked(false);
				int result = selectedItem.indexOf(position);
				if(result != -1) {
					// ����������selectedItem��ȥ��
					selectedItem.remove(result);
				}
			} else {
				if(selectedItem.size() == maxSelect) {
					// �ﵽѡ�����������ޣ�����ѡ��
					Toast.makeText(activity, "���ѡ��" + String.valueOf(maxSelect) + "��", Toast.LENGTH_SHORT).show();
				} else {
					checkBox.setChecked(true);
					selectedItem.add(position);
				}
			}
		}
	};
}
