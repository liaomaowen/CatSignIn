package com.liao.nfcclient;
//注册界面
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.liao.AppVariables.Variables;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registration extends Activity{
	EditText et_name;
	EditText et_phone;
	EditText et_number;
	EditText et_mima;
	EditText et_mima2;
	Button bt_reg;
	Context context;
//------------------------
	SharedPreferences pref;
	Editor editor;
//--------------------------
	String ip;
	Socket socket;
	BufferedWriter writer;
	BufferedReader reader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		this.context = this;
		ip=new Variables().getIP();
//		Toast.makeText(context, ip, Toast.LENGTH_SHORT).show();
		init();//初始化
		connect();//与服务器建立连接
		Log.i("main", "111111111111111111111111111111111111111111111111");
		//让注册键到最后我们填重复密码的时候为可以点击的
		et_mima2.addTextChangedListener(watcher);
		bt_listener();
	}
	
	private void connect() {
		//读取并显示
		AsyncTask<Void, String, Void> read = new AsyncTask<Void, String, Void>() {
			//处理耗时操作
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					Log.i("main", "222222222222222222222222222222222222222222");
//					socket = new Socket();
//					
//					socket.connect(new InetSocketAddress(ip,12345),5000);
					socket = new Socket(ip,12345);
					Log.i("main", "333333333333333333333333333333333333333333333333");
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					Log.i("main", "4444444444444444444444444444444444444444444444444444444");
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (UnknownHostException e) {
					Toast.makeText(context, "与服务器建立连接失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(context, "与服务器建立连接失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				
				 
				
				return null;
			}
			//更新UI界面
			@Override
			protected void onProgressUpdate(String... values) {
				super.onProgressUpdate(values);
			}
		};
		read.execute();//执行
	}

	private void bt_listener() {
		bt_reg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				connect();//与服务器建立连接
				String mima = et_mima.getText().toString().trim();
				String mima2 = et_mima2.getText().toString().trim();
				String phone = et_phone.getText().toString().trim();
				String name = et_name.getText().toString().trim();
				String number = et_number.getText().toString().trim();
				if (phone.length()==11) {
					if(mima.length()>=6){
						if(mima.equals(mima2)){
							//存入数据库
							editor.putString("UserName", name);
							editor.putString("UserPhone", phone);
							editor.putString("UserPassword", mima);
							editor.putString("UserNumber", number);
							editor.commit();
							//-------------------
							//JSON数据封装
							JsonObject object = new JsonObject();
							object.addProperty("Studentname", name);//添加对象的属性
							object.addProperty("Phonenumber", phone);
							object.addProperty("Password", mima);
							object.addProperty("Studentnumber", number);
							object.addProperty("LateTime", 0);
							object.addProperty("SingIn", "false");//默认没有迟到
							object.addProperty("bool", "registration");//表示注册，在服务端用这里来判断不同activity传来的数据
							String WriterLine = object.toString();
							
							try {
								writer.write(WriterLine.replace("\n", " ")+"\n");
								writer.flush();
								writer.close();//关闭输入流
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							
							tiaozhuan();
						}else{
							Toast.makeText(context, "两次输入密码不相同", Toast.LENGTH_SHORT).show();
						}
				}else{
					Toast.makeText(context, "密码太短，请输入大于6位的密码", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(context, "手机号格式错误", Toast.LENGTH_SHORT).show();
			}
		}


		private void tiaozhuan() {
			Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
			//延迟2秒跳转
			Handler handler = new Handler();
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent();
					intent.setClass(context, Login.class);
					context.startActivity(intent);
					finish();
				}
			};
			handler.postDelayed(runnable, 1000);
		}
	});

}

private void init() {
	et_name = (EditText) findViewById(R.id.et_name);
	et_phone = (EditText) findViewById(R.id.et_phone);
	et_number = (EditText) findViewById(R.id.et_number);
	et_mima = (EditText) findViewById(R.id.et_mima);
	et_mima2 = (EditText) findViewById(R.id.et_mima2);
	bt_reg = (Button) findViewById(R.id.bt_registration);
	bt_reg.setEnabled(false);
	pref = getSharedPreferences("user", MODE_PRIVATE);
	editor = pref.edit();
}

TextWatcher watcher = new TextWatcher() {

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		//让注册键到最后我们填重复密码的时候为可以点击的
		bt_reg.setEnabled(true);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}
};
	@Override
	protected void onDestroy() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	};

	
}
