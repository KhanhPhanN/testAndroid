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
    ImageView today_complete;// Chọn  ngày mà người dùng muốn xem
    TextView day_complete, havenot;// day_complete hiển thị ngày đang chọn, havenot hiển thị khi chưa hoàn thành công việc nào
    ListView list_complete;// Hiển thị danh sách công việc đà hoàn thành
    Button unfinshied;// Đánh dấu chưa hoàn thành
    Database database;// Khai báo đối tượng để truy xuất vào cơ sở dữ liệu
    JobAdapter jobAdapter;// Khai báp adapter cho ListView
    List<Job> model, choose;// model lưu trữ tất cả công việc có trong cơ sở dữ liệu, choose lưu trữ công việc trong ngày được chọn
    List<String> filter;// Chứa chủ đề để tìm kiếm theo bộ lọc
    Spinner filter_complete;// Tạo bộ lọc tìm kiếm
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_job);// Tham chiếu đến giao diện được sử dụng
        ActionBar actionBar = getSupportActionBar();// Thiết lập thanh tiêu đề
        actionBar.setTitle("Đã hoàn thành"); //Thiết lập tiêu đề nếu muốn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        today_complete = findViewById(R.id.image_complete);// Tham chiếu đến hình ảnh chọn lịch
        day_complete   = findViewById(R.id.day_complete);// Tham chiếu đến phần tử hiển thị ngày được chọn
        list_complete  = findViewById(R.id.list_complete);// Tham chiếu đến ListView hiển thị danh sách
        unfinshied     = findViewById(R.id.unfinished);// Tham chiếu đến button đánh dấu chưa hoàn thành
        havenot        = findViewById(R.id.havenotdone1);// Tham chiếu đến TextView hiển thị khi không có công việc nào trong danh sách
        filter_complete = findViewById(R.id.filter_complete);// Tham chiếu đến Spinner tạo bộ lọc
        database       = new Database(CompleteJob.this, "database", null, 1);// Khởi tạo cơ sở dữ liệu
        model          = new ArrayList<>();// Khai báo mảng lưu trữ tất cả công việc
        choose         = new ArrayList<>();// Khai báo mảng lưu trữ công việc trong ngày được chọn
        Intent intent  = getIntent();// Khai báo intent nhận dữ liệu khi chuyển màn hình
        Bundle bundle  = intent.getBundleExtra("Data");// Khởi tạo Bundle nhận dữ liệu
        final String day     = bundle.getString("Day");// Lấy dữ liệu từ bundle
        day_complete.setText(day);// Hiển thị ngày được chọn
        today_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện chọn ngày

                final Calendar calendar = Calendar.getInstance();// Khởi tạo đối tượng lấy thời gian hiện tại
                final int day           = calendar.get(Calendar.DATE);// Lấy ngày hiện tại
                final int month         = calendar.get(Calendar.MONTH);// Lấy tháng hiện tại
                final int year          = calendar.get(Calendar.YEAR);// Lấy năm hiện tại
                DatePickerDialog datePickerDialog = new DatePickerDialog(CompleteJob.this, new DatePickerDialog.OnDateSetListener() {// Đặt sự kiện chọn lịch
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {// Chọn 1 ngày trong lịch
                        calendar.set(year,month,dayOfMonth);// Đặt lại ngày tháng năm cho đối tượng Calendar
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");// Định dạng ngày tháng năm
                        day_complete.setText(simpleDateFormat.format(calendar.getTime()));// Hiển thị ngày được chọn
                        getData(day_complete.getText().toString());// Lấy dữ liệu từ cơ sở dữ liệu và hiển thị lên danh sách
                    }
                }, year,month,day);
                datePickerDialog.show();// Hiển thị DatePickerDialog
            }
        });
        filter = new ArrayList<>();filter.add("Tất cả");filter.add("Cuộc họp");filter.add("Du lịch");filter.add("Sinh nhật");filter.add("Cà phê");filter.add("Hằng ngày");filter.add("Khác");// Thêm tên chủ đề để tạo bộ lọc
        SpinnerAdapter adapterfilter = new SpinnerAdapter(CompleteJob.this, R.layout.layout_spinner, filter);// Tạo adapter cho Spinner
        adapterfilter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);// Lựa chọn kiểu danh sách cho Spinner
        filter_complete.setAdapter(adapterfilter);// Gán adapter cho Spinner
        filter_complete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {// Đặt sự kiện khi chọn 1 phần tử trong Spinner
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){// Nếu người dùng chọn hiển thị tất cả
                    getData(day_complete.getText().toString());// Hiển thị tất cả công việc
                }else{// Nếu người dùng chọn hiển thị theo 1 chủ đề nào đó
                    filter(day_complete.getText().toString(), filter.get(i));// Thực hiện tìm kiếm và hiển thì công việc theo chủ đề
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {// Không chọn phần tử nào
            // Không làm gì cả
            }
        });
       unfinshied.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {// Đặt sự kiện khôi phục lại tất cả công việc có trong danh sách
               AlertDialog.Builder builder = new AlertDialog.Builder(CompleteJob.this);// Tạo cửa sổ xác nhận người dùng
               builder.setMessage("Bạn có chắc chắn tất cả công việc trong danh sách trên đều chưa hoàn thành không?");// Đặt câu hỏi cho người dùng
               builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {// Nếu người dùng xác nhận
                       for(Job i: choose){// Duyệt danh sách
                           database.SQLQuery("DELETE FROM HoanThanh WHERE Id  = '"+i.getId()+"'");// Xóa dữ liêu trong bảng HoanThanh
                           String query = "INSERT INTO CongViec VALUES(null,'"+i.getDate()+"','"+i.getTime_start()+"','"+i.getTime_end()+"','"+i.getSubject()+"','"+i.getContent()+"','"+i.isComplete()+"')";// Lệnh thêm công việc vào bảng CongViec
                           database.SQLQuery(query);// Thực hiện lệnh thêm
                       }
                       getData(day_complete.getText().toString());// Đổ lại dữ liệu lên danh sách
                   }
               });
               builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {// Nếu người dùng không xác nhận
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                   // Không làm gì cả
                   }
               });
               builder.show();// Hiển thị cửa sổ xác nhận ngườ dùng
           }
       });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Đặt sự kiện cho menu
        switch (item.getItemId()){// Nếu người dùng nhấn vào menu
            case android.R.id.home: {// Nhấn nút quay về trên thanh tiêu đề
                Intent intent1 = new Intent(CompleteJob.this, MainActivity.class);// Tạo intent chuyển màn hình
                startActivity(intent1);// Thực hiện chuyển màn hình
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);// Tạo hiệu ứng khi chuyển  màn hình
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void getData(String s){
        Cursor cursor = database.SQLSelect("SELECT * FROM HoanThanh");// Khai báo con trỏ trỏ vào dòng đầu tiên của bảng
        model.clear();// Xóa mảng chứa tất cả công việc có trong cơ sở dữ liệu
        while (cursor.moveToNext()) {// Nếu còn phần hàng tiếp theo trong bảng
            if(cursor.getString(6).equals("false"))// Nếu công việc là không quan trọng
                model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));// Thêm vào mảng model với thuộc tính quan trọng bằng false
            if(cursor.getString(6).equals("true"))// Nếu công việc là quan trọng
                model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));// Thêm vào mảng model với thuộc tính quan trọng bằng true
        }
        JobInDay(s);// Lọc ra các công việc có trong người được chọn hiển thị
        jobAdapter = new JobAdapter(CompleteJob.this, R.layout.done_row, choose);// Khai báo lại adapter cho ListView hiển thị danh sách công việc
        list_complete.setAdapter(jobAdapter);// Gán adapter cho ListView
        if(choose.size()==0){// Nếu không có công việc nào trong danh sách
            havenot.setVisibility(View.VISIBLE);// Thông báo không có công việc sẽ được hiển thị
        }else// Nếu có công việc trong danh sách
            havenot.setVisibility(View.INVISIBLE);// Thông báo không có công việc sẽ được ẩn
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
        choose.clear();// Xóa mảng chứa công việc của ngày được chọn
        for(Job i: model){// Duyệt tất cả các công việc có trong cơ sở dữ liệu
            if(i.getDate().equals(day) && i.getSubject().equals(f)){// Kiểm tra ngày và chủ đề
                choose.add(i);// Thêm vào mảng hiển thị lại danh sách công việc
            }
        }
        Collections.sort(choose, new compareToJob());// Sắp xếp lại danh sách theo thứ tự thời gian bắt đầu
        jobAdapter = new JobAdapter(CompleteJob.this, R.layout.job_row, choose);// Khai báo adapter cho ListView
        list_complete.setAdapter(jobAdapter);// Gán adapter cho ListView
    }
    // Tạo holder cho listView
    class JobHolder {
        TextView time_detail;//Hiển thị thời gian
        TextView subject_complete;//Hiển thị chủ đề
        ImageView type_complete, call_back, important;// type_complete hình ảnh thể hiện chủ đề, call_back khôi phục lại công việc, important độ quan trọng của công việc
    }
    // Tạo Adapter cho listview
    class JobAdapter extends ArrayAdapter<Job>{
        public JobAdapter(Context context, int layout, List<Job> list) {
            super(context, layout,list);// Viết lại contructor
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            JobHolder jobHolder;// Khai báo Holder cho từng View
            View row = convertView;
            if(row==null){// Nếu dòng trong ListView chưa có từ trước
                LayoutInflater inflater        = getLayoutInflater();// Khởi tạo inflater để lấy lấy dữ liệu từ layout
                row                            = inflater.inflate(R.layout.done_row, parent, false);// Gán dữ liệu từ layout done_row.xml vào biến row
                jobHolder                      = new JobHolder();// Gán giá trị cho biến holder
                jobHolder.subject_complete     = row.findViewById(R.id.subject_complete);// Tham chiếu đến thành phần hiển thị chủ đề
                jobHolder.time_detail          = row.findViewById(R.id.time_detail_complete);// Tham chiếu đến thành phần hiển thị thời gian
                jobHolder.type_complete        = row.findViewById(R.id.type_complete);// Tham chiếu đến thành phần hiển thị hình ảnh chủ đề
                jobHolder.call_back            = row.findViewById(R.id.come_back);// Tham chiếu đến thành phần hiển thị hình ảnh khôi phục công việc
                jobHolder.important            = row.findViewById(R.id.done_important);// Tham chiếu đến thành phần hiển thị độ quan trọng công việc
                final Job job                  = choose.get(position);// Lấy ra phần tử công việc của dòng
                switch (job.getSubject()){// Xác định chủ đề của công việc hiển thị
                    case "Cuộc họp" :   jobHolder.type_complete.setImageResource(R.drawable.meeting); break;// Đặt hình ảnh phù hợp với chủ đề Cuộc họp
                    case "Du lịch"  :   jobHolder.type_complete.setImageResource(R.drawable.travel); break;// Đặt hình ảnh phù hợp với chủ đề Du lịch
                    case "Sinh nhật":   jobHolder.type_complete.setImageResource(R.drawable.birth); break;// Đặt hình ảnh phù hợp với chủ đề Sinh nhật
                    case "Cà phê"   :   jobHolder.type_complete.setImageResource(R.drawable.tea); break;// Đặt hình ảnh phù hợp với chủ đề Cà phê
                    case "Hằng ngày":   jobHolder.type_complete.setImageResource(R.drawable.daily); break;// Đặt hình ảnh phù hợp với chủ đề Hằng ngày
                    case "Khác"     :   jobHolder.type_complete.setImageResource(R.drawable.diference); break;// Đặt hình ảnh phù hợp với chủ đề Khác
                }
                if(job.isComplete())// Nếu là công việc quan trọng
                {
                    jobHolder.important.setImageResource(R.drawable.importantyellow);// Gán biểu tượng quan trọng
                }
                else {// Nếu không phải là công việc quan trọng
                    jobHolder.important.setImageResource(R.drawable.important);// Gán biểu tượng không quan trọng
                }
                jobHolder.time_detail.setText(job.getTime_start().toString() + "--" + job.getTime_end().toString());// Hiển thị thời gian của công việc
                jobHolder.subject_complete.setText(job.getSubject());// Hiển thị chủ đề của công việc
                jobHolder.call_back.setOnClickListener(new View.OnClickListener() {// Đặt sự kiện quay lại cho từng công việc
                    @Override
                    public void onClick(View v) {// Đặt sự kiện khôi phục lại công việc
                        AlertDialog.Builder builder = new AlertDialog.Builder(CompleteJob.this);// Khai báo 1 cửa sổ để xác nhận người dùng
                        builder.setMessage("Bạn có chắc chắn chưa hoàn thành công việc này không"); // Đặt câu hỏi cho người dùng
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {// Người dùng xác nhận đồng ý
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.SQLQuery("DELETE FROM HoanThanh WHERE Id  = '"+job.getId()+"'");// Xóa dữ liệu trong CSDL
                                String query = "INSERT INTO CongViec VALUES(null,'"+job.getDate()+"','"+job.getTime_start()+"','"+job.getTime_end()+"','"+job.getSubject()+"','"+job.getContent()+"','"+job.isComplete()+"')";// Lệnh thêm lại công việc vào CSDL
                                database.SQLQuery(query);// Thực hiện lệnh thêm vào CSDL
                                getData(day_complete.getText().toString()); // Lấy dữ liệu từ CSDL và đổ lại lên danh sách
                            }
                        });
                        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {// Nếu không đồng ý xác nhận
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             // Không làm gì cả
                            }
                        });
                        builder.show();// Hiển thị cửa sổ xác nhận
                    }
                });
                row.setTag(jobHolder);// Lưu trữ dữ liệu được thiết lập
            }else{
                row.getTag();// Lấy dữ liệu được thiết lập từ trước
            }
            return row;
        }
    }
    // Tạo holder cho Spinner
    class SpinnerHolder {
        TextView name;// Tên chủ đề
    }
    class SpinnerAdapter extends ArrayAdapter<String>{
        public SpinnerAdapter(@NonNull Context context, int resource, List<String> list) {
            super(context, resource, list);// Viết lại Contructor
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
                spinnerHolder.name.setText(filter.get(position));// Hiển thị tên chủ đề trong danh sách của mảng lựa chọn tìm kiếm
                convertView.setTag(spinnerHolder);// Lưu các giá trị được thiết lập
            }else// Nếu View đã được khởi tạo
                convertView.getTag();// Lấy các giá trị được khởi tạo từ trước
            return convertView;
        }
    }
}
