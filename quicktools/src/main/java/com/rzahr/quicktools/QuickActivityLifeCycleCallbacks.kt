package com.rzahr.quicktools

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * @author Rashad Zahr
 *
 * this class is used mainly to fetch the current activity displayed in the application
 */
/*class QuickActivityLifeCycleCallbacks : Application.ActivityLifecycleCallbacks {

    var currentActivity: Activity? = null

    override fun onActivityPaused(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity?) {
        //currentActivity = activity
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        // currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity?) {
        // currentActivity = activity
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        currentActivity = activity
    }
}*/



/**
 * @author Rashad Zahr
 *
 * this class is used mainly to fetch the current activity displayed in the application
 */
class QuickActivityLifeCycleCallbacksNew : Application.ActivityLifecycleCallbacks {

    var currentActivity: WeakReference<Activity?>? = null

    override fun onActivityPaused(activity: Activity?) {
        currentActivity = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity?) {
        currentActivity = WeakReference(activity)
    }

    override fun onActivityDestroyed(activity: Activity?) {
        //currentActivity = activity
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        // currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity?) {
        // currentActivity = activity
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        currentActivity = WeakReference(activity)
    }
}