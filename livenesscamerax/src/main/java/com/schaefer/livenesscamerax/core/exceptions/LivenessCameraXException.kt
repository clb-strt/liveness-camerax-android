package com.schaefer.livenesscamerax.core.exceptions

internal sealed class LivenessCameraXException : Exception() {

    data class StartCameraException(
        override val message: String?,
        override val cause: Throwable?
    ) : LivenessCameraXException()

    data class ContextSwitchException(
        override val message: String? = ErrorsTypes.CONTEXT_SWITCHED.name,
        override val cause: Throwable? = null
    ) : LivenessCameraXException()
}
