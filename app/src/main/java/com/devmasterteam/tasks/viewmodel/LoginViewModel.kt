package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.helper.BiometricHelper
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val personRepository = PersonRepository(application.applicationContext)
    private val priorityRepository = PriorityRepository(application.applicationContext)
    private val securityPreferences = SecurityPreferences(application.applicationContext)

    private val _validation = MutableLiveData<ValidationModel>()
    private val _loggedUser = MutableLiveData<Boolean>()

    val login: LiveData<ValidationModel> = _validation
    val loggedUser: LiveData<Boolean> = _loggedUser


    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        personRepository.login(email, password, object : APIListener<PersonModel> {
            override fun onSucess(model: PersonModel) {
                securityPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                securityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                securityPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                RetrofitClient.addHeaders(model.token, model.personKey)

                _validation.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _validation.value = ValidationModel(message)
            }

        })
    }

    /**
     * Verifica se usuário está logado
     */
    fun verifyAuthentication() {
        val token = securityPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val personKey = securityPreferences.get(TaskConstants.SHARED.PERSON_KEY)

        RetrofitClient.addHeaders(token, personKey)

        val logged = (token != "" && personKey != "")

        if (!logged) {
            priorityRepository.list(object : APIListener<List<PriorityModel>> {
                override fun onSucess(model: List<PriorityModel>) {
                    priorityRepository.save(model)
                }

                override fun onFailure(message: String) {
                    _validation.value = ValidationModel(message)
                }

            })
        }

        _loggedUser.value = logged && BiometricHelper.isBiometricAvailible(getApplication())

    }

}