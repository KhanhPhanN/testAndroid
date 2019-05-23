package com.example.enterc.workmanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class CompleteJob extends AppCompatActivity {
    ImageView today_complete;
    TextView day_complete, havenot;
    ListView list_complete;
    Button unfinshied, back_complete;
    Database database;
    JobAdapter jobAdapter;
    List<Job> model, choose;
    List<String> filter;
    Spinner filter_complete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_job);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đã hoàn thành"); //Thiết lập tiêu đề nếu muốn
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setLogo(R.drawable.list);    //Icon muốn hiện thị
//        actionBar.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        today_complete = findViewById(R.id.image_complete);
        day_complete   = findViewById(R.id.day_complete);
        list_complete  = findViewById(R.id.list_complete);
        unfinshied     = findViewById(R.id.unfinished);
        havenot        = findViewById(R.id.havenotdone1);
//        back_complete  = findViewById(R.id.back_complete);
        filter_complete = findViewById(R.id.filter_complete);
        database       = new Database(CompleteJob.this, "database", null, 1);
        model          = new ArrayList<>();
        choose         = new ArrayList<>();
        Intent intent  = getIntent();
        Bundle bundle  = intent.getBundleExtra("Data");
        final String day     = bundle.getString("Day");
        day_complete.setText(day);
        today_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                final int day           = calendar.get(Calendar.DATE);
                final int month         = calendar.get(Calendar.MONTH);
                final int year          = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CompleteJob.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        day_complete.setText(simpleDateFormat.format(calendar.getTime()));
                        getData(day_complete.getText().toString());
                    }
                }, year,month,day);
                datePickerDialog.show();
            }
        });
        filter = new ArrayList<>();
        filter.add("Tất cả");
        filter.add("Cuộc họp");
        filter.add("Du lịch");
        filter.add("Sinh nhật");
        filter.add("Cà phê");
        filter.add("Hằng ngày");
        filter.add("Khác");


        //ArrayAdapter<String> adapterfilter = new ArrayAdapter(CompleteJob.this, android.R.layout.simple_spinner_item,filter);
        SpinnerAdapter adapterfilter = new SpinnerAdapter(CompleteJob.this, R.layout.layout_spinner, filter);
        adapterfilter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        filter_complete.setAdapter(adapterfilter);
        filter_complete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//           Toast.makeText(MainActivity.this,i+"",Toast.LENGTH_SHORT).show();
                if(i==0){
                    getData(day_complete.getText().toString());
                }else{
                    filter(day_complete.getText().toString(), filter.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
       unfinshied.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder builder = new AlertDialog.Builder(CompleteJob.this);
               builder.setMessage("Bạn có chắc chắn tất cả công việc trong danh sách trên đều chưa hoàn thành không?");
               builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       for(Job i: choose){
                           i.setComplete(!i.isComplete());
                           database.SQLQuery("DELETE FROM HoanThanh WHERE Id  = '"+i.getId()+"'");
                           //Job job = new Job(date, time_start, time_end, sub, cont, false);
                           String query = "INSERT INTO CongViec VALUES(null,'"+i.getDate()+"','"+i.getTime_start()+"','"+i.getTime_end()+"','"+i.getSubject()+"','"+i.getContent()+"','"+i.isComplete()+"')";
                           database.SQLQuery(query);
                       }

                       getData(day_complete.getText().toString());
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
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: {
                Intent intent1 = new Intent(CompleteJob.this, MainActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);
            }

        }
        return super.onOptionsItemSelected(item);
    }
    public void getData(String s){
        Cursor cursor = database.SQLSelect("SELECT * FROM HoanThanh");
        model.clear();
        while (cursor.moveToNext()) {
            if(cursor.getString(6).equals("false"))
                model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));
            if(cursor.getString(6).equals("true"))
                model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));
        }
        JobInDay(s);
        jobAdapter = new JobAdapter(CompleteJob.this, R.layout.done_row, choose);
        list_complete.setAdapter(jobAdapter);
        if(choose.size()==0){
            havenot.setVisibility(View.VISIBLE);
        }else
            havenot.setVisibility(View.INVISIBLE);
    }
    // danh sách công việc theo ngày
    public void JobInDay(String day){
        choose.clear();
        //jobAdapter.clear();
        for(Job i: model){
            Log.d("AAAA", i.toString());
            if(i.getDate().equals(day)){
                choose.add(i);
                //jobAdapter.notifyDataSetChanged();
            }
        }
        Collections.sort(choose, new compareToJob());
    }
    // Lọc dữ liệu theo subject
    public void filter(String day, String f){
        choose.clear();
        for(Job i: model){
            if(i.getDate().equals(day) && i.getSubject().equals(f)){
                Log.d("XXX",i.toString());
                choose.add(i);
                //jobAdapter.notifyDataSetChanged();
            }
        }
        Collections.sort(choose, new compareToJob());
        jobAdapter = new JobAdapter(CompleteJob.this, R.layout.job_row, choose);
        list_complete.setAdapter(jobAdapter);
    }
    // Tạo holder cho listView
    class JobHolder {
        TextView time_detail;
        TextView subject_complete;
        ImageView type_complete, call_back, important;
    }
    // Tạo Adaptẻ cho listview
    class JobAdapter extends ArrayAdapter<Job>{

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
                jobHolder.type_complete   = row.findViewById(R.id.type_complete);
                jobHolder.call_back       = row.findViewById(R.id.come_back);
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
                jobHolder.time_detail.setText(job.getTime_start().toString() + "--" + job.getTime_end().toString());
                jobHolder.subject_complete.setText(job.getSubject());
                jobHolder.call_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {                        AlertDialog.Builder builder = new AlertDialog.Builder(CompleteJob.this);
                        builder.setMessage("Bạn có chắc chắn chưa hoàn thành công việc này không");
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.SQLQuery("DELETE FROM HoanThanh WHERE Id  = '"+job.getId()+"'");
                                //Job job = new Job(date, time_start, time_end, sub, cont, false);
                                String query = "INSERT INTO CongViec VALUES(null,'"+job.getDate()+"','"+job.getTime_start()+"','"+job.getTime_end()+"','"+job.getSubject()+"','"+job.getContent()+"','"+job.isComplete()+"')";
                                database.SQLQuery(query);
                                getData(day_complete.getText().toString());
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
                                Intent intentAlarm = new Intent(CompleteJob.this,AlarmRecevier.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("nameSubject",job.getSubject());
                                intentAlarm.putExtra("nameBundle", bundle);
                                pendingIntent = PendingIntent.getBroadcast(CompleteJob.this, codePending, intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT);
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
    class SpinnerHolder {
        TextView name;
    }
    class SpinnerAdapter extends ArrayAdapter<String>{
        public SpinnerAdapter(@NonNull Context context, int resource, List<String> list) {
            super(context, resource, list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            SpinnerHolder spinnerHolder;
            if(convertView==null){
                spinnerHolder = new SpinnerHolder();
                LayoutInflater inflater = getLayoutInflater();
                convertView   = inflater.inflate(R.layout.layout_spinner, null);
                spinnerHolder.name = convertView.findViewById(R.id.item_spinner);
                spinnerHolder.name.setText(filter.get(position));
                convertView.setTag(spinnerHolder);
            }else
                convertView.getTag();
            return convertView;
        }
    }
}
