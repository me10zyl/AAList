package site.zy1.aalist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import site.zy1.aalist.R;
import site.zy1.aalist.model.TotalListItem;

import java.util.List;

/**
 * Created by ZyL on 2016/6/6.
 */
public class TotalListAdapter extends BaseAdapter{
    private Context context;
    private List<TotalListItem> items;

    public TotalListAdapter(Context context, List<TotalListItem> items) {
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
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TotalListItem item = items.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_totallist, null);
        TextView tv_balance1 =  (TextView)view.findViewById(R.id.tv_balance1);
        TextView tv_balance2 =  (TextView)view.findViewById(R.id.tv_balance2);
        TextView tv_balance3 =  (TextView)view.findViewById(R.id.tv_balance3);
        TextView tv_total =  (TextView)view.findViewById(R.id.tv_total);
        TextView tv_avg =  (TextView)view.findViewById(R.id.tv_avg);
        TextView tv_month =  (TextView)view.findViewById(R.id.tv_month);

        tv_balance1.setText("￥" + item.getSubtotalZyl());
        tv_balance2.setText("￥" + item.getSubtotalPwh());
        tv_balance3.setText("￥" + item.getSubtotalTql());
        tv_total.setText("￥" + item.getTotal());
        tv_avg.setText("￥" + item.getAverage());
        tv_month.setText(String.valueOf(item.getMonth()) + "月份");
        return view;
    }
}
