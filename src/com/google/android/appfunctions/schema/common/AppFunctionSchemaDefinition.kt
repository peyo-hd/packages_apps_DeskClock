package com.google.android.appfunctions.schema.common

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class AppFunctionSchemaDefinition(
    val category: String,
    val name: String,
    val version: Int,
)
