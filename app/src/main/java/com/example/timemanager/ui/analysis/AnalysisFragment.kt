package com.example.timemanager.ui.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timemanager.R
import com.example.timemanager.databinding.FragmentAnalysisBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class AnalysisFragment : Fragment() {

    private lateinit var analysisViewModel: AnalysisViewModel
    private var _binding: FragmentAnalysisBinding? = null

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
            val textView = view.findViewById<TextView>(R.id.calendarDayText)

            // With ViewBinding
            // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
        }
        val calendarView=binding.calendarView
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
            }
        }
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}