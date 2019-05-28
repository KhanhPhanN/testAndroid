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
    JobAdapter jobAdapter; // Khai báo đối tượng Adapter cho danh sách công việc
    List<Job> model, choose = new ArrayList<>(); // model dùng để lưu tất cả công việc, choose dùng để lưu các công việc được lựa chọn hiển thị
    ListView listJobs;// Khai báo đổi tượng hiển thị danh sách
    InputCheck inputCheck;// Khai báo đối tượng kiểm tra đầu vào khi thêm công việc
    ImageView dayCalender, addJob, importantFilter;// dayCalender dùng để chọn ngày hiển thị, addJob dùng để thêm công việc mới, importantFilter dùng để chọn xem các công viêc quan trọng hoặc không
    TextView dayshow, label_today, havenot;// dayshow dùng để hiển thị ngày được chọn, label_today dùng để hiển thị thứ của ngày được chọn, havenot dùng để hiển thị khi không có công việc nào
    Database database; // Tạo đối tượng để dử dụng các lệnh truye vấn trong cơ sở dữ liệu
    Spinner filter; // Sử dụng Spinner để sử dụng bộ lọc
    List<String> list, filterl; // Tạo danh sách sử dụng cho Spinner
    Button del_all, done_all; // del_all để xóa tất cả công việc, done_all để hoàn thành tất cả công việc
    int check_im = 3; // Kiểm tra mức độ quan trọng của công việc
    AlarmNotification alarmNotification; // Tạo đối tượng để sử dụng các hàm thông báo và hủy
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// Hiển thị menu cho ứng dụng
        new MenuInflater(this).inflate(R.menu.option,menu);// Tham chiếu đến file menu
        return super.onCreateOptionsMenu(menu);// Trả về giá trị nếu có menu trong file menu trong thư mục res
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {// Đặt chức năng cho từng menu
        if(item.getItemId()==R.id.completeJob){// Chức năng xem công việc đã hoàn thành
            Intent intent = new Intent(MainActivity.this,CompleteJob.class);// Tạo intent để chuyển sang màn hình xem danh sách công việc đã hoàn thành
            Bundle bundle = new Bundle(); // Tạo Bundle để chứa dữ liệu
            bundle.putString("Day",dayshow.getText().toString());// thêm dữ liệu vào bundle, ở đây là ngày muốn hiển thị công việc hoàn thành
            intent.putExtra("Data",bundle);// Thêm bundle vào intent
            startActivity(intent);// Thực hiện chuyển màn hình
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);// Tạo hiệu ứng cho chuyển màn hình
        }
        if(item.getItemId()==R.id.history){// Chức năng xem lịch sử xóa các công việc
            Intent intent = new Intent(MainActivity.this,HistoryJob.class);// Tạo intent để chuyển sang màn hình xem lịch sử công việc
            startActivity(intent);// Thực hiện chuyển màn hình
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);// Tạo hiệu ứng cho chuyển màn hình
        }
        if(item.getItemId()==R.id.achart){// Chức năng xem biểu đồ thống kê
            Intent intent = new Intent(MainActivity.this,ChartActivity.class);// Tạo intent để chuyển sang màn hình xem biểu đồ thống kê công việc
            Bundle bundle = new Bundle();// Tạo Bundle để chứa dữ liệu
            bundle.putString("Day",dayshow.getText().toString());// thêm dữ liệu vào bundle, ở đây là ngày trong tuần hiển thị số lượng công việc
            intent.putExtra("Data",bundle);// Thêm bundle vào intent
            startActivity(intent);// Thực hiện chuyển màn hình
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);// Tạo hiệu ứng cho chuyển màn hình
        }
        if(item.getItemId()==R.id.share){// Chức năng chia sẻ công việc hôm được chọn qua Gmail
            sendMail();// Thực hiện chuyển sang ứng dụng Gmail trong máy để nhập Gmail người nhận
        }
        return super.onOptionsItemSelected(item);// Nếu có menu được chọn sẽ trả về true nếu không là false
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
        setContentView(R.layout.activity_main);// Tham chiếu đến layout sử dụng cho màn hình chính
        ActionBar actionBar = getSupportActionBar();// Lấy thanh tiêu đề
        assert actionBar != null;
        actionBar.setTitle(R.string.wordmanager); //Thiết lập tiêu đề nếu muốn
        actionBar.setDisplayShowHomeEnabled(true);//
        actionBar.setLogo(R.drawable.list);    //Icon muốn hiện thị
        actionBar.setDisplayUseLogoEnabled(true);// Sử dụng logo
        alarmNotification = new AlarmNotification(MainActivity.this);// Gán giá trị cho đối tượng sử dụng các hàm tạo và hủy thông báo
        listJobs                = findViewById(R.id.list_jobs); // Tham chiếu đến danh sách hiển thị công việc
        dayCalender             = findViewById(R.id.today); // Tham chiếu đến hình ảnh lịch để chọn ngày
        dayshow                 = findViewById(R.id.day);// Tham chiếu đến hiển thị ngày
        label_today             = findViewById(R.id.label_today);// Tham chiếu đến hiển thị thứ
        addJob                  = findViewById(R.id.add_job);// Tham chiếu đến hình ảnh thêm công việc mới
        del_all                 = findViewById(R.id.delete_all);// Tham chiếu đến button để xóa hết công việc trong danh sách
        done_all                = findViewById(R.id.done_all);// Tham chiếu đến button để đánh dấu hoàn thành tất cả công việc trong danh sách
        final Calendar calendar = Calendar.getInstance(); // Sử dụng đối tượng Calendar để lấy thời gian hiện tại
        final int day           = calendar.get(Calendar.DATE);// Lấy ngày hiện tại
        final int month         = calendar.get(Calendar.MONTH);// Lấy tháng hiện tại
        final int year          = calendar.get(Calendar.YEAR);// Lấy năm hiên tại
        filter                  = findViewById(R.id.filter);// Tham chiếu đến Spinner dùng để lọc dữ liệu
        havenot                 = findViewById(R.id.havenot);// Tham chiếu đến thành phần hiển thị nếu không có công việc nào trong danh sách
        importantFilter         = findViewById(R.id.ImportantFilter);// Tham chiếu đến thành phần để tìm kiếm theo mức độ quan trọng
        importantFilter.setImageResource(R.drawable.star);// Gán giá trị bạn đầu là hiển thị tất cả công việc không quan trọng và quan trọng
        list = new ArrayList<>();// Khởi tạo mảng chứa các chủ đề
        filterl = new ArrayList<>();// Khởi tạo mảng chứa category để tìm kiếm theo chủ đề
        filterl.add("Tất cả");filterl.add("Cuộc họp");filterl.add("Du lịch");filterl.add("Sinh nhật");filterl.add("Cà phê");filterl.add("Hằng ngày");filterl.add("Khác");// Thêm dữ liệu cho mảng tìm kiếm
        SpinnerAdapter adapterfilter = new SpinnerAdapter(MainActivity.this, R.layout.layout_spinner, filterl);// Khai báo adapter cho Spinner bộ lọc tìm kiếm
        adapterfilter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);// Chọn kiểu hiển thị chỉ được chọn 1 phần tử trong danh sách
        filter.setAdapter(adapterfilter);// Gán adapter cho Spinner
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {// Đặt sự kiện khi chọn 1 phàn tử Spinner
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {// Một phần tử nếu được chọn
                if(i==0){// Nếu người dùng chọn hiển thị tất cả công việc trong danh sách
                    getData(dayshow.getText().toString());// Hiển thị tất cả danh sách của ngày được chọn
                }else{
                    filter(dayshow.getText().toString(), filterl.get(i));// Hiển thị công việc theo category được chọn của ngày được chọn
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {// Nếu không chọn gì trong Spinner
            // Không làm gì cả
            }
        });
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");// Định dạng thời gian hiển thị
        dayshow.setText(simpleDateFormat.format(calendar.getTime()));// Hiển thị ngày hiện tại khi mkhoiwr động ứng dụng
        database    = new Database(this, "database", null,1); // Khởi tạo cơ sở dữ liệu với tên là database version 1
        database.SQLQuery("CREATE TABLE IF NOT EXISTS CongViec(Id INTEGER PRIMARY KEY AUTOINCREMENT, Date VARCHAR(30),TimeS VARCHAR(10), TimeE VARCHAR(10),Subject VARCHAR(100),Content VARCHAR(1000),Complete VARCHAR(6))");// Tạo bảng CongViec nếu bảng chưa có trong cơ sở dữ liệu
        database.SQLQuery("CREATE TABLE IF NOT EXISTS HoanThanh(Id INTEGER PRIMARY KEY AUTOINCREMENT, Date VARCHAR(30),TimeS VARCHAR(10), TimeE VARCHAR(10),Subject VARCHAR(100),Content VARCHAR(1000),Complete VARCHAR(6))");// Tạo bảng HoanThanh nếu bảng chưa có trong cơ sở dữ liệu
        database.SQLQuery("CREATE TABLE IF NOT EXISTS History(Id INTEGER PRIMARY KEY AUTOINCREMENT, Date VARCHAR(30),TimeS VARCHAR(10), TimeE VARCHAR(10),Subject VARCHAR(100),Content VARCHAR(1000),Complete VARCHAR(6))");// Tạo bảng History nếu bảng chưa có trong cơ sở dữ liệu
        model       = new ArrayList<>();// Khởi tạo mảng chứa dữ liệu của tất cả công việc có trong cơ sở dữ liệu
        inputCheck  = new InputCheck();// Khởi tạo biến kiểm tra dữ liệu đầu vào khi thêm 1 công việc mới
        jobAdapter  = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khởi tạo adapter cho ListView hiển thị danh sách
        listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView hiển thị danh sách công việc
        getData(dayshow.getText().toString());// Lấy dữ liệu danh sách công việc và hiển thị trên ListView
        dayCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện chọn ngày muốn hiển thị danh sách công việc
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {// Khởi tạo DatePickerDialog để hiển thị lịch
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {// Chọn 1 ngày trong lịch
                        calendar.set(year,month,dayOfMonth);// Gán lại ngày, tháng, năm
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");// Định dạng thời gian hiển thị
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);// Lấy ra ngày trong tuần
                        dayshow.setText(simpleDateFormat.format(calendar.getTime()));// Đặt lại ngày hiển thị theo ngày người dùng chọn
                        switch (dayOfWeek){
                            case 2: label_today.setText(R.string.monday); break;// Hiển thị ngày được chọn là ngày thứ hai trong tuần
                            case 3: label_today.setText(R.string.tuesday); break;// Hiển thị ngày được chọn là ngày thứ ba trong tuần
                            case 4: label_today.setText(R.string.wednesday); break;// Hiển thị ngày được chọn là ngày thứ tư trong tuần
                            case 5: label_today.setText(R.string.thursday); break;// Hiển thị ngày được chọn là ngày thứ năm trong tuần
                            case 6: label_today.setText(R.string.friday); break;// Hiển thị ngày được chọn là ngày thứ sáu trong tuần
                            case 7: label_today.setText(R.string.saturday); break;// Hiển thị ngày được chọn là ngày thứ bảy trong tuần
                            default: label_today.setText(R.string.sunday); break;// Hiển thị ngày được chọn là ngày chủ nhật trong tuần
                        }
                        //getData(dayshow.getText().toString());
                        filter(dayshow.getText().toString(),filter.getSelectedItem().toString());// Hiển thị lại danh sách công việc theo ngày được chọn
                    }
                }, year,month,day);
                datePickerDialog.show();// Hiển thị lịch cho người dùng thực hiện chuyển ngày hiển thị
            }
        });
        // Thêm công việc mới
        addJob.setOnClickListener(new View.OnClickListener() {// Nhấn nút thêm mới công việc
            @Override
            public void onClick(View v) {
                addJob();// Gọi hàm thêm mới công việc
            }
        });
        del_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện xóa tất cả công việc
                if(choose.size()==0){// Nếu không có công việc trong danh sách
                    Toast.makeText(MainActivity.this, R.string.nojob, Toast.LENGTH_SHORT).show();
                }else{// Nếu có công việc trong danh sách
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);// Khởi tạo cửa sổ yêu cầu xác nhận của người dùng
                    builder.setMessage(R.string.delsure);// Đặt câu hỏi cho người dùng
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {// Nếu người dùng xác nhận
                            for (Job i: choose){// Duyệt lần lượt các phần tử có trong danh sách
                                database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+i.getId()+"'");// Thực hiện xóa dữ liệu trong bảng CongViec
                                String query = "INSERT INTO History VALUES(null,'"+i.getDate()+"','"+i.getTime_start()+"','"+i.getTime_end()+"','"+i.getSubject()+"','"+i.getContent()+"','"+i.isComplete()+"')";// Lệnh thêm công việc vào bảng History
                                 database.SQLQuery(query);// Thực hiện lệnh
                                 alarmNotification.delNotification(i, MainActivity.this);// Xóa thông báo cho các công việc được xóa
                            }
                            getData(dayshow.getText().toString());// Hiển thị lại danh sách công việc
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {// Nếu người dùng không xác nhận
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        // Không làm gì cả
                        }
                    });
                    builder.show();// Hiển thị cửa sổ yêu cầu xác nhận người dùng
                }
            }
        });
        importantFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện tìm kiếm theo mức độ quan trong công việc
                if(check_im==1){// Đang hiển thị công việ quan trọng
                    check_im=2;// Chuyển sang hiển thị công việc không quan trọng
                    importantFilter.setImageResource(R.drawable.important);// Hiển thị biểu tượng cho công việc không quan trọng
                    Toast.makeText(MainActivity.this, "Công việc không quan trọng", Toast.LENGTH_SHORT).show();// Hiển thị thông báo nhỏ cho người dùng
                    filter(dayshow.getText().toString(), filter.getSelectedItem().toString());// Hiển thị lại danh sách theo bộ lọc người dùng chọn
                } else if(check_im==2){// Đang hiển thị công việc không quan trọng
                    check_im=3;// Chuyển sang hiển thị tất cả công việc
                    importantFilter.setImageResource(R.drawable.star);// Hiển thị biểu tượng cho tất cả công việc
                    Toast.makeText(MainActivity.this, "Tất cả công việc", Toast.LENGTH_SHORT).show();// Hiển thị thông báo nhỏ cho người dùng
                    filter(dayshow.getText().toString(), filter.getSelectedItem().toString());// Hiển thị lại danh sách theo bộ lọc người dùng chọn
                }
                else{// Đang hiển thị tất cả công việc
                    check_im=1;// Chuyển sang hiển thị công việc quan trọng
                    importantFilter.setImageResource(R.drawable.importantyellow);// Hiển thị biểu tượng cho công việc quan trọng
                    Toast.makeText(MainActivity.this, "Công việc quan trọng", Toast.LENGTH_SHORT).show();// Hiển thị thông báo nhỏ cho người dùng
                    filter(dayshow.getText().toString(), filter.getSelectedItem().toString());// Hiển thị lại danh sách theo bộ lọc người dùng chọn
                }
            }
        });
        done_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện đánh dấu hoàn thành tất cả công việc
                if(choose.size()==0){// Nếu không có công việc nào trong danh sách
                    Toast.makeText(MainActivity.this, R.string.nojob, Toast.LENGTH_SHORT).show();// Thông báo không có công việc trong danh sách
                }else{// Nếu có công việc trong danh sách
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);// Tạo cửa sổ yêu cầu xác nhận người dùng
                    builder.setMessage(R.string.notice1);// Đặt câu hỏi cho người dùng
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {// Nếu người dùng xác nhận
                            boolean check = false;// Sử dụng biến check để kiểm tra những công việc được phép đánh dấu hoàn thành
                            for (Job i: choose){// Duyệt danh sách công việc
                                Calendar c = Calendar.getInstance();// Lấy 1 đối tượng lịch
                                SimpleDateFormat formatday = new SimpleDateFormat("dd/MM/yyyy");// Định dạng thời gian theo ngày tháng năm
                                SimpleDateFormat formattime= new SimpleDateFormat("HH:mm");// Định dạng lại thời gian theo giờ phút
                                String dateD            = formatday.format(c.getTime());// Lấy ngày hiện tại
                                String timeD            = formattime.format(c.getTime());// Lấy giò và phút hiện tại
                                if(dayshow.getText().toString().compareTo(dateD)>0){// Kiểm tra ngày hiện tại có phải ngày sớm hơn ngày của công việc được chọn đánh dấu hoàn thành
                                  check = true;// Nếu đúng biến check đổi thành true
                                }else{// Nếu ngày hiện tại muộn hơn ngày của công việc được chọn
                                    if(i.getTime_start().compareTo(timeD)>0){// Kiểm tra thời gian hiện tại có sớm hơn thời gian bắt đầu công việc không
                                        check = true;// Nếu đúng biến check đổi thành true
                                    }else{// Nếu thỏa mãn hết các yêu cầu để đánh dấu hoàn thành
                                        //i.setComplete(!i.isComplete());
                                        database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+i.getId()+"'");// Thực hiện xóa công việc trong
                                        String query = "INSERT INTO HoanThanh VALUES(null,'"+i.getDate()+"','"+i.getTime_start()+"','"+i.getTime_end()+"','"+i.getSubject()+"','"+i.getContent()+"','"+i.isComplete()+"')";// Lệnh thêm công việc vào bảng HoanThanh
                                        database.SQLQuery(query);// Thực hiện lệnh
                                        filter(dayshow.getText().toString(),filter.getSelectedItem().toString());// Hiển thị lại danh sách công việc theo ngày được chọn
                                        alarmNotification.delNotification(i, MainActivity.this);// Hủy thông báo cho công việc được đánh dấu hoàn thành
                                    }
                                }
                            }
                            if(check){// Kiểm tra biến check xem có công việc nào không thỏa mãn điều kiện đánh dấu hoàn thành không
                                Toast.makeText(MainActivity.this,"Các công việc chưa đến thời gian không thể hoàn thành", Toast.LENGTH_SHORT).show();// Nếu có thông báo cho người dùng vì sao không thể đánh dấu hoàn thành các công việc đó
                            }
                            //getData(dayshow.getText().toString());
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {// Nếu người dùng không xác nhận
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Không làm gì cả
                        }
                    });
                    builder.show();// Hiển thị cửa sổ xác nhận
                }
            }
        });
    }
    // Lấy dữ liệu trong database
    public void getData(String s) {
        Cursor cursor = database.SQLSelect("SELECT * FROM CongViec");// Khai báo con trỏ trỏ vào dòng đầu tiên của bảng
        model.clear();// Xóa mảng chứa tất cả công việc có trong cơ sở dữ liệu
        while (cursor.moveToNext()) {// Nếu còn phần hàng tiếp theo trong bảng
            if(cursor.getString(6).equals("false"))// Nếu công việc là không quan trọng
            model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));// Thêm vào mảng model với thuộc tính quan trọng bằng false
            if(cursor.getString(6).equals("true"))// Nếu công việc là quan trọng
            model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));// Thêm vào mảng model với thuộc tính quan trọng bằng true
        }
        JobInDay(s);// Lọc ra các công việc có trong người được chọn hiển thị
        jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khai báo lại adapter cho ListView hiển thị danh sách công việc
        listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView
        if(choose.size()==0){// Nếu không có công việc nào trong danh sách
           havenot.setVisibility(View.VISIBLE);// Thông báo không có công việc sẽ được hiển thị
        }else { // Nếu có công việc trong danh sách
            havenot.setVisibility(View.INVISIBLE);// Thông báo không có công việc sẽ được ẩn
        }
    }
    // danh sách công việc theo ngày
    public void JobInDay(String day){
        choose.clear();// Xóa mảng chứa công việc của ngày được chọn
        for(Job i: model){// Duyệt tất cả các công việc có trong cơ sở dữ liệu
            if(i.getDate().equals(day)){// Kiểm tra ngày
                choose.add(i);// Thêm vào mảng hiển thị lại danh sách công việc
            }
        }
        Collections.sort(choose, new compareToJob());// Sắp xếp lại danh sách theo thứ tự thời gian bắt đầu
    }
    // Lọc dữ liệu theo subject
    public void filter(String day, String f){
        choose.clear();// Xóa mảng hiển thị công việc theo ngày được họn
        if(f.equals("Tất cả")){// Nếu người dùng muốn hiển thị tất cả công việc
            if (check_im == 1) {// Kiểm tra người dùng có chọn hiển thị công việc quan trọng
                for (Job i : model) {// Duyệt danh sách
                    if (i.getDate().equals(day) && i.isComplete()) {// Kiểm tra ngày được chọn và các công việc quan trọng
                        choose.add(i);// Thêm vào mảng sẽ hiển thị
                    }
                }
                Collections.sort(choose, new compareToJob());// Sắp xếp lại mảng theo thời gian bắt đầu công việc
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khai báo adapter cho ListView hiển thị danh sách công việc
                listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView hiển thị danh sách công việc
            } else if (check_im == 2) {// Kiểm tra người dùng có chọn hiển thị công việc không quan trọng
                for (Job i : model) {// Duyệt danh sách
                    if (i.getDate().equals(day)  && !i.isComplete()) {// Kiểm tra ngày được chọn và các công việc không quan trọng
                        choose.add(i);// Thêm vào mảng sẽ hiển thị
                    }
                }
                Collections.sort(choose, new compareToJob());// Sắp xếp lại mảng theo thời gian bắt đầu công việc
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khai báo adapter cho ListView hiển thị danh sách công việc
                listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView hiển thị danh sách công việc
            } else {
                for (Job i : model) {// Duyệt danh sách
                    if (i.getDate().equals(day)) {// Kiểm tra ngày được chọn
                        choose.add(i);// Thêm vào mảng sẽ hiển thị
                    }
                }
                Collections.sort(choose, new compareToJob());// Sắp xếp lại mảng theo thời gian bắt đầu công việc
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khai báo adapter cho ListView hiển thị danh sách công việc
                listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView hiển thị danh sách công việc
            }
        }else {// Người dùng lọc theo chủ đề và độ quan trọng
            if (check_im == 1) {// Kiểm tra người dùng có chọn hiển thị công việc quan trọng
                for (Job i : model) {// Duyệt danh sách
                    if (i.getDate().equals(day) && i.getSubject().equals(f) && i.isComplete()) {// Kiểm tra ngày được chọn, chủ đề và các công việc quan trọng
                        choose.add(i);// Thêm vào mảng sẽ hiển thị
                    }
                }
                Collections.sort(choose, new compareToJob());// Sắp xếp lại mảng theo thời gian bắt đầu công việc
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khai báo adapter cho ListView hiển thị danh sách công việc
                listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView hiển thị danh sách công việc
            } else if (check_im == 2) {// Kiểm tra người dùng có chọn hiển thị công việc không quan trọng
                for (Job i : model) {// Duyệt danh sách
                    if (i.getDate().equals(day) && i.getSubject().equals(f) && !i.isComplete()) {// Kiểm tra ngày được chọn, chủ đề và các công việc không quan trọng
                        choose.add(i);// Thêm vào mảng sẽ hiển thị
                    }
                }
                Collections.sort(choose, new compareToJob());// Sắp xếp lại mảng theo thời gian bắt đầu công việc
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khai báo adapter cho ListView hiển thị danh sách công việc
                listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView hiển thị danh sách công việc
            } else {
                for (Job i : model) {// Duyệt danh sách
                    if (i.getDate().equals(day) && i.getSubject().equals(f)) {// Kiểm tra ngày được chọn, chủ đề
                        choose.add(i);// Thêm vào mảng sẽ hiển thị
                    }
                }
                Collections.sort(choose, new compareToJob());// Sắp xếp lại mảng theo thời gian bắt đầu công việc
                jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khai báo adapter cho ListView hiển thị danh sách công việc
                listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView hiển thị danh sách công việc
            }
        }
    }
    // Tạo holder cho listView
    class JobHolder {
        TextView time_detail;//Hiển thị thời gian
        TextView subj;// Hiển thị chủ đề
        ImageView label_del, subrow,checkPass, important; // label_del xóa công việc, subrow hình ảnh thể hiện chủ đề, checkPass đánh dấu đã hoàn thành, important đánh dấu mức độ công việc
    }
    // Tạo Adapter cho listview
    class JobAdapter extends ArrayAdapter<Job>{
        JobAdapter(Context context, int layout, List<Job> list) {
            super(context, layout,list);// Viết lại contructor
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {// Khởi tạo View cho từng dòng trong ListView
             final JobHolder jobHolder;// Khai báo Holder cho từng View
             View row = convertView;
            if(row==null){// Nếu dòng trong ListView chưa có từ trước
                LayoutInflater inflater  = getLayoutInflater();// Khởi tạo inflater để lấy lấy dữ liệu từ layout
                row                      = inflater.inflate(R.layout.job_row, parent, false);// Gán dữ liệu từ layout job_row.xml vào biến row
                jobHolder                = new JobHolder();// Gán giá trị cho biến holder
                jobHolder.time_detail    = row.findViewById(R.id.time_detail);// Tham chiếu đến thành phần hiển thị thời gian
                jobHolder.subj           = row.findViewById(R.id.subj);// Tham chiếu đến thành phần hiển thị chủ đề
                jobHolder.checkPass      = row.findViewById(R.id.checkPass);// Tham chiếu đến thành phần hiển thị đánh dấu đã hoàn thành
                jobHolder.label_del      = row.findViewById(R.id.label_del);// Tham chiếu đến thành phần hiển thị chức năng xóa
                jobHolder.subrow         = row.findViewById(R.id.imageSubrow);// Tham chiếu đến thành phần hiển thị hình ảnh của chủ đề
                jobHolder.important      = row.findViewById(R.id.important);// Tham chiếu đến thành phần hiển thị độ quan trọng
                final Job job            = choose.get(position);// Lấy phần tử hiện tại của View
                switch (job.getSubject()){// Xác định chủ đề của công việc hiển thị
                    case "Cuộc họp" :   jobHolder.subrow.setImageResource(R.drawable.meeting); break;// Đặt hình ảnh phù hợp với chủ đề Cuộc họp
                    case "Du lịch"  :   jobHolder.subrow.setImageResource(R.drawable.travel); break;// Đặt hình ảnh phù hợp với chủ đề Du lịch
                    case "Sinh nhật":   jobHolder.subrow.setImageResource(R.drawable.birth); break;// Đặt hình ảnh phù hợp với chủ đề Sinh nhật
                    case "Cà phê"   :   jobHolder.subrow.setImageResource(R.drawable.tea); break;// Đặt hình ảnh phù hợp với chủ đề Cà phê
                    case "Hằng ngày":   jobHolder.subrow.setImageResource(R.drawable.daily); break;// Đặt hình ảnh phù hợp với chủ đề Hằng ngày
                    case "Khác"     :   jobHolder.subrow.setImageResource(R.drawable.diference); break;// Đặt hình ảnh phù hợp với chủ đề Khác
                }

                jobHolder.time_detail.setText(job.getTime_start().toString() + "--" + job.getTime_end().toString());// Hiển thị thời gian của công việc
                jobHolder.subj.setText(job.getSubject());// Hiển thị chủ đề của công việc
                if(job.isComplete())// Nếu là công việc quan trọng
                {
                    jobHolder.important.setImageResource(R.drawable.importantyellow);// Gán biểu tượng quan trọng
                }
                else {// Nếu không phải là công việc quan trọng
                    jobHolder.important.setImageResource(R.drawable.important);// Gán biểu tượng không quan trọng
                }
                jobHolder.checkPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// Đặt sự kiện đánh dấu choàn thành cho công việc
                        Calendar c = Calendar.getInstance();// Khai báo biến Calendar để lấy thời gian hiện tại
                        SimpleDateFormat formatday = new SimpleDateFormat("dd/MM/yyyy");// Định dạng ngày, tháng, năm
                        SimpleDateFormat formattime= new SimpleDateFormat("HH:mm");// Định dạng giờ, phút
                        String dateD            = formatday.format(c.getTime());// Lấy ngày hiện tại
                        String timeD            = formattime.format(c.getTime());// Lấy giờ, phút hiện tại
                        if(dayshow.getText().toString().compareTo(dateD)>0){ // Kiểm tra ngày hiện tại có sớm hơn ngày của công việc không?
                            Toast.makeText(MainActivity.this,R.string.notice2, Toast.LENGTH_SHORT).show();// Nếu có thông báo không thể đánh dấu hoàn thành
                        }else{// Nếu không
                            if(job.getTime_start().compareTo(timeD)>0){// Kiểm tra thời gian hiện tại có sớm hơn thời gian bắt đầu công việc không
                                Toast.makeText(MainActivity.this, R.string.notice3, Toast.LENGTH_SHORT).show();// Nếu có thông báo không thể đánh dấu hoàn thành
                            }else{// Nếu thỏa mãn điều kiện đánh dấu hoàn thành
                                database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+job.getId()+"'");// Xóa công việc trong bảng COngViec
                                String query = "INSERT INTO HoanThanh VALUES(null,'"+job.getDate()+"','"+job.getTime_start()+"','"+job.getTime_end()+"','"+job.getSubject()+"','"+job.getContent()+"','"+job.isComplete()+"')";// Lệnh thêm vào bảng HoanThanh
                                database.SQLQuery(query);// Thực hiện lệnh thêm
                               // getData(dayshow.getText().toString());
                                filter(dayshow.getText().toString(), filter.getSelectedItem().toString());// Hiển thị danh sách công việc
                                alarmNotification.delNotification(job, MainActivity.this);// Xóa thông báo cho công việc đươc đánh dấu hoàn thành
                            }
                        }// Thêm thông báo nhỏ cho người dùng
                    }
                });
                jobHolder.important.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// Đặt sự kiện người dùng đặt đây là công việc quan trọng hoặc bỏ công việc quan trọng
                        job.setComplete(!job.isComplete());// Đổi lại biến trong công việc hiện tại được chọn
                        String update = "UPDATE CongViec SET Complete = '"+job.isComplete()+"' WHERE Id = '"+job.getId()+"'";// Lệnh cập nhật lại cơ sở dữ liệu
                        database.SQLQuery(update);// Thực hiện lệnh cập nhật
                        if(job.isComplete())// Nếu là công việc quan trọng
                        {
                            jobHolder.important.setImageResource(R.drawable.importantyellow);// Đặt lại biểu tượng công việc quan trọng
                            Toast.makeText(MainActivity.this,R.string.addbookmark, Toast.LENGTH_SHORT).show();// Thêm thông báo nhỏ cho người dùng
                        }
                        else {
                            jobHolder.important.setImageResource(R.drawable.important);// Đặt lại biểu tượng công việc không quan trọng
                            Toast.makeText(MainActivity.this,R.string.delbookmark, Toast.LENGTH_SHORT).show();// Thêm thông báo nhỏ cho người dùng
                        }

                    }
                });
                jobHolder.label_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// Đặt sự kiện xóa công việc được chọn
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);// Tạo cửa sổ xác nhận người dùng
                        builder.setMessage(R.string.delonejob);// Đặt câu hỏi xác nhânh
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {// Nếu người dùng xác nhận
                                database.SQLQuery("DELETE FROM CongViec WHERE Id  = '"+job.getId()+"'");// Xóa công việc trong bảng CongViec
                                String query = "INSERT INTO History VALUES(null,'"+job.getDate()+"','"+job.getTime_start()+"','"+job.getTime_end()+"','"+job.getSubject()+"','"+job.getContent()+"','"+job.isComplete()+"')";// Lệnh thêm vào bảng History
                                database.SQLQuery(query);// Thực hiện lệnh thêm
                                //getData(dayshow.getText().toString());
                                filter(dayshow.getText().toString(), filter.getSelectedItem().toString());// Hiển thị lại danh sách công việc
                                alarmNotification.delNotification(job, MainActivity.this);// Xóa thông báo công việc
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {// Nếu người dùng không xác nhận
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             // Không làm gì cả
                            }
                        });
                        builder.show();// Hiển thị cửa sổ để người dùng xác nhận
                    }
                });
                row.setTag(jobHolder);// Lưu các thông tin id để sử dụng lại cho lần sau
            }else{// Nếu view có sẵn từ trước
                row.getTag();// Gán lại giá trị id cho dòng trên ListView
            }
            row.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện khi click vào 1 phần tử trong danh sách
                @Override
                public void onClick(View v) {// Đặt sự kiện khi nhấn vào từng công việc trong danh sách
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
    // Tạo holder cho Spinner tìm kiếm theo bộ lọc
    class SpinnerHolder {
        TextView name;// Hiển thị tên của chủ đề
    }
    // Tạo Adapter cho Spinner tìm kiếm theo bộ lọc
    class SpinnerAdapter extends ArrayAdapter<String>{
        public SpinnerAdapter(@NonNull Context context, int resource, List<String> list_s) {
            super(context, resource, list_s);// Cập nhật lại contructor
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            SpinnerHolder spinnerHolder;// Khởi tạo holder cho Spinner tìm kiếm
            if(convertView==null){// Nếu View chưa được khởi tạo
                spinnerHolder = new SpinnerHolder();// Gán giá trị cho holder của Spinner tìm kiếm
                LayoutInflater inflater = getLayoutInflater();
                convertView   = inflater.inflate(R.layout.layout_spinner, null);// Lấy layout cho Spinner tìm kiếm
                spinnerHolder.name = convertView.findViewById(R.id.item_spinner);// Tham chiếu đến phần tử hiện tên chủ đề
                spinnerHolder.name.setText(filterl.get(position));// Hiển thị tên chủ đề trong danh sách của mảng lựa chọn tìm kiếm
                convertView.setTag(spinnerHolder);// Lưu các giá trị được thiết lập
            }else// Nếu View đã được khởi tạo
                convertView.getTag();// Lấy các giá trị được khởi tạo từ trước
            return convertView;
        }
    }
    class SpinnerAdapterA extends ArrayAdapter<String>{
        public SpinnerAdapterA(@NonNull Context context, int resource, List<String> list_s) {
            super(context, resource, list_s);// Khởi tại lạo Contructor
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            SpinnerHolder spinnerHolder;// Khởi tạo holder cho Spinner lựa chọn tên chủ đề khi thêm công việc mới
            if(convertView==null){// Nếu View chưa được khởi tạo
                spinnerHolder = new SpinnerHolder();// Gán giá trị cho holder của Spinner lựa chọn chủ đề khi thêm công việc mới
                LayoutInflater inflater = getLayoutInflater();
                convertView   = inflater.inflate(R.layout.layout_spinner, null);// Lấy layout cho Spinner lựa chọn chủ đề khi thêm công việc mới
                spinnerHolder.name = convertView.findViewById(R.id.item_spinner);// Tham chiếu đến phần tử hiện tên chủ đề
                spinnerHolder.name.setText(list.get(position));// Hiển thị tên chủ đề trong danh sách của mảng lựa chọn khi thêm công việc mới
                convertView.setTag(spinnerHolder);// Lưu các giá trị được thiết lập
            }else
                convertView.getTag();// Lấy các giá trị được khởi tạo từ trước
            return convertView;
        }
    }
    public void sendMail(){
        String s = "Công việc trong ngày "+dayshow.getText().toString()+"\n---------------------------\n";// Phần nội dụng được hiển thị mặc định
        for(Job i: choose){// Duyệt danh sách
            s+=i.getTime_start()+"--"+i.getTime_end()+"   : "+i.getSubject()+"\n"+"Chi tiết: "+i.getContent()+"\n---------------------------\n";// Thêm các công việc
        }
        Intent intent = new Intent(Intent.ACTION_SEND);// Khởi tại Intent để chuyển sang ứng dụng Gmail
        //intent.putExtra(Intent.EXTRA_EMAIL,"khanh.pv166286@sis.hust.edu.vn");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Lịch làm việc của tôi");// Gắn giá trị subject khi chuyển sang ứng dụng Gmail
        intent.putExtra(Intent.EXTRA_TEXT,s);// Gắn giá trị nội dung khi chuyển sang ứng dụng Gmail
        intent.setType("mesage/rfc822");
        startActivity(Intent.createChooser(intent,"Công việc"));// Thực hiện chuyển màn hình
    }
    public void showListJob(){
        Collections.sort(choose, new compareToJob());// Sắp xếp lại công việc theo thời gian bắt đầu
        jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);// Khởi tạo Adapter cho ListView hiển thị danh sách công việc
        listJobs.setAdapter(jobAdapter);// Gán adapter cho ListView hiển thị danh sách công việc
    }
}

