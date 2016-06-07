package site.zy1.aalist.activitiy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import site.zy1.aalist.R;
import site.zy1.aalist.model.AAListItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends ActionBarActivity {
    private SharedPreferences sharedPreferences;
    private String username;
    private TextView tv_setName;
    private EditText et_balance, et_description, et_date;
    private String version;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message message) {
            switch (message.what) {
                case 0:
                    final String balance = et_balance.getText().toString().trim();
                    final String description = et_description.getText().toString().trim();
                    final String date = et_date.getText().toString().trim();
                    new AsyncTask<String, Void, AAListItem>() {
                        @Override
                        protected AAListItem doInBackground(String... strings) {
                            HttpURLConnection conn = null;
                            try {
                                URL url = new URL("http://" + address + "/add.json");
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setConnectTimeout(5000);
                                conn.setRequestProperty("accept", "*/*");
                                conn.setRequestProperty("connection", "Keep-Alive");
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type",
                                        "application/json");
                                conn.setRequestProperty("charset", "utf-8");
                                conn.setDoOutput(true);
                                conn.setDoInput(true);
                                JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(conn.getOutputStream()));
                                jsonWriter.beginObject().name("name").value(username).name("balance").value(balance).name("description").value(description).name("date").value(date).endObject();
                                jsonWriter.flush();
                                jsonWriter.close();

                                JsonReader jsonReader = new JsonReader(new InputStreamReader(conn.getInputStream()));
                                jsonReader.beginObject();
                                boolean success = false;
                                while (jsonReader.hasNext()) {
                                    String name = jsonReader.nextName();
                                    if ("success".equals(name)) {
                                        success = jsonReader.nextBoolean();
                                    } else if ("message".equals(name)) {
                                        String message = jsonReader.nextString();
                                        Log.i("response", message);
                                    }
                                }
                                jsonReader.endObject();
                                jsonReader.close();
                                if (success) {
                                    Intent intent = new Intent();
                                    intent.setClass(AddActivity.this.getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Message m = new Message();
                                    m.what = -1;
                                    handler.dispatchMessage(m);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Message m = new Message();
                                m.what = -1;
                                handler.dispatchMessage(m);
                            } finally {
                                if (conn != null) {
                                    conn.disconnect();
                                }
                            }
                            return null;
                        }
                    }.execute();
                    break;
                case -1:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddActivity.this.getApplicationContext(), "没联网或者服务器出错", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case -3:
                    new AsyncTask<String, Void, AAListItem>() {
                        @Override
                        protected AAListItem doInBackground(String... strings) {
                            HttpURLConnection conn  = null;
                            try {
                                URL  url = new URL("http://" + address + "/version.json");
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestProperty("accept", "*/*");
                                conn.setRequestProperty("connection", "Keep-Alive");
                                conn.setRequestProperty("Content-Type",
                                        "application/json");
                                conn.setRequestProperty("charset", "utf-8");
                                conn.setDoInput(true);
                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                                JsonReader jsonReader = new JsonReader(br);
                                String newVersion = "";
                                String downloadUrl = "";
                                String message = "";
                                jsonReader.beginObject();
                                while(jsonReader.hasNext()){
                                    String name = jsonReader.nextName();
                                    if("version".equals(name)){
                                        newVersion  = jsonReader.nextString();
                                    }else if("url".equals(name)){
                                        downloadUrl = jsonReader.nextString();
                                    }else if("message".equals(name)){
                                        message = jsonReader.nextString();
                                    }
                                }
                                jsonReader.endObject();
                                jsonReader.close();
                                if(!newVersion.equals(version)){
                                    Message m = new Message();
                                    m.what = -4;
                                    Bundle data = new Bundle();
                                    data.putString("url", downloadUrl);
                                    data.putString("message", message);
                                    data.putString("version", newVersion);
                                    m.setData(data);
                                    handler.dispatchMessage(m);
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                if(conn!= null){
                                    conn.disconnect();
                                }
                            }
                            return null;
                        }
                    }.execute();
                    break;
                case -4:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                            final Bundle data = message.getData();
                            builder.setTitle("有新版本"+data.getString("version")+"了，是否要进行更新？（有更新一定要更新哦）"+data.getString("message")).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Uri uri = Uri.parse(data.getString("url"));
                                    Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("取消", null).show();
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private String address;

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void list(View v) {
        Intent intent = new Intent();
        intent.setClass(AddActivity.this.getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        version = getResources().getString(R.string.version);
        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        tv_setName = (TextView) findViewById(R.id.tv_setName);
        et_balance = (EditText) findViewById(R.id.et1_balance);
        et_date = (EditText) findViewById(R.id.et_date);
        et_description = (EditText) findViewById(R.id.et_description);
        address = getResources().getString(R.string.address);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        et_date.setText(sdf.format(date));
        refreshName();
        Message m = new Message();
        m.what = -3;
        handler.dispatchMessage(m);
    }

    private void refreshName() {
        username = sharedPreferences.getString("name", null);
        if (username != null) {
            tv_setName.setText("你是" + username);
            tv_setName.setTextColor(Color.BLACK);
        } else {
            tv_setName.setText("你还没有设置名字！点击右上角菜单设置！");
            tv_setName.setTextColor(Color.RED);
        }
    }

    private boolean validate() {
        final String balance = et_balance.getText().toString().trim();
        final String description = et_description.getText().toString().trim();
        final String date = et_date.getText().toString().trim();
        if (username == null) {
            Toast.makeText(this.getApplicationContext(), "请在右上角菜单中选择你是谁", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (balance.equals("")) {
            Toast.makeText(this.getApplicationContext(), "请填写金额", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (description.equals("")) {
            Toast.makeText(this.getApplicationContext(), "请填写用途", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (date.equals("")) {
            Toast.makeText(this.getApplicationContext(), "请填写日期", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void add(View view) {
        if (validate()) {
            Message m = new Message();
            m.what = 0;
            handler.dispatchMessage(m);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    protected void onResume() {
        Log.i("ces", "onResume");
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setName) {
            final String[] items = {"曾艺伦", "彭万红", "唐琪琳"};
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", items[i]);
                    editor.apply();
                    editor.commit();
                }
            };
            new AlertDialog.Builder(this).setTitle("你是？").setSingleChoiceItems(items, 0, onClickListener).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    refreshName();
                    dialogInterface.dismiss();
                }
            }).show();
            return true;
        }
        ;
        return super.onOptionsItemSelected(item);
    }


}
