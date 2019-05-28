package com.example.enterc.workmanager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    private CombinedChart mChart;// Nền vẽ biểu đồ
    List<String> dayOfMonthleap = new ArrayList<>();// Chứa ngày năm nhuận
    List<String> dayOfMonth     = new ArrayList<>();// Chứa ngày năm thường
    List<Job> modelunfinsed,modelfinsed, choose;// modedlunfinshed chứa công việc chưa hoàn thành, modelfinsed chưa công việc đã hoàn thành
    List<String> listFilter, listDay;// listFilter chứa tiêu đề hàng ngang của biểu đồ chủ đề công việc, listDay danh sách ngày trong tuần sẽ hiện thị để vẽ biểu đồ
    static int[] countJobType;// Mảng chứa số lượng các công việc theo chủ đề
    int dayOfyear;// Ngày hiện tại của năm
    int[] countJobDay, countUnfinshed, countFinished;// Lần lượt chứa tổng số lượng công việc, số công việc chưa hoàn thành, số công việc đã hoàn thành trong 1 tuần
    Database database;// Khai báo biến truy xuất database
    Button c_all,c_fi, c_un, c_de;// c_all dùng để chuyển sang chế độ xem số lượng tất cả công việc, c_fi dùng để chuyển sang chế độ xem số lượng công việc đã hoàn thành, c_un dùng để chuyển sang chế độ xem số lượng công việc chưa hoàn thành, c_de chuyển chế chế độ mặc định
    TextView titel, move, test1, test2;// titel là tên biểu đồ, move dùng để thay đổi loại biểu đồ, test1 dùng để di chuyển sang tuần trước , test2 dùng để di chuyển sang tuần sau
    String rand;// Lấy ra ngày đầu tiên trong tuần đang hiện thị dùng để tính tuần sau và trước tuần đó
    boolean m = true;// Dùng để xác định biểu đồ nào sẽ được vẽ
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// Đặt sự kiên cho menu
        switch (item.getItemId()){// Tìm id của menu người dùng tác động
            case android.R.id.home: {// Nếu người dùng nhấn nút quay về trên thanh tiêu đề
                Intent intent1 = new Intent(ChartActivity.this, MainActivity.class);// Khởi tạo Intent để chuyển màn hình
                startActivity(intent1);// Thực hiện chuyển màn hình
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);// Tạo hiệu ứng khi chuyển màn hình
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);// Tham chiếu đến layout sử dụng
        ActionBar actionBar = getSupportActionBar();// Lấy thanh tiêu đề
        actionBar.setTitle("Biểu đồ"); //Thiết lập tiêu đề nếu muốn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent      = getIntent();// Lấy dữ liệu từ màn hình trước chuyển đến
        Bundle bundle      = intent.getBundleExtra("Data");// Lấy dữ liệu từ intent chuyển sang bundle
        final String day   = bundle.getString("Day");// Lấy dừ liệu bundle
        database           = new Database(ChartActivity.this, "database", null, 1);// Khởi tạo giá trị cho database
        modelunfinsed      = new ArrayList<>();// Khởi tạo mảng lưu trữ công việc chưa hoàn thành
        modelfinsed        = new ArrayList<>();//  Khởi tạo mảng lưu trữ công việc hoàn thành
        choose             = new ArrayList<>();
        listFilter         = new ArrayList<>();// Khởi tạo mảng lưu trữ ngày trong tuần hiển thị biểu đồ
        countJobType       = new int[6];// Mảng lưu trữ số lượng công việc ứng với 6 chủ đề
        countJobDay        = new int[7];// Mảng lưu trữ tổng số công việc ứng với 7 ngày trong tuần
        countFinished      = new int[7];// Mảng lưu trữ số công việc đã hoàn thành ứng với 7 ngày trong tuần
        countUnfinshed     = new int[7];// Mảng lưu trữ số công việc chưa hoàn thành ứng với 7 ngày trong tuần
        listFilter.add("Cuộc họp"); listFilter.add("Du lịch"); listFilter.add("Sinh nhật"); listFilter.add("Cà phê"); listFilter.add("Hằng ngày"); listFilter.add("Khác");// Thêm tên chủ đề vào mảng
        getData();// Lấy dữ liệu từ trong cơ sở dữ liệu
        filter();// Lấy số lượng công việc theo chủ đề
        mChart = (CombinedChart) findViewById(R.id.combinedChart);// Tham chiếu đến thành phần sẽ vẽ biểu đồ trên đó
        mChart.getDescription().setEnabled(false);// Đặt mô tả cho biểu đồ
        mChart.setBackgroundColor(Color.WHITE);// Đặt màu nền
        mChart.setDrawGridBackground(false);// Không chia ô trong nền
        mChart.setDrawBarShadow(false);//Không vẽ bóng
        mChart.setHighlightFullBarEnabled(false);
        c_all = findViewById(R.id.chart_all);// Tham chiếu đến thành phần chọn hiển thị vẽ biểu đồ số lượng tất cả công việc trong tuần
        c_fi  = findViewById(R.id.chart_finshed);// Tham chiếu đến thành phần chọn hiển thị vẽ biểu đồ số lượng công việc đã hoàn thành trong tuần
        c_un  = findViewById(R.id.chart_unfinshed);// Tham chiếu đến thành phần chọn hiển thị vẽ biểu đồ số lượng công việc chưa hoàn thành trong tuần
        c_de  = findViewById(R.id.chart_default);// Tham chiếu đến thành phần chọn hiển thị vẽ biểu đồ mặc định
        titel = findViewById(R.id.chart_label);// Tham chiếu đến thành phần chọn hiển thị tên của biểu đồ
        move  = findViewById(R.id.move_chart);// Tham chiếu đến thành phần chọn chuyển loại biểu đồ
        test1 = findViewById(R.id.textViewTestBe);// Tham chiếu đến thành phần chọn hiển thị vẽ biểu đồ tuần trước tuần hiện tại
        test2 = findViewById(R.id.textViewTestAf);// Tham chiếu đến thành phần chọn hiển thị vẽ biểu đồ tuần sau tuần hiện tại
        setData();// Gán dữ liệu cho mảng ngày trong năm
        listDay = getDayOfWeek(day);// Lấy ra các ngày của tuần hiện tại
        countJobDay();// Đếm số lượng công việc trong tuần
        for(int i=0;i<listDay.size();i++){// Duyệt danh sách ngày trong tuần
            listDay.set(i,listDay.get(i).substring(0,5));// Không hiển thị năm trên biểu đồ
        }
        c_de.setTextColor(Color.GREEN);// Hiển thị màu xanh cho chế độ mặc định
        c_all.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê tất cả số lượng công việc
        c_fi.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc đã hoàn thành
        c_un.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc chưa hoàn thành
        drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);// Vẽ biểu đồ thống kê số lượng công việc trong tuần
        c_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện người dùng chọn hiển thị biểu đồ mặc định
                c_de.setTextColor(Color.GREEN);// Hiển thị màu xanh cho chế độ mặc định
                c_all.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê tất cả số lượng công việc
                c_fi.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc đã hoàn thành
                c_un.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc chưa hoàn thành
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);// Vẽ biểu đồ thống kê số lượng công việc trong tuần
            }
        });
        c_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện người dùng chọn hiển thị biểu đồ số lượng tất cả công việc
                titel.setText("Biểu đồ tất cả công việc hiện có");// Đặt lại tiêu đề cho biểu đồ
                c_de.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê mặc định
                c_all.setTextColor(Color.GREEN);// Hiển thị màu xanh cho lựa chọn xem thống kê tất cả số lượng công việc
                c_fi.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc đã hoàn thành
                c_un.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc chưa hoàn thành
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,false,false);// Vẽ biểu đồ thống kê số lượng tất cả công việc trong tuần
            }
        });
        c_fi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện người dùng chọn hiển thị biểu đồ số lượng công việc đã hoàn thành
                titel.setText("Biểu đồ sô lượng công việc đã hoàn thành");// Đặt lại tiêu đề cho biểu đồ
                c_de.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê mặc định
                c_all.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê tất cả số lượng công việc
                c_fi.setTextColor(Color.GREEN);// Hiển thị màu xanh cho lựa chọn xem thống kê số lượng công việc đã hoàn thành
                c_un.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc chưa hoàn thành
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,false,true,false);// Vẽ biểu đồ thống kê số lượng công việc đã hoàn thành trong tuần
            }
        });
        c_un.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện người dùng chọn hiển thị biểu đồ số lượng công việc chưa hoàn thành
                titel.setText("Biểu đồ sô lượng công việc chưa hoàn thành");// Đặt lại tiêu đề cho biểu đồ
                c_de.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê mặc định
                c_all.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê tất cả số lượng công việc
                c_fi.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc đã hoàn thành
                c_un.setTextColor(Color.GREEN);// Hiển thị màu xanh cho lựa chọn xem thống kê số lượng công việc chưa hoàn thành
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,false,false,true);// Vẽ biểu đồ thống kê số lượng công việc chưa hoàn thành trong tuần
            }
        });
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m){// Đang ở biểu đồ thống kế số lượng công việc theo chủ đề
                    move.setText("Chuyển sang biểu đồ thống kê số lượng công việc >");// Đặt nhãn để người dùng chuyển biểu đồ
                    titel.setText("Biểu đồ thống kê loại công việc");// Đặt lại tiêu đề cho biểu đồ
                    c_all.setVisibility(View.INVISIBLE);// Ẩn chọn xem biểu đồ thống kê số lượng tất cả công việc
                    c_de.setVisibility(View.INVISIBLE);// Ẩn chọn xem biểu đồ mặc định
                    c_fi.setVisibility(View.INVISIBLE);// Ẩn chọn xem biểu đồ thống kê số lượng công việc đã hoàn thành
                    c_un.setVisibility(View.INVISIBLE);// Ẩn chọn xem biểu đồ thống kê số lượng công việc chưa hoàn thành
                    test1.setVisibility(View.INVISIBLE);// Ẩn chuyển sang biểu đồ tuần trước tuần hiện tại
                    test2.setVisibility(View.INVISIBLE);// Ẩn chuyển sang biểu đồ tuần sau tuần hiện tại
                    drawChart(listFilter, countJobType);//  Vẽ biểu đồ thông kê số lượng công việc theo chủ đề
                    m=false;
                }
                    else{// Đang ở biểu đồ thống kế số lượng công việc theo ngày trong tuần
                    move.setText("Chuyển sang biểu đồ thống kê loại công việc >");// Đặt nhãn để người dùng chuyển biểu đồ
                    titel.setText("Biểu đồ thống kê số lượng công việc");// Đặt lại tiêu đề cho biểu đồ
                    c_all.setVisibility(View.VISIBLE);// Hiển thi chức năng chọn xem biểu đồ thống kê số lượng tất cả công việc
                    c_de.setVisibility(View.VISIBLE);// Hiển thi chức năng chọn xem biểu đồ mặc định
                    c_fi.setVisibility(View.VISIBLE);// Hiển thi chức năng chọn xem biểu đồ thống kê số lượng công việc đã hoàn thành
                    c_un.setVisibility(View.VISIBLE);// Hiển thi chức năng chọn xem biểu đồ thống kê số lượng công việc chưa hoàn thành
                    test1.setVisibility(View.VISIBLE);// Hiện thi chức năng chuyển sang biểu đồ tuần trước tuần hiện tại
                    test2.setVisibility(View.VISIBLE);// Hiện thi chức năng sang biểu đồ tuần trước tuần hiện tại
                    c_de.setTextColor(Color.GREEN);// Hiển thị màu xanh cho lựa chọn xem thống kê mặc định
                    c_all.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê tất cả số lượng công việc
                    c_fi.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê số lượng công việc đã hoàn thành
                    c_un.setTextColor(Color.BLACK);// Hiển thị màu đen cho lựa chọn xem thống kê  số lượng công việc chưa hoàn thành
                    drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);// Vẽ biểu đồ thống kê số lượng công việc trong tuần
                    m=true;
                }

            }
        });
        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện chuyển biểu đồ tuần trước tuần hiện tại
                listDay = getDayOfWeekBefore(dayOfyear);// Lấy danh sách ngày tuần trước của tuần hiện tại
                countJobDay();// Đếm số lượng công việc của tuần trước
                for(int i=0;i<listDay.size();i++){// Duyệt danh sách ngày
                    listDay.set(i,listDay.get(i).substring(0,5));// Không hiển thị năm
                }
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);// Vẽ biểu đồ mới
            }
        });
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Đặt sự kiện chuyển biểu đồ tuần sau tuần hiện tại
                listDay = getDayOfWeekAfter(dayOfyear);// Lấy danh sách ngày tuần sau của tuần hiện tại
                countJobDay();// Đếm số lượng công việc của tuần sau
                for(int i=0;i<listDay.size();i++){// Duyệt danh sách ngày
                    listDay.set(i,listDay.get(i).substring(0,5));// Không hiển thị năm
                }
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);// Vẽ biểu đồ mới
            }
        });
    }
