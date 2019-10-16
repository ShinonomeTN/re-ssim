package com.shinonometn.re.ssim.caterpillar.application.commons.agent

import com.shinonometn.re.ssim.caterpillar.application.commons.CaptureTaskStage

data class ProfileAgentMessage(val stage : CaptureTaskStage, val message: String)