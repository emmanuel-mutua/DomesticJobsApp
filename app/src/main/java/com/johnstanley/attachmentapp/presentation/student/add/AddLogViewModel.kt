package com.johnstanley.attachmentapp.presentation.student.add

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.johnstanley.attachmentapp.data.database.ImageToDeleteDao
import com.johnstanley.attachmentapp.data.database.ImageToUploadDao
import com.johnstanley.attachmentapp.data.database.entity.ImageToDelete
import com.johnstanley.attachmentapp.data.database.entity.ImageToUpload
import com.johnstanley.attachmentapp.data.model.AttachmentLog
import com.johnstanley.attachmentapp.data.model.GalleryImage
import com.johnstanley.attachmentapp.data.model.GalleryState
import com.johnstanley.attachmentapp.data.model.RequestState
import com.johnstanley.attachmentapp.data.repository.FirebaseAttachmentLogRepo
import com.johnstanley.attachmentapp.utils.Contants.ADD_SCREEN_ARGUMENT_KEY
import com.johnstanley.attachmentapp.utils.fetchImagesFromFirebase
import com.johnstanley.attachmentapp.utils.toInstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AddLogViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imageToUploadDao: ImageToUploadDao,
    private val imageToDeleteDao: ImageToDeleteDao,
) : ViewModel() {
    val galleryState = GalleryState()
    var uiState by mutableStateOf(UiState())
        private set

    init {
        getAttachmentLogIdArgument()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fetchSelectedAttachmentLog()
        }
    }

    private fun getAttachmentLogIdArgument() {
        uiState = uiState.copy(
            selectedAttachLogId = savedStateHandle.get<String>(
                key = ADD_SCREEN_ARGUMENT_KEY,
            ),
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchSelectedAttachmentLog() {
        if (uiState.selectedAttachLogId != null) {
            viewModelScope.launch {
                FirebaseAttachmentLogRepo.getSelectedAttachmentLog(attachmentId = uiState.selectedAttachLogId!!)
                    .catch {
                        emit(RequestState.Error(Exception("Attachment Log is already deleted.")))
                    }
                    .collect { attachmentLog ->
                        if (attachmentLog is RequestState.Success) {
                            setSelectedLog(attachmentLog = attachmentLog.data)
                            setTitle(title = attachmentLog.data.title)
                            setDescription(description = attachmentLog.data.description)

                            fetchImagesFromFirebase(
                                remoteImagePaths = attachmentLog.data.images,
                                onImageDownload = { downloadedImage ->
                                    galleryState.addImage(
                                        GalleryImage(
                                            image = downloadedImage,
                                            remoteImagePath = extractImagePath(
                                                fullImageUrl = downloadedImage.toString(),
                                            ),
                                        ),
                                    )
                                },
                            )
                        }
                    }
            }
        }
    }

    private fun setSelectedLog(attachmentLog: AttachmentLog) {
        uiState = uiState.copy(selectedAttachLog = attachmentLog)
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            uiState = uiState.copy(updatedDateTime = zonedDateTime.toLocalDateTime().toInstant())
        }
    }

    fun upsertLog(
        attachmentLog: AttachmentLog,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedAttachLogId != null) {
                updateAttachmentLog(attachmentLog = attachmentLog, onSuccess = onSuccess, onError = onError)
            } else {
                insertAttachmentLog(
                    attachmentLog = attachmentLog,
                    onSuccess = onSuccess,
                    onError = onError,
                )
            }
        }
    }

    private suspend fun insertAttachmentLog(
        attachmentLog: AttachmentLog,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val result =
            FirebaseAttachmentLogRepo.insertAttachmentLog(
                attachmentLog = attachmentLog.apply {
                    if (uiState.updatedDateTime != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            date =   uiState.updatedDateTime?.toEpochMilli() ?: 0L
                        }
                    }
                },
            )
        if (result is RequestState.Success) {
            uploadImagesToFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }

    private suspend fun updateAttachmentLog(
        attachmentLog: AttachmentLog,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val result =
            FirebaseAttachmentLogRepo.updateAttachmentLog(
                attachmentLog = attachmentLog.apply {
                    id = uiState.selectedAttachLogId!!
                    date = if (uiState.updatedDateTime != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            uiState.updatedDateTime?.toEpochMilli() ?: 0L
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }
                    } else {
                        uiState.selectedAttachLog!!.date
                    }
                },
            )
        if (result is RequestState.Success) {
            uploadImagesToFirebase()
            deleteImagesFromFirebase()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }

    fun deleteAttachmentLog(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedAttachLogId != null) {
                val result =
                    FirebaseAttachmentLogRepo.deleteAttachmentLog(id = uiState.selectedAttachLogId!!)
                if (result is RequestState.Success) {
                    withContext(Dispatchers.Main) {
                        uiState.selectedAttachLog?.let {
                            deleteImagesFromFirebase(images = it.images)
                        }
                        onSuccess()
                    }
                } else if (result is RequestState.Error) {
                    withContext(Dispatchers.Main) {
                        onError(result.error.message.toString())
                    }
                }
            }
        }
    }

    fun addImage(image: Uri, imageType: String) {
        val remoteImagePath = "images/${FirebaseAuth.getInstance().currentUser?.uid}/" +
            "${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath,
            ),
        )
    }

    private fun uploadImagesToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
                .addOnProgressListener {
                    val sessionUri = it.uploadSessionUri
                    if (sessionUri != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            imageToUploadDao.addImageToUpload(
                                ImageToUpload(
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    imageUri = galleryImage.image.toString(),
                                    sessionUri = sessionUri.toString(),
                                ),
                            )
                        }
                    }
                }
        }
    }

    private fun deleteImagesFromFirebase(images: List<String>? = null) {
        val storage = FirebaseStorage.getInstance().reference
        if (images != null) {
            images.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch(Dispatchers.IO) {
                            imageToDeleteDao.addImageToDelete(
                                ImageToDelete(remoteImagePath = remotePath),
                            )
                        }
                    }
            }
        } else {
            galleryState.imagesToBeDeleted.map { it.remoteImagePath }.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch(Dispatchers.IO) {
                            imageToDeleteDao.addImageToDelete(
                                ImageToDelete(remoteImagePath = remotePath),
                            )
                        }
                    }
            }
        }
    }

    private fun extractImagePath(fullImageUrl: String): String {
        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }
}

data class UiState(
    val selectedAttachLogId: String? = null,
    val selectedAttachLog: AttachmentLog? = null,
    val title: String = "",
    val description: String = "",
    val updatedDateTime: Instant? = null,
)
