package com.example.smackchat.utilities

const val BASE_URL = "https://chatsmackjs.herokuapp.com/v1/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CRATE_USER ="${BASE_URL}user/add"
const val URL_GET_USER_BY_EMAIL = "${BASE_URL}user/byEmail/"
const val URL_CHANNELS = "${BASE_URL}channel"
const val URL_GET_MESSAGE ="${BASE_URL}message/byChannel/"
//"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
const val EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z]+)*(\\.[A-Za-z]{2,})$"
const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
//Broadcast Constants
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"

const val SOCKET_URL = "https://chatsmackjs.herokuapp.com/"

const val PREFS_FILENAME = "prefs"
const val IS_LOGGED_IN = "isLoggedIn"
const val AUTH_TOKEN = "authToken"
const val USER_EMAIL = "userEmail"