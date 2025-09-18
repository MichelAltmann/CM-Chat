package com.cmchat.retrofit

import com.cmchat.model.LoginRequest
import com.cmchat.model.User
import com.cmchat.retrofit.model.FriendsResponse
import com.cmchat.retrofit.model.ImageResponse
import com.cmchat.retrofit.model.InfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
                    e.message
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
                        e.message
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
                    e.message
                }
                NetworkResponse.Failed(Exception(errorMessage))
            }
        } catch (e : Exception){
            NetworkResponse.Failed(e)
        }
    }

    override suspend fun acceptFriend(id: Int, friendId: Int): NetworkResponse<InfoResponse> {
        return try {
            val response = apiService.acceptFriend(id, friendId)
            if (response.isSuccessful){
                NetworkResponse.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("message")
                } catch (e : JSONException){
                    e.message
                }
                NetworkResponse.Failed(Exception(errorMessage))
            }
        } catch (e : Exception){
            NetworkResponse.Failed(e)
        }
    }

    override suspend fun refuseFriend(id: Int, friendId: Int): NetworkResponse<InfoResponse> {
        return try {
            val response = apiService.refuseFriend(id, friendId)
            if (response.isSuccessful){
                NetworkResponse.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("message")
                } catch (e : JSONException){
                    e.message
                }
                NetworkResponse.Failed(Exception(errorMessage))
            }
        } catch (e : Exception){
            NetworkResponse.Failed(e)
        }
    }

    override suspend fun uploadImage(image: MultipartBody.Part): NetworkResponse<ImageResponse> {
        return try {
            val response = apiService.uploadImage(image)
            if (response.isSuccessful){
                NetworkResponse.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("message")
                } catch (e : JSONException){
                    e.message
                }
                NetworkResponse.Failed(Exception(errorMessage))
            }
        } catch (e : Exception){
            NetworkResponse.Failed(e)
        }
    }

    override suspend fun deleteImage(imageId : String?): NetworkResponse<InfoResponse> {
        return try {
            val response = apiService.deleteImage(imageId)
            if (response.isSuccessful){
                NetworkResponse.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("message")
                } catch (e : JSONException){
                    e.message
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
    suspend fun acceptFriend(id : Int, friendId : Int) : NetworkResponse<InfoResponse>
    suspend fun refuseFriend(id : Int, friendId : Int) : NetworkResponse<InfoResponse>
    suspend fun uploadImage(image : MultipartBody.Part) : NetworkResponse<ImageResponse>
    suspend fun deleteImage(imageId : String?) : NetworkResponse<InfoResponse>
}