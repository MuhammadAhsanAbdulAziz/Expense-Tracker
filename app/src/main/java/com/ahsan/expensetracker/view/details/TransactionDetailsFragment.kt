package com.ahsan.expensetracker.view.details

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cleanTextContent
import com.ahsan.expensetracker.R
import com.ahsan.expensetracker.databinding.FragmentTransactionDetailsBinding
import com.ahsan.expensetracker.models.Transaction
import com.ahsan.expensetracker.utils.viewState.DetailState
import com.ahsan.expensetracker.viewmodels.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.ahsan.expensetracker.view.base.BaseFragment
import hide
import pakistaniRupee
import show
import snack

@AndroidEntryPoint
class TransactionDetailsFragment : BaseFragment<FragmentTransactionDetailsBinding, TransactionViewModel>() {
    private val args: TransactionDetailsFragmentArgs by navArgs()
    override val viewModel: TransactionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val transaction = args.transaction
        getTransaction(transaction.id)
        observeTransaction()
    }

    private fun getTransaction(id: Int) {
        viewModel.getByID(id)
    }

    private fun observeTransaction() = lifecycleScope.launchWhenCreated {

        viewModel.detailState.collect { detailState ->

            when (detailState) {
                DetailState.Loading -> {
                }
                is DetailState.Success -> {
                    onDetailsLoaded(detailState.transaction)
                }
                is DetailState.Error -> {
                    binding.root.snack(
                        string = R.string.text_error
                    )
                }
                DetailState.Empty -> {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun onDetailsLoaded(transaction: Transaction) = with(binding.transactionDetails) {
        title.text = transaction.title
        amount.text = pakistaniRupee(transaction.amount).cleanTextContent
        type.text = transaction.transactionType
        tag.text = transaction.tag
        date.text = transaction.date
        note.text = transaction.note
        createdAt.text = transaction.createdAtDateFormat

        binding.editTransaction.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("transaction", transaction)
            }
            findNavController().navigate(
                R.id.action_transactionDetailsFragment_to_editTransactionFragment,
                bundle
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                viewModel.deleteByID(args.transaction.id)
                    .run {
                        findNavController().navigateUp()
                    }
            }
            R.id.action_share_text -> shareText()
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("StringFormatMatches")
    private fun shareText() = with(binding) {
        val shareMsg = getString(
            R.string.share_message,
            transactionDetails.title.text.toString(),
            transactionDetails.amount.text.toString(),
            transactionDetails.type.text.toString(),
            transactionDetails.tag.text.toString(),
            transactionDetails.date.text.toString(),
            transactionDetails.note.text.toString(),
            transactionDetails.createdAt.text.toString()
        )

        val intent = ShareCompat.IntentBuilder(requireActivity())
            .setType("text/plain")
            .setText(shareMsg)
            .intent

        startActivity(Intent.createChooser(intent, null))
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
}
