package com.mvvm.component.ext

import java.util.regex.Pattern

/**
 * Extension method to check if String is Phone Number.
 */
fun String.isPhone(): Boolean {
    val pattern = Pattern.compile("^0?(1[3-9][0-9])[0-9]{8}$")
    return pattern.matcher(this).matches()
}

/**
 * Extension method to check if String is Email.
 */
fun String.isEmail(): Boolean {
    val pattern = Pattern.compile("^([a-z0-9A-Z]+[-|_|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}\$")
    return pattern.matcher(this).matches()
}