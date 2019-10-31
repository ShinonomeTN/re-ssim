package com.shinonometn.re.ssim.data.kingo.application.service

import com.shinonometn.commons.tools.Randoms
import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.dto.CaptureTaskDetails
import com.shinonometn.re.ssim.data.kingo.application.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.entity.CaterpillarSetting
import com.shinonometn.re.ssim.data.kingo.application.repository.CaptureTaskRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
open class TaskService(private val captureTaskRepository: CaptureTaskRepository,
                  private val caterpillarService: CaterpillarService,
                  private val bundleService: BundleService,
                  private val importService: ImportService) {

    open fun get(id: Int): Optional<CaptureTaskDetails> {
        val captureTask: CaptureTask = captureTaskRepository.findById(id).orElse(null) ?: return Optional.empty()

        return Optional.of(CaptureTaskDetails().apply {
            this.taskInfo = captureTask
            this.bundling = bundleService.getBundleInfo(captureTask)
            this.taskThread = caterpillarService.getSpiderStatus(captureTask.id!!).orElse(null)
            this.importing = importService.getByTaskId(captureTask.id!!).orElse(null)
        })
    }

    open fun findAll(pageable: Pageable): Page<CaptureTaskDetails> {
        return captureTaskRepository.findAll(pageable).map {
            CaptureTaskDetails().apply {
                this.taskInfo = it
                this.bundling = bundleService.getBundleInfo(it)
                this.taskThread = caterpillarService.getSpiderStatus(it.id!!).orElse(null)
                this.importing = importService.getByTaskId(it.id!!).orElse(null)
            }
        }
    }

    /**
     * Create a termName capture task with code
     *
     * @param termCode termName code
     * @return created task
     */
    open fun create(termCode: String, caterpillarSetting: CaterpillarSetting): CaptureTask {
        val captureTask = CaptureTask()

        captureTask.createDate = Date()

        captureTask.termCode = termCode

        captureTask.termName = caterpillarService
                .cachedTermItemList()
                .find { it.identity == termCode }?.title ?: throw BusinessException("term_not_exists")

        captureTask.versionCode = Randoms.randomTimeId()

        captureTask.captureProfile = caterpillarSetting.caterpillarProfile

        return captureTaskRepository.save(captureTask)
    }

    /**
     * Delete a task
     *
     * @param id task id
     */
    open fun delete(id: Int) {
        caterpillarService.getSpiderStatus(id).ifPresent {
            if ("Running" == it.status) it.stop()
            caterpillarService.removeSpider(id)
        }

        captureTaskRepository.deleteById(id)
        bundleService.deleteByTask(id)
        importService.deleteByTask(id)
    }
}