package com.danmurphyy.testapphotelbooking.utils

class EmailValidator {
    fun isValidEmail(email: String): Boolean {
        // Implement your email validation logic here
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
        return email.matches(emailRegex)
    }
}
