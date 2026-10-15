package com.example.booknest.util

/**
 * Why a request failed.
 *
 * The data layer only reports *what* went wrong. The UI decides how to word it
 * (see ui/ErrorMessages.kt), so no user-facing text lives in the data layer and
 * raw exception messages are never shown to the user.
 */
enum class AppError {
    /** The login endpoint rejected the username/password (HTTP 4xx). */
    INVALID_CREDENTIALS,

    /** The dashboard endpoint rejected the keypass (HTTP 4xx), or no keypass was supplied. */
    INVALID_KEYPASS,

    /** No connection, DNS failure or connection dropped. */
    NETWORK,

    /** The server did not respond in time. */
    TIMEOUT,

    /** The server failed (HTTP 5xx). */
    SERVER,

    /** The response was empty or its JSON did not match the expected shape. */
    UNEXPECTED_RESPONSE,

    /** Anything else. */
    UNKNOWN
}
