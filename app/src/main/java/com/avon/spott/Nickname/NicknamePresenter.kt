package com.avon.spott.Nickname

import android.annotation.SuppressLint
import com.avon.spott.Data.NicknmaeResult
import com.avon.spott.Data.Token
import com.avon.spott.Data.User
import com.avon.spott.Utils.*
import com.google.gson.GsonBuilder
import retrofit2.HttpException

class NicknamePresenter(val nicknameView: NicknameContract.View) : NicknameContract.Presenter {

    private val TAG: String = "NicknamePresenter"

    init {
        nicknameView.presenter = this
    }

    override fun navigateUp() {
        nicknameView.navigateUp()
    }

    override fun isNickname(nickname: String) {
        nicknameView.enableSignUp(Validator.validNickname(nickname))
    }

    @SuppressLint("CheckResult")
    override fun signUp(baseUrl: String, user: User) {
        logd(TAG, "PASER : "+Parser.toJson(user)) //테스트테스트
            Retrofit(baseUrl).postFieldNonHeader("/spott/account", Parser.toJson(user)).subscribe({ response ->
            logd(TAG, "response code: ${response.code()}, response body : ${response.body()}")
            val result = response.body()?.let { Parser.fromJson<NicknmaeResult>(it) }
            if (result != null) {
                nicknameView.signUp(result.sign_up)
            }
        }, { throwable ->
            loge(TAG, throwable.message)
            if (throwable is HttpException) {
                val exception = throwable
                loge(
                    TAG,
                    "http exception code: ${exception.code()}, http exception message: ${exception.message()}"
                )
            }
        })
    }

    @SuppressLint("CheckResult")
    override fun getToken(baseUrl: String, email: String, password: String) {
        Retrofit(baseUrl).signIn("/spott/token", email, password).subscribe({ response ->
            logd(TAG, "response code: ${response.code()}, response body : ${response.body()}")

            val jsonObj = response.body()
            if(jsonObj != null) {
                val token = GsonBuilder().create().fromJson(jsonObj, Token::class.java)
                nicknameView.getToken(token)
            }
        }, { throwable ->
            loge(TAG, throwable.message)
            if (throwable is HttpException) {
                loge(
                    TAG,
                    "http exception code: ${throwable.code()}, http exception message: ${throwable.message()}"
                )
            }
        })
    }
}