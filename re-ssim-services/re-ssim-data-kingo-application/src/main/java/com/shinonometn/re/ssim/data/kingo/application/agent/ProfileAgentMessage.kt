package com.shinonometn.re.ssim.data.kingo.application.agent

import com.shinonometn.re.ssim.data.kingo.application.base.CaptureTaskStage

data class ProfileAgentMessage(val stage: CaptureTaskStage, val message: String)