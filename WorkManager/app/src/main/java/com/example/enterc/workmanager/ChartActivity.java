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
    private CombinedChart mChart;
    List<String> dayOfMonthleap = new ArrayList<>();
    List<String> dayOfMonth     = new ArrayList<>();
    List<Job> modelunfinsed,modelfinsed, choose;
    List<String> listFilter, listDay;
    static int[] countJobType;
    int dayOfyear;
    int[] countJobDay, countUnfinshed, countFinished;
    Database database;
    Button c_all,c_fi, c_un, c_de;
    TextView titel, move, test1, test2;
    String rand;
    boolean m = true;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: {
                Intent intent1 = new Intent(ChartActivity.this, MainActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_end);
            }

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Biểu đồ"); //Thiết lập tiêu đề nếu muốn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent  = getIntent();
        Bundle bundle  = intent.getBundleExtra("Data");
        final String day     = bundle.getString("Day");
        database = new Database(ChartActivity.this, "database", null, 1);
        modelunfinsed    = new ArrayList<>();
        modelfinsed      = new ArrayList<>();
        choose   = new ArrayList<>();
        listFilter = new ArrayList<>();
        countJobType  = new int[6];
        countJobDay = new int[7];
        countFinished = new int[7];
        countUnfinshed = new int[7];
        listFilter.add("Cuộc họp"); listFilter.add("Du lịch"); listFilter.add("Sinh nhật"); listFilter.add("Cà phê"); listFilter.add("Hằng ngày"); listFilter.add("Khác");
        getData();
        filter();
        mChart = (CombinedChart) findViewById(R.id.combinedChart);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        //mChart.setOnChartValueSelectedListener(this);
        c_all = findViewById(R.id.chart_all);
        c_fi = findViewById(R.id.chart_finshed);
        c_un = findViewById(R.id.chart_unfinshed);
        c_de = findViewById(R.id.chart_default);
        titel = findViewById(R.id.chart_label);
        move = findViewById(R.id.move_chart);
        test1 = findViewById(R.id.textViewTestBe);
        test2 = findViewById(R.id.textViewTestAf);
        setData();
        listDay = getDayOfWeek(day);
        countJobDay();
        for(int i=0;i<listDay.size();i++){
            listDay.set(i,listDay.get(i).substring(0,5));
        }
        //drawChart(listFilter, countJobType);
        c_de.setTextColor(Color.GREEN);
        c_all.setTextColor(Color.BLACK);
        c_fi.setTextColor(Color.BLACK);
        c_un.setTextColor(Color.BLACK);
        drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);
        c_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c_de.setTextColor(Color.GREEN);
                c_all.setTextColor(Color.BLACK);
                c_fi.setTextColor(Color.BLACK);
                c_un.setTextColor(Color.BLACK);
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);
            }
        });
        c_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titel.setText("Biểu đồ tất cả công việc hiện có");
                c_de.setTextColor(Color.BLACK);
                c_all.setTextColor(Color.GREEN);
                c_fi.setTextColor(Color.BLACK);
                c_un.setTextColor(Color.BLACK);
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,false,false);
            }
        });
        c_fi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titel.setText("Biểu đồ sô lượng công việc đã hoàn thành");
                c_de.setTextColor(Color.BLACK);
                c_all.setTextColor(Color.BLACK);
                c_fi.setTextColor(Color.GREEN);
                c_un.setTextColor(Color.BLACK);
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,false,true,false);
            }
        });
        c_un.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titel.setText("Biểu đồ sô lượng công việc chưa hoàn thành");
                c_de.setTextColor(Color.BLACK);
                c_all.setTextColor(Color.BLACK);
                c_fi.setTextColor(Color.BLACK);
                c_un.setTextColor(Color.GREEN);
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,false,false,true);
            }
        });
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m){
                    move.setText("Chuyển sang biểu đồ thống kê số lượng công việc >");
                    titel.setText("Biểu đồ thống kê loại công việc");
                    c_all.setVisibility(View.INVISIBLE);
                    c_de.setVisibility(View.INVISIBLE);
                    c_fi.setVisibility(View.INVISIBLE);
                    c_un.setVisibility(View.INVISIBLE);
                    test1.setVisibility(View.INVISIBLE);
                    test2.setVisibility(View.INVISIBLE);

                    drawChart(listFilter, countJobType);
                    m=false;
                }
                    else{
                    move.setText("Chuyển sang biểu đồ thống kê loại công việc >");
                    titel.setText("Biểu đồ thống kê số lượng công việc");
                    c_all.setVisibility(View.VISIBLE);
                    c_de.setVisibility(View.VISIBLE);
                    c_fi.setVisibility(View.VISIBLE);
                    c_un.setVisibility(View.VISIBLE);
                    test1.setVisibility(View.VISIBLE);
                    test2.setVisibility(View.VISIBLE);
                    c_de.setTextColor(Color.GREEN);
                    c_all.setTextColor(Color.BLACK);
                    c_fi.setTextColor(Color.BLACK);
                    c_un.setTextColor(Color.BLACK);
                    drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);
                    m=true;
                }

            }
        });
        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDay = getDayOfWeekBefore(dayOfyear);
                countJobDay();
                for(int i=0;i<listDay.size();i++){
                    listDay.set(i,listDay.get(i).substring(0,5));
                }
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);
            }
        });
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDay = getDayOfWeekAfter(dayOfyear);
                countJobDay();
                for(int i=0;i<listDay.size();i++){
                    listDay.set(i,listDay.get(i).substring(0,5));
                }
                drawChartDay(listDay, countJobDay, countFinished, countUnfinshed,true,true,true);
            }
        });
    }
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, "Value: "
                + e.getY()
                + ", index: "
                + h.getX()
                + ", DataSet index: "
                + h.getDataSetIndex(), Toast.LENGTH_SHORT).show();
    }
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
                return l.get((int) value % l.size());
            }
        });
        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) dataChart(x));
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
                return l.get((int) value % l.size());
            }
        });
        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        if(all)
        lineDatas.addDataSet((ILineDataSet) dataChartAll(x));
        if(fi)
        lineDatas.addDataSet((ILineDataSet) dataChartFinshed(y));
        if(un)
        lineDatas.addDataSet((ILineDataSet) dataChartUnFinshed(z));
        data.setData(lineDatas);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();
    }
    // Vẽ biểu đồ cho thống kê loại công việc
    private static DataSet dataChart(int[] x) {
    LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int index = 0; index < x.length; index++) {
            entries.add(new Entry(index, x[index]));
        }
        LineDataSet set = new LineDataSet(entries, "Công việc");
        set.setColor(Color.YELLOW);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.YELLOW);
        set.setCircleRadius(5f);
        set.setFillColor(Color.YELLOW);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);
        return set;
    }
    // Vẽ đường biểu đồ cho tất cả công việc (tổng công việc hoàn thành và chưa hoàn thành)
    private static DataSet dataChartAll(int[] x) {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int index = 0; index < x.length; index++) {
            entries.add(new Entry(index, x[index]));
        }
        LineDataSet set = new LineDataSet(entries, "Tất cả");
        set.setColor(Color.YELLOW);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.YELLOW);
        set.setCircleRadius(5f);
        set.setFillColor(Color.YELLOW);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);
        return set;
    }
    // Vẽ biểu đồ cho các công việc chưa hoàn thành
    private static DataSet dataChartUnFinshed(int[] x) {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int index = 0; index < x.length; index++) {
            entries.add(new Entry(index, x[index]));
        }
        LineDataSet set = new LineDataSet(entries, "Chưa hoàn thành");
        set.setColor(Color.RED);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.RED);
        set.setCircleRadius(5f);
        set.setFillColor(Color.RED);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.RED);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);
        return set;
    }
    // Vẽ biểu đồ cho các công việc đã hoàn thành
    private static DataSet dataChartFinshed(int[] x) {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int index = 0; index < x.length; index++) {
            entries.add(new Entry(index, x[index]));
        }
        LineDataSet set = new LineDataSet(entries, "Đã hoàn thành");
        set.setColor(Color.GREEN);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.GREEN);
        set.setCircleRadius(5f);
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);
        return set;
    }
    // Lấy danh sách ngày trong tuần cần hiển thị vẽ biểu đồ
    public List getDayOfWeek(String date){
        Calendar calendar = Calendar.getInstance();
        List<String> list = new ArrayList<>();
        String[] d = date.split("/");
        int countBefore = 0;
        int countAfter  = 0;
        if(d.length==3)
        {
            int day = Integer.parseInt(d[0]);
            int month = Integer.parseInt(d[1]);
            int year  = Integer.parseInt(d[2]);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            calendar.set(year,month-1,day );
            String s = simpleDateFormat.format(calendar.getTime());
            if(year%4==0){
                 dayOfyear = calendar.get(Calendar.DAY_OF_YEAR);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if(dayOfWeek==1){
                    countBefore = 6;
                    countAfter  = 0;
                }else{
                    countBefore = dayOfWeek-2;
                    countAfter  = 8-dayOfWeek;
                }
                String si = "";
                int index=0;
                int index1=366;
                for(int k = dayOfyear-countBefore; k<dayOfyear;k++){
                    if(k<1){
                        list.add(dayOfMonthleap.get(366+k-1)+'/'+(year-1));
                        //si+=dayOfMonthleap.get(366+k-1)+"--";
                    }else
                        list.add(dayOfMonthleap.get(k-1)+"/"+year);
                   // si+=dayOfMonthleap.get(k-1)+"--";
                }
                for(int k = dayOfyear; k<=dayOfyear+countAfter;k++){
                    if(k>366){
                        list.add(dayOfMonthleap.get(index)+"/"+(year+1));
                        //si+=dayOfMonthleap.get(index)+"--";
                        index++;
                    }else
                     list.add(dayOfMonthleap.get(k-1)+"/"+year);
                        //si+=dayOfMonthleap.get(k-1)+"--";
                }

               // tv.setText("Ngày trong năm: "+dayOfyear+"\nNgày trong tuần"+dayOfWeek+"\n"+si);
            }else{
                dayOfyear = calendar.get(Calendar.DAY_OF_YEAR);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if(dayOfWeek==1){
                    countBefore = 6;
                    countAfter  = 0;
                }else{
                    countBefore = dayOfWeek-2;
                    countAfter  = 8-dayOfWeek;
                }
                String si = "";
                int index=0;
                for(int k = dayOfyear-countBefore; k<dayOfyear;k++){
                    if(k<1){
                        list.add(dayOfMonth.get(365+k-1)+'/'+(year-1));
                       // si+=dayOfMonth.get(365+k-1)+"--";
                    }else
                        list.add(dayOfMonth.get(k-1)+"/"+year);
                       //si+=dayOfMonth.get(k-1)+"--";
            }
                for(int k = dayOfyear; k<=dayOfyear+countAfter;k++){
                    if(k>365){
                        list.add(dayOfMonth.get(index)+"/"+(year+1));
                        //si+=dayOfMonth.get(index)+"--";
                        index++;
                    }else
                        list.add(dayOfMonth.get(k-1)+"/"+year);
                    //si+=dayOfMonth.get(k-1)+"--";
                }
                //tv.setText("Ngày trong năm: "+dayOfyear+"\nNgày trong tuần"+dayOfWeek+"\n"+si);
            }
        }
        else {
           Toast.makeText(ChartActivity.this, "Ngày tháng không đúng", Toast.LENGTH_SHORT).show();
        }
     return list;
    }
   // Lấy danh sách ngày 1 tuần trước tuần hiện tại
    public List getDayOfWeekBefore(int currentDay){
        String[] date = rand.split("/");
        String x;
        if(Integer.parseInt(date[2])%4==0){
            if(dayOfyear<7){
                x=dayOfMonthleap.get(365+dayOfyear-7)+"/"+(Integer.parseInt(date[2])-1);
            }else{
                x=dayOfMonthleap.get(dayOfyear-7)+"/"+Integer.parseInt(date[2]);
            }
        }else{
            if(dayOfyear<7){
                x=dayOfMonth.get(365+dayOfyear-7)+"/"+(Integer.parseInt(date[2])-1);
            }else{
                x=dayOfMonth.get(dayOfyear-7)+"/"+Integer.parseInt(date[2]);
            }
        }
        List<String> list = getDayOfWeek(x);
        return list;
    }
    // Lấy danh sách ngày 1 tuần sau tuần hiện tại
    public List getDayOfWeekAfter(int currentDay){
        String[] date = rand.split("/");
        String x;
        if(Integer.parseInt(date[2])%4==0){
            if(dayOfyear>359){
                x=dayOfMonthleap.get(dayOfyear+7-366)+"/"+(Integer.parseInt(date[2])-1);
            }else{
                x=dayOfMonthleap.get(dayOfyear+7)+"/"+Integer.parseInt(date[2]);
            }
        }else{
            if(dayOfyear>358){
                x=dayOfMonth.get(dayOfyear+7-365)+"/"+(Integer.parseInt(date[2])-1);
            }else{
                x=dayOfMonth.get(dayOfyear+7)+"/"+Integer.parseInt(date[2]);
            }
        }
        List<String> list = getDayOfWeek(x);
        return list;
    }
    // Lấy dữ liệu từ database
    public void getData(){
        Cursor cursor = database.SQLSelect("SELECT * FROM CongViec");
        Cursor cursorC = database.SQLSelect("SELECT * FROM HoanThanh");
        modelunfinsed.clear();
        modelfinsed.clear();
        while (cursor.moveToNext()) {
            if(cursor.getString(6).equals("false"))
                modelunfinsed.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));
            if(cursor.getString(6).equals("true"))
                modelunfinsed.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));
        }
        while (cursorC.moveToNext()) {
            if(cursorC.getString(6).equals("false"))
                modelfinsed.add(new Job(cursorC.getInt(0),cursorC.getString(1),cursorC.getString(2),cursorC.getString(3),cursorC.getString(4),cursorC.getString(5),false));
            if(cursorC.getString(6).equals("true"))
                modelfinsed.add(new Job(cursorC.getInt(0),cursorC.getString(1),cursorC.getString(2),cursorC.getString(3),cursorC.getString(4),cursorC.getString(5),true));
        }
    }
    // Lọc dữ liệu theo subject
    public void filter(){
        List<Job> model = new ArrayList<>();
        model.addAll(modelunfinsed);
        model.addAll(modelfinsed);
        for (int i =0;i<6;i++){
            countJobType[i] =0;
        }
        for(Job i: model){
         switch (i.getSubject()){
             case "Cuộc họp" : countJobType[0]++; break;
             case "Du lịch"  : countJobType[1]++; break;
             case "Sinh nhật": countJobType[2]++; break;
             case "Cà phê"   : countJobType[3]++; break;
             case "Hằng ngày": countJobType[4]++; break;
             case "Khác"     : countJobType[5]++; break;
         }
        }
    }
    // Đếm số lượng công việc trong 1 tuần, theo chưa hoàn thành, đã hoàn thành và cả hai
    public  void countJobDay(){
        rand = listDay.get(0);
     List<Job> model = new ArrayList<>();
        for (int i =0;i<7;i++){
            countJobDay[i]    = 0 ;
            countUnfinshed[i] = 0 ;
            countFinished[i]  = 0 ;
        }
    for(Job i: modelunfinsed){
            for(int j = 0; j<7; j++)
            if(i.getDate().equals(listDay.get(j))){
                countUnfinshed[j]++;
                break;
            }
    }
        for(Job i: modelfinsed){
            for(int j = 0; j<7; j++)
                if(i.getDate().equals(listDay.get(j))){
                    countFinished[j]++;
                    break;
                }
        }
        for(int j = 0; j<7; j++){
            countJobDay[j] = countFinished[j]+countUnfinshed[j];
        }
    }
    // Gán giá trị cho mảng các ngày trong năm
    public void setData(){
        for(int i = 1; i<=12;i++){
            if(i==1 || i==3 || i==5 || i==7 || i==8 || i==10 || i==12 ){
                for(int j=1;j<=31;j++){
                    String i1, j1;
                    if(i<10){
                        i1="0"+i;
                    }else{
                        i1=""+i;
                    }
                    if(j<10){
                        j1="0"+j;
                    }else{
                        j1=""+j;
                    }
                    dayOfMonth.add(j1+"/"+i1);
                }
            }else if(i==2) {
                for (int j = 1; j <= 28; j++) {
                    String i1, j1;
                    if(i<10){
                        i1="0"+i;
                    }else{
                        i1=""+i;
                    }
                    if(j<10){
                        j1="0"+j;
                    }else{
                        j1=""+j;
                    }
                    dayOfMonth.add(j1+"/"+i1);
                }
            }else{
                for(int j =1;j<=30;j++){
                    String i1, j1;
                    if(i<10){
                        i1="0"+i;
                    }else{
                        i1=""+i;
                    }
                    if(j<10){
                        j1="0"+j;
                    }else{
                        j1=""+j;
                    }
                    dayOfMonth.add(j1+"/"+i1);
                }
            }
        }
        for(int i = 1; i<=12;i++){
            if(i==1 || i==3 || i==5 || i==7 || i==8 || i==10 || i==12 ){
                for(int j=1;j<=31;j++){
                    String i1, j1;
                    if(i<10){
                        i1="0"+i;
                    }else{
                        i1=""+i;
                    }
                    if(j<10){
                        j1="0"+j;
                    }else{
                        j1=""+j;
                    }
                    dayOfMonthleap.add(j1+"/"+i1);
                }
            }else if(i==2) {
                for (int j = 1; j <= 29; j++) {
                    String i1, j1;
                    if(i<10){
                        i1="0"+i;
                    }else{
                        i1=""+i;
                    }
                    if(j<10){
                        j1="0"+j;
                    }else{
                        j1=""+j;
                    }
                    dayOfMonthleap.add(j1+"/"+i1);
                }
            }else{
                for(int j =1;j<=30;j++){
                    String i1, j1;
                    if(i<10){
                        i1="0"+i;
                    }else{
                        i1=""+i;
                    }
                    if(j<10){
                        j1="0"+j;
                    }else{
                        j1=""+j;
                    }
                    dayOfMonthleap.add(j1+"/"+i1);
                }
            }
        }
    }
}
