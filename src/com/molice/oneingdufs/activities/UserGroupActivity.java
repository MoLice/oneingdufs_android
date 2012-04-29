package com.molice.oneingdufs.activities;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.utils.DatabaseHelper;
import com.molice.oneingdufs.utils.HttpConnectionHandler;
import com.molice.oneingdufs.utils.ProjectConstants;
import com.molice.oneingdufs.utils.DatabaseHelper.DB;
import com.molice.oneingdufs.utils.HttpConnectionUtils;

import android.app.ExpandableListActivity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class UserGroupActivity extends ExpandableListActivity {
	private JSONArray groups;
	private JSONArray childs;
	
	DatabaseHelper databaseHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_group);
		
		groups = new JSONArray();
		childs = new JSONArray();
		
		databaseHelper = new DatabaseHelper(this);
		databaseHelper.createTableIfNotExist(DB.GROUP);
		
		// �������󣬻�ȡ����������Ϣ
		new HttpConnectionUtils(connectionHandler, this).get(ProjectConstants.URL.home_group, null);
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO �����ʾ���û�������
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}
	
	@Override
	public void onGroupCollapse(int groupPosition) {
		super.onGroupCollapse(groupPosition);
	}
	
	@Override
	public void onGroupExpand(int groupPosition) {
		super.onGroupExpand(groupPosition);
	}
	
	HttpConnectionHandler connectionHandler = new HttpConnectionHandler(this) {
		protected void onSucceed(JSONObject result) {
			super.onSucceed(result);
			groups = result.optJSONArray("group");
			int length = groups.length();
			for(int i=0; i<length; i++) {
				// ��ȡһ��������Ϣ
				JSONObject group = groups.optJSONObject(i);
				// ������ݵ�childs
				childs.put(group.optJSONArray("member"));
				
				// ��������Ϣ�������ݿ�
				ContentValues row = new ContentValues();
				row.put("id", group.optInt("id"));
				row.put("name", group.optString("name"));
				row.put("member", group.optJSONArray("member").toString());
				row.put("timestamp", group.optString("timestamp"));
				databaseHelper.updateOrInsert(DB.GROUP, row, "id", String.valueOf(group.optInt("id")));
			}
			
			setListAdapter(new ExpandableAdapter());
		};
		protected void onComplete(HttpClient httpClient) {
			super.onComplete(httpClient);
			databaseHelper.closeAll();
		};
	};
	
	class ExpandableAdapter extends BaseExpandableListAdapter {
		private LayoutInflater inflater;

		public ExpandableAdapter() {
			inflater = LayoutInflater.from(UserGroupActivity.this);
		}
		@Override
		public int getGroupCount() {
			return groups.length();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childs.optJSONArray(groupPosition).length();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			JSONObject groupData = groups.optJSONObject(groupPosition);
			TextView name = (TextView) inflater.inflate(R.layout.user_group_grouplayout, null);
			name.setTag(groupPosition);
			name.setText(groupData.optString("name"));
			return name;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// ��ȡ��ǰ����ó�Ա����Ϣ
			JSONObject userData = childs.optJSONArray(groupPosition).optJSONObject(childPosition);
			View child = inflater.inflate(R.layout.user_group_childlayout, null);
			// ���ó�Ա�������ݴ洢ΪTag
			child.setTag(userData);
			// ���øó�Ա����
			TextView username = (TextView) child.findViewById(R.id.text);
			username.setText(userData.optString("username"));
			// �����Ƿ���ͬAPN����ʾ��ͬ��ʽ�ġ����족��ť
			TextView action = (TextView) child.findViewById(R.id.action);
			if(userData.optBoolean("apn_enabled")) {
				action.setBackgroundColor(getResources().getColor(R.color.blue_light));
				action.setEnabled(true);
			} else {
				action.setBackgroundColor(getResources().getColor(R.color.black_light));
				action.setEnabled(false);
			}
			return child;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}
