package com.example.capstone_project.view.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.capstone_project.databinding.FragmentStatsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(layoutInflater)

        setUpChart(binding.chart)
        //setDataChart(binding.chart)
        setDataGroupChart(binding.chart)


        return binding.root
    }

    private fun setUpChart(barChart: BarChart) {

    }

    private fun setDataChart(barChart: BarChart) {
        barChart.setScaleEnabled(false)

        val valueList = ArrayList<BarEntry>()

        valueList.add(BarEntry(1f, 1f))
        valueList.add(BarEntry(2f, 2f))
        valueList.add(BarEntry(3f, 3f))
        valueList.add(BarEntry(4f, 5f))

        val barDataSet = BarDataSet(valueList, "test")

        val data = BarData(barDataSet)

        barChart.data = data
        barChart.invalidate()
    }

    private fun setDataGroupChart(barChart: BarChart) {

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
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("", "MON","TUE","WED","THU","FRI","SAT","Today"))

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

        val barDataSet1: BarDataSet = BarDataSet(getBarEntity1(), "Remembered")
        val barDataSet2: BarDataSet = BarDataSet(getBarEntity2(), "Forgotten")

        barDataSet1.color = Color.BLUE
        barDataSet2.color = Color.RED

        val data = BarData(barDataSet1, barDataSet2)
        data.barWidth = 0.4f
        barChart.data = data
        barChart.groupBars(0.5f, 0.2f, 0.01f)
        barChart.invalidate()
    }

    private fun getBarEntity1(): MutableList<BarEntry>? {
        val barEntity = ArrayList<BarEntry>()
        barEntity.add(BarEntry(1f, 10f))
        barEntity.add(BarEntry(2f, 2f))
        barEntity.add(BarEntry(3f, 2f))
        barEntity.add(BarEntry(4f, 3f))
        barEntity.add(BarEntry(5f, 2f))
        barEntity.add(BarEntry(6f, 6f))
        barEntity.add(BarEntry(7f, 10f))
        return barEntity
    }

    private fun getBarEntity2(): MutableList<BarEntry>? {
        val barEntity = ArrayList<BarEntry>()
        barEntity.add(BarEntry(1f, 10f))
        barEntity.add(BarEntry(2f, 2f))
        barEntity.add(BarEntry(3f, 2f))
        barEntity.add(BarEntry(4f, 1f))
        barEntity.add(BarEntry(5f, 1f))
        barEntity.add(BarEntry(6f, 2f))
        barEntity.add(BarEntry(7f, 10f))
        return barEntity
    }
}