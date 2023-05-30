package com.cmchat.retrofit

import com.cmchat.model.LoginRequest
import com.cmchat.model.User
import com.cmchat.retrofit.model.FriendsResponse
import com.cmchat.retrofit.model.SignupResponse
import org.json.JSONException
import org.json.JSONObject

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

    override suspend fun getFriends(id: Int): NetworkResponse<FriendsResponse> {
        return try {
            val response = apiService.getFriends(id)
            if (response.isSuccessful){
                NetworkResponse.Success(response.body()!!)
            } else {
                NetworkResponse.Failed(Exception())
            }
        } catch (e : Exception){
            NetworkResponse.Failed(e)
        }
    }

    override suspend fun signup(user: User): NetworkResponse<SignupResponse> {
        return try {
            val response = apiService.signup(user)
            if (response.isSuccessful){
                NetworkResponse.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("message")
                } catch (e : JSONException){
                    "Uknown error"
                }
                NetworkResponse.Failed(Exception(errorMessage))
            }
        } catch (e : Exception){
            NetworkResponse.Failed(e)
        }
    }
}

interface RepositoryInterface {
    suspend fun login(loginRequest: LoginRequest) : NetworkResponse<User>
    suspend fun getFriends(id: Int) : NetworkResponse<FriendsResponse>

    suspend fun signup(user: User) : NetworkResponse<SignupResponse>
}