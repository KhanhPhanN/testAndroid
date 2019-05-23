package com.example.enterc.workmanager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.nio.BufferUnderflowException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    JobAdapter jobAdapter;
    List<Job> model, choose = new ArrayList<>();
    ListView listJobs;
    InputCheck inputCheck;
    ImageView dayCalender, addJob, importantFilter;
    TextView dayshow, label_today, havenot;
    Database database;
    Spinner filter;
    List<String> list, filterl;
    Button del_all, done_all;
    int check_im = 3;
    int CODE = 0;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.option,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        if(item.getItemId()==R.id.completeJob){
            Intent intent = new Intent(MainActivity.this,CompleteJob.class);
            Bundle bundle = new Bundle();
            bundle.putString("Day",dayshow.getText().toString());
            intent.putExtra("Data",bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);
        }
        if(item.getItemId()==R.id.history){
            Intent intent = new Intent(MainActivity.this,HistoryJob.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);
        }
        if(item.getItemId()==R.id.achart){
            Intent intent = new Intent(MainActivity.this,ChartActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("Day",dayshow.getText().toString());
            intent.putExtra("Data",bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);
        }
        return super.onOptionsItemSelected(item);
    }
    public void addJob() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Bỏ tiêu đề
        dialog.setContentView(R.layout.add_work);
        dialog.show();
        ImageView calender = dialog.findViewById(R.id.add_calender);
        ImageView time_start = dialog.findViewById(R.id.add_time_start);
        ImageView time_end = dialog.findViewById(R.id.add_time_end);
        final EditText show_calender = dialog.findViewById(R.id.show_calender);
        final EditText show_time_start = dialog.findViewById(R.id.show_time_start);
        final EditText show_time_end = dialog.findViewById(R.id.show_time_end);
        final ImageView subject = dialog.findViewById(R.id.imagesub);
        final Spinner spinner                 = dialog.findViewById(R.id.item_subject);
        final EditText content = dialog.findViewById(R.id.add_content);
        Button save = dialog.findViewById(R.id.save);
        Button cancel = dialog.findViewById(R.id.cancel);
        final ImageView add_important = dialog.findViewById(R.id.add_important);
        list.clear();
        list.add("Cuộc họp");
        list.add("Du lịch");
        list.add("Sinh nhật");
        list.add("Cà phê");
        list.add("Hằng ngày");
        list.add("Khác");
        SpinnerAdapterA adapter = new SpinnerAdapterA(MainActivity.this, R.layout.layout_spinner, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                switch (i){
                    case 0:   subject.setImageResource(R.drawable.meeting); break;
                    case 1:   subject.setImageResource(R.drawable.travel); break;
                    case 2:   subject.setImageResource(R.drawable.birth); break;
                    case 3:   subject.setImageResource(R.drawable.tea); break;
                    case 4:   subject.setImageResource(R.drawable.daily); break;
                    default:  subject.setImageResource(R.drawable.diference); break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final Calendar calendar = Calendar.getInstance();
        show_calender.setText(dayshow.getText().toString());
        // chọn lịch
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        show_calender.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        // chọn thời gian bắt đầu
        final Calendar[] calendarOne = new Calendar[1];
        time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarOne[0] = Calendar.getInstance();
                String[] arr = show_calender.getText().toString().split("/");
                final int y = Integer.parseInt(arr[2]);
                final int m = Integer.parseInt(arr[1])-1;
                final int d = Integer.parseInt(arr[0]);
                final int hour_start = calendarOne[0].get(Calendar.HOUR_OF_DAY);
                int minute_start = calendarOne[0].get(Calendar.MINUTE);
                TimePickerDialog datePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendarOne[0].set(y, m, d, hourOfDay, minute);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        show_time_start.setText(simpleDateFormat.format(calendarOne[0].getTime()));
                    }
                }, hour_start, minute_start, true);
                datePickerDialog.show();
            }
        });


        // chọn thời gian kết thúc
        time_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                final int hour_start = calendar.get(Calendar.HOUR_OF_DAY);
                int minute_start = calendar.get(Calendar.MINUTE);
                TimePickerDialog datePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(0, 0, 0, hourOfDay, minute);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        show_time_end.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, hour_start, minute_start, true);
                datePickerDialog.show();
            }
        });
        final boolean[] check_important = {false};
        add_important.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_important[0] = !check_important[0];
                if(check_important[0]){
                    add_important.setImageResource(R.drawable.importantyellow);
                    Toast.makeText(MainActivity.this, "Quan trọng", Toast.LENGTH_SHORT).show();
                }else{
                    add_important.setImageResource(R.drawable.important);
                    Toast.makeText(MainActivity.this, "Không quan trọng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Lưu thông tin công việc
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = show_calender.getText().toString();
                String time_start = show_time_start.getText().toString();
                String time_end = show_time_end.getText().toString();
                String sub = spinner.getSelectedItem().toString();
                String cont = content.getText().toString();
                if (!inputCheck.isValidDate(date)) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.dateformat1), Toast.LENGTH_SHORT).show();
                } else if (inputCheck.isValidTime(time_start) == 1 || inputCheck.isValidTime(time_end) == 1) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat1), Toast.LENGTH_SHORT).show();
                } else if (inputCheck.isValidTime(time_start) == 2 || inputCheck.isValidTime(time_end) == 2) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat2), Toast.LENGTH_SHORT).show();
                } else if (inputCheck.isValidTime(time_start) == 3 || inputCheck.isValidTime(time_end) == 3) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat3), Toast.LENGTH_SHORT).show();
                } else if (!inputCheck.isEndthanStart(time_start, time_end)) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.startthanend), Toast.LENGTH_SHORT).show();
                } else if (cont.length() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.contentWrong), Toast.LENGTH_SHORT).show();
                } else {
                    String query = "INSERT INTO CongViec VALUES(null,'"+date+"','"+time_start+"','"+time_end+"','"+sub+"','"+cont+"','"+check_important[0]+"')";
                    database.SQLQuery(query);
                    getData(dayshow.getText().toString());
                    // Cài đặt thông báo
                    AlarmManager alarmManager;
                    PendingIntent pendingIntent;
                    String[] arrDay = date.split("/");
                    String[] arrTime = time_start.split(":");
                    int codePending = Integer.parseInt(arrDay[0]+arrDay[1]+arrTime[0]+arrTime[1]);
                    // Thông báo công việc gần nhất
                    alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intentAlarm = new Intent(MainActivity.this,AlarmRecevier.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("nameSubject",sub);
                    intentAlarm.putExtra("nameBundle", bundle);
                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, codePending, intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendarOne[0].getTimeInMillis(), pendingIntent);
                    // Hết thông báo
                    dialog.dismiss();
                }
            }
        });

        // Nhấn cancel nếu không muốn làm gì
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.wordmanager); //Thiết lập tiêu đề nếu muốn
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.list);    //Icon muốn hiện thị
        actionBar.setDisplayUseLogoEnabled(true);
        listJobs                = findViewById(R.id.list_jobs);
        dayCalender             = findViewById(R.id.today);
        dayshow                 = findViewById(R.id.day);
        label_today             = findViewById(R.id.label_today);
        addJob                  = findViewById(R.id.add_job);
        del_all                 = findViewById(R.id.delete_all);
        done_all                = findViewById(R.id.done_all);
        final Calendar calendar = Calendar.getInstance();
        final int day           = calendar.get(Calendar.DATE);
        final int month         = calendar.get(Calendar.MONTH);
        final int year          = calendar.get(Calendar.YEAR);
        filter                  = findViewById(R.id.filter);
        havenot                 = findViewById(R.id.havenot);
        importantFilter         = findViewById(R.id.ImportantFilter);
        importantFilter.setImageResource(R.drawable.star);
        list = new ArrayList<>();
        filterl = new ArrayList<>();
        filterl.add("Tất cả");
        filterl.add("Cuộc họp");
        filterl.add("Du lịch");
        filterl.add("Sinh nhật");
        filterl.add("Cà phê");
        filterl.add("Hằng ngày");
        filterl.add("Khác");
        SpinnerAdapter adapterfilter = new SpinnerAdapter(MainActivity.this, R.layout.layout_spinner, filterl);
        adapterfilter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        filter.setAdapter(adapterfilter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    getData(dayshow.getText().toString());
                }else{
                    filter(dayshow.getText().toString(), filterl.get(i));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dayshow.setText(simpleDateFormat.format(calendar.getTime()));
        database    = new Database(this, "database", null,1);
        database.SQLQuery("CREATE TABLE IF NOT EXISTS CongViec(Id INTEGER PRIMARY KEY AUTOINCREMENT, Date VARCHAR(30),TimeS VARCHAR(10), TimeE VARCHAR(10),Subject VARCHAR(100),Content VARCHAR(1000),Complete VARCHAR(6))");
        database.SQLQuery("CREATE TABLE IF NOT EXISTS HoanThanh(Id INTEGER PRIMARY KEY AUTOINCREMENT, Date VARCHAR(30),TimeS VARCHAR(10), TimeE VARCHAR(10),Subject VARCHAR(100),Content VARCHAR(1000),Complete VARCHAR(6))");
        database.SQLQuery("CREATE TABLE IF NOT EXISTS History(Id INTEGER PRIMARY KEY AUTOINCREMENT, Date VARCHAR(30),TimeS VARCHAR(10), TimeE VARCHAR(10),Subject VARCHAR(100),Content VARCHAR(1000),Complete VARCHAR(6))");
        model       = new ArrayList<>();
        inputCheck  = new InputCheck();
        jobAdapter  = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
        listJobs.setAdapter(jobAdapter);
        getData(dayshow.getText().toString());
        dayCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        Log.d("DAY",dayOfWeek+"");
                        dayshow.setText(simpleDateFormat.format(calendar.getTime()));
                        switch (dayOfWeek){
                            case 2: label_today.setText(R.string.monday); break;
                            case 3: label_today.setText(R.string.tuesday); break;
                            case 4: label_today.setText(R.string.wednesday); break;
                            case 5: label_today.setText(R.string.thursday); break;
                            case 6: label_today.setText(R.string.friday); break;
                            case 7: label_today.setText(R.string.saturday); break;
                            default: label_today.setText(R.string.sunday); break;
                        }

                        getData(dayshow.getText().toString());
                    }
                }, year,month,day);
                datePickerDialog.show();
            }
        });
        // Thêm công việc mới
        addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addJob();
            }
        });
        del_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose.size()==0){
                    Toast.makeText(MainActivity.this, R.string.nojob, Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.delsure);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Job i: choose){
                                database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+i.getId()+"'");
                                String query = "INSERT INTO History VALUES(null,'"+i.getDate()+"','"+i.getTime_start()+"','"+i.getTime_end()+"','"+i.getSubject()+"','"+i.getContent()+"','"+i.isComplete()+"')";
                                 database.SQLQuery(query);
                                 delNotification(i);
                            }
                            getData(dayshow.getText().toString());
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        });

        importantFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_im==1){
                    check_im=2;
                    importantFilter.setImageResource(R.drawable.important);
                    Toast.makeText(MainActivity.this, "Công việc không quan trọng", Toast.LENGTH_SHORT).show();
                    filter(dayshow.getText().toString(), filter.getSelectedItem().toString());
                } else if(check_im==2){
                    check_im=3;
                    importantFilter.setImageResource(R.drawable.star);
                    Toast.makeText(MainActivity.this, "Tất cả công việc", Toast.LENGTH_SHORT).show();
                    filter(dayshow.getText().toString(), filter.getSelectedItem().toString());
                }
                else{
                    check_im=1;
                    importantFilter.setImageResource(R.drawable.importantyellow);
                    Toast.makeText(MainActivity.this, "Công việc quan trọng", Toast.LENGTH_SHORT).show();
                    filter(dayshow.getText().toString(), filter.getSelectedItem().toString());
                }
            }
        });

        done_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose.size()==0){
                    Toast.makeText(MainActivity.this, R.string.nojob, Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.notice1);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean check = false;
                            for (Job i: choose){
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat formatday = new SimpleDateFormat("dd/MM/yyyy");
                                SimpleDateFormat formattime= new SimpleDateFormat("HH:mm");
                                String dateD            = formatday.format(c.getTime());
                                String timeD            = formattime.format(c.getTime());
                                if(dayshow.getText().toString().compareTo(dateD)>0){
                                  check = true;
                                }else{
                                    if(i.getTime_start().compareTo(timeD)>0){
                                        check = true;
                                    }else{
                                        i.setComplete(!i.isComplete());
                                        database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+i.getId()+"'");
                                        String query = "INSERT INTO HoanThanh VALUES(null,'"+i.getDate()+"','"+i.getTime_start()+"','"+i.getTime_end()+"','"+i.getSubject()+"','"+i.getContent()+"','"+i.isComplete()+"')";
                                        database.SQLQuery(query);
                                        getData(dayshow.getText().toString());
                                        delNotification(i);
                                    }
                                }
//
//
//
//                        database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+i.getId()+"'");
//                        String query = "INSERT INTO HoanThanh VALUES(null,'"+i.getDate()+"','"+i.getTime_start()+"','"+i.getTime_end()+"','"+i.getSubject()+"','"+i.getContent()+"','"+i.isComplete()+"')";
//                        database.SQLQuery(query);
//                        delNotification(i);
                            }
                            if(check){
                                Toast.makeText(MainActivity.this,"Các công việc chưa đến thời gian không thể hoàn thành", Toast.LENGTH_SHORT).show();
                            }
                            getData(dayshow.getText().toString());
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        });
    }
    // Lấy dữ liệu trong database
    public void getData(String s) {
        Log.d("BBB","ok");
        Cursor cursor = database.SQLSelect("SELECT * FROM CongViec");
        model.clear();
        while (cursor.moveToNext()) {
            if(cursor.getString(6).equals("false"))
            model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));
            if(cursor.getString(6).equals("true"))
            model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));
        }
        JobInDay(s);
        jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
        listJobs.setAdapter(jobAdapter);
        if(choose.size()==0){
           havenot.setVisibility(View.VISIBLE);
        }else {
            havenot.setVisibility(View.INVISIBLE);
        }
    }
    // danh sách công việc theo ngày
    public void JobInDay(String day){
        choose.clear();
        for(Job i: model){
            if(i.getDate().equals(day)){
                choose.add(i);
            }
        }
        Collections.sort(choose, new compareToJob());
    }
    // Lọc dữ liệu theo subject
    public void filter(String day, String f){
        choose.clear();
        if(f.equals("Tất cả")){
            if (check_im == 1) {
                for (Job i : model) {
                    if (i.getDate().equals(day) && i.isComplete()) {
                        choose.add(i);
                    }
                }
                Collections.sort(choose, new compareToJob());
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
                listJobs.setAdapter(jobAdapter);
            } else if (check_im == 2) {
                for (Job i : model) {
                    if (i.getDate().equals(day)  && !i.isComplete()) {
                        choose.add(i);
                    }
                }
                Collections.sort(choose, new compareToJob());
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
                listJobs.setAdapter(jobAdapter);
            } else {
                for (Job i : model) {
                    if (i.getDate().equals(day)) {
                        choose.add(i);
                    }
                }
                Collections.sort(choose, new compareToJob());
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
                listJobs.setAdapter(jobAdapter);
            }
        }else {
            if (check_im == 1) {
                for (Job i : model) {
                    if (i.getDate().equals(day) && i.getSubject().equals(f) && i.isComplete()) {
                        choose.add(i);
                    }
                }
                Collections.sort(choose, new compareToJob());
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
                listJobs.setAdapter(jobAdapter);
            } else if (check_im == 2) {
                for (Job i : model) {
                    if (i.getDate().equals(day) && i.getSubject().equals(f) && !i.isComplete()) {
                        choose.add(i);
                    }
                }
                Collections.sort(choose, new compareToJob());
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
                listJobs.setAdapter(jobAdapter);
            } else {
                for (Job i : model) {
                    if (i.getDate().equals(day) && i.getSubject().equals(f)) {
                        choose.add(i);
                    }
                }
                Collections.sort(choose, new compareToJob());
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
                listJobs.setAdapter(jobAdapter);
            }
        }
    }
    // Tạo holder cho listView
    class JobHolder {
        TextView time_detail;
        TextView subj;
        ImageView label_del, subrow,checkPass, important;
    }
    // Tạo Adaptẻ cho listview
    class JobAdapter extends ArrayAdapter<Job>{

        JobAdapter(Context context, int layout, List<Job> list) {
            super(context, layout,list);
            // super(MainActivity.this, R.layout.job_row, model);

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
             final JobHolder jobHolder;
             View row = convertView;
            if(row==null){
                LayoutInflater inflater  = getLayoutInflater();
                row                      = inflater.inflate(R.layout.job_row, parent, false);
                jobHolder                = new JobHolder();
                //jobHolder.sub            = row.findViewById(R.id.sub);
                jobHolder.time_detail    = row.findViewById(R.id.time_detail);
                jobHolder.subj           = row.findViewById(R.id.subj);
                jobHolder.checkPass      = row.findViewById(R.id.checkPass);
                jobHolder.label_del      = row.findViewById(R.id.label_del);
                jobHolder.subrow         = row.findViewById(R.id.imageSubrow);
                jobHolder.important      = row.findViewById(R.id.important);
                final Job job            = choose.get(position);
               // jobHolder.sub.setText(" "+job.getSubject().toString().charAt(0)+" ");
                switch (job.getSubject()){
                    case "Cuộc họp" :   jobHolder.subrow.setImageResource(R.drawable.meeting); break;
                    case "Du lịch"  :   jobHolder.subrow.setImageResource(R.drawable.travel); break;
                    case "Sinh nhật":   jobHolder.subrow.setImageResource(R.drawable.birth); break;
                    case "Cà phê"   :   jobHolder.subrow.setImageResource(R.drawable.tea); break;
                    case "Hằng ngày":   jobHolder.subrow.setImageResource(R.drawable.daily); break;
                    case "Khác"     :   jobHolder.subrow.setImageResource(R.drawable.diference); break;
                }

                jobHolder.time_detail.setText(job.getTime_start().toString() + "--" + job.getTime_end().toString());
                jobHolder.subj.setText(job.getSubject());
                if(job.isComplete())
                {
                    jobHolder.important.setImageResource(R.drawable.importantyellow);
                }
                else {
                    jobHolder.important.setImageResource(R.drawable.important);
                }
                jobHolder.checkPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat formatday = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat formattime= new SimpleDateFormat("HH:mm");
                        String dateD            = formatday.format(c.getTime());
                        String timeD            = formattime.format(c.getTime());
                        Log.d("KIEM",dateD +"\n"+timeD);
                        if(dayshow.getText().toString().compareTo(dateD)>0){
                            Toast.makeText(MainActivity.this,R.string.notice2, Toast.LENGTH_SHORT).show();
                        }else{
                            if(job.getTime_start().compareTo(timeD)>0){
                                Toast.makeText(MainActivity.this, R.string.notice3, Toast.LENGTH_SHORT).show();
                            }else{
                                //Toast.makeText(MainActivity.this,"Thành công", Toast.LENGTH_SHORT).show();
                                database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+job.getId()+"'");
                                String query = "INSERT INTO HoanThanh VALUES(null,'"+job.getDate()+"','"+job.getTime_start()+"','"+job.getTime_end()+"','"+job.getSubject()+"','"+job.getContent()+"','"+job.isComplete()+"')";
                                database.SQLQuery(query);
                                getData(dayshow.getText().toString());
                                delNotification(job);
                            }
                        }
                    }
                });
                jobHolder.important.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        job.setComplete(!job.isComplete());
                        String update = "UPDATE CongViec SET Complete = '"+job.isComplete()+"' WHERE Id = '"+job.getId()+"'";
                        database.SQLQuery(update);
                        if(job.isComplete())
                        {
                            jobHolder.important.setImageResource(R.drawable.importantyellow);
                            Toast.makeText(MainActivity.this,R.string.addbookmark, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            jobHolder.important.setImageResource(R.drawable.important);
                            Toast.makeText(MainActivity.this,R.string.delbookmark, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                jobHolder.label_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(R.string.delonejob);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+job.getId()+"'");
                                String query = "INSERT INTO History VALUES(null,'"+job.getDate()+"','"+job.getTime_start()+"','"+job.getTime_end()+"','"+job.getSubject()+"','"+job.getContent()+"','"+job.isComplete()+"')";
                                database.SQLQuery(query);
                                getData(dayshow.getText().toString());
                                delNotification(job);
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(MainActivity.this,position+"",Toast.LENGTH_SHORT).show();
                    Log.d("TEST",position+"");
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Bỏ tiêu đề
                    dialog.setContentView(R.layout.work_detail);
                   // final EditText subject   = dialog.findViewById(R.id.subject);
                    TextView time            = dialog.findViewById(R.id.time);
                    final EditText detail    = dialog.findViewById(R.id.detail);
                    Button edit        = dialog.findViewById(R.id.button2);
                    Button del         = dialog.findViewById(R.id.button3);
                    Button back        = dialog.findViewById(R.id.button4);
                    final Spinner spinner    = dialog.findViewById(R.id.spinner);
                    final ImageView  image   = dialog.findViewById(R.id.imageView2);
                    final ImageView detail_important = dialog.findViewById(R.id.detail_important);
                    final Job job            = choose.get(position);
                    switch (job.getSubject()){
                        case "Cuộc họp": {
                            list.clear();
                            list.add("Cuộc họp");
                            list.add("Du lịch");
                            list.add("Sinh nhật");
                            list.add("Cà phê");
                            list.add("Hằng ngày");
                            list.add("Khác");
                        }; break;
                        case "Du lịch": {
                            list.clear();
                            list.add("Du lịch");
                            list.add("Cuộc họp");
                            list.add("Sinh nhật");
                            list.add("Cà phê");
                            list.add("Hằng ngày");
                            list.add("Khác");
                        }; break;
                        case "Sinh nhật": {
                            list.clear();
                            list.add("Sinh nhật");
                            list.add("Cuộc họp");
                            list.add("Du lịch");
                            list.add("Cà phê");
                            list.add("Hằng ngày");
                            list.add("Khác");
                        }; break;
                        case "Cà phê": {
                            list.clear();
                            list.add("Cà phê");
                            list.add("Du lịch");
                            list.add("Cuộc họp");
                            list.add("Sinh nhật");
                            list.add("Hằng ngày");
                            list.add("Khác");
                        }; break;
                        case "Hằng ngày": {
                            list.clear();
                            list.add("Hằng ngày");
                            list.add("Cuộc họp");
                            list.add("Du lịch");
                            list.add("Sinh nhật");
                            list.add("Cà phê");
                            list.add("Khác");
                        }; break;
                        case "Khác": {
                            list.clear();
                            list.add("Du lịch");
                            list.add("Cuộc họp");
                            list.add("Sinh nhật");
                            list.add("Cà phê");
                            list.add("Hằng ngày");
                            list.add("Khác");
                        }; break;
                    };
                    //subject.setText(job.getSubject());
                    time.setText(job.getTime_start()+"--"+job.getTime_end()+"\n"+job.getDate());
                    detail.setText(job.getContent());
                    if(job.isComplete()){
                        detail_important.setImageResource(R.drawable.importantyellow);
                    }else{
                        detail_important.setImageResource(R.drawable.important);
                    }
                    //ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item,list);
                   SpinnerAdapterA adapter = new SpinnerAdapterA(MainActivity.this, R.layout.layout_spinner, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            //Toast.makeText(MainActivity.this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            switch (spinner.getSelectedItem().toString()){
                                case "Cuộc họp" :   image.setImageResource(R.drawable.meeting); break;
                                case "Du lịch"  :   image.setImageResource(R.drawable.travel); break;
                                case "Sinh nhật":   image.setImageResource(R.drawable.birth); break;
                                case "Cà phê"   :   image.setImageResource(R.drawable.tea); break;
                                case "Hằng ngày":   image.setImageResource(R.drawable.daily); break;
                                case "Khác"     :   image.setImageResource(R.drawable.diference); break;
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    detail_important.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            job.setComplete(!job.isComplete());
                            if(job.isComplete()){
                                detail_important.setImageResource(R.drawable.importantyellow);
                            }else{
                                detail_important.setImageResource(R.drawable.important);
                            }
                        }
                    });
                    dialog.show();
                    // Chỉnh sửa
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            job.setContent(detail.getText().toString());
                            job.setSubject(spinner.getSelectedItem().toString());
                            String update = "UPDATE CongViec SET Subject = '"+spinner.getSelectedItem().toString()+"', Content='"+detail.getText().toString()+"', Complete='"+job.isComplete()+"' WHERE Id = '"+job.getId()+"'";
                            database.SQLQuery(update);
                            getData(dayshow.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    // Xóa
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String delete = "DELETE FROM CongViec WHERE Id  = '"+job.getId()+"'";
                            database.SQLQuery(delete);
                            getData(dayshow.getText().toString());
                            delNotification(job);
                            dialog.dismiss();
                        }
                    });
                    // Quay lại
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            return row;
        }
    }
    class SpinnerHolder {
        TextView name;
    }
    class SpinnerAdapter extends ArrayAdapter<String>{
        public SpinnerAdapter(@NonNull Context context, int resource, List<String> list_s) {
            super(context, resource, list_s);
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
                spinnerHolder.name.setText(filterl.get(position));
                convertView.setTag(spinnerHolder);
            }else
                convertView.getTag();
            return convertView;
        }
    }
    class SpinnerAdapterA extends ArrayAdapter<String>{
        public SpinnerAdapterA(@NonNull Context context, int resource, List<String> list_s) {
            super(context, resource, list_s);
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
                spinnerHolder.name.setText(list.get(position));
                convertView.setTag(spinnerHolder);
            }else
                convertView.getTag();
            return convertView;
        }
    }
    // Hàm hủy thông báo khi xóa hoặc đánh dấu hoàn thành công việc
    public void delNotification(Job job){
        AlarmManager alarmManager;
        PendingIntent pendingIntent;
        // Hủy thông báo công việc
        String[] arrDay  = job.getDate().split("/");
        String[] arrTime = job.getTime_start().split(":");
        int code         = Integer.parseInt(arrDay[0]+arrDay[1]+arrTime[0]+arrTime[1]);
        alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentAlarm = new Intent(MainActivity.this,AlarmRecevier.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, code, intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}

