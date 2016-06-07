package site.zy1.aalist.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import site.zy1.aalist.R;
import site.zy1.aalist.model.AAListItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ZyL on 2016/6/6.
 */
public class AAListAdapter extends BaseAdapter{
    private Context context;
    private List<AAListItem> items;

    public Context getContext() {
        return context;
    }

    public void setItems(List<AAListItem> items) {
        this.items = items;
    }

    public AAListAdapter(Context context, List<AAListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AAListItem item = items.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_aalist, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_description = (TextView) view.findViewById(R.id.tv_description);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        TextView tv_balance = (TextView) view.findViewById(R.id.tv_balance);
        tv_name.setText(item.getName());
        tv_description.setText(item.getDescription());
        Date date = item.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);
        tv_date.setText(dateStr);
        tv_balance.setText("￥"+ item.getBalance());
        return view;
    }
}
