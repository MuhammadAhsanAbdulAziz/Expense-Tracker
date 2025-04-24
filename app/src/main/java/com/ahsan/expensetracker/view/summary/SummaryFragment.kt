package com.ahsan.expensetracker.view.summary

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ahsan.expensetracker.databinding.FragmentSummaryBinding
import com.ahsan.expensetracker.utils.viewState.SummaryState
import com.ahsan.expensetracker.viewmodels.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.ahsan.expensetracker.view.base.BaseFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import pakistaniRupee

@AndroidEntryPoint
class SummaryFragment : BaseFragment<FragmentSummaryBinding, TransactionViewModel>() {
    override val viewModel: TransactionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadSummary()

        lifecycleScope.launchWhenStarted {
            viewModel.summaryState.collect { state ->
                when (state) {
                    is SummaryState.Loading -> { /* show loader */ }
                    is SummaryState.Success -> {
                        showSummary(state.income, state.expense, state.savings)
                    }
                    is SummaryState.Error -> { /* show error */ }
                }
            }
        }

    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSummaryBinding.inflate(inflater, container, false)

    private fun showSummary(income: Double, expense: Double, savings: Double) {
        binding.tvTotalIncome.text = "Total Income: ${pakistaniRupee(income)}"
        binding.tvTotalExpense.text = "Total Expense: ${pakistaniRupee(expense)}"
        binding.tvNetSavings.text = "Net Savings: ${pakistaniRupee(savings)}"

        val entries = listOf(
            PieEntry(income.toFloat(), "Income"),
            PieEntry(expense.toFloat(), "Expense"),
            PieEntry(savings.toFloat(), "Savings")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"), // Income - Green
                Color.parseColor("#F44336"), // Expense - Red
                Color.parseColor("#2196F3")  // Savings - Blue
            )
            valueTextSize = 14f
            valueTextColor = Color.WHITE
        }

        val pieData = PieData(dataSet)

        with(binding.pieChart) {
            data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            setUsePercentValues(true)
            legend.isEnabled = true
            setHoleColor(Color.TRANSPARENT)
            invalidate()
        }
    }

}
