package com.example.test.mysmsreader;

import android.view.ViewDebug;

import java.util.Calendar;

/**
 * Created by za3200057 on 12/19/2016.
 */

public class Transaction {

    public static final int TRANSACTION_DEPOSIT = 115;
    public static final int TRANSACTION_PAYMENT = 507;
    public static final int TRANSACTION_WITDRAWEL = 244;
    public static final int TRANSACTION_NONE = 211;

    private String _supplier = "";
    private int _type = TRANSACTION_NONE;
    private double _amount = 0.0;
    private double _saldo = 0.0;
    private String _date;
    private String _smsData;

    public Transaction() {
        final Calendar calendar = Calendar.getInstance();
        _date = calendar.DAY_OF_MONTH + "/" + (calendar.MONTH + 1) + "/" + Calendar.YEAR;
    }

    public double get_saldo() {
        return _saldo;
    }

    public void set_saldo(double _saldo) {
        this._saldo = _saldo;
    }

    public String get_supplier() {
        return _supplier;
    }

    public void set_supplier(String _supplier) {
        this._supplier = _supplier;
    }

    public int get_type() {
        return _type;
    }

    public void set_type(int _type) {
        this._type = _type;
    }

    public double get_amount() {
        return _amount;
    }

    public void set_amount(double _amount) {
        this._amount = _amount;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_smsData() {
        return _smsData;
    }

    public void set_smsData(String _smsData) {
        this._smsData = _smsData;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "_supplier='" + _supplier + '\'' +
                ", _type=" + _type +
                ", _amount=" + _amount +
                ", _saldo=" + _saldo +
                ", _date='" + _date + '\'' +
                ", _smsData='" + _smsData + '\'' +
                '}';
    }
}
