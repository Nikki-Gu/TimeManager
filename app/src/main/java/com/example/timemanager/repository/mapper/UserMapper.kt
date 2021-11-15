package com.example.timemanager.repository.mapper

import com.example.timemanager.db.entity.UserEntity
import com.example.timemanager.db.model.User

object UserMapper {
    fun UserEntity?.toDomain(): User? = this?.let{
        User(
            id = it.id,
            name = it.name,
            date = it.date,
            timesFocus = it.timesFocus,
            totalDuration = it.totalDuration
        )
    }

    fun User?.toDomain(): UserEntity? = this?.let{
        UserEntity(
            id = it.id,
            name = it.name,
            date = it.date,
            timesFocus = it.timesFocus,
            totalDuration = it.totalDuration
        )
    }
}