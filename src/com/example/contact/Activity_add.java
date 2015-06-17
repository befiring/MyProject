package com.example.contact;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Activity_add extends ActionBarActivity {

	private TextView Ename = null;
	private TextView Ephone = null;
	private EditText edit_name = null;
	private EditText edit_phone = null;
	private Button ok = null;
	private Button cancel = null;
	private MyDataBaseHelper helper;
	private SQLiteDatabase mySQL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		helper = new MyDataBaseHelper(this);
		mySQL = helper.getWritableDatabase();
		this.Ename = (TextView) super.findViewById(R.id.Ename);
		this.Ephone = (TextView) super.findViewById(R.id.Ephone);
		this.edit_name = (EditText) super.findViewById(R.id.edit_name);
		this.edit_phone = (EditText) super.findViewById(R.id.edit_phone);
		this.ok = (Button) super.findViewById(R.id.ok);
		this.cancel = (Button) super.findViewById(R.id.cancel);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!edit_phone.getText().toString().equals("")) {
					helper.insert(mySQL, edit_name.getText().toString(),
							edit_phone.getText().toString());
					Activity_add.this.finish();

				} else {
					Dialog dlg = new AlertDialog.Builder(Activity_add.this)
							.setTitle("提示").setMessage("请输入电话号码！").create();
					dlg.show();
				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Activity_add.this.finish();
			}
		});

	}
}
