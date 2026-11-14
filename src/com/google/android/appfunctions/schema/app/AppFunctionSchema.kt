package com.google.android.appfunctions.schema.app

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class AppFunctionSchema(
    val category: String,
    val name: String,
    val version: Int,
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class AppSchemaImpl(
    val description: String = "",
    val displayNameStringRes: Int = 0,
    val enabledByDefault: Boolean = true,
    val restrictCallersWithExecuteAppFunctions: Boolean = false,
)
