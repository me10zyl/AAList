package site.zy1.aalist.activitiy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import site.zy1.aalist.R;
import site.zy1.aalist.adapter.AAListAdapter;
import site.zy1.aalist.adapter.TotalListAdapter;
import site.zy1.aalist.model.AAListItem;
import site.zy1.aalist.model.TotalListItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private ListView lv1, lv2;
    private String address;
    private List<AAListItem> aaList = new ArrayList<AAListItem>();
    private List<TotalListItem> totalList = new ArrayList<TotalListItem>();
    private AAListAdapter aaListAdapter;
    private TotalListAdapter totalListAdapter;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message message) {
            switch (message.what) {
                case 0:
                    new AsyncTask<String, Void, AAListItem>(){

                        @Override
                        protected AAListItem doInBackground(String... strings) {
                            HttpURLConnection conn;
                            try {
                                URL url = new URL("http://" + address + "/list.json");
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestProperty("accept", "*/*");
                                conn.setRequestProperty("connection", "Keep-Alive");
                                conn.setRequestMethod("GET");
                                conn.setRequestProperty("Content-Type",
                                        "application/json");
                                conn.setRequestProperty("charset", "utf-8");
                                conn.setDoInput(true);
                                conn.setConnectTimeout(10000);

                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                                aaList.clear();
                                totalList.clear();
                                JsonReader jsonReader = new JsonReader(br);
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()) {
                                    jsonReader.beginObject();
                                    AAListItem item = new AAListItem();
                                    while (jsonReader.hasNext()) {
                                        String name = jsonReader.nextName();
                                        if ("id".equals(name)) {
                                            item.setId(jsonReader.nextInt());
                                        } else if ("balance".equals(name)) {
                                            item.setBalance(jsonReader.nextDouble());
                                        } else if ("description".equals(name)) {
                                            item.setDescription(jsonReader.nextString());
                                        } else if ("date".equals(name)) {
                                            String date = jsonReader.nextString();
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            item.setDate(sdf.parse(date));
                                        } else if( "name".equals(name)){
                                            item.setName(jsonReader.nextString());
                                        }
                                    }
                                    aaList.add(item);
                                    jsonReader.endObject();
                                }
                                jsonReader.endArray();
                                jsonReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Message m = new Message();
                                m.what = -1;
                                handler.dispatchMessage(m);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Message m = new Message();
                                m.what = -1;
                                handler.dispatchMessage(m);
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    calcTotal();
                                    aaListAdapter.notifyDataSetChanged();
                                    totalListAdapter.notifyDataSetChanged();
                                }
                            });
                            return null;
                        }
                    }.execute();
                    break;
                case 2:
                    new AsyncTask<String, Void, AAListItem>() {
                        @Override
                        protected AAListItem doInBackground(String... strings) {
                            HttpURLConnection conn = null;
                            try {
                                URL url = new URL("http://" + address + "/delete/" + message.arg1);
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestProperty("accept", "*/*");
                                conn.setRequestProperty("connection", "Keep-Alive");
                                conn.setRequestMethod("GET");
                                conn.setRequestProperty("Content-Type",
                                        "application/json");
                                conn.setRequestProperty("charset", "utf-8");
                                conn.setDoInput(true);
                                conn.setConnectTimeout(10000);

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
                                Message m = new Message();
                                if (success) {
                                    m.what = -2;
                                }else{
                                    m.what = -1;
                                }
                                handler.dispatchMessage(m);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Message m = new Message();
                                m.what = -1;
                                handler.dispatchMessage(m);
                            }finally {
                                if(conn!= null){
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
                            Toast.makeText(MainActivity.this.getApplicationContext(), "没联网或者服务器出错", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case -2:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this.getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            Message m = new Message();
                            m.what = 0;
                            handler.dispatchMessage(m);
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        address = getResources().getString(R.string.address);
        lv1 = (ListView) findViewById(R.id.lv1);
        lv2 = (ListView) findViewById(R.id.lv2);
        aaListAdapter = new AAListAdapter(this.getApplicationContext(), aaList);
        totalListAdapter = new TotalListAdapter(this.getApplicationContext(), totalList);
        Message m = new Message();
        m.what = 0;
        handler.dispatchMessage(m);
        lv1.setAdapter(aaListAdapter);
        lv2.setAdapter(totalListAdapter);
        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final AAListItem item = (AAListItem) aaListAdapter.getItem(i);
                new AlertDialog.Builder(MainActivity.this).setTitle("确定要删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Message m  = new Message();
                        m.what = 2;
                        m.arg1 = item.getId();
                        handler.dispatchMessage(m);
                    }
                }).setNegativeButton("取消", null).show();
                return false;
            }
        });
    }

    private void calcTotal(){
        Calendar calendar = Calendar.getInstance();
        for(AAListItem aa : aaList){
            TotalListItem current = new TotalListItem();
            Date date = aa.getDate();
            calendar.setTime(date);
            int month = calendar.get(Calendar.MONTH) + 1;
            current.setMonth(month);
            boolean existed = false;
            for(TotalListItem total: totalList){
                if(total.getMonth() == month){
                    addBalance(total, aa);
                    existed = true;
                    break;
                }
            }
            if(!existed){
                addBalance(current, aa);
                totalList.add(current);
            }
        }
    }

    private void addBalance(TotalListItem total, AAListItem aa){
        if("曾艺伦".equals(aa.getName())){
            total.setSubtotalZyl(total.getSubtotalZyl() + aa.getBalance());
        }else if("彭万红".equals(aa.getName())){
            total.setSubtotalPwh(total.getSubtotalPwh() + aa.getBalance());
        }else if("唐琪琳".equals(aa.getName())) {
            total.setSubtotalTql(total.getSubtotalTql() + aa.getBalance());
        }
        total.setTotal(total.getTotal() + aa.getBalance());
        total.setAverage(total.getTotal() / 3.0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
