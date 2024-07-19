package com.example.capstone_project.view.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.R
import com.example.capstone_project.databinding.FragmentStatsBinding
import com.example.capstone_project.helper.ConstraintsHelper
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(layoutInflater)
        setUp(binding.chartStats)
        return binding.root
    }

    private fun setUp(barChart: BarChart) {

        lifecycleScope.launch(Dispatchers.IO) {

            val statDao = AppDatabase(requireContext()).statDao()
            val charDataRememberList: MutableList<Float> = mutableListOf()
            val charDataForgottenList: MutableList<Float> = mutableListOf()
            val xLabels: MutableList<String> = mutableListOf("")
            val cal = Calendar.getInstance()

            cal.time = Date()
            cal.add(Calendar.DATE, ConstraintsHelper.maxDay * -1)

            val chartDataRememberMap = statDao.getAllByDateInfoAndIsRemember(
                ConstraintsHelper.dfDateInfo.format(cal.time),
                true
            ).associate { it.dateInfo to it.cnt }.toMap()

            val chartDataForgottenMap = statDao.getAllByDateInfoAndIsRemember(
                ConstraintsHelper.dfDateInfo.format(cal.time),
                false
            ).associate { it.dateInfo to it.cnt }.toMap()

            for (i in ConstraintsHelper.minDay..ConstraintsHelper.maxDay) {

                cal.add(Calendar.DATE, 1)
                xLabels.add(ConstraintsHelper.dfXAxis.format(cal.time))

                if (chartDataRememberMap[ConstraintsHelper.dfDateInfo.format(cal.time)] == null) {
                    charDataRememberList.add(0f)
                } else {
                    chartDataRememberMap[ConstraintsHelper.dfDateInfo.format(cal.time)]?.let {
                        charDataRememberList.add(it.toFloat())
                    }
                }

                if (chartDataForgottenMap[ConstraintsHelper.dfDateInfo.format(cal.time)] == null) {
                    charDataForgottenList.add(0f)
                } else {
                    chartDataForgottenMap[ConstraintsHelper.dfDateInfo.format(cal.time)]?.let {
                        charDataForgottenList.add(it.toFloat())
                    }
                }
            }

            // for testing progress bar
            delay(500)

            launch(Dispatchers.Main) {
                setDataGroupChart(barChart, charDataRememberList, charDataForgottenList, xLabels)
            }
        }
    }

    private fun setDataGroupChart(barChart: BarChart, chartData1: List<Float>, chartData2: List<Float>, xLabels: List<String>) {

        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawBorders(false)

        val description = Description()
        description.isEnabled = false
        barChart.description = description

        barChart.animateY(1000)
        barChart.animateX(1000)

        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)

        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.setDrawAxisLine(false)
        leftAxis.textColor = Color.BLUE

        val rightAxis: YAxis = barChart.axisRight
        rightAxis.setDrawAxisLine(false)
        rightAxis.textColor = Color.BLUE

        val legend: Legend = barChart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 14f
        legend.textColor = Color.BLACK
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        val barDataSet1 = BarDataSet(getBarEntity(chartData1), getString(R.string.stats_remembered))
        val barDataSet2 = BarDataSet(getBarEntity(chartData2), getString(R.string.stats_forgotten))

        barDataSet1.color = Color.BLUE
        barDataSet2.color = Color.RED

        val data = BarData(barDataSet1, barDataSet2)
        data.barWidth = 0.4f
        barChart.data = data
        barChart.groupBars(0.5f, 0.2f, 0.01f)
        barChart.invalidate()

        binding.pbStats.visibility = View.GONE
        binding.chartStats.visibility = View.VISIBLE
    }

    private fun getBarEntity(chartData: List<Float>): MutableList<BarEntry> {
        val barEntity = ArrayList<BarEntry>()
        for (i in ConstraintsHelper.minDay..ConstraintsHelper.maxDay) {
            barEntity.add(BarEntry(i.toFloat(), chartData[i-1]))
        }
        return barEntity
    }
}