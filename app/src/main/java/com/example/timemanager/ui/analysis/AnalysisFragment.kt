package com.example.timemanager.ui.analysis

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timemanager.R
import com.example.timemanager.databinding.CalendarDayLayoutBinding
import com.example.timemanager.databinding.FragmentAnalysisBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class AnalysisFragment : Fragment() {

    private lateinit var analysisViewModel: AnalysisViewModel
    private var _binding: FragmentAnalysisBinding? = null
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private var selectedDate: LocalDate? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        analysisViewModel =
            ViewModelProvider(this).get(AnalysisViewModel::class.java)

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

        return root
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
        /*eventsAdapter.apply {
            events.clear()
            events.addAll(this@Example3Fragment.events[date].orEmpty())
            notifyDataSetChanged()
        }*/
        binding.date.text = selectionFormatter.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}