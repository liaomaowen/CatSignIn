package com.liao.nfcclient;
//ע�����
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
		init();//��ʼ��
		connect();//���������������
		Log.i("main", "111111111111111111111111111111111111111111111111");
		//��ע���������������ظ������ʱ��Ϊ���Ե����
		et_mima2.addTextChangedListener(watcher);
		bt_listener();
	}
	
	private void connect() {
		//��ȡ����ʾ
		AsyncTask<Void, String, Void> read = new AsyncTask<Void, String, Void>() {
			//�����ʱ����
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
					Toast.makeText(context, "���������������ʧ��", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(context, "���������������ʧ��", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				
				 
				
				return null;
			}
			//����UI����
			@Override
			protected void onProgressUpdate(String... values) {
				super.onProgressUpdate(values);
			}
		};
		read.execute();//ִ��
	}

	private void bt_listener() {
		bt_reg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				connect();//���������������
				String mima = et_mima.getText().toString().trim();
				String mima2 = et_mima2.getText().toString().trim();
				String phone = et_phone.getText().toString().trim();
				String name = et_name.getText().toString().trim();
				String number = et_number.getText().toString().trim();
				if (phone.length()==11) {
					if(mima.length()>=6){
						if(mima.equals(mima2)){
							//�������ݿ�
							editor.putString("UserName", name);
							editor.putString("UserPhone", phone);
							editor.putString("UserPassword", mima);
							editor.putString("UserNumber", number);
							editor.commit();
							//-------------------
							//JSON���ݷ�װ
							JsonObject object = new JsonObject();
							object.addProperty("Studentname", name);//��Ӷ��������
							object.addProperty("Phonenumber", phone);
							object.addProperty("Password", mima);
							object.addProperty("Studentnumber", number);
							object.addProperty("LateTime", 0);
							object.addProperty("SingIn", "false");//Ĭ��û�гٵ�
							object.addProperty("bool", "registration");//��ʾע�ᣬ�ڷ�������������жϲ�ͬactivity����������
							String WriterLine = object.toString();
							
							try {
								writer.write(WriterLine.replace("\n", " ")+"\n");
								writer.flush();
								writer.close();//�ر�������
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							
							tiaozhuan();
						}else{
							Toast.makeText(context, "�����������벻��ͬ", Toast.LENGTH_SHORT).show();
						}
				}else{
					Toast.makeText(context, "����̫�̣����������6λ������", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(context, "�ֻ��Ÿ�ʽ����", Toast.LENGTH_SHORT).show();
			}
		}


		private void tiaozhuan() {
			Toast.makeText(context, "ע��ɹ�", Toast.LENGTH_SHORT).show();
			//�ӳ�2����ת
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
		//��ע���������������ظ������ʱ��Ϊ���Ե����
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
