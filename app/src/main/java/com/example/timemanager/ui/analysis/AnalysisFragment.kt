package com.example.timemanager.ui.analysis

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanager.R
import com.example.timemanager.databinding.CalendarDayLayoutBinding
import com.example.timemanager.databinding.FragmentAnalysisBinding
import com.example.timemanager.db.dao.RecordDao
import com.example.timemanager.db.model.Record
import com.example.timemanager.di.RepositoryModule
import com.example.timemanager.ui.home.adapter.RecordsAdapter
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AnalysisFragment : Fragment() {
    @Inject
    lateinit var recordDao: RecordDao

    private val analysisViewModel: AnalysisViewModel by viewModels {
        AnalysisViewModelFactory(
            recordRepository = RepositoryModule.provideRecordRepository(recordDao)
        )
    }

    private var _binding: FragmentAnalysisBinding? = null
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private var selectedDate: LocalDate? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val adapter = RecordsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val toolbar = binding.homeToolbar


        // toolbar设置
        toolbar.title = "数据统计"

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayLayoutBinding.bind(view)
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }
        val calendarView=binding.calendarView
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.textView.text = day.date.dayOfMonth.toString()
                val textView = container.binding.calendarDayText
                if (day.owner == DayOwner.THIS_MONTH) {
                    if (day.date == selectedDate){
                        container.textView.setTextColor(Color.BLUE)
                        textView.setBackgroundResource(R.drawable.selected_circle)
                    }
                    else{
                        textView.background = null
                        container.textView.setTextColor(Color.GRAY)
                    }
                } else {
                    container.textView.setTextColor(Color.WHITE)
                }
            }
        }
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        binding.calendarView.monthScrollListener = {
            /*homeActivityToolbar.title = if (it.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }*/

            // Select the first day of the month when
            // we scroll to a new month.
            selectDate(it.yearMonth.atDay(1))
        }

        val sumFocusData1=binding.sumFocusData1
        val sumFocusData2=binding.sumFocusData2
        val sumFocusData3=binding.sumFocusData3
        val todayFocusData1=binding.todayFocusData1
        val todayFocusData2=binding.todayFocusData2

        var times = -1
        var duration = -1L

        analysisViewModel.timesTillNow().observe(viewLifecycleOwner, Observer<Int?> {
            if (it != null){
                times = it
            }
            else{
                times = 0
            }
            sumFocusData1.text=times.toString()
            if (times != -1 && duration != -1L) {
                if (times == 0) {
                    sumFocusData3.text="0秒"
                }
                else {
                    sumFocusData3.text=secondToString(duration/times)
                }
            }
        })
        analysisViewModel.durationTillNow().observe(viewLifecycleOwner, Observer<Long?> {
            if (it == null) {
                duration=0
            }
            else{
                duration=it
            }
            sumFocusData2.text=secondToString(duration)
            if (times != -1 && duration != -1L){
                if (times == 0) {
                    sumFocusData3.text="0秒"
                }
                else {
                    sumFocusData3.text=secondToString(duration/times)
                }
            }
        })
        analysisViewModel.timesOfDate(Calendar.getInstance().time).observe(viewLifecycleOwner, Observer<Int?> {todayFocusData1.text=it.toString()})
        analysisViewModel.durationOfDate(Calendar.getInstance().time).observe(viewLifecycleOwner, Observer<Long?> {
            if (it == null) {
                todayFocusData2.text="0秒"
            }
            else{
                todayFocusData2.text=secondToString(it)
            }
        })
        val recyclerView=binding.tasksRecyclerView
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return root
    }

    private fun secondToString(seconds: Long): String {
        if (seconds == 0L || seconds == null){
            return "0秒"
        }
        var s = ""
        if (seconds > 3600) {
            s += (seconds/3600).toString() + "时 "
        }
        if (seconds > 60) {
            s += ((seconds-(seconds/3600)*3600)/60).toString() + "分 "
        }
        s += (seconds%60).toString() + "秒"
        return s
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate) {
        binding.date.text = selectionFormatter.format(date)
        val selectedFocusData1=binding.selectedFocusData1
        val selectedFocusData2=binding.selectedFocusData2
        val dateOffset = date.plusDays(1)
        val zonedDateTime = dateOffset.atStartOfDay(ZoneOffset.ofHours(8))
        analysisViewModel.timesOfDate(Date.from(zonedDateTime.toInstant())).observe(viewLifecycleOwner, Observer<Int?> {
            selectedFocusData1.text=it.toString()
        })
        analysisViewModel.durationOfDate(Date.from(zonedDateTime.toInstant())).observe(viewLifecycleOwner, Observer<Long?> {
            if (it == null) {
                selectedFocusData2.text="0秒"
            }
            else{
                selectedFocusData2.text=secondToString(it)
            }
        })
        // recordOfDate现在的实现有问题，返回的是查询当日的第一个Record，而不是当日的所有Record
        // 改动时希望recordOfDate返回List<Record>，包含当日的所有Record
        // 改好之后，1. 先把下面这行的Observer<Record?>改成Observer<List<Record>?>
        analysisViewModel.recordOfDate(Date.from(zonedDateTime.toInstant())).observe(viewLifecycleOwner, Observer<Record?> {
            if (it != null) {
        // 2. 把下面这行改成   var recordList = it
                var recordList = listOf<Record>(it,it,it,it,it,it,it,it,it,it,it)
                adapter.submitList(recordList)
            }
            else {
                var empty = Record(0,0,"无当日数据",0L,false,null)
                var recordList = listOf<Record>(empty)
                adapter.submitList(recordList)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}