package com.shinonometn.re.ssim.data.kingo.application.caterpillar.agent

import com.shinonometn.re.ssim.data.kingo.application.caterpillar.base.CaptureTaskStage

data class ProfileAgentMessage(val stage: CaptureTaskStage, val message: String)