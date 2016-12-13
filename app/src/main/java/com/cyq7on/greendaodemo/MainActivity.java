package com.cyq7on.greendaodemo;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cyq7on.greendao.UserDao;
import com.cyq7on.greendaodemo.entity.User;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private UserDao userDao;
    private List<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CommonAdapter adapter;
    private EditText etName,etAge,etSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        etName = (EditText) findViewById(R.id.et_name);
        etAge = (EditText) findViewById(R.id.et_age);
        etSex = (EditText) findViewById(R.id.et_sex);
        Button btnInsert = (Button) findViewById(R.id.btn_insert);
        btnInsert.setOnClickListener(this);
        Button btnDelete = (Button) findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);
        Button btnQuery = (Button) findViewById(R.id.btn_query);
        btnQuery.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new CommonAdapter<User>(this,R.layout.item,userList) {
            @Override
            protected void convert(ViewHolder holder, User user, int position) {
                holder.setText(R.id.tv_id,String.valueOf(user.getId()));
                holder.setText(R.id.tv_name,user.getName());
                holder.setText(R.id.tv_age,user.getAge());
                holder.setText(R.id.tv_sex,user.getSex());
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                showFragment(position);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userDao = MyApplication.getContext().getDaoSession().getUserDao();
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_insert:
                insert();
                break;
            case R.id.btn_delete:
                delete();
                break;
            case R.id.btn_query:
                query();
                break;
            default:
                break;
        }
    }


    private void insert() {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String sex = etSex.getText().toString().trim();
        User user = new User(null,name,age,sex);
        userDao.insert(user);
        userList.add(user);
        adapter.notifyDataSetChanged();
    }

    private void delete() {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String sex = etSex.getText().toString().trim();
        StringBuilder sql = new StringBuilder("delete from user");
        if(name.length() > 0){
            sql.append(" where name = '" + name + "'");
        }else {
            sql.append(" where name like '" + "%" + "'");
        }
        if(age.length() > 0){
            sql.append(" and age = '" + age + "'");
        }
        if(sex.length() > 0){
            sql.append(" and sex = '" + sex + "'");
        }
        SQLiteDatabase db = MyApplication.getContext().getSqLiteDatabase();
        db.execSQL(sql.toString());
        userList.clear();
        userList.addAll(userDao.loadAll());
        adapter.notifyDataSetChanged();
    }

    private void update(int index,String name,String age,String sex) {
        Long id = userList.get(index).getId();
        StringBuilder sql = new StringBuilder("update user");
        sql.append(" set name = '" + name + "',");
        sql.append(" age = '" + age + "',");
        sql.append(" sex = '" + sex + "'");
        sql.append(" where _id = '" + id + "'");
        SQLiteDatabase db = MyApplication.getContext().getSqLiteDatabase();
        db.execSQL(sql.toString());
        Log.d("sql",sql.toString());
        //只有第一次修改，界面会刷新
        /*userList.clear();
        userList.addAll(userDao.loadAll());
        adapter.notifyDataSetChanged();*/
        User user = new User(id,name,age,sex);
        userList.remove(index);
        userList.add(index,user);
        adapter.notifyItemChanged(index);
    }


    private void showFragment(final int index) {
        final View v = getLayoutInflater().inflate(R.layout.content_edit,null);
        final EditText etName = (EditText) v.findViewById(R.id.et_name);
        final EditText etAge = (EditText) v.findViewById(R.id.et_age);
        final EditText etSex = (EditText) v.findViewById(R.id.et_sex);

        User user = userList.get(index);
        etName.setText(user.getName());
        etAge.setText(user.getAge());
        etSex.setText(user.getSex());
        AlertDialog dialog = new AlertDialog.Builder(this).setView(v)
                .setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = etName.getText().toString().trim();
                        String age = etAge.getText().toString().trim();
                        String sex = etSex.getText().toString().trim();
                        update(index,name,age,sex);
                    }
                }).setNegativeButton("取消",null).create();
        dialog.show();
    }

    private void query() {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String sex = etSex.getText().toString().trim();
        StringBuilder sql = new StringBuilder("select * from user");
        if(name.length() > 0){
            sql.append(" where name = '" + name + "'");
        }else {
            sql.append(" where name like '" + "%" + "'");
        }
        if(age.length() > 0){
            sql.append(" and age = '" + age + "'");
        }
        if(sex.length() > 0){
            sql.append(" and sex = '" + sex + "'");
        }
        SQLiteDatabase db = MyApplication.getContext().getSqLiteDatabase();
        Cursor cursor = db.rawQuery(sql.toString(),null);
        userList.clear();
        while (cursor.moveToNext()) {
            String[] columnNames = cursor.getColumnNames();
            Log.d("test", Arrays.toString(columnNames));
            Long id = cursor.getLong(cursor.getColumnIndex("_id"));
            name = cursor.getString(cursor.getColumnIndex("NAME"));
            age = cursor.getString(cursor.getColumnIndex("AGE"));
            sex = cursor.getString(cursor.getColumnIndex("SEX"));
            User user = new User(id,name,age,sex);
            userList.add(user);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
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
