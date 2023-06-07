package com.canolabs.zonablava.helpers

object Constants {

    // Default Location which is Valencia
    const val DEFAULT_LOCATION_LATITUDE = 39.465421
    const val DEFAULT_LOCATION_LONGITUDE = -0.369390

    // REGEXP patterns
    const val USERNAME_PATTERN = "^[\\w.@+-]+\\Z"
    const val NAME_PATTERN = "^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\h]+$"
    const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z._-]+\\.+[a-z]+"
    const val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&?!+=]).{8,}\$"

}