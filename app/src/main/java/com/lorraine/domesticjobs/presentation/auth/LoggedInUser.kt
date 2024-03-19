package com.lorraine.domesticjobs.presentation.auth

//No usages
open class LoggedInUser {
    object Student : LoggedInUser()
    object Staff : LoggedInUser()
}
// fun getUserData(collection: String) {
//    var role by mutableStateOf("")
//    _registerState.update {
//        it.copy(
//            isLoading = true,
//        )
//    }
//    viewModelScope.launch {
//        storageService.getUserData(
//            collection = collection,
//            uid = currentUserId,
//            onSuccess = { document ->
//                role = document.getString("role").orEmpty()
//                _registerState.update {
//                    it.copy(
//                        role = role,
//                    )
//                }
//                val fullName = document.getString("fullName") ?: ""
//                val email = document.getString("email") ?: ""
//                val phoneNumber = document.getString("phoneNumber") ?: ""
//
//                if (role == Contants.StudentText) {
//                    val registrationNumber = document.getString("registrationNumber") ?: ""
//                    _studentData.update {
//                        it.copy(
//                            fullName = fullName,
//                            email = email,
//                            registrationNumber = registrationNumber,
//                            phoneNumber = phoneNumber,
//                            role = role,
//                        )
//                    }
//                    Log.d("Vm", _registerState.value.role)
//                    Log.d("Vm", _studentData.value.phoneNumber)
//                    Log.d("Vm", _studentData.value.registrationNumber)
//                    Log.d("Vm", _studentData.value.email)
//                } else {
//                    _staffData.update {
//                        it.copy(
//                            fullName = fullName,
//                            email = email,
//                            phoneNumber = phoneNumber,
//                            role = role,
//                        )
//                    }
//                }
//            },
//        )
//    }
//    _registerState.update {
//        it.copy(
//            isLoading = false,
//        )
//    }
// }
