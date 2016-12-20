package com.example.test.mysmsreader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadSMS extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_sms);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    readSmsDatabase();
                } else {
                    // Permission Denied
                    Toast.makeText(ReadSMS.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void startReadingSMSDatabase(View v) {
        int hasPermission= 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasPermission = checkSelfPermission(Manifest.permission.READ_SMS);

            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }

        readSmsDatabase();
    }

    private void readSmsDatabase() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor != null) {
            cursor.moveToLast();
            Log.i("cursor", String.valueOf(cursor.getCount()));
            String[] columnNames = cursor.getColumnNames();
            Log.i("cursor_culumn_names", columnNames.toString());
            if (cursor.getCount() > 0) {
                do {
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    if (body.contains("TJEK") ||
                            body.contains("KKRT") ||
                            body.contains("gereserveer")) {
                        Transaction transaction = null;
                        if (body.contains("gereserveer")) {
                            transaction = processDebitCardTransaction(body);

                        } else {
                            transaction = processCurrentAccountTransaction(body);
                        }
                        if (transaction != null) {
                            Log.i("transaction", transaction.toString());
                        }
                    }
                } while (cursor.moveToPrevious());
            }
        }
    }

    private Transaction processCurrentAccountTransaction(String body) {
        Transaction transaction = null;

        final String regex = " (\\d.+?) (.+?), (.+?), R(\\d.+?), (.*)";
        final String string = body;

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);

        if (matcher.find()) {
        /* sample output from https://regex101.com/
        Group 1.	n/a	`7/10/16`
        Group 2.	n/a	`SETTLEMENT/C - ACB KREDIET`
        Group 3.	n/a	`INVESTECPBH CELLIERS`
        Group 4.	n/a	`1,462.00`
        Group 5.	n/a	`Beskikbaar R27,123.27 Hulp 0860008600; HEYDETG008`
         */
            transaction = new Transaction();
            transaction.set_date(matcher.group(1));
            transaction.set_type(getTransactionType(matcher.group(2)));
            transaction.set_supplier(matcher.group(3));
            String amountStr = matcher.group(4);
            amountStr = amountStr.replace(",","");
            transaction.set_amount(Double.parseDouble(amountStr));
            transaction.set_saldo(getAmountFromStr(matcher.group(5)));
        }
        return transaction;
    }

    private int getTransactionType(String transactionTypeStr) {
        int transactionType = Transaction.TRANSACTION_NONE;

        if (transactionTypeStr.contains("ACB KREDIET") ||
                transactionTypeStr.contains("IBANK BETALING VAN") ||
                transactionTypeStr.contains("IBANK OORPLASING")) {
            transactionType = Transaction.TRANSACTION_DEPOSIT;
        } else if (transactionTypeStr.contains("AFTREKORDER NA") ||
                transactionTypeStr.contains("IBANK BETALING NA") ||
                transactionTypeStr.contains("NAEDO OPSPR") ||
                transactionTypeStr.contains("EKSTERN NA") ||
                transactionTypeStr.contains("ACB DEBIET")) {
            transactionType = Transaction.TRANSACTION_PAYMENT;
        } else if (transactionTypeStr.contains("OTM OPVRAGING")) {
            transactionType = Transaction.TRANSACTION_WITDRAWEL;
        }

        return transactionType;
    }

    private double getAmountFromStr(String transactionStr) {
        double saldo = 0.0;

        final String regex = "R(\\d.+?) ";
        final String string = transactionStr;

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);

        if (matcher.find()) {
            saldo = getAmount(matcher.group(1));
        }
        return saldo;
    }

    private double getAmount(String amountStr) {
        String saldoStr = amountStr;
        saldoStr = saldoStr.replace(",","");
        try {
            return Double.parseDouble(saldoStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Transaction processDebitCardTransaction(String transactionStr) {
        Transaction transaction = null;

        final String regex = " (\\d.+?) (.+?) R(\\d.+?) (.*)";
        final String string = transactionStr;

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(string);


        if (matcher.find()) {

//             Group 1: 06/10/16
//             Group 2: GADGET CANDY EAST RAND BOKSBUR het
//             Group 3: 249.00
//             Group 4: gereserveer vir 'n aankoop. Jou beskikbare saldo is R26,558.97 Help 0860008600; HEYDETG008

            transaction = new Transaction();
            transaction.set_date(matcher.group(1));
            transaction.set_supplier(matcher.group(2));
            transaction.set_amount(getAmount(matcher.group(3)));
            transaction.set_saldo(getAmountFromStr(matcher.group(4)));
            transaction.set_smsData(transactionStr);

            transaction.set_type(Transaction.TRANSACTION_PAYMENT);
        }

        return transaction;
    }
}
