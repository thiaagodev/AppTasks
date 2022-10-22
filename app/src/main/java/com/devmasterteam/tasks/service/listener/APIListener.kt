package com.devmasterteam.tasks.service.listener

interface APIListener<T> {
    fun onSucess(model: T)

    fun onFailure(message: String)
}