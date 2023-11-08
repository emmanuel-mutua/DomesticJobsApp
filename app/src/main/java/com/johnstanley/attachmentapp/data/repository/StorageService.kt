package com.johnstanley.attachmentapp.data.repository

import com.johnstanley.attachmentapp.data.Response
import com.johnstanley.attachmentapp.presentation.auth.StaffData
import com.johnstanley.attachmentapp.presentation.auth.StudentData

typealias AddUserResponse = Response<Boolean>

interface StorageService {
    suspend fun addStudent(user: StudentData): AddUserResponse
    suspend fun addStaff(user: StaffData): AddUserResponse
}


