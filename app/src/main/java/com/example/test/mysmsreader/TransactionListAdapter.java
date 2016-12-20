package com.example.test.mysmsreader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.test.mysmsreader.R;
import com.example.test.mysmsreader.Transaction;

import java.util.List;

/**
 * Created by za3200057 on 12/20/2016.
 */

public class TransactionListAdapter extends ArrayAdapter<Transaction> {

    private final Activity _activity;
    private final List<Transaction> _transactionList;
    private LayoutInflater _inflater = null;

    public TransactionListAdapter(Context context, int resource, List<Transaction> transactionList) {
        super(context, resource, transactionList);
        _activity = (Activity) context;
        _transactionList = transactionList;
        _inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return _transactionList.size();
    }

    @Nullable
    @Override
    public Transaction getItem(int position) {
        return _transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView category;
        public TextView amount;
        public TextView supplier;
        public TextView date;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                view = _inflater.inflate(R.layout.listview_transaction_item, null);
                holder = new ViewHolder();

                holder.category = (TextView) view.findViewById(R.id.category);
                holder.amount = (TextView) view.findViewById(R.id.amount);
                holder.supplier = (TextView) view.findViewById(R.id.suplier);
                holder.date = (TextView) view.findViewById(R.id.date);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.category.setText(Transaction.getTransactionDisc(_transactionList.get(position).get_type()));
            switch (_transactionList.get(position).get_type()) {
                case Transaction.TRANSACTION_PAYMENT:
                    holder.category.setTextColor(Color.RED);
                    break;
                case Transaction.TRANSACTION_WITDRAWEL:
                    holder.category.setTextColor(Color.rgb(255,140,0));
                    break;
                case Transaction.TRANSACTION_DEPOSIT:
                    holder.category.setTextColor(Color.BLUE);
                    break;
                default:
                    holder.category.setTextColor(Color.BLACK);
                    break;
            }
            holder.amount.setText("R" + String.valueOf(_transactionList.get(position).get_amount()));
            holder.supplier.setText(_transactionList.get(position).get_supplier());
            holder.date.setText(_transactionList.get(position).get_date());

        } catch (Exception e) {
        }
        return view;
    }
}

