package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.remote.PersonService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val personRepository = PersonRepository(application.applicationContext)
    private val securityPreferences = SecurityPreferences(application.applicationContext)

    private val _create = MutableLiveData<ValidationModel>()

    val create: LiveData<ValidationModel> = _create

    fun create(name: String, email: String, password: String) {
        personRepository.create(name, email, password, object : APIListener<PersonModel> {
            override fun onSucess(model: PersonModel) {
                securityPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                securityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                securityPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                RetrofitClient.addHeaders(model.token, model.personKey)

                _create.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _create.value = ValidationModel(message)
            }

        })
    }

}