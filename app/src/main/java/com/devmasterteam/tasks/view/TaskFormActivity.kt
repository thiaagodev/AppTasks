package com.devmasterteam.tasks.view

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.ActivityRegisterBinding
import com.devmasterteam.tasks.databinding.ActivityTaskFormBinding
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.viewmodel.RegisterViewModel
import com.devmasterteam.tasks.viewmodel.TaskFormViewModel
import java.text.SimpleDateFormat

class TaskFormActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: TaskFormViewModel
    private lateinit var binding: ActivityTaskFormBinding
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private var listPriority: List<PriorityModel> = listOf()
    private var taskIdentification = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Variáveis da classe
        viewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)
        binding = ActivityTaskFormBinding.inflate(layoutInflater)

        viewModel.loadPriorities()

        // Eventos
        binding.buttonSave.setOnClickListener(this)
        binding.buttonDate.setOnClickListener(this)

        loadDataFromActivity()

        observe()

        // Layout
        setContentView(binding.root)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_date -> handleDate()
            R.id.button_save -> handleSave()
        }
    }

    override fun onDateSet(v: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        binding.buttonDate.text = dateFormat.format(calendar.time)
    }

    private fun getIndex(priorityId: Int): Int {
        return listPriority.indexOf(
            listPriority.filter { it.id == priorityId }[0]
        )
    }

    private fun observe() {
        viewModel.priorityList.observe(this) { it ->
            listPriority = it
            val list = it.map { it.description }
            val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
            binding.spinnerPriority.adapter = adapter
        }

        viewModel.taskSave.observe(this) {
            if (it.status()) {
                if (taskIdentification == 0) {
                    toast("Tarefa criada com sucesso")
                } else {
                    toast("Tarefa atualizada com sucesso")
                }
                finish()
            } else {
                toast(it.message())
            }
        }

        viewModel.task.observe(this) {
            binding.editDescription.setText(it.description)
            binding.checkComplete.isChecked = it.complete

            val date = SimpleDateFormat("yyyy-MM-dd").parse(it.dueData)
            binding.buttonDate.text = dateFormat.format(date)

            binding.spinnerPriority.setSelection(getIndex(it.priorityId))
        }

        viewModel.taskLoad.observe(this) {
            if (!it.status()) {
                toast(it.message())
                finish()
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun handleSave() {
        val task = TaskModel().apply {
            this.id = taskIdentification
            this.description = binding.editDescription.text.toString()
            this.complete = binding.checkComplete.isChecked
            this.dueData = binding.buttonDate.text.toString()

            val index = binding.spinnerPriority.selectedItemPosition
            this.priorityId = listPriority[index].id
        }

        viewModel.save(task)
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras
        val taskId = bundle?.getInt(TaskConstants.BUNDLE.TASKID)

        if (taskId !== null) {
            taskIdentification = taskId
            viewModel.load(taskIdentification)
        }
    }

}