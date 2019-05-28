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
    List<Job> choose;// List chứa dữ liệu công việc
    Database database;// Khởi tạo đối tượng để truy xuất cơ sở dữ liệu
    JobAdapter jobAdapter;// Khởi tạo adapter hiển thị danh sách công việc bị xóa
    ListView listView;// ListView hiển thị danh sách
    Button del_history;// Xóa lịch sử công việc
    TextView havenotdone;// TextView hiển thị khi không có lịch sử
    AlarmNotification alarmNotification;// Tạo lại thông báo khi khôi phục công việc
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Chọn phần tử trên menu
        switch (item.getItemId()){
            case android.R.id.home: {// Nhấn nút quay về trên thanh tiêu đề
                Intent intent1 = new Intent(HistoryJob.this, MainActivity.class); // Khởi tạo Intent chuyển màn hình quay về màn hình chính
                startActivity(intent1);// Thực hiện chuyển màn hình
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);// Tạo hiệu ứng chuyển màn hình
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_job);// Tham chiếu đến layout muốn sử dụng
        ActionBar actionBar = getSupportActionBar();// Gọi đối tượng thanh tiêu đề
        actionBar.setTitle("Lịch sử"); //Thiết lập tiêu đề nếu muốn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alarmNotification = new AlarmNotification(HistoryJob.this);// Gán giá trị cho biến
        choose = new ArrayList<>();// Khởi tạo mảng để lưu dữ liệu
        database = new Database(this, "database", null, 1);// Khởi tạo cơ sở dữ liệu
        listView = findViewById(R.id.list_history);// Tham chiếu đến ListView hiển thị danh sách công việc
        del_history = findViewById(R.id.history_del);// Tham chiếu đến button xóa
        havenotdone = findViewById(R.id.havenotdone);// Tham chiếu đến TextView được hiển thị khi lịch sử bị trống
        getData();// Lấy dữ liệu và hiển thị danh sách công việc
        del_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện xóa lịch sử các công việc
                for(Job i: choose){// Duyệt danh sách
                    database.SQLQuery("DELETE FROM History WHERE Id  = '"+i.getId()+"'");// Xóa công việc trong bảng History
                }
                getData();// Cập nhật danh sách
            }
        });
    }
    // Tạo holder cho LiStView hiển thị lịch sử xóa
    class JobHolder {
        TextView time_detail;// Hiển thị thời gian
        TextView subject_complete;// Hiển thị chủ đề
        ImageView type_complete, call_back, important;// Hiển thị hình ảnh ứng với chủ để, hiển thị nút khôi phục và hiển thị độ quan trọng
    }
    // Tạo Adapter cho listview
    class JobAdapter extends ArrayAdapter<Job> {
        public JobAdapter(Context context, int layout, List<Job> list) {
            super(context, layout,list);// Khởi tạo lại Contructor
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            JobHolder jobHolder;// Khởi tạo holder từng dòng trong danh ListView
            View row = convertView;
            if(row==null){// Nếu dòng trong ListView chưa được khởi tạo
                LayoutInflater inflater     = getLayoutInflater();
                row                         = inflater.inflate(R.layout.done_row, parent, false);// Khởi tạo dòng
                jobHolder                   = new JobHolder();// Gán giá trị cho holder
                jobHolder.subject_complete  = row.findViewById(R.id.subject_complete);// Tham chiếu đến phần tử hiển thị chủ đề công việc
                jobHolder.time_detail       = row.findViewById(R.id.time_detail_complete);// Tham chiếu đến phần tử hiển thị thời gian công việc
                jobHolder.type_complete     = row.findViewById(R.id.type_complete);// Tham chiếu đến phần tử hiển thị hình ảnh chủ đề công việc
                jobHolder.call_back         = row.findViewById(R.id.come_back);// Tham chiếu đến phần tử hiển thị hình ảnh khôi phục công việc
                jobHolder.important         = row.findViewById(R.id.done_important);// Tham chiếu đến phần tử hiển thị độ quan trọng công việc
                final Job job               = choose.get(position);// Lấy phần tử hiện tại
                switch (job.getSubject()){
                    case "Cuộc họp" :   jobHolder.type_complete.setImageResource(R.drawable.meeting); break;// Đặt hình ảnh phù hợp với chủ đề Cuộc họp
                    case "Du lịch"  :   jobHolder.type_complete.setImageResource(R.drawable.travel); break;// Đặt hình ảnh phù hợp với chủ đề Du lịch
                    case "Sinh nhật":   jobHolder.type_complete.setImageResource(R.drawable.birth); break;// Đặt hình ảnh phù hợp với chủ đề Sinh nhật
                    case "Cà phê"   :   jobHolder.type_complete.setImageResource(R.drawable.tea); break;// Đặt hình ảnh phù hợp với chủ đề Cà phê
                    case "Hằng ngày":   jobHolder.type_complete.setImageResource(R.drawable.daily); break;// Đặt hình ảnh phù hợp với chủ đề Hằng ngày
                    case "Khác"     :   jobHolder.type_complete.setImageResource(R.drawable.diference); break;// Đặt hình ảnh phù hợp với chủ đề Khác
                }
                if(job.isComplete())// Nếu là công việc quan trong
                {
                    jobHolder.important.setImageResource(R.drawable.importantyellow);// Đặt hình ảnh phù hợp với công việc quan trọng
                }
                else {// Nếu là công việc không quan trọng
                    jobHolder.important.setImageResource(R.drawable.important);// Đặt hình ảnh phù hợp với công việc không quan trọng
                }
                jobHolder.time_detail.setText(job.getTime_start().toString() + "--" + job.getTime_end().toString() + "\n" + job.getDate());// Hiển thị thời gian công việc
                jobHolder.time_detail.setTextSize(15);// Đặt lại cỡ chữ
                jobHolder.subject_complete.setText(job.getSubject());// Hiển thị chủ đề công việc
                jobHolder.call_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// Đặt sự kiện khôi phục lại công việc
                        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryJob.this);// Khai báo 1 cửa sổ để xác nhận người dùng
                        builder.setMessage("Bạn có chắc chắn chưa hoàn thành công việc này không?");// Thiết lập câu hỏi cho người dùng
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {// Nếu người dùng đôngg ý xác nhận
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.SQLQuery("DELETE FROM History WHERE Id  = '"+job.getId()+"'"); // Xóa dữ liệu trong CSDL
                                String query = "INSERT INTO CongViec VALUES(null,'"+job.getDate()+"','"+job.getTime_start()+"','"+job.getTime_end()+"','"+job.getSubject()+"','"+job.getContent()+"','"+job.isComplete()+"')";// Lệnh thêm vào CSDL
                                database.SQLQuery(query);// Thực hiện lệnh thêm vào CSDL
                                getData();// Đọc dữ liệu từ CSDL và đổ lại lên danh sách
                                alarmNotification.Notification(job, HistoryJob.this);// Thiết lập lại thông báo cho công việc được qua trở lại
                            }
                        });
                        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {// Nếu người dùng không xác nhận
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Không làm gì hết
                            }
                        });
                        builder.show();// Hiển thị cửa sổ để người dùng thực hiện các chức năng
                    }
                });
                row.setTag(jobHolder);// Lưu các giá trị đước thiết lập
            }else{
                row.getTag();// Lấy các giá trị được thiết lạp từ trước
            }
            return row;
        }
    }
    public void getData(){
        Cursor cursor = database.SQLSelect("SELECT * FROM History");// Khai báo con trỏ trỏ vào dòng đầu tiên của bảng
        choose.clear();// Xóa mảng chứa tất cả công việc có trong cơ sở dữ liệu
        while (cursor.moveToNext()) {// Nếu còn phần hàng tiếp theo trong bảng
            if(cursor.getString(6).equals("false"))
                choose.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));// Thêm vào mảng model với thuộc tính quan trọng bằng false
            if(cursor.getString(6).equals("true"))
                choose.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));// Thêm vào mảng model với thuộc tính quan trọng bằng true
        }
        Collections.reverse(choose);// Đảo ngược mảng để người dùng xem công việc xóa gần nhất của mình
        jobAdapter = new JobAdapter(this, R.layout.done_row, choose);// Khai báo lại adapter cho ListView hiển thị danh sách công việc
        listView.setAdapter(jobAdapter);// Gán adapter cho ListView
        if(choose.size()==0){// Nếu không có lịch sử
            havenotdone.setVisibility(View.VISIBLE);// Hiển thị thông báo không có lịch sử
        }else// Nếu có lịch sử
            havenotdone.setVisibility(View.INVISIBLE);// Ẩn hiển thị thông báo không có lịch sử
    }
}
