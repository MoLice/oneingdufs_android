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
		
		// 初始化tab
		LayoutInflater.from(this).inflate(R.layout.common_lost, tabHost.getTabContentView(), true);
		// tab1：拾获物品登记
		tabHost.addTab(tabHost.newTabSpec("pickup")
				.setIndicator("拾获登记", getResources().getDrawable(R.drawable.ic_tab_lost_cur))
				.setContent(R.id.common_lost_pickup));
		// tab2：捡到物品公布
		tabHost.addTab(tabHost.newTabSpec("lost")
				.setIndicator("失物招领", getResources().getDrawable(R.drawable.ic_tab_pickup))
				.setContent(R.id.common_lost_lost));
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
			}
		});
		
		// 初始化Tab1
		add_pic = (ImageView) findViewById(R.id.common_lost_pickup_picture);
		// 添加标签
		photo = new PhotoSelector(this);
		photo.setXYProportion(4, 3);
		
		addPic();
		addTag();
		addButtonEvent();
		
		// 初始化Tab2
		// 添加查看方式的点击事件
		byDatetime = (TextView) findViewById(R.id.common_lost_lost_byDatetime);
		byDatetimeLine = findViewById(R.id.common_lost_lost_byDatetime_line);
		byDatetimeColor = getResources().getColor(R.color.green);
		byTag = (TextView) findViewById(R.id.common_lost_lost_byTag);
		byTagLine = findViewById(R.id.common_lost_lost_byTag_line);
		byTagColor = getResources().getColor(R.color.blue);
		black_light = getResources().getColor(R.color.black_light);
		changeViewType();
		// 添加ListView
		listView = (ListView) findViewById(R.id.common_lost_lostlist);
		// 模拟从服务器得到的失物招领数据
		try {
			listData = new JSONArray("[{\"picture\":null,\"tag\":\"校园卡 绿色 20081000139\",\"location\":\"二饭门口\",\"datetime\":\"03-29 15:56\",\"details\":\"应该是啥啥啥来着\"},{\"picture\":null,\"tag\":\"校园卡 绿色 20081000139\",\"location\":\"二饭门口\",\"datetime\":\"03-29 15:56\",\"details\":\"应该是啥啥啥来着\"},{\"picture\":null,\"tag\":\"校园卡 绿色 20081000139\",\"location\":\"二饭门口\",\"datetime\":\"03-29 15:56\",\"details\":\"应该是啥啥啥来着\"},{\"picture\":null,\"tag\":\"校园卡 绿色 20081000139\",\"location\":\"二饭门口\",\"datetime\":\"03-29 15:56\",\"details\":\"应该是啥啥啥来着\"},{\"picture\":null,\"tag\":\"校园卡 绿色 20081000139\",\"location\":\"二饭门口\",\"datetime\":\"03-29 15:56\",\"details\":\"应该是啥啥啥来着\"}]");
		} catch (Exception e) {
			Log.e("JSON错误", "CommonLostActivity#listData, e=" + e.toString());
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
//		dialog.setButton("确定", new DialogInterface.OnClickListener() {
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
				Toast.makeText(CommonLostActivity.this, "已保存", Toast.LENGTH_SHORT).show();
			}
		});
	}
	/**
	 * 根据不同查看方式显示ListView
	 */
	private void changeViewType() {
		byDatetime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				byDatetime.setTextColor(byDatetimeColor);
				byDatetimeLine.setBackgroundColor(byDatetimeColor);
				byTag.setTextColor(black_light);
				byTagLine.setBackgroundColor(black_light);
				Toast.makeText(CommonLostActivity.this, "按照发布时间查看", Toast.LENGTH_SHORT).show();
			}
		});
		byTag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				byDatetime.setTextColor(black_light);
				byDatetimeLine.setBackgroundColor(black_light);
				byTag.setTextColor(byTagColor);
				byTagLine.setBackgroundColor(byTagColor);
				Toast.makeText(CommonLostActivity.this, "按照标签查看", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * 将服务端返回的数据处理后显示到ListView上
	 */
	private void setListViewAdapter() {
		if(listData != null) {
			// 生成适配器，绑定数据
			SimpleAdapter simpleAdapter = new SimpleAdapter(this,
					getListData(listData),// 数据源
					R.layout.common_lost_lostitem,// ListItem的XML layout
					new String[] {
						"picture",
						"tag",
						"location",
						"datetime",
						"details"
					},// 数据源里的key
					new int[] {
						R.id.common_lost_lostitem_img,
						R.id.common_lost_lostitem_tag,
						R.id.common_lost_lostitem_location,
						R.id.common_lost_lostitem_datetime,
						R.id.common_lost_lostitem_details
					});// 上面的key在ListItem的XML layout里对应的控件ID
			listView.setAdapter(simpleAdapter);
			listView.setOnItemClickListener(itemClickListener);
		}
	}
	
	/**
	 * 将存储有所有Item数据的JSONArray转换为ListView的数据源
	 * @param listData 从服务器返回的JSON数据
	 * @return ListView数据源
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
	 * 将单个物品的数据从JSONObject转换为Map
	 * @param itemData 单个Item数据
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
				.setItems(new CharSequence[] {"查看大图", "联系对方"}, new DialogInterface.OnClickListener() {
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