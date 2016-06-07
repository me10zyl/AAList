package site.zy1.aalist.activitiy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONTokener;
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
        public boolean handleMessage(Message message) {
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
                case -1:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this.getApplicationContext(), "没联网或者服务器出错", Toast.LENGTH_SHORT).show();
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
    }

    private void calcTotal(){
        Calendar calendar = Calendar.getInstance();
        for(AAListItem aa : aaList){
            TotalListItem totalListItem = new TotalListItem();
            Date date = aa.getDate();
            calendar.setTime(date);
            int month = calendar.get(Calendar.MONTH);
            Log.i("month", month + "");
            totalListItem.setMonth(month + 1);
            if(totalList.contains(totalListItem)){
                for(TotalListItem total: totalList){
                    if(total.getMonth() == month){
                        setBalance(total, aa);
                        break;
                    }
                }
            }else{
                setBalance(totalListItem, aa);
                totalList.add(totalListItem);
            }
        }
    }

    private void setBalance(TotalListItem total, AAListItem aa){
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
