package site.zy1.aalist.activitiy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import site.zy1.aalist.R;
import site.zy1.aalist.adapter.AAListAdapter;
import site.zy1.aalist.adapter.TotalListAdapter;
import site.zy1.aalist.model.AAListItem;
import site.zy1.aalist.model.TotalListItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ListView lv1, lv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv1 = (ListView) findViewById(R.id.lv1);
        lv2 = (ListView) findViewById(R.id.lv2);
        List<AAListItem> aaList = new ArrayList<AAListItem>();
        AAListItem item = new AAListItem();
        item.setDescription("买菜");
        item.setDate(new Date());
        item.setBalance(1234);
        item.setName("PWH");
        aaList.add(item);
        lv1.setAdapter(new AAListAdapter(this.getApplicationContext(), aaList));
        List<TotalListItem> totalList = new ArrayList<TotalListItem>();
        TotalListItem totalListItem = new TotalListItem();
        totalListItem.setSubtotalZyl(11.1);
        totalListItem.setSubtotalPwh(11.1);
        totalListItem.setSubtotalTql(11.1);
        totalListItem.setAverage(1000);
        totalListItem.setMonth(1);
        totalListItem.setTotal(3000);
        totalList.add(totalListItem);
        lv2.setAdapter(new TotalListAdapter(this.getApplicationContext(), totalList));
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
