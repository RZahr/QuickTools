package com.rzahr.quicktools

import android.app.Activity
import android.app.Application
import android.app.IntentService
import android.app.Service
import android.content.Context
import javax.inject.Inject

/**
 * @author Rashad Zahr
 *
 * this class is *required* to be created from the application class
 */
@Suppress("unused")
class QuickInjectable @Inject constructor(var quickPref: QuickPref, val clickGuard: QuickClickGuard, application: Application) {

    //val mQuickActivityLifecycleCallbacks = QuickActivityLifeCycleCallbacks()
    val mQuickActivityLifecycleCallbacks = QuickActivityLifeCycleCallbacksNew()
    lateinit var mApplication: Application

    init {

        instance = this
        registerActivityLifeCycleCallbacks(application)
    }

    private fun registerActivityLifeCycleCallbacks(application: Application) {

        setApplication(application)
        application.registerActivityLifecycleCallbacks(mQuickActivityLifecycleCallbacks)
    }

    private fun setApplication(application: Application) {

        mApplication = application
    }

    companion object {

        private var instance: QuickInjectable? = null

        fun pref() : QuickPref {

            return instance!!.quickPref
        }

        fun clickGuard() : QuickClickGuard {

            return instance!!.clickGuard
        }

        fun currentActivity(): Activity? {

            //return instance?.mQuickActivityLifecycleCallbacks?.currentActivity?
            return instance?.mQuickActivityLifecycleCallbacks?.currentActivity?.get()
        }

        fun applicationContext() : Context {
            return instance!!.mApplication
        }

        fun get(activity: Activity): Application {

            return activity.application
        }

        fun get(service: IntentService): Application {

            return service.application
        }

        fun get(service: Service): Application {

            return service.application
        }
    }
}