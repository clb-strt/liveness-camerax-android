package com.schaefer.livenesscamerax.domain.checker

import com.schaefer.livenesscamerax.core.extensions.orFalse
import com.schaefer.livenesscamerax.domain.model.FaceResult
import com.schaefer.livenesscamerax.domain.model.HeadMovement

private const val EYE_OPENED_PROBABILITY = 0.4F
private const val IS_SMILING_PROBABILITY = 0.3F
private const val EULER_Y_RIGHT_MOVEMENT = 35
private const val EULER_Y_LEFT_MOVEMENT = -35

internal class LivenessCheckerImpl : LivenessChecker {

    override fun isEyeOpened(eyeOpenedProbabilityValue: Float?): Boolean {
        return eyeOpenedProbabilityValue?.let { (it > EYE_OPENED_PROBABILITY) }.orFalse()
    }

    override fun checkBothEyes(
        leftEyeProbability: Float?,
        rightEyeProbability: Float?,
        callbackBlinked: (Boolean) -> Unit
    ): Boolean {
        val leftEyeProbability = leftEyeProbability?.let { it > EYE_OPENED_PROBABILITY }.orFalse()
        val rightEyeProbability = rightEyeProbability?.let { it > EYE_OPENED_PROBABILITY }.orFalse()

        return (leftEyeProbability && rightEyeProbability)
            .also {
                if (it.not()) callbackBlinked.invoke(true)
            }
    }

    override fun hasMoreThanOneFace(listFaceResult: List<FaceResult>) =
        listFaceResult.isNotEmpty() && listFaceResult.size > 1

    override fun checkSmile(
        smilingProbability: Float?,
        callbackSmiled: (Boolean) -> Unit
    ): Boolean {
        return smilingProbability?.let { it > IS_SMILING_PROBABILITY }.orFalse().also { isSmiling ->
            if (isSmiling) callbackSmiled.invoke(isSmiling)
        }
    }

    override fun detectEulerYMovement(
        headEulerAngleY: Float
    ): HeadMovement {
        return when {
            headEulerAngleY > EULER_Y_RIGHT_MOVEMENT -> HeadMovement.RIGHT
            headEulerAngleY < EULER_Y_LEFT_MOVEMENT -> HeadMovement.LEFT
            else -> HeadMovement.CENTER
        }
    }

    override fun validateHeadMovement(
        face: FaceResult,
        headMovement: HeadMovement,
        removeCurrentStep: (Boolean) -> Unit
    ) {
        if (detectEulerYMovement(face.headEulerAngleY) == headMovement) {
            removeCurrentStep(true)
        }
    }

    override fun validateAtLeastOneEyeIsOpen(face: FaceResult): Boolean {
        return isEyeOpened(face.leftEyeOpenProbability) || isEyeOpened(face.rightEyeOpenProbability)
    }
}