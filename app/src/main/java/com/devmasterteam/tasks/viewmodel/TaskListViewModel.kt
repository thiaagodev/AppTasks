package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.repository.TaskRepository

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(application.applicationContext)

    private val _tasks = MutableLiveData<List<TaskModel>>()

    val tasks: LiveData<List<TaskModel>> = _tasks

    fun list() {
        taskRepository.list(object : APIListener<List<TaskModel>> {
            override fun onSucess(model: List<TaskModel>) {
                _tasks.value = model
            }

            override fun onFailure(message: String) {

            }

        })
    }

}