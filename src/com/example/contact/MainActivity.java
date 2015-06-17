package com.example.contact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import android.R.color;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private MyDataBaseHelper helper = null;
	SQLiteDatabase mySQL = null;
	private Button insert = null;
	private Button query = null;
	private Button download = null;
	private TextView tv1=null;
	private ProgressBar progressBar = null;
	private List<Map<String, Object>> phonelist = new ArrayList<Map<String, Object>>();
	public ListView result = null;
	private String[] items = { "拨打电话", "发送短信", "编辑联系人", "删除联系人" };
	private int FLAG = 0;
	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	private static final int DOWNLOAD_FAILED = 3;
	private int progress = 0;
	private boolean cancelUpdate = false;
	private Dialog mDownloadDialog = null;
	SimpleAdapter adapter = null;
	PhoneAdapter phoneAdapter = null;
	DownloadThread dThread = null;
	public Handler mHandler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				progressBar.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件

				Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT)
						.show();
				break;
			case DOWNLOAD_FAILED:
				
				Toast.makeText(MainActivity.this, "该文件已存在",
						Toast.LENGTH_SHORT).show();
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		helper = new MyDataBaseHelper(this);
		mySQL = helper.getWritableDatabase();
		this.insert = (Button) super.findViewById(R.id.insert);
		this.query = (Button) super.findViewById(R.id.query);
		this.download = (Button) super.findViewById(R.id.download);
		this.result = (ListView) super.findViewById(R.id.result);
		this.tv1=(TextView)super.findViewById(R.id.tv1);
		tv1.setText(new LoadJni().getResult());

		insert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Activity_add.class);
				startActivity(intent);
			}
		});
		getPhoneList();
		phoneAdapter = new PhoneAdapter(MainActivity.this, phonelist);
		result.setAdapter(phoneAdapter);
		result.setVisibility(View.GONE);
		query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (FLAG) {
				case 0:
					getPhoneList();
					phoneAdapter.myList = phonelist;
					phoneAdapter.notifyDataSetChanged();
					result.setVisibility(View.VISIBLE);
					query.setText("收起");
					FLAG = 1;
					break;
				case 1:
					result.setVisibility(View.GONE);
					query.setText("查询");
					FLAG = 0;

				default:
					break;
				}

			}
		});
		result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				getPhoneList();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Activity_edit.class);
				intent.putExtra("id", phonelist.get(position).get("id")
						.toString());
				intent.putExtra("name", phonelist.get(position).get("name")
						.toString());
				intent.putExtra("phone", phonelist.get(position).get("phone")
						.toString());
				startActivity(intent);

			}
		});
		result.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				showContactDialog(items, position);
				return true;
			}
		});
		download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setTitle("下载中");
				final LayoutInflater inflater = LayoutInflater
						.from(MainActivity.this);
				View view = inflater.inflate(R.layout.download_progress, null);
				progressBar = (ProgressBar) view
						.findViewById(R.id.downloadprogress);
				builder.setView(view);
				// 取消更新
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// 设置取消状态
								cancelUpdate = true;
							}
						});
				mDownloadDialog = builder.create();
				// Window mWindow=mDownloadDialog.getWindow();
				// WindowManager.LayoutParams lp=mWindow.getAttributes();
				// mWindow.setAttributes(lp);
				mDownloadDialog.show();
				mDownloadDialog.setCanceledOnTouchOutside(false);
				dThread = new DownloadThread();
				dThread.start();
			}
		});
		// download.setVisibility(View.GONE);
	}

	private void showContactDialog(final String[] arg, final int position) {
		new AlertDialog.Builder(this)
				.setTitle(
						helper.query2(mySQL).get(position).get("name").toString())
				.setItems(arg, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Uri uri = null;
						String tel = helper.query2(mySQL).get(position)
								.get("phone").toString();

						switch (which) {

						case 0:// 打电话
							uri = Uri.parse("tel:" + tel);
							Intent it = new Intent(Intent.ACTION_CALL, uri);
							startActivity(it);

							break;

						case 1:// 发短息
							uri = Uri.parse("smsto:" + tel);
							Intent it2 = new Intent();
							it2.setAction(Intent.ACTION_SENDTO);
							it2.putExtra("sms_body", "");
							it2.setType("vnd.android-dir/mms-sms");
							it2.setData(uri);
							startActivity(it2);

							break;

						case 2:// 查看详细 修改联系人资料
							getPhoneList();
							Intent intent = new Intent();
							intent.setClass(MainActivity.this,
									Activity_edit.class);
							intent.putExtra("id",
									phonelist.get(position).get("id")
											.toString());
							intent.putExtra("name", phonelist.get(position)
									.get("name").toString());
							intent.putExtra("phone", phonelist.get(position)
									.get("phone").toString());
							startActivity(intent);
							break;

						case 3:// 删除

							helper.delete(
									mySQL,
									Integer.parseInt(phonelist.get(position)
											.get("id").toString()));
							getPhoneList();
							phoneAdapter.myList = phonelist;
							phoneAdapter.notifyDataSetChanged();
							break;

						}
					}
				}).show();
	}

	public class PhoneAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater inflater;
		private List<Map<String, Object>> myList;

		public PhoneAdapter(Context c, List<Map<String, Object>> list) {
			this.mContext = c;
			this.myList = list;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return myList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (v == null) {
				viewHolder = new ViewHolder();
				v = inflater.inflate(R.layout.phoneitem, null);
				viewHolder.idTextView = (TextView) v.findViewById(R.id.item_id);
				viewHolder.nameTextView = (TextView) v
						.findViewById(R.id.item_name);
				viewHolder.phoneTextView = (TextView) v
						.findViewById(R.id.item_phone);
				v.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) v.getTag();
			}
			viewHolder.idTextView.setText(myList.get(position).get("id")
					.toString());
			viewHolder.nameTextView.setText(myList.get(position).get("name")
					.toString());
			viewHolder.phoneTextView.setText(myList.get(position).get("phone")
					.toString());
			return v;
		}

	}

	class ViewHolder {
		TextView nameTextView;
		TextView phoneTextView;
		TextView idTextView;
	}

	class DownloadThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					String path = Environment.getExternalStorageDirectory()
							.toString() + File.separator + "bitmap";
					// URL url = new URL("http://192.168.31.176/s3.jpg");
					URL url = new URL("http://192.168.31.175/PANTUMPRINT.apk");
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File file = new File(path);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(path, "flower.jpg");
					if (!apkFile.exists()) {
						FileOutputStream fos = new FileOutputStream(apkFile);

						int count = 0;
						// 缓存
						byte buf[] = new byte[1024];
						// 写入到文件中
						do {
							int numread = is.read(buf);
							count += numread;
							// 计算进度条位置
							progress = (int) (((float) count / length) * 100);
							// 更新进度
							mHandler.sendEmptyMessage(DOWNLOAD);
							if (numread <= 0) {
								// 下载完成
								mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
								break;
							}
							// 写入文件
							fos.write(buf, 0, numread);
						} while (!cancelUpdate);// 点击取消就停止下载.
						fos.close();
						is.close();
					}

					else {
						mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
						
					}

				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mDownloadDialog.dismiss();

		}

	}

	public void getPhoneList() {
		phonelist = helper.query2(mySQL);
	}
}
