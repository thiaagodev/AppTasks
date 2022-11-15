package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.TaskRepository

class TaskFormViewModel(application: Application) : AndroidViewModel(application) {

    private val priorityRepository = PriorityRepository(application.applicationContext)
    private val taskRepository = TaskRepository(application.applicationContext)

    private val _priorityList = MutableLiveData<List<PriorityModel>>()
    private val _taskSave = MutableLiveData<ValidationModel>()
    private val _task = MutableLiveData<TaskModel>()
    private val _taskLoad = MutableLiveData<ValidationModel>()

    val priorityList: LiveData<List<PriorityModel>> = _priorityList
    val taskSave: LiveData<ValidationModel> = _taskSave
    val task: LiveData<TaskModel> = _task
    val taskLoad: LiveData<ValidationModel> = _taskLoad

    fun loadPriorities() {
        _priorityList.value = priorityRepository.list()
    }

    fun save(task: TaskModel) {
        val listener = object : APIListener<Boolean> {
            override fun onSucess(model: Boolean) {
                _taskSave.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _taskSave.value = ValidationModel(message)
            }

        }

        if (task.id == 0) {
            taskRepository.create(task, listener)
        } else {
            taskRepository.update(task, listener)
        }

    }

    fun load(id: Int) {
        taskRepository.load(id, object : APIListener<TaskModel> {
            override fun onSucess(model: TaskModel) {
                _task.value = model
            }

            override fun onFailure(message: String) {
                _taskLoad.value = ValidationModel(message)
            }

        })
    }

}