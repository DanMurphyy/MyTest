package com.danmurphyy.testapphotelbooking

interface IViewHandler {
    fun showProgressBar() {}

    fun hideProgressBar() {}

    fun showError(error: String) {}
}