package com.google.android.appfunctions.service

import android.app.appfunctions.AppFunctionException
import android.app.appfunctions.AppFunctionService
import android.app.appfunctions.ExecuteAppFunctionRequest
import android.app.appfunctions.ExecuteAppFunctionResponse
import android.content.pm.SigningInfo
import android.os.CancellationSignal
import android.os.OutcomeReceiver
import com.google.android.appfunctions.internal.AppFunctionServiceDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class MainAppFunctionService : AppFunctionService() {

    private lateinit var serviceScope: CoroutineScope
    private lateinit var delegate: AppFunctionServiceDelegate

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        delegate = AppFunctionServiceDelegate(
            appContext = applicationContext,
            workerCoroutineContext = Dispatchers.IO,
            mainCoroutineContext = Dispatchers.Main.immediate,
        )
    }

    override fun onExecuteFunction(
        request: ExecuteAppFunctionRequest,
        callingPackage: String,
        callingPackageSigningInfo: SigningInfo,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<ExecuteAppFunctionResponse, AppFunctionException>,
    ) {
        val job = delegate.onExecuteFunction(
            request = request,
            callingPackageName = callingPackage,
            scope = serviceScope,
            callback = callback,
        )
        cancellationSignal.setOnCancelListener {
            job.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::serviceScope.isInitialized) {
            serviceScope.cancel()
        }
    }
}
