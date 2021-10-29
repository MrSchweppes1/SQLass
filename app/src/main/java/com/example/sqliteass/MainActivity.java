package com.example.sqliteass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button addBt, clearBt;
    EditText NameET, AuthorET, StyleET, CostET;
    DBhelper dBhelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBt = (Button) findViewById(R.id.AddBut);

        clearBt = (Button) findViewById(R.id.ClearBut);
        addBt.setOnClickListener(this);
        clearBt.setOnClickListener(this);

        NameET = (EditText) findViewById(R.id.NameET);
        AuthorET = (EditText) findViewById(R.id.Author);
        StyleET = (EditText) findViewById(R.id.Style);
        CostET = (EditText) findViewById(R.id.Cost);

        dBhelper = new DBhelper(this);
        database = dBhelper.getWritableDatabase();
        UpdateTable();

    }

    public void UpdateTable() {
        Cursor cursor = database.query(DBhelper.TABLE_CONTACTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBhelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBhelper.KEY_NAME);
            int authIndex = cursor.getColumnIndex(DBhelper.KEY_MAKER);
            int styleIndex = cursor.getColumnIndex(DBhelper.KEY_CITY);
            int costIndex = cursor.getColumnIndex(DBhelper.KEY_PRICE);

            TableLayout layOutput = findViewById(R.id.TabLay);
            layOutput.removeAllViews();
            do {
                TableRow TBrow = new TableRow(this);
                TBrow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID = new TextView(this);
                params.weight = 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                TBrow.addView(outputID);

                TextView outputNm = new TextView(this);
                params.weight = 2.0f;
                outputNm.setLayoutParams(params);
                outputNm.setText(cursor.getString(nameIndex));
                TBrow.addView(outputNm);

                TextView outputAuth = new TextView(this);
                params.weight = 2.0f;
                outputAuth.setLayoutParams(params);
                outputAuth.setText(cursor.getString(authIndex));
                TBrow.addView(outputAuth);

                TextView outStyle = new TextView(this);
                params.weight = 2.0f;
                outStyle.setLayoutParams(params);
                outStyle.setText(cursor.getString(styleIndex));
                TBrow.addView(outStyle);

                TextView outCost = new TextView(this);
                params.weight = 1.0f;
                outCost.setLayoutParams(params);
                outCost.setText(cursor.getString(costIndex));
                TBrow.addView(outCost);




                Button clearOnEl = new Button(this);
                clearOnEl.setOnClickListener(this);
                params.weight = 1.0f;
                clearOnEl.setLayoutParams(params);
                clearOnEl.setText("Удоли");
                clearOnEl.setId(cursor.getInt(idIndex));
                TBrow.addView(clearOnEl);

                layOutput.addView(TBrow);


            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.AddBut:
                String name = NameET.getText().toString();
                String auth = AuthorET.getText().toString();
                String style = StyleET.getText().toString();
                String cost = CostET.getText().toString();


                contentValues = new ContentValues();
                if (name.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Необходимо название товара...", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    contentValues.put(DBhelper.KEY_NAME, name);
                    contentValues.put(DBhelper.KEY_MAKER, auth);
                    contentValues.put(DBhelper.KEY_CITY, style);
                    contentValues.put(DBhelper.KEY_PRICE, cost);

                    database.insert(DBhelper.TABLE_CONTACTS, null, contentValues);
                }

                UpdateTable();
                NameET.setText(null);
                AuthorET.setText(null);
                StyleET.setText(null);
                CostET.setText(null);
                break;


            case R.id.ClearBut:
                database.delete(DBhelper.TABLE_CONTACTS, null, null);
                TableLayout layOutput = findViewById(R.id.TabLay);
                layOutput.removeAllViews();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();

                database.delete(DBhelper.TABLE_CONTACTS, DBhelper.KEY_ID + " = ?", new String[]{String.valueOf((v.getId()))});
                contentValues = new ContentValues();
                Cursor cursorupd = database.query(DBhelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorupd.moveToFirst()) {
                    int idIndex = cursorupd.getColumnIndex(DBhelper.KEY_ID);
                    int nameIndex = cursorupd.getColumnIndex(DBhelper.KEY_NAME);
                    int authIndex = cursorupd.getColumnIndex(DBhelper.KEY_MAKER);
                    int styleIndex = cursorupd.getColumnIndex(DBhelper.KEY_CITY);
                    int costIndex = cursorupd.getColumnIndex(DBhelper.KEY_PRICE);
                    int realId = 1;
                    do {
                        if (cursorupd.getInt(idIndex) > realId) {
                            contentValues.put(dBhelper.KEY_ID, realId);
                            contentValues.put(DBhelper.KEY_NAME, cursorupd.getString(nameIndex));
                            contentValues.put(DBhelper.KEY_MAKER, cursorupd.getString(authIndex));
                            contentValues.put(DBhelper.KEY_CITY, cursorupd.getString((styleIndex)));
                            contentValues.put(DBhelper.KEY_PRICE, cursorupd.getString((costIndex)));
                            database.replace(DBhelper.TABLE_CONTACTS, null, contentValues);
                        }
                        realId++;
                    } while (cursorupd.moveToNext());
                    if (cursorupd.moveToLast() && v.getId()!=realId) {
                        database.delete(DBhelper.TABLE_CONTACTS, DBhelper.KEY_ID + " = ?", new String[]{cursorupd.getString(idIndex)});
                    }
                }
                break;
        }
    }


}