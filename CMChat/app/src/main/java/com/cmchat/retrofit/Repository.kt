package com.cmchat.retrofit

import com.cmchat.model.LoginRequest
import com.cmchat.model.User

class Repository(private val apiService: ApiService) : RepositoryInterface{
    override suspend fun login(loginRequest: LoginRequest) : NetworkResponse<User> {
        return try {
            val response = apiService.login(loginRequest = loginRequest)
            if (response.isSuccessful){
                NetworkResponse.Success(response.body()!!)
            } else {
                NetworkResponse.Failed(Exception())
            }
        } catch (e : Exception){
            NetworkResponse.Failed(e)
        }
    }
}

interface RepositoryInterface {
    suspend fun login(loginRequest: LoginRequest) : NetworkResponse<User>
}