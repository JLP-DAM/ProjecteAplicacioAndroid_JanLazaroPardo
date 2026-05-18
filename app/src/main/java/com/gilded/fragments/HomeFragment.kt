package com.gilded.fragments

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gilded.MainActivity
import com.gilded.R
import com.gilded.models.Receipt
import com.gilded.recyclerview.ReceiptsRecyclerViewAdapter
import com.gilded.services.PreferencesKeys
import com.gilded.services.SettingsDataStore
import com.gilded.viewmodels.CurrentReceiptViewModel
import com.gilded.viewmodels.FilterViewModel
import com.gilded.viewmodels.ReceiptsViewModel
import com.gilded.viewmodels.UsageDataViewModel
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import com.gilded.viewmodels.SettingsViewModel


class HomeFragment : Fragment() {
    private lateinit var receiptsRecyclerView: RecyclerView
    private lateinit var receiptsRecyclerViewAdapter: ReceiptsRecyclerViewAdapter

    private lateinit var currentBalanceTextView: TextView

    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()

    private val currentReceiptViewModel: CurrentReceiptViewModel by activityViewModels()

    private val filterViewModel: FilterViewModel by activityViewModels()
    private val usageDataViewModel: UsageDataViewModel by activityViewModels()

    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false)

        receiptsRecyclerView = homeFragmentView.findViewById(R.id.receipts)
        currentBalanceTextView = homeFragmentView.findViewById(R.id.currentBalance)

        val helpButton: Button = homeFragmentView.findViewById(R.id.help)
        val settingsImageButton: ImageButton = homeFragmentView.findViewById(R.id.settings)
        val filterImageButton: ImageButton = homeFragmentView.findViewById(R.id.filter)
        val voiceRecognitionImageButton: ImageButton = homeFragmentView.findViewById(R.id.voiceRecognition)

        receiptsRecyclerView.layoutManager = LinearLayoutManager(context)

        receiptsRecyclerViewAdapter = ReceiptsRecyclerViewAdapter(
            receipts = receiptsViewModel.getReceipts(),
            onReceiptClick = { receipt ->

                currentReceiptViewModel.setReceipt(receipt)

                val receiptViewerFragment = ReceiptViewerFragment()

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainerView, receiptViewerFragment)
                    ?.commit()
            }
        )

        receiptsRecyclerView.adapter = receiptsRecyclerViewAdapter

        receiptsViewModel.receipts.observe(viewLifecycleOwner) {
            receiptsRecyclerViewAdapter.updateList(receiptsViewModel.getReceipts())
            updateCurrentBalance()
        }

        updateCurrentBalance()

        helpButton.setOnClickListener {
            val helpFragment = HelpFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, helpFragment)
                ?.commit()
        }

        settingsImageButton.setOnClickListener {
            val settingsFragment = SettingsFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, settingsFragment)
                ?.commit()
        }

        filterImageButton.setOnClickListener {
            val filterFragment = FilterFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, filterFragment)
                ?.commit()
        }

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.getAdapterPosition()
                receiptsViewModel.removeReceipt(position)
                receiptsRecyclerViewAdapter.notifyDataSetChanged()
            }
        }

        val receiptTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        receiptTouchHelper.attachToRecyclerView(receiptsRecyclerView)

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        speechRecognizer.setRecognitionListener(object: RecognitionListener {
            override fun onBeginningOfSpeech() {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onResults(results: Bundle) {
                val data: ArrayList<String>? = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (data == null) {return}

                var fragment: Fragment? = null

                if (data.indexOf("inici") != -1) {
                    fragment = HomeFragment()
                } else if(data.indexOf("afegir") != -1) {
                    fragment = ReceiptCreatorFragment()
                } else if(data.indexOf("transaccions") != -1) {
                    fragment = TransactionsFragment()
                } else if (data.indexOf("configuració") != -1) {
                    fragment = SettingsFragment()
                } else if (data.indexOf("ajuda") != -1) {
                    fragment = HelpFragment()
                } else if (data.indexOf("filtre") != -1) {
                    fragment = FilterFragment()
                }

                if (fragment == null) {return}

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainerView, fragment)
                    ?.commit()
            }

            override fun onRmsChanged(rmsdB: Float) {}
        })

        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ca")

        var listening = false

        voiceRecognitionImageButton.setOnClickListener {
            if (activity != null) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 200)

            }

            if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
                return@setOnClickListener
            }

            listening = !listening

            if (!listening) {
                voiceRecognitionImageButton.setColorFilter(resources.getColor(R.color.white))

                speechRecognizer.stopListening()
            } else {
                speechRecognizer.startListening(recognizerIntent)

                voiceRecognitionImageButton.setColorFilter(resources.getColor(R.color.green))
            }
        }

        if (settingsViewModel.voiceNavigation.value ?: false) {
            voiceRecognitionImageButton.visibility = ViewGroup.VISIBLE
        } else {
            voiceRecognitionImageButton.visibility = ViewGroup.GONE
        }

        filterViewModel.filteredCategory.observe(viewLifecycleOwner) { updateFilter() }

        filterViewModel.incomeVisible.observe(viewLifecycleOwner) { updateFilter() }

        filterViewModel.expensesVisible.observe(viewLifecycleOwner) { updateFilter() }

        usageDataViewModel.getFromFirebase()

        return homeFragmentView
    }

    fun updateCurrentBalance() {
        var currentBalance = 0.0

        for (receipt in receiptsViewModel.getReceipts()) {
            currentBalance = currentBalance + receipt.amount
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val currencySymbol = SettingsDataStore.getCurrencySymbol(requireContext())
            currentBalanceTextView.text = "${currentBalance}${currencySymbol.first()}"
        }
    }

    fun updateFilter() {
        val newReceipts = ArrayList<Receipt>()

        for (receipt in receiptsViewModel.getReceipts()) {
            if (filterViewModel.filteredCategory.value != null) {
                if (receipt.category != filterViewModel.filteredCategory.value) {continue}
            }

            if (receipt.amount >= 0 && !filterViewModel.incomeVisible.value!!) {continue}
            if (receipt.amount < 0 && !filterViewModel.expensesVisible.value!!) {continue}

            newReceipts.add(receipt)
        }

        receiptsRecyclerViewAdapter.updateList(newReceipts)
    }
}