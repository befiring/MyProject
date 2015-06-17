package com.example.contact;

import android.R.integer;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Activity_edit extends ActionBarActivity {
	private EditText edit_name = null;
	private EditText edit_phone = null;
	private Button ok = null;
	private Button cancel = null;
	private MyDataBaseHelper helper;
	private SQLiteDatabase mySQL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		helper = new MyDataBaseHelper(this);
		mySQL = helper.getWritableDatabase();
		this.edit_name = (EditText) super.findViewById(R.id.edit_name);
		this.edit_phone = (EditText) super.findViewById(R.id.edit_phone);
		this.ok = (Button) super.findViewById(R.id.ok);
		this.cancel = (Button) super.findViewById(R.id.cancel);
		Intent intent = super.getIntent();
		final int id = Integer.parseInt(intent.getStringExtra("id"));
		edit_name.setText(intent.getStringExtra("name"));
		edit_phone.setText(intent.getStringExtra("phone"));
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!edit_phone.getText().toString().equals("")) {
					helper.update(mySQL, id, edit_name.getText().toString(),
							edit_phone.getText().toString());
					Activity_edit.this.finish();
				} else {
					Dialog dlg = new AlertDialog.Builder(Activity_edit.this)
							.setTitle("��ʾ").setMessage("������绰���룡")
							.setPositiveButton("ȷ��", null).create();
					dlg.show();
				}

			}

		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Activity_edit.this.finish();
			}
		});
	}

}