//    public void onValueSelected(Entry e, Highlight h) {
//        Toast.makeText(this, "Value: "
//                + e.getY()
//                + ", index: "
//                + h.getX()
//                + ", DataSet index: "
//                + h.getDataSetIndex(), Toast.LENGTH_SHORT).show();
//    }
    // Vẽ biểu đồ phấn bố loại
    public void drawChart(final List<String> l, int[] x){
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return l.get((int) value % l.size());// Chia đều khoảng cho các tiêu đề
            }
        });
        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();// Khai báo đường biểu diễn trên biểu đồ
        lineDatas.addDataSet((ILineDataSet) dataChart(x));// Thêm đường biểu diễn vào biểu đồ
        data.setData(lineDatas);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        mChart.setData(data);
        mChart.invalidate();
    }
    // Vẽ biểu đồ colong việc theo ngày
    public void drawChartDay(final List<String> l, int[] x, int[] y, int[] z, boolean all, boolean fi, boolean un){
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return l.get((int) value % l.size());// Chia đều khoảng các các tiêu đề
            }
        });
        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();// Khai báo đường biểu diễn trên biểu đồ
        if(all)
        lineDatas.addDataSet((ILineDataSet) dataChartAll(x));// Thêm đường biểu diễn số lượng tất cả công việc vào biểu đồ
        if(fi)
        lineDatas.addDataSet((ILineDataSet) dataChartFinshed(y));// Thêm đường biểu diễn số lượng công việc đã hoàn thành vào biểu đồ
        if(un)
        lineDatas.addDataSet((ILineDataSet) dataChartUnFinshed(z));// Thêm đường biểu diễn số lượng công việc chưa hoàn thành vào biểu đồ
        data.setData(lineDatas);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        mChart.setData(data);
        mChart.invalidate();
    }
    // Vẽ biểu đồ cho thống kê loại công việc
    private static DataSet dataChart(int[] x) {
    LineData d = new LineData();// Khai báo đường biểu diễn trên biểu đồ
        ArrayList<Entry> entries = new ArrayList<Entry>();// Khai báo dữ liệu đầu vào
        for (int index = 0; index < x.length; index++) {
            entries.add(new Entry(index, x[index]));// Thêm dữ liệu đầu vào để vẽ biểu đồ
        }
        LineDataSet set = new LineDataSet(entries, "Công việc");// Thêm tiêu đề cho các đường biểu diễn
        set.setColor(Color.YELLOW);// Màu của đường biểu diễn
        set.setLineWidth(2.5f);// Độ rộng đường biểu diễn
        set.setCircleColor(Color.YELLOW);// Màu các nút đường biểu diễn
        set.setCircleRadius(5f);// Độ rộng của nút đường biểu diễn
        set.setFillColor(Color.YELLOW);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);// Có hiển thị giá trị tại các nút
        set.setValueTextSize(10f);// Độ rộng của giá trị tại các nút
        set.setValueTextColor(Color.GREEN);// Màu của giá trị tại các nút
        set.setAxisDependency(YAxis.AxisDependency.LEFT);// Xác định vị trí hiển thị tên của đường biểu diễn
        d.addDataSet(set);
        return set;
    }
    // Vẽ đường biểu đồ cho tất cả công việc (tổng công việc hoàn thành và chưa hoàn thành)
    private static DataSet dataChartAll(int[] x) {
        LineData d = new LineData();// Khai báo đường biểu diễn trên biểu đồ
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int index = 0; index < x.length; index++) {
            entries.add(new Entry(index, x[index]));// Thêm dữ liệu đầu vào để vẽ biểu đồ
        }
        LineDataSet set = new LineDataSet(entries, "Tất cả");// Thêm tiêu đề cho các đường biểu diễn
        set.setColor(Color.YELLOW);// Màu của đường biểu diễn
        set.setLineWidth(2.5f);// Độ rộng đường biểu diễn
        set.setCircleColor(Color.YELLOW);// Màu các nút đường biểu diễn
        set.setCircleRadius(5f);// Độ rộng của nút đường biểu diễn
        set.setFillColor(Color.YELLOW);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);// Có hiển thị giá trị tại các nút
        set.setValueTextSize(10f);// Độ rộng của giá trị tại các nút
        set.setValueTextColor(Color.GREEN);// Màu của giá trị tại các nút
        set.setAxisDependency(YAxis.AxisDependency.LEFT);// Xác định vị trí hiển thị tên của đường biểu diễn
        d.addDataSet(set);
        return set;
    }
    // Vẽ biểu đồ cho các công việc chưa hoàn thành
    private static DataSet dataChartUnFinshed(int[] x) {
        LineData d = new LineData();// Khai báo đường biểu diễn trên biểu đồ
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int index = 0; index < x.length; index++) {
            entries.add(new Entry(index, x[index]));// Thêm dữ liệu đầu vào để vẽ biểu đồ
        }
        LineDataSet set = new LineDataSet(entries, "Chưa hoàn thành");// Thêm tiêu đề cho các đường biểu diễn
        set.setColor(Color.RED);// Màu của đường biểu diễn
        set.setLineWidth(2.5f);// Độ rộng đường biểu diễn
        set.setCircleColor(Color.RED);// Màu các nút đường biểu diễn
        set.setCircleRadius(5f);// Độ rộng của nút đường biểu diễn
        set.setFillColor(Color.RED);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);// Có hiển thị giá trị tại các nút
        set.setValueTextSize(10f);// Độ rộng của giá trị tại các nút
        set.setValueTextColor(Color.RED);// Màu của giá trị tại các nút
        set.setAxisDependency(YAxis.AxisDependency.LEFT);// Xác định vị trí hiển thị tên của đường biểu diễn
        d.addDataSet(set);
        return set;
    }
    // Vẽ biểu đồ cho các công việc đã hoàn thành
    private static DataSet dataChartFinshed(int[] x) {
        LineData d = new LineData();// Khai báo đường biểu diễn trên biểu đồ
        ArrayList<Entry> entries = new ArrayList<>();
        for (int index = 0; index < x.length; index++) {
            entries.add(new Entry(index, x[index]));// Thêm dữ liệu đầu vào để vẽ biểu đồ
        }
        LineDataSet set = new LineDataSet(entries, "Đã hoàn thành");// Thêm tiêu đề cho các đường biểu diễn
        set.setColor(Color.GREEN);// Màu của đường biểu diễn
        set.setLineWidth(2.5f);// Độ rộng đường biểu diễn
        set.setCircleColor(Color.GREEN);// Màu các nút đường biểu diễn
        set.setCircleRadius(5f);// Độ rộng của nút đường biểu diễn
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);// Có hiển thị giá trị tại các nút
        set.setValueTextSize(10f);// Độ rộng của giá trị tại các nút
        set.setValueTextColor(Color.GREEN);// Màu của giá trị tại các nút
        set.setAxisDependency(YAxis.AxisDependency.LEFT);// Xác định vị trí hiển thị tên của đường biểu diễn
        d.addDataSet(set);
        return set;
    }
    // Lấy danh sách ngày trong tuần cần hiển thị vẽ biểu đồ
    public List getDayOfWeek(String date){
        Calendar calendar = Calendar.getInstance();// Khai báo đối tượng Calendar xác định ngày trong tuần và ngày trong năm
        List<String> list = new ArrayList<>();// Khai báo danh sách lưu trữ ngày trong tuần muốn vẽ biểu đồ
        String[] d = date.split("/");// Lấy ngày, tháng, năm của ngày trong tuần muốn vẽ biểu đồ
        int countBefore = 0;// Số ngày tính từ chủ nhật đến ngày hiện tại đang xét
        int countAfter  = 0;// Số ngày tính từ thứ bảy đến ngày hiện tại đang xét
        if(d.length==3)
        {
            int day = Integer.parseInt(d[0]);// Lấy ngày đang xét
            int month = Integer.parseInt(d[1]);// Lấy tháng đang xét
            int year  = Integer.parseInt(d[2]);// Lấy năm đang xét
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");// ĐỊnh dạng
            calendar.set(year,month-1,day );// Đặt lại ngày cho đối tượng Canlendar
            //String s = simpleDateFormat.format(calendar.getTime());
            if(year%4==0){// Nếu là năm nhuận
                 dayOfyear = calendar.get(Calendar.DAY_OF_YEAR);// Lấy ra thứ tự ngày trong năm của ngày đang xét
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);// Lấy ra thứ tự ngày trong tuần của ngày đang xét
                if(dayOfWeek==1){// Nếu ngày trong tuần là 1 thì có nghĩa là ngày đang xét là chủ nhật
                    countBefore = 6;// Sẽ lấy 6 ngày trước để đủ 1 tuần
                    countAfter  = 0;// Không cần ngày tiếp theo
                }else{
                    countBefore = dayOfWeek-2;// Gán số lượng ngày thiếu tính từ ngày đó đến chủ nhật
                    countAfter  = 8-dayOfWeek;// Gán số lượng ngày thiếu tính từ ngày đó đến thứ 7
                }
                int index=0;
                for(int k = dayOfyear-countBefore; k<dayOfyear;k++){// Thêm ngày từ ngày đang xét đến chủ nhật
                    if(k<1){// Ngày sang năm trước của năm đang xét
                        list.add(dayOfMonthleap.get(366+k-1)+'/'+(year-1));// Thêm ngày vào mảng
                    }else// Nếu ngày trong năm đang được xét
                        list.add(dayOfMonthleap.get(k-1)+"/"+year);// Thêm ngày vào mảng
                }
                for(int k = dayOfyear; k<=dayOfyear+countAfter;k++){
                    if(k>366){// Ngày sang năm sau của năm đang xét
                        list.add(dayOfMonthleap.get(index)+"/"+(year+1));// Thêm ngày vào mảng
                        index++;
                    }else// Nếu ngày trong năm đang được xét
                     list.add(dayOfMonthleap.get(k-1)+"/"+year);// Thêm ngày vào mảng
                }

            }else{// Nếu là năm thường
                dayOfyear = calendar.get(Calendar.DAY_OF_YEAR);// Lấy ra thứ tự ngày trong năm của ngày đang xét
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);// Lấy ra thứ tự ngày trong tuần của ngày đang xét
                if(dayOfWeek==1){// Nếu ngày trong tuần là 1 thì có nghĩa là ngày đang xét là chủ nhật
                    countBefore = 6;// Sẽ lấy 6 ngày trước để đủ 1 tuần
                    countAfter  = 0;// Không cần ngày tiếp theo
                }else{
                    countBefore = dayOfWeek-2;// Gán số lượng ngày thiếu tính từ ngày đó đến chủ nhật
                    countAfter  = 8-dayOfWeek;// Gán số lượng ngày thiếu tính từ ngày đó đến thứ 7
                }
                int index=0;
                for(int k = dayOfyear-countBefore; k<dayOfyear;k++){// Thêm ngày từ ngày đang xét đến chủ nhật
                    if(k<1){// Ngày sang năm trước của năm đang xét
                        list.add(dayOfMonth.get(365+k-1)+'/'+(year-1));// Thêm ngày vào mảng
                    }else// Nếu ngày trong năm đang được xét
                        list.add(dayOfMonth.get(k-1)+"/"+year);// Thêm ngày vào mảng
            }
                for(int k = dayOfyear; k<=dayOfyear+countAfter;k++){// Thêm ngày từ ngày đang xét đến thứ 7
                    if(k>365){// Ngày sang năm sau của năm đang xét
                        list.add(dayOfMonth.get(index)+"/"+(year+1));// Thêm ngày vào mảng
                        index++;
                    }else// Nếu ngày trong năm đang được xét
                        list.add(dayOfMonth.get(k-1)+"/"+year);// Thêm ngày vào mảng
                }
            }
        }
        else {
           Toast.makeText(ChartActivity.this, "Ngày tháng không đúng", Toast.LENGTH_SHORT).show();// Thông báo định dạng ngày không đúng
        }
     return list;
    }
   // Lấy danh sách ngày 1 tuần trước tuần hiện tại
    public List getDayOfWeekBefore(int currentDay){
        String[] date = rand.split("/");// Lấy ra ngày tháng năm của 1 ngày trong tuần đang xét
        String x;// Lưu 1 ngày trong tuần trước của tuần đang xét
        if(Integer.parseInt(date[2])%4==0){// Nếu là năm nhuận
            if(dayOfyear<7){// Nếu ngày trong tuần trước tuần đang xét nằm ở năm khác
                x=dayOfMonthleap.get(365+dayOfyear-7)+"/"+(Integer.parseInt(date[2])-1);// Gán giá trị 1 ngày trong tuần trước tuần đang xét
            }else{// Nếu ngày trong tuần trước tuần đang xét nằm ở năm đang xét
                x=dayOfMonthleap.get(dayOfyear-7)+"/"+Integer.parseInt(date[2]);// Gán giá trị 1 ngày trong tuần trước tuần đang xét
            }
        }else{// Nếu không phải năm nhuận
            if(dayOfyear<7){// Nếu ngày trong tuần trước tuần đang xét nằm ở năm khác
                x=dayOfMonth.get(365+dayOfyear-7)+"/"+(Integer.parseInt(date[2])-1);// Gán giá trị 1 ngày trong tuần trước tuần đang xét
            }else{// Nếu ngày trong tuần trước tuần đang xét nằm ở năm đang xét
                x=dayOfMonth.get(dayOfyear-7)+"/"+Integer.parseInt(date[2]);// Gán giá trị 1 ngày trong tuần trước tuần đang xét
            }
        }
        List<String> list = getDayOfWeek(x);// Lấy danh sách ngày tuần trước tuần đang xét
        return list;
    }
    // Lấy danh sách ngày 1 tuần sau tuần hiện tại
    public List getDayOfWeekAfter(int currentDay){
        String[] date = rand.split("/");// Lấy ra ngày tháng năm của 1 ngày trong tuần đang xét
        String x;// Lưu 1 ngày trong tuần sau của tuần đang xét
        if(Integer.parseInt(date[2])%4==0){// Nếu là năm nhuận
            if(dayOfyear>359){// Nếu ngày trong tuần sau tuần đang xét nằm ở năm khác
                x=dayOfMonthleap.get(dayOfyear+7-366)+"/"+(Integer.parseInt(date[2])-1);// Gán giá trị 1 ngày trong tuần sau tuần đang xét
            }else{// Nếu ngày trong tuần sau tuần đang xét nằm ở năm đang xét
                x=dayOfMonthleap.get(dayOfyear+7)+"/"+Integer.parseInt(date[2]);// Gán giá trị 1 ngày trong tuần sau tuần đang xét
            }
        }else{// Nếu không phải năm nhuận
            if(dayOfyear>358){// Nếu ngày trong tuần sau tuần đang xét nằm ở năm khác
                x=dayOfMonth.get(dayOfyear+7-365)+"/"+(Integer.parseInt(date[2])-1);// Gán giá trị 1 ngày trong tuần sau tuần đang xét
            }else{// Nếu ngày trong tuần sau tuần đang xét nằm ở năm đang xét
                x=dayOfMonth.get(dayOfyear+7)+"/"+Integer.parseInt(date[2]);// Gán giá trị 1 ngày trong tuần sau tuần đang xét
            }
        }
        List<String> list = getDayOfWeek(x);// Lấy danh sách ngày tuần sau tuần đang xét
        return list;
    }
    // Lấy dữ liệu từ database
    public void getData(){
        Cursor cursor = database.SQLSelect("SELECT * FROM CongViec");// Khai báo con trỏ trỏ vào dòng đầu tiên trong bảng CongViec
        Cursor cursorC = database.SQLSelect("SELECT * FROM HoanThanh");// Khai báo con trỏ trỏ vào dòng đầu tiên trong bảng HoanThanh
        modelunfinsed.clear();// Xóa mảng lưu các công việc chưa hoàn thành
        modelfinsed.clear();// Xóa mảng lưu các công việc đã hoàn thành
        while (cursor.moveToNext()) {// Nếu còn phần hàng tiếp theo trong bảng
            if(cursor.getString(6).equals("false"))
                modelunfinsed.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));// Thêm vào mảng modelunfinshed với thuộc tính quan trọng bằng false
            if(cursor.getString(6).equals("true"))
                modelunfinsed.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));// Thêm vào mảng modelunfinshed với thuộc tính quan trọng bằng true
        }
        while (cursorC.moveToNext()) {// Nếu còn phần hàng tiếp theo trong bảng
            if(cursorC.getString(6).equals("false"))
                modelfinsed.add(new Job(cursorC.getInt(0),cursorC.getString(1),cursorC.getString(2),cursorC.getString(3),cursorC.getString(4),cursorC.getString(5),false));// Thêm vào mảng modelfinished với thuộc tính quan trọng bằng false
            if(cursorC.getString(6).equals("true"))
                modelfinsed.add(new Job(cursorC.getInt(0),cursorC.getString(1),cursorC.getString(2),cursorC.getString(3),cursorC.getString(4),cursorC.getString(5),true));// Thêm vào mảng modelfinished với thuộc tính quan trọng bằng true
        }
    }
    // Lọc dữ liệu theo subject
    public void filter(){
        List<Job> model = new ArrayList<>();// Khai báo mảng lưu trữ toàn bộ công việc cả chưa hoàn thành và đã hoàn thành
        model.addAll(modelunfinsed);// Thêm vào model các công việc chưa hoàn thành
        model.addAll(modelfinsed);// Thêm vào model các công việc đã hoàn thành
        for (int i =0;i<6;i++){
            countJobType[i] =0;// Gán giá trị ban đầu cho biến đếm số lượng
        }
        for(Job i: model){// Duyệt mảng công việc
         switch (i.getSubject()){
             case "Cuộc họp" : countJobType[0]++; break; // Nếu công việc có chủ đề là Cuộc họp
             case "Du lịch"  : countJobType[1]++; break; // Nếu công việc có chủ đề là Du lịch
             case "Sinh nhật": countJobType[2]++; break; // Nếu công việc có chủ đề là Sinh nhật
             case "Cà phê"   : countJobType[3]++; break; // Nếu công việc có chủ đề là Cà phê
             case "Hằng ngày": countJobType[4]++; break; // Nếu công việc có chủ đề là Hằng ngày
             case "Khác"     : countJobType[5]++; break; // Nếu công việc có chủ đề là Khác
         }
        }
    }
    // Đếm số lượng công việc trong 1 tuần, theo chưa hoàn thành, đã hoàn thành và cả hai
    public  void countJobDay(){
        rand = listDay.get(0);// Lấy ngày đầu tiên trong tuần hiện tại
        for (int i =0;i<7;i++){
            countJobDay[i]    = 0 ;// Gán giá trị ban đầu cho các biến đếm số lượng tất cả công việc trong tuần
            countUnfinshed[i] = 0 ;// Gán giá trị ban đầu cho các biến đếm số lượng công việc chưa hoàn thành trong tuần
            countFinished[i]  = 0 ;// Gán giá trị ban đầu cho các biến đếm số lượng công việc đã hoàn thành trong tuần
        }
    for(Job i: modelunfinsed){// Duyệt mảng công việc chưa hoàn thành
            for(int j = 0; j<7; j++)
            if(i.getDate().equals(listDay.get(j))){// Kiểm tra ngày của công việc có trùng với ngày nào đó trong tuần đang xét không
                countUnfinshed[j]++;// Nếu có tăng biến đếm thêm 1
                break;
            }
    }
        for(Job i: modelfinsed){// Duyệt mảng công việc đã hoàn thành
            for(int j = 0; j<7; j++)
                if(i.getDate().equals(listDay.get(j))){// Kiểm tra ngày của công việc có trùng với ngày nào đó trong tuần đang xét không
                    countFinished[j]++;// Nếu có tăng biến đếm thêm 1
                    break;
                }
        }
        for(int j = 0; j<7; j++){
            countJobDay[j] = countFinished[j]+countUnfinshed[j];// Tổng số lượng cả công việc chưa và đã hoàn thành
        }
    }
    // Gán giá trị cho mảng các ngày trong năm
    public void setData(){
        // Thêm vào năm thường
        for(int i = 1; i<=12;i++){// Duyệt các tháng
            if(i==1 || i==3 || i==5 || i==7 || i==8 || i==10 || i==12 ){// Nếu là các tháng 1,3,5,7,8,10,12 sẽ có 31 ngày
                for(int j=1;j<=31;j++){
                    String i1, j1;
                    if(i<10){// Nếu tháng nhỏ hơn 10
                        i1="0"+i;// Thêm chữ số 0 trước tháng, 1->01
                    }else{// Nếu tháng lớn hơn 10
                        i1=""+i;//Không thêm
                    }
                    if(j<10){// Nếu ngày nhỏ hơn 10
                        j1="0"+j;// Thêm chữ số 0 trước ngày, 1->01
                    }else{// Nếu ngày lớn hơn 10
                        j1=""+j;// Không thêm
                    }
                    dayOfMonth.add(j1+"/"+i1);// Thêm ngày vào mảng
                }
            }else if(i==2) {// Nếu là tháng 2
                for (int j = 1; j <= 28; j++) {
                    String i1, j1;
                    if(i<10){// Nếu tháng nhỏ hơn 10
                        i1="0"+i;// Thêm chữ số 0 trước tháng, 1->01
                    }else{// Nếu tháng lớn hơn 10
                        i1=""+i;// Không thêm
                    }
                    if(j<10){// Nếu ngày nhỏ hơn 10
                        j1="0"+j;// Thêm chữ số 0 trước ngày, 1->01
                    }else{// Nếu ngày lớn hơn 10
                        j1=""+j;// Không thêm
                    }
                    dayOfMonth.add(j1+"/"+i1);// Thêm ngày vào mảng
                }
            }else{// Nếu là các tháng còn lại
                for(int j =1;j<=30;j++){
                    String i1, j1;
                    if(i<10){// Nếu tháng nhỏ hơn 10
                        i1="0"+i;// Thêm chữ số 0 trước tháng, 1->01
                    }else{// Nếu tháng lớn hơn 10
                        i1=""+i;// Không thêm
                    }
                    if(j<10){// Nếu ngày nhỏ hơn 10
                        j1="0"+j;// Thêm chữ số 0 trước ngày, 1->01
                    }else{// Nếu ngày lớn hơn 10
                        j1=""+j;// Không thêm
                    }
                    dayOfMonth.add(j1+"/"+i1);// Thêm ngày vào mảng
                }
            }
        }
        // Thêm ngày năm nhuận
        for(int i = 1; i<=12;i++){// Duyệt các tháng trong năm
            if(i==1 || i==3 || i==5 || i==7 || i==8 || i==10 || i==12 ){// Nếu là các tháng 1,3,5,7,8,10,12 sẽ có 31 ngày
                for(int j=1;j<=31;j++){
                    String i1, j1;
                    if(i<10){// Nếu tháng nhỏ hơn 10
                        i1="0"+i;// Thêm chữ số 0 trước tháng, 1->01
                    }else{// Nếu tháng lớn hơn 10
                        i1=""+i;// Không thêm
                    }
                    if(j<10){// Nếu ngày nhỏ hơn 10
                        j1="0"+j;// Thêm chữ số 0 trước ngày, 1->01
                    }else{// Nếu ngày lớn hơn 10
                        j1=""+j;
                    }// Không thêm
                    dayOfMonthleap.add(j1+"/"+i1);// Thêm ngày vào mảng
                }
            }else if(i==2) {// Nếu là tháng 2
                for (int j = 1; j <= 29; j++) {
                    String i1, j1;
                    if(i<10){// Nếu tháng nhỏ hơn 10
                        i1="0"+i;// Thêm chữ số 0 trước tháng, 1->01
                    }else{// Nếu tháng lớn hơn 10
                        i1=""+i;// Không thêm
                    }
                    if(j<10){// Nếu ngày nhỏ hơn 10
                        j1="0"+j;// Thêm chữ số 0 trước ngày, 1->01
                    }else{// Nếu ngày lớn hơn 10
                        j1=""+j;// Không thêm
                    }
                    dayOfMonthleap.add(j1+"/"+i1);// Thêm ngày vào mảng
                }
            }else{// Các tháng còn lại
                for(int j =1;j<=30;j++){
                    String i1, j1;
                    if(i<10){// Nếu tháng nhỏ hơn 10
                        i1="0"+i;// Thêm chữ số 0 trước tháng, 1->01
                    }else{// Nếu tháng lớn hơn 10
                        i1=""+i;// Không thêm
                    }
                    if(j<10){// Nếu ngày nhỏ hơn 10
                        j1="0"+j;// Thêm chữ số 0 trước ngày, 1->01
                    }else{// Nếu ngày lớn hơn 10
                        j1=""+j;// Không thêm
                    }
                    dayOfMonthleap.add(j1+"/"+i1);// Thêm ngày vào mảng
                }
            }
        }
    }
}
