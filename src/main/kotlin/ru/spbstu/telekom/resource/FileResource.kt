package ru.spbstu.telekom.resource

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.FileNotFoundException
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Files.copy
import java.nio.file.Path
import java.nio.file.Paths.get
import java.nio.file.StandardCopyOption
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

@RestController
@RequestMapping("/file")
class FileResource {

    @GetMapping("/all")
    fun getFilesNames(): ResponseEntity<List<String>> {
        val namesList = mutableListOf<String>()
        val filePath = get(DIRECTORY).toAbsolutePath().normalize().listDirectoryEntries()
        filePath.forEach {
            namesList.add(it.name)
        }
        return ResponseEntity.ok().body(namesList)
    }

    @PostMapping("/upload")
    fun uploadFiles(@RequestParam("files") files: List<MultipartFile>): ResponseEntity<List<String>> {
        val namesList = mutableListOf<String>()
        files.forEach {
            val name = StringUtils.cleanPath(it.originalFilename ?: "")
            val storage = get(DIRECTORY, name).toAbsolutePath().normalize()
            copy(it.inputStream, storage, StandardCopyOption.REPLACE_EXISTING)
            namesList.add(name)
        }
        return ResponseEntity.ok().body(namesList)
    }

    @GetMapping("/download/{fileName}")
    fun downloadFile(@PathVariable("fileName") fileName: String): ResponseEntity<Resource> {
        val filePath = get(DIRECTORY).toAbsolutePath().normalize().resolve(fileName)
        if (!Files.exists(filePath)) {
            throw FileNotFoundException("$fileName was not found")
        }
        val resource = UrlResource(filePath.toUri())
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=$fileName")
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
            .headers(headers).body(resource)
    }

    companion object {
        val DIRECTORY = "D:\\TelecomDownloads\\"
    }
}