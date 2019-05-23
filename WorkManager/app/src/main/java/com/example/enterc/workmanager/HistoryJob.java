package com.example.enterc.workmanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HistoryJob extends AppCompatActivity {
    List<Job> choose;
    Database database;
    JobAdapter jobAdapter;
    ListView listView;
    Button del_history;
    TextView havenotdone;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: {
                Intent intent1 = new Intent(HistoryJob.this, MainActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);
            }

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_job);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Lịch sử"); //Thiết lập tiêu đề nếu muốn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        choose = new ArrayList<>();
        database = new Database(this, "database", null, 1);
        listView = findViewById(R.id.list_history);
        del_history = findViewById(R.id.history_del);
        havenotdone = findViewById(R.id.havenotdone);
        getData();
        del_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Job i: choose){
                    database.SQLQuery("DELETE FROM History WHERE Id  = '"+i.getId()+"'");
                }
                getData();
            }
        });
    }
    class JobHolder {
        TextView time_detail;
        TextView subject_complete;
        ImageView type_complete, call_back, important;
    }

    // Tạo Adaptẻ cho listview
    class JobAdapter extends ArrayAdapter<Job> {

        public JobAdapter(Context context, int layout, List<Job> list) {
            super(context, layout,list);
            // super(MainActivity.this, R.layout.job_row, model);

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            JobHolder jobHolder;
            View row = convertView;
            if(row==null){
                LayoutInflater inflater  = getLayoutInflater();
                row                      = inflater.inflate(R.layout.done_row, parent, false);
                jobHolder                = new JobHolder();
                jobHolder.subject_complete            = row.findViewById(R.id.subject_complete);
                jobHolder.time_detail    = row.findViewById(R.id.time_detail_complete);
                jobHolder.type_complete  = row.findViewById(R.id.type_complete);
                jobHolder.call_back      = row.findViewById(R.id.come_back);
                jobHolder.important      = row.findViewById(R.id.done_important);
                final Job job            = choose.get(position);
                switch (job.getSubject()){
                    case "Cuộc họp" :   jobHolder.type_complete.setImageResource(R.drawable.meeting); break;
                    case "Du lịch"  :   jobHolder.type_complete.setImageResource(R.drawable.travel); break;
                    case "Sinh nhật":   jobHolder.type_complete.setImageResource(R.drawable.birth); break;
                    case "Cà phê"   :   jobHolder.type_complete.setImageResource(R.drawable.tea); break;
                    case "Hằng ngày":   jobHolder.type_complete.setImageResource(R.drawable.daily); break;
                    case "Khác"     :   jobHolder.type_complete.setImageResource(R.drawable.diference); break;
                }
                if(job.isComplete())
                {
                    jobHolder.important.setImageResource(R.drawable.importantyellow);
                }
                else {
                    jobHolder.important.setImageResource(R.drawable.important);
                }
                jobHolder.time_detail.setText(job.getTime_start().toString() + "--" + job.getTime_end().toString() + "\n" + job.getDate());
                jobHolder.time_detail.setTextSize(15);
                jobHolder.subject_complete.setText(job.getSubject());
                jobHolder.call_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryJob.this);
                        builder.setMessage("Bạn có chắc chắn chưa hoàn thành công việc này không");
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.SQLQuery("DELETE FROM History WHERE Id  = '"+job.getId()+"'");
                                String query = "INSERT INTO CongViec VALUES(null,'"+job.getDate()+"','"+job.getTime_start()+"','"+job.getTime_end()+"','"+job.getSubject()+"','"+job.getContent()+"','"+job.isComplete()+"')";
                                database.SQLQuery(query);
                                getData();
                                Calendar calendar = Calendar.getInstance();
                                AlarmManager alarmManager;
                                PendingIntent pendingIntent;
                                String[] arrDay = job.getDate().split("/");
                                String[] arrTime = job.getTime_start().split(":");
                                final int y = Integer.parseInt(arrDay[2]);
                                final int m = Integer.parseInt(arrDay[1])-1;
                                final int d = Integer.parseInt(arrDay[0]);
                                int h       = Integer.parseInt(arrTime[0]);
                                int mi      = Integer.parseInt(arrTime[1]);
                                calendar.set(y,m,d,h,mi);
                                int codePending = Integer.parseInt(arrDay[0]+arrDay[1]+arrTime[0]+arrTime[1]);
                                // Thông báo công việc gần nhất
                                alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
                                Intent intentAlarm = new Intent(HistoryJob.this,AlarmRecevier.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("nameSubject",job.getSubject());
                                intentAlarm.putExtra("nameBundle", bundle);
                                pendingIntent = PendingIntent.getBroadcast(HistoryJob.this, codePending, intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);
                            }
                        });
                        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                });
                row.setTag(jobHolder);
            }else{
                row.getTag();
            }
            return row;
        }
    }
    public void getData(){
        Cursor cursor = database.SQLSelect("SELECT * FROM History");
        choose.clear();
        while (cursor.moveToNext()) {
            if(cursor.getString(6).equals("false"))
                choose.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));
            if(cursor.getString(6).equals("true"))
                choose.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));
        }
        Collections.reverse(choose);
        jobAdapter = new JobAdapter(this, R.layout.done_row, choose);
        listView.setAdapter(jobAdapter);
        if(choose.size()==0){
            havenotdone.setVisibility(View.VISIBLE);
        }else
            havenotdone.setVisibility(View.INVISIBLE);
    }
}
