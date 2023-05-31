package com.cmchat.retrofit

import com.cmchat.model.LoginRequest
import com.cmchat.model.User
import com.cmchat.retrofit.model.FriendsResponse
import com.cmchat.retrofit.model.InfoResponse
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

    override suspend fun getFriends(id: Int, status : Int): NetworkResponse<FriendsResponse> {
        return try {
            val response = apiService.getFriends(id, status)
            if (response.isSuccessful){
                NetworkResponse.Success(response.body()!!)
            } else {
                NetworkResponse.Failed(Exception())
            }
        } catch (e : Exception){
            NetworkResponse.Failed(e)
        }
    }

    override suspend fun signup(user: User): NetworkResponse<InfoResponse> {
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

    override suspend fun edit(user: User): NetworkResponse<User> {
            return try {
                val response = apiService.edit(user)
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

    override suspend fun addFriend(id: Int, friendUsername: String): NetworkResponse<InfoResponse> {
        return try {
            val response = apiService.addFriend(id, friendUsername)
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
    suspend fun getFriends(id: Int, status : Int) : NetworkResponse<FriendsResponse>
    suspend fun signup(user: User) : NetworkResponse<InfoResponse>
    suspend fun edit(user: User) : NetworkResponse<User>
    suspend fun addFriend(id: Int, friendUsername : String) : NetworkResponse<InfoResponse>
}