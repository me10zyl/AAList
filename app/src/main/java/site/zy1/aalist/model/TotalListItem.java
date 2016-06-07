package site.zy1.aalist.model;

/**
 * Created by ZyL on 2016/6/6.
 */
public class TotalListItem {
    private double subtotalZyl;
    private double subtotalPwh;
    private double subtotalTql;
    private double  total;
    private double average;
    private int month;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getSubtotalZyl() {
        return subtotalZyl;
    }

    public void setSubtotalZyl(double subtotalZyl) {
        this.subtotalZyl = subtotalZyl;
    }

    public double getSubtotalPwh() {
        return subtotalPwh;
    }

    public void setSubtotalPwh(double subtotalPwh) {
        this.subtotalPwh = subtotalPwh;
    }

    public double getSubtotalTql() {
        return subtotalTql;
    }

    public void setSubtotalTql(double subtotalTql) {
        this.subtotalTql = subtotalTql;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
