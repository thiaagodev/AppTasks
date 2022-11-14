package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.TaskRepository

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(application.applicationContext)
    private val priorityRepository = PriorityRepository(application.applicationContext)

    private val _tasks = MutableLiveData<List<TaskModel>>()
    private val _delete = MutableLiveData<ValidationModel>()

    val tasks: LiveData<List<TaskModel>> = _tasks
    val delete: LiveData<ValidationModel> = _delete

    fun list() {
        taskRepository.list(object : APIListener<List<TaskModel>> {
            override fun onSucess(model: List<TaskModel>) {
                model.forEach {
                    it.priorityDescription = priorityRepository.getDescription(it.priorityId)
                }
                _tasks.value = model
            }

            override fun onFailure(message: String) {

            }

        })
    }

    fun delete(id: Int) {
        taskRepository.delete(id, object : APIListener<Boolean> {
            override fun onSucess(model: Boolean) {
                list()
            }

            override fun onFailure(message: String) {
                _delete.value = ValidationModel(message)
            }

        })
    }

}