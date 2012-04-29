package com.molice.oneingdufs.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.utils.PhotoSelector;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class CommonLostActivity extends TabActivity {
	private TabHost tabHost;
	
	private ImageView add_pic;
	private PhotoSelector photo;
	private Bitmap picture;
	private Button cancel;
	private Button submit;
	
	private TextView byDatetime;
	private View byDatetimeLine;
	private int byDatetimeColor;
	private TextView byTag;
	private View byTagLine;
	private int byTagColor;
	private int black_light;
	private ListView listView;
	private JSONArray listData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tabHost = getTabHost();
		
		// ��ʼ��tab
		LayoutInflater.from(this).inflate(R.layout.common_lost, tabHost.getTabContentView(), true);
		// tab1��ʰ����Ʒ�Ǽ�
		tabHost.addTab(tabHost.newTabSpec("pickup")
				.setIndicator("ʰ��Ǽ�", getResources().getDrawable(R.drawable.ic_tab_lost_cur))
				.setContent(R.id.common_lost_pickup));
		// tab2������Ʒ����
		tabHost.addTab(tabHost.newTabSpec("lost")
				.setIndicator("ʧ������", getResources().getDrawable(R.drawable.ic_tab_pickup))
				.setContent(R.id.common_lost_lost));
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
			}
		});
		
		// ��ʼ��Tab1
		add_pic = (ImageView) findViewById(R.id.common_lost_pickup_picture);
		// ��ӱ�ǩ
		photo = new PhotoSelector(this);
		photo.setXYProportion(4, 3);
		
		addPic();
		addTag();
		addButtonEvent();
		
		// ��ʼ��Tab2
		// ��Ӳ鿴��ʽ�ĵ���¼�
		byDatetime = (TextView) findViewById(R.id.common_lost_lost_byDatetime);
		byDatetimeLine = findViewById(R.id.common_lost_lost_byDatetime_line);
		byDatetimeColor = getResources().getColor(R.color.green);
		byTag = (TextView) findViewById(R.id.common_lost_lost_byTag);
		byTagLine = findViewById(R.id.common_lost_lost_byTag_line);
		byTagColor = getResources().getColor(R.color.blue);
		black_light = getResources().getColor(R.color.black_light);
		changeViewType();
		// ���ListView
		listView = (ListView) findViewById(R.id.common_lost_lostlist);
		// ģ��ӷ������õ���ʧ����������
		try {
			listData = new JSONArray("[{\"picture\":null,\"tag\":\"У԰�� ��ɫ 20081000139\",\"location\":\"�����ſ�\",\"datetime\":\"03-29 15:56\",\"details\":\"Ӧ����ɶɶɶ����\"},{\"picture\":null,\"tag\":\"У԰�� ��ɫ 20081000139\",\"location\":\"�����ſ�\",\"datetime\":\"03-29 15:56\",\"details\":\"Ӧ����ɶɶɶ����\"},{\"picture\":null,\"tag\":\"У԰�� ��ɫ 20081000139\",\"location\":\"�����ſ�\",\"datetime\":\"03-29 15:56\",\"details\":\"Ӧ����ɶɶɶ����\"},{\"picture\":null,\"tag\":\"У԰�� ��ɫ 20081000139\",\"location\":\"�����ſ�\",\"datetime\":\"03-29 15:56\",\"details\":\"Ӧ����ɶɶɶ����\"},{\"picture\":null,\"tag\":\"У԰�� ��ɫ 20081000139\",\"location\":\"�����ſ�\",\"datetime\":\"03-29 15:56\",\"details\":\"Ӧ����ɶɶɶ����\"}]");
		} catch (Exception e) {
			Log.e("JSON����", "CommonLostActivity#listData, e=" + e.toString());
		}
		setListViewAdapter();
	}
	private void addPic() {
		add_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				photo.startTakeNewPhoto();
			}
		});
	}
	
	private void addTag() {
//		checkBoxLister = new CheckBoxLister(this, R.array.common_lost_tags, R.array.common_lost_tags_details, 3);
//		dialog = checkBoxLister.createDialog();
//		dialog.setButton("ȷ��", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				StringBuilder tags = new StringBuilder();
//				String[] tagsArray = getResources().getStringArray(R.array.common_lost_tags);
//				Object[] selectedItem = checkBoxLister.getSelectedItemIndex();
//				for(int i=0; i<selectedItem.length; i++) {
//					tags.append(tagsArray[new Integer(String.valueOf(selectedItem[i]))]).append(" ");
//				}
//				tag.setText(tags);
//				dialog.dismiss();
//			}
//		});
//		tag_btn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				checkBoxLister.setSelected(tag.getText().toString().split(" "));
//				dialog.show();
//			}
//		});
	}
	private void addButtonEvent() {
		cancel = (Button) findViewById(R.id.common_lost_pickup_cancel);
		submit = (Button) findViewById(R.id.common_lost_pickup_submit);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CommonLostActivity.this, "�ѱ���", Toast.LENGTH_SHORT).show();
			}
		});
	}
	/**
	 * ���ݲ�ͬ�鿴��ʽ��ʾListView
	 */
	private void changeViewType() {
		byDatetime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				byDatetime.setTextColor(byDatetimeColor);
				byDatetimeLine.setBackgroundColor(byDatetimeColor);
				byTag.setTextColor(black_light);
				byTagLine.setBackgroundColor(black_light);
				Toast.makeText(CommonLostActivity.this, "���շ���ʱ��鿴", Toast.LENGTH_SHORT).show();
			}
		});
		byTag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				byDatetime.setTextColor(black_light);
				byDatetimeLine.setBackgroundColor(black_light);
				byTag.setTextColor(byTagColor);
				byTagLine.setBackgroundColor(byTagColor);
				Toast.makeText(CommonLostActivity.this, "���ձ�ǩ�鿴", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * ������˷��ص����ݴ������ʾ��ListView��
	 */
	private void setListViewAdapter() {
		if(listData != null) {
			// ������������������
			SimpleAdapter simpleAdapter = new SimpleAdapter(this,
					getListData(listData),// ����Դ
					R.layout.common_lost_lostitem,// ListItem��XML layout
					new String[] {
						"picture",
						"tag",
						"location",
						"datetime",
						"details"
					},// ����Դ���key
					new int[] {
						R.id.common_lost_lostitem_img,
						R.id.common_lost_lostitem_tag,
						R.id.common_lost_lostitem_location,
						R.id.common_lost_lostitem_datetime,
						R.id.common_lost_lostitem_details
					});// �����key��ListItem��XML layout���Ӧ�Ŀؼ�ID
			listView.setAdapter(simpleAdapter);
			listView.setOnItemClickListener(itemClickListener);
		}
	}
	
	/**
	 * ���洢������Item���ݵ�JSONArrayת��ΪListView������Դ
	 * @param listData �ӷ��������ص�JSON����
	 * @return ListView����Դ
	 */
	private List<Map<String, Object>> getListData(JSONArray listData) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int length = listData.length();
		for(int i=0; i<length; i++) {
			list.add(getItemData(listData.optJSONObject(i)));
		}
		
		return list;
	}
	
	/**
	 * ��������Ʒ�����ݴ�JSONObjectת��ΪMap
	 * @param itemData ����Item����
	 * @return
	 */
	private Map<String, Object> getItemData(JSONObject itemData) {
		Map<String, Object> map = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = itemData.keys();
		String key;
		while(iterator.hasNext()) {
			key = iterator.next();
			if(key.equals("picture")) {
				map.put(key, R.drawable.logo);
			} else {
				map.put(key, itemData.opt(key));
			}
		}
		return map;
	}
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			new AlertDialog.Builder(CommonLostActivity.this)
				.setItems(new CharSequence[] {"�鿴��ͼ", "��ϵ�Է�"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		photo.onActivityResult(requestCode, resultCode, data);
		picture = photo.getResultPicture();
		add_pic.setImageBitmap(picture);
		super.onActivityResult(requestCode, resultCode, data);
	}
}