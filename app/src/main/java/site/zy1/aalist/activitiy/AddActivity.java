package site.zy1.aalist.activitiy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import site.zy1.aalist.R;

public class AddActivity extends ActionBarActivity {
    private SharedPreferences sharedPreferences;
    private String username;
    private TextView tv_setName;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
        tv_setName = (TextView) findViewById(R.id.tv_setName);
        refreshName();
    }

    private void refreshName(){
        username = sharedPreferences.getString("name", null);
        if (username != null) {
            tv_setName.setText("你是" + username);
            tv_setName.setTextColor(Color.BLACK);
        } else {
            tv_setName.setText("你还没有设置名字！点击右上角菜单设置！");
            tv_setName.setTextColor(Color.RED);
        }
    }

    public void add(View view) {
        if(username == null){
            Toast.makeText(this.getApplicationContext(), "请在右上角菜单中选择你是谁", Toast.LENGTH_SHORT);
        }
        Intent intent = new Intent();
        intent.setClass(this.getApplicationContext(), MainActivity.class);
        startActivity(intent);
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
        };
        return super.onOptionsItemSelected(item);
    }


}
