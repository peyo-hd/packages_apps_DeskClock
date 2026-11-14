package com.android.deskclock.appaf

import android.content.Context
import android.util.Log
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.GenericDocument
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.RemoveByDocumentIdRequest
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.builtintypes.Timer
import androidx.appsearch.platformstorage.PlatformStorage
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TimerAppSearchManager(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private var appSearchSession: AppSearchSession? = null
    private val isInitialized = MutableStateFlow(false)

    init {
        scope.launch(Dispatchers.IO) {
            try {
                val session = PlatformStorage.createSearchSessionAsync(
                    PlatformStorage.SearchContext.Builder(context, DATABASE_NAME).build()
                ).await(context.mainExecutor)
                val schemaRequest = SetSchemaRequest.Builder()
                    .addDocumentClasses(Timer::class.java)
                    .build()
                session.setSchemaAsync(schemaRequest).await(context.mainExecutor)
                appSearchSession = session
                isInitialized.value = true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize TimerAppSearchManager", e)
            }
        }
    }

    private suspend fun awaitInitialization() {
        isInitialized.first { it }
    }

    suspend fun addTimer(timer: Timer) {
        awaitInitialization()
        val session = appSearchSession ?: return
        withContext(Dispatchers.IO) {
            val request = PutDocumentsRequest.Builder()
                .addDocuments(timer)
                .build()
            session.putAsync(request).await(context.mainExecutor)
        }
    }

    fun deleteTimer(namespace: String, id: String) {
        scope.launch {
            runCatching { removeTimer(namespace, id) }
                .onFailure { Log.e(TAG, "Failed to remove timer id=$id", it) }
        }
    }

    suspend fun removeTimer(namespace: String, id: String) {
        awaitInitialization()
        val session = appSearchSession ?: return
        withContext(Dispatchers.IO) {
            val request = RemoveByDocumentIdRequest.Builder(namespace)
                .addIds(id)
                .build()
            session.removeAsync(request).await(context.mainExecutor)
        }
    }

    suspend fun queryLatestTimers(queryExpression: String = ""): List<Timer> {
        awaitInitialization()
        val session = appSearchSession ?: return emptyList()
        return withContext(Dispatchers.IO) {
            val spec = SearchSpec.Builder()
                .addFilterDocumentClasses(Timer::class.java)
                .setResultCountPerPage(100)
                .build()
            val results = session.search(queryExpression, spec)
            val timers = mutableListOf<Timer>()
            var page = results.nextPageAsync.await(context.mainExecutor)
            while (page.isNotEmpty()) {
                for (result in page) {
                    val doc: GenericDocument = result.genericDocument
                    timers.add(doc.toDocumentClass(Timer::class.java))
                }
                page = results.nextPageAsync.await(context.mainExecutor)
            }
            timers
        }
    }

    private suspend fun <T> ListenableFuture<T>.await(executor: Executor): T =
        suspendCancellableCoroutine { cont ->
            addListener(
                {
                    try {
                        cont.resume(get())
                    } catch (e: Exception) {
                        cont.resumeWithException(e.cause ?: e)
                    }
                },
                executor,
            )
            cont.invokeOnCancellation { cancel(false) }
        }

    companion object {
        const val DATABASE_NAME = "deskclock_timer_db"
        private const val TAG = "TimerAppSearchManager"
    }
}
