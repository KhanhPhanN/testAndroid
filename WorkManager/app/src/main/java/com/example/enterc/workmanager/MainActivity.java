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
    AlarmNotification alarmNotification;
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
        final Dialog dialog = new Dialog(this);// Khai báo 1 cửa sổ để thêm công việc
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Bỏ tiêu đề cho cửa sổ này
        dialog.setContentView(R.layout.add_work);// Tham chiếu đến layout add_work
        dialog.show();// Hiển thị cửa sổ
        ImageView calender             = dialog.findViewById(R.id.add_calender);   // Tham chiếu đến hình ảnh lịch
        ImageView time_start           = dialog.findViewById(R.id.add_time_start); // Tham chiếu đến hình ảnh đồng hồ
        ImageView time_end             = dialog.findViewById(R.id.add_time_end);   // Tham chiếu đến hình ảnh đồng hồ
        final EditText show_calender   = dialog.findViewById(R.id.show_calender);  // Tham chiếu đến ô nhập ngày
        final EditText show_time_start = dialog.findViewById(R.id.show_time_start);// Tham chiếu đến ô nhập thời gian bắt đầu
        final EditText show_time_end   = dialog.findViewById(R.id.show_time_end);  // Tham chiếu đến ô nhập thời gian kết thúc
        final ImageView subject        = dialog.findViewById(R.id.imagesub);       // Tham chiếu đến hình ảnh tương ứng cho các chủ đề
        final Spinner spinner          = dialog.findViewById(R.id.item_subject);   // Tham chiếu đến spinner để lựa chọn chủ đề
        final EditText content         = dialog.findViewById(R.id.add_content);    // Tham chiếu đến ô nhập nội dung chi tiết công việc
        Button save                    = dialog.findViewById(R.id.save);           // Tham chiếu đến nút dùng để lưu công việc
        Button cancel                  = dialog.findViewById(R.id.cancel);         // Tham chiếu đến nút dùng để tắt cửa sổ thêm và không làm gì cả
        final ImageView add_important  = dialog.findViewById(R.id.add_important);  // Tham chiếu đến hình ảnh để đánh dấu mức độ quan trọng công việc
        list.clear();           // Xóa mảng list
        list.add("Cuộc họp");   // Thêm chủ đề cuộc họp
        list.add("Du lịch");    // Thêm chủ đề du lịch
        list.add("Sinh nhật");  // Thêm chủ đề sinh nhật
        list.add("Cà phê");     // Thêm chủ đề cà phê
        list.add("Hằng ngày");  // Thêm chủ đề hằng ngày
        list.add("Khác");       // Thêm chủ đề khác
        SpinnerAdapterA adapter = new SpinnerAdapterA(MainActivity.this, R.layout.layout_spinner, list);// Khai báo 1 apdapter sử dụng cho Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);                       // Khai báo kiểu danh sách cho Spinner
        spinner.setAdapter(adapter); // áp dụng adapter cho Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {// Đặt sự kiện cho Spinner khi chọn 1 phần tử trong danh sách
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:   subject.setImageResource(R.drawable.meeting); break;   // Nếu lựa chọn chủ đề là cuộc họp
                    case 1:   subject.setImageResource(R.drawable.travel); break;    // Nếu lựa chọn chủ đề là du lịch
                    case 2:   subject.setImageResource(R.drawable.birth); break;     // Nếu lựa chọn chủ đề là sinh nhật
                    case 3:   subject.setImageResource(R.drawable.tea); break;       // Nếu lựa chọn chủ đề là cà phê
                    case 4:   subject.setImageResource(R.drawable.daily); break;     // Nếu lựa chọn chủ đề là hằng ngày
                    default:  subject.setImageResource(R.drawable.diference); break; // Nếu lựa chọn chủ đề là khác
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {// Đặt sự kiện nếu không chọn phần tử nào trong Spinner

            }
        });
        final Calendar calendar = Calendar.getInstance();    // Khai báo 1 đối tượng Calendar để lấy danh sách ngày
        show_calender.setText(dayshow.getText().toString()); // Hiển thị ngày mặc định
        // chọn lịch
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện khi chọn ngày
                int day   = calendar.get(Calendar.DATE);  // Lấy ngày hiện tại
                int month = calendar.get(Calendar.MONTH); // Lấy tháng hiện tại
                int year  = calendar.get(Calendar.YEAR);  // Lấy năm hiện tại
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() { // Đặt sự kiện chọn ngày qua đối tượng DatePickerDialog
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth); // Hiển thị ngày trong lịch là ngày tháng năm hiện tại của hệ thống
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Định dạng lại cách hiển thị ngày
                        show_calender.setText(simpleDateFormat.format(calendar.getTime())); // Hiển thị ngày được chọn trên ô nhập ngày
                    }
                }, year, month, day);
                datePickerDialog.show();// Hiển thị lịch cho người dùng thực hiện các sự kiện được đặt ở trên
            }
        });
        // chọn thời gian bắt đầu
        final Calendar[] calendarOne = new Calendar[1];           // Khai báo đối tượng Calendar toàn cục trong hàm để sử dụng cho thông báo
        time_start.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện cho việc chọn thời gian bắt đầu
            @Override
            public void onClick(View v) {
                calendarOne[0]       = Calendar.getInstance();// Gán giá trị cho đối tượng Calendar
                String[] arr         = show_calender.getText().toString().split("/"); // Cắt chuỗi để lấy giá trị ngày tháng năm hiện tại được chọn
                final int y          = Integer.parseInt(arr[2]);   // Lấy năm đang được chọn
                final int m          = Integer.parseInt(arr[1])-1; // Lấy tháng đang được chọn
                final int d          = Integer.parseInt(arr[0]);   // Lấy ngày đang được chọn
                final int hour_start = calendarOne[0].get(Calendar.HOUR_OF_DAY); // Lấy giờ trong ngày
                int minute_start     = calendarOne[0].get(Calendar.MINUTE);      // Lấy phút trong ngày
                TimePickerDialog datePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override// Đặt sự kiện lấy thời gian qua TimePickerDialog
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendarOne[0].set(y, m, d, hourOfDay, minute); // Đặt thời gian mặc định là giờ, phút của ngày tháng năm được chọn
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm"); // Định dạng giờ và phút hiển thị
                        show_time_start.setText(simpleDateFormat.format(calendarOne[0].getTime())); // Hiển thị thời gian trên ô bắt đầu
                    }
                }, hour_start, minute_start, true);
                datePickerDialog.show(); // Hiển thị TimePickerDialog để người dùng thực hiện các sự kiện
            }
        });
        // chọn thời gian kết thúc
        time_end.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện chọn thời gian kết thúc
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();             // Khai báo và gán giá trị cho 1 đối tượng Calendar
                final int hour_start    = calendar.get(Calendar.HOUR_OF_DAY); // Lấy giờ hiện tại trong ngày
                int minute_start        = calendar.get(Calendar.MINUTE);      // Lấy phút hiện tại trong ngày
                TimePickerDialog datePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(0, 0, 0, hourOfDay, minute);// Đặt thời gian mặc định là giờ, phút hiện tại cảu hệ thống
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm"); // Định dạng lại giờ và phút hiển thị
                        show_time_end.setText(simpleDateFormat.format(calendar.getTime())); // Hiển thị thời gian được chọn lên ô kết thúc
                    }
                }, hour_start, minute_start, true);
                datePickerDialog.show(); // Hiển thị TimePickerDialog cho người dùng để thực hiện các sự kiện
            }
        });
        final boolean[] check_important = {false};// Khai báo 1 biến boolean để kiểm tra sự kiện là quan trong hay không quan trọng
        add_important.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện để kiểm tra mức độ công việc
            @Override
            public void onClick(View v) {
                check_important[0] = !check_important[0];// Tự động đảo ngược
                if(check_important[0]){// Nếu công việc là quan trọng
                    add_important.setImageResource(R.drawable.importantyellow); // Hiển thị biểu tượng quan trọng
                    Toast.makeText(MainActivity.this, "Quan trọng", Toast.LENGTH_SHORT).show(); // Thông báo ngắn cho người dùng
                }else{
                    add_important.setImageResource(R.drawable.important); // Hiển thị biểu tượng không quan trọng
                    Toast.makeText(MainActivity.this, "Không quan trọng", Toast.LENGTH_SHORT).show();// Thông báo ngắn cho người dùng
                }
            }
        });
        // Lưu thông tin công việc
        save.setOnClickListener(new View.OnClickListener() { // Đặt dự kiện lưu thông tin công việc cào cơ sở dữ liệu
            @Override
            public void onClick(View v) {
                String date       = show_calender.getText().toString();  // Lấy dữ liệu tại ô chọn ngày
                String time_start = show_time_start.getText().toString();// Lấy dữ liệu tại ô thời gian bắt đầu
                String time_end   = show_time_end.getText().toString();  // Lấy dữ liệu tại ô thời gian kết thúc
                String sub        = spinner.getSelectedItem().toString();// Lấy dữ liệu phần tử của Spinner
                String cont       = content.getText().toString();        // Lấy dữ liệu nội dung chi tiết công việc
                if (!inputCheck.isValidDate(date)) { // Kiểm tra ngày có hợp lệ không
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.dateformat1), Toast.LENGTH_SHORT).show(); // Nếu không sẽ thông báo
                } else if (inputCheck.isValidTime(time_start) == 1 || inputCheck.isValidTime(time_end) == 1) {     // Kiểm tra thời gian có hợp lệ không
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat1), Toast.LENGTH_SHORT).show();// Nếu không sẽ thông báo
                } else if (inputCheck.isValidTime(time_start) == 2 || inputCheck.isValidTime(time_end) == 2) { // Kiểm tra thời gian có hợp lệ không
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat2), Toast.LENGTH_SHORT).show();// Nếu không sẽ thông báo
                } else if (inputCheck.isValidTime(time_start) == 3 || inputCheck.isValidTime(time_end) == 3) {// Kiểm tra thời gian có hợp lệ không
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat3), Toast.LENGTH_SHORT).show();// Nếu không sẽ thông báo
                } else if (!inputCheck.isEndthanStart(time_start, time_end)) {// Kiểm tra thời gian có hợp lệ không
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.startthanend), Toast.LENGTH_SHORT).show();// Nếu không sẽ thông báo
                } else if (cont.length() == 0) { // Kiểm tra nội dung chi tiết không được phép để trống
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.contentWrong), Toast.LENGTH_SHORT).show(); // Nếu lỗi sẽ có thông báo
                } else {// Nếu thỏa mãn mọi điều kiện
                    String query = "INSERT INTO CongViec VALUES(null,'"+date+"','"+time_start+"','"+time_end+"','"+sub+"','"+cont+"','"+check_important[0]+"')";// Lệnh thêm công việc vào CSDL
                    database.SQLQuery(query); // Thực hiện thêm công việc vào CSDL
                    getData(dayshow.getText().toString()); // Lấy dữ liệu trong CSDL rồi đổ lên danh sách
                    // Cài đặt thông báo
                     alarmNotification.Notification(new Job(1, date, time_start,time_end,sub,cont, check_important[0]), MainActivity.this);
                    // Hết thông báo
                    dialog.dismiss();// Đóng cửa sổ thêm công việc
                }
            }
        });
        // Nhấn cancel nếu không muốn làm gì
        cancel.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện nhấn nút hủy bỏ
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Đóng cửa sổ thêm công việc
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
        alarmNotification = new AlarmNotification(MainActivity.this);
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
                                 alarmNotification.delNotification(i, MainActivity.this);
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
                                        alarmNotification.delNotification(i, MainActivity.this);
                                    }
                                }
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
                                alarmNotification.delNotification(job, MainActivity.this);
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
                                alarmNotification.delNotification(job, MainActivity.this);
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
            row.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện khi click vào 1 phần tử trong danh sách
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(MainActivity.this); // Khai náo 1 cửa sổ
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Bỏ tiêu đề
                    dialog.setContentView(R.layout.work_detail); // Tham chiếu layout work_detail đến cửa sổ này
                    TextView time            = dialog.findViewById(R.id.time); // TextView hiển thị thời gian công việc
                    final EditText detail    = dialog.findViewById(R.id.detail); // Tham chiếu đến ô hiển thị và chỉnh sửa nội dung công việc
                    Button edit              = dialog.findViewById(R.id.button2); // Tham chiếu đến nút chỉnh sửa
                    Button del               = dialog.findViewById(R.id.button3); // Tham chiếu đến nút xóa
                    Button back              = dialog.findViewById(R.id.button4); // Tham chiếu đến nút quay về
                    final Spinner spinner    = dialog.findViewById(R.id.spinner); // Tham chiếu đến Spinner
                    final ImageView  image   = dialog.findViewById(R.id.imageView2); // Tham chiếu đến hình ảnh hiển thị chủ đề công việc
                    final ImageView detail_important = dialog.findViewById(R.id.detail_important); // Tham chiếu hình ảnh hiển thị mức độ công việc
                    final Job job            = choose.get(position); // Lấy ra phần tử được chọn
                    switch (job.getSubject()){
                        case "Cuộc họp" : { list.clear();list.add("Cuộc họp");list.add("Du lịch");list.add("Sinh nhật");list.add("Cà phê");list.add("Hằng ngày");list.add("Khác"); }; break;// Gán lại mảng
                        case "Du lịch"  : { list.clear();list.add("Du lịch");list.add("Cuộc họp");list.add("Sinh nhật");list.add("Cà phê");list.add("Hằng ngày");list.add("Khác"); }; break;// Gán lại mảng
                        case "Sinh nhật": {list.clear();list.add("Sinh nhật");list.add("Cuộc họp");list.add("Du lịch");list.add("Cà phê");list.add("Hằng ngày");list.add("Khác"); }; break; // Gán lại mảng
                        case "Cà phê"   : { list.clear();list.add("Cà phê");list.add("Du lịch");list.add("Cuộc họp");list.add("Sinh nhật");list.add("Hằng ngày");list.add("Khác"); }; break;// Gán lại mảng
                        case "Hằng ngày": {list.clear();list.add("Hằng ngày");list.add("Cuộc họp");list.add("Du lịch");list.add("Sinh nhật");list.add("Cà phê");list.add("Khác");}; break;  // Gán lại mảng
                        case "Khác": {list.clear();list.add("Khác");list.add("Cuộc họp");list.add("Du lịch");list.add("Sinh nhật");list.add("Cà phê");list.add("Hằng ngày");}; break;       // Gán lại mảng
                    };
                    time.setText(job.getTime_start()+"--"+job.getTime_end()+"\n"+job.getDate());// Hiển thị thời gian của công việc được chọn
                    detail.setText(job.getContent()); // Hiển thị nội dung công việc
                    if(job.isComplete()){// Kiểm tra có phải là công việc quan trọng hay không
                        detail_important.setImageResource(R.drawable.importantyellow); // Nếu có hiển thị biểu tượng công việc quan trọng
                    }else{
                        detail_important.setImageResource(R.drawable.important); // Nếu không hiển thị biểu tượng công việc không quan trọng
                    }
                   SpinnerAdapterA adapter = new SpinnerAdapterA(MainActivity.this, R.layout.layout_spinner, list);// Khai báo adapter để có thể chọn chủ đề
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice); // Khai báo kiểu danh sách cho Spinner
                    spinner.setAdapter(adapter); // Áp dụng adapter cho Spinner
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {// Đặt sự kiện khi chọn 1 phần tử trong Spinner
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            switch (spinner.getSelectedItem().toString()){// Phần tử được chọn
                                case "Cuộc họp" :   image.setImageResource(R.drawable.meeting); break;  // Thay đổi hình ảnh phù hợp với chủ đề Cuộc họp
                                case "Du lịch"  :   image.setImageResource(R.drawable.travel); break;   // Thay đổi hình ảnh phù hợp với chủ đề Du lịch
                                case "Sinh nhật":   image.setImageResource(R.drawable.birth); break;    // Thay đổi hình ảnh phù hợp với chủ đề Sinh nhật
                                case "Cà phê"   :   image.setImageResource(R.drawable.tea); break;      // Thay đổi hình ảnh phù hợp với chủ đề Cà phê
                                case "Hằng ngày":   image.setImageResource(R.drawable.daily); break;    // Thay đổi hình ảnh phù hợp với chủ đề Hằng ngày
                                case "Khác"     :   image.setImageResource(R.drawable.diference); break;// Thay đổi hình ảnh phù hợp với chủ đề Khác
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {// Nếu không làm gì
                        }
                    });
                    detail_important.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện cho chức năng thay đổi mức độ của công việc
                        @Override
                        public void onClick(View v) {
                            job.setComplete(!job.isComplete());// Thay đổi ngược lại với mức độ hiện có trước đó
                            if(job.isComplete()){
                                detail_important.setImageResource(R.drawable.importantyellow);// Thay đổi sang biểu tượng quan trọng
                            }else{
                                detail_important.setImageResource(R.drawable.important); // Thay đổi sang biểu tượng không quan trọng
                            }
                        }
                    });
                    dialog.show();// Hiển thị cửa sổ cho người dùng thực hiện thao tác
                    // Chỉnh sửa
                    edit.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện chỉnh sửa công việc và lưu lại vào CSDL
                        @Override
                        public void onClick(View v) {
                            job.setContent(detail.getText().toString());// Đặt lại nội dung công việc
                            job.setSubject(spinner.getSelectedItem().toString()); // Đặt lại chủ đề công việc
                            String update = "UPDATE CongViec SET Subject = '"+spinner.getSelectedItem().toString()+"', Content='"+detail.getText().toString()+"', Complete='"+job.isComplete()+"' WHERE Id = '"+job.getId()+"'"; // Lệnh update trong CSDL
                            database.SQLQuery(update); // Thực hiện lệnh update
                            getData(dayshow.getText().toString()); // Đổ lại dữ liệu từ CSDL lên danh sách
                            dialog.dismiss();// Đóng cửa sổ
                        }
                    });
                    // Xóa
                    del.setOnClickListener(new View.OnClickListener() { // Đặt sự kiện xóa công việc
                        @Override
                        public void onClick(View v) {
                            String delete = "DELETE FROM CongViec WHERE Id  = '"+job.getId()+"'";// Lệnh xóa dữ liệu trong CSDL
                            database.SQLQuery(delete); // Thực hiện lệnh xóa
                            getData(dayshow.getText().toString()); // Lấy dữ liệu từ CSDL và đổ lại lên danh sách
                            alarmNotification.delNotification(job,MainActivity.this); // Xóa thông báo công việc
                            dialog.dismiss(); // Đóng cửa sổ
                        }
                    });
                    // Quay lại
                    back.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện cho chức năng quay lại và không làm gì cả
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();// Đóng cửa sổ
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
}

