package com.shinonometn.re.ssim.data.kingo.application.courses.service

import com.shinonometn.re.ssim.commons.BusinessException
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.entity.CaptureTask
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.repository.CaptureTaskRepository
import com.shinonometn.re.ssim.data.kingo.application.caterpillar.service.CaterpillarFileManageService
import com.shinonometn.re.ssim.data.kingo.application.courses.commons.ImportTaskStatus
import com.shinonometn.re.ssim.data.kingo.application.courses.entity.ImportTask
import com.shinonometn.re.ssim.data.kingo.application.courses.repository.ImportTaskRepository
import com.shinonometn.re.ssim.data.kingo.application.courses.task.CourseDataImportTask
import org.apache.commons.io.FileUtils
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.core.task.TaskExecutor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
class ImportTaskService(private val captureTaskRepository: CaptureTaskRepository,
                        private val importTaskRepository: ImportTaskRepository,
                        private val mongoTemplate: MongoTemplate,
                        private val fileManageService: CaterpillarFileManageService,
                        private val taskExecutor: TaskExecutor) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun list(pageable: Pageable): Page<ImportTask> = importTaskRepository.findAll(pageable)

    fun save(importTask: ImportTask): ImportTask = importTaskRepository.save(importTask)

    fun delete(taskId: Int) {
        val importTask = importTaskRepository
                .findById(taskId)
                .orElseThrow { BusinessException("import_task_not_exists") }

        if (importTask.status == ImportTaskStatus.IMPORTING) throw BusinessException("import_task_running")

        importTaskRepository.delete(importTask)

        if (importTask.captureTaskId == null || !captureTaskRepository.existsById(importTask.captureTaskId!!)) {
            try {
                FileUtils.deleteDirectory(fileManageService.contextOf(importTask.captureTaskId).file)
            } catch (e: IOException) {
                logger.warn("Could not delete directory for import task $taskId")
            }

        }
    }

    fun isCaptureTaskRelated(captureTaskId: String): Boolean {
        return importTaskRepository.existsByCaptureTaskId(captureTaskId.toInt())
    }

    fun latestVersionOf(termName: String): String? {
        // Get latest version from finished import tasks
        val latestTask = mongoTemplate.query(ImportTask::class.java)
                .matching(query(where("termName").`is`(termName).and("finishDate").ne(null)))
                .stream()
                .max(Comparator.comparingLong { l -> l.finishDate!!.time })
                .orElse(null)

        return if (latestTask != null && latestTask.id != null) latestTask.id else Optional.ofNullable(courseInfoService.query(
                project("term", "batchId"),
                match(where("term").`is`(termName)),
                group("term").addToSet("batchId").`as`("versions"),
                project("versions")
        ).getUniqueMappedResult())
                .orElse(Document().append("versions", null))
                .get("versions", ArrayList<String>())
                .stream()
                .max(Comparator.naturalOrder<T>())
                .orElse(null)

        // If not found, find from exists courses
        // Because version id is batchId and it is UUID, so sort the
        // list and the latest item normally is the new version code
    }

    /**
     * Start importing data to database
     *
     * @param taskId taskId
     * @return task base info
     */
    fun start(taskId: Int): CaptureTask {

        val captureTask = captureTaskRepository.findById(taskId).orElse(null)
                ?: throw BusinessException("task_not_found")

        val dataFolder = fileManageService.contextOf(taskId)

        val importTask = ImportTask()

        importTask.dataPath = dataFolder.domainPath
        importTask.captureTaskId = taskId
        importTask.createDate = Date()

        taskExecutor.execute(CourseDataImportTask(
                this,
                save(importTask),
                dataFolder
        ))

        return captureTask
    }

    fun findOne(id: String): Optional<ImportTask> {
        return importTaskRepository.findById(id)
    }
}
