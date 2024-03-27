package com.doaamosallam.trendysteps.utils

private const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
private const val MIN_PASSWORD_LENGTH = 11

// create extension function for email validation
fun String.isEmailValid(): Boolean {
    return this.isNotEmpty() && this.matches(EMAIL_PATTERN.toRegex())
}

//create extension function for password validation
fun String.isPasswordValid(): Boolean {
    return this.isNotEmpty() && this.length >= MIN_PASSWORD_LENGTH
}

//create extension function for name validation
fun String.isNameValid(): Boolean {
    return this.isNotEmpty()
}

//create extension function for confirm password validation
fun String.isConfirmPasswordValid(password: String): Boolean {
    return this == password
}
