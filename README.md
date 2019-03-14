# QuickTools

**Kotlin library containing various functions that can be used accross android projects**

[![](https://jitpack.io/v/rzahr/QuickTools.svg)](https://jitpack.io/#rzahr/QuickTools)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg)](https://github.com/dwyl/esta/issues)
[![HitCount](http://hits.dwyl.io/rzahr/quicktools.svg)](http://hits.dwyl.io/rzahr/quicktools)

Classes
========

## **[QuickInjectable]** ## 

**mandatory** to be injected in the Application class as follows:
```@Inject lateinit var mInjectable: QuickInjectable```

 **```pref()```**: function used to save and retreive from the shared preference
 
 **```clickGuard()```**: function used to prevent multi-rapid clicks on a view
 
 **```currentActivity```**: function used to retrieve the current active activity
 
 
## **[QuickNotificationUtils]** ## 

**mandatory** to be created in the application module if notifications are used in the project:

 **```initializer```**: mandatory function that will create a channel notification which can be used then across the application after being initialized from the application module
 
## **[QuickTrafficCalculator]** ## 

Used to calculate the internet consumption of the application in a month. The Consumption is automatically reset every 30 days

 **```check```**: call the following function from the MainActivity
 
 **To check the consumption:**
 
 ```
  val currentMonthDownloadConsumption = QuickTrafficCalculator.TOTAL_SAVED_DOWNLOAD_TRAFFIC_KEY.rzPrefVal<Long>() * 0.001 * 0.001
  val currentMonthUploadConsumption = QuickTrafficCalculator.TOTAL_SAVED_UPLOAD_TRAFFIC_KEY.rzPrefVal<Long>() * 0.001 * 0.001
 ```
  
## **[QuickLogWriter]** ## 

object used to perform logging:

Example use:

 ```
    QuickLogWriter.errorLogging("No results", e.toString())
    QuickLogWriter.debugLogging("Printing matches")
    QuickLogWriter.getCallerClass(3)// retrieves the calling method, line etc
    QuickLogWriter.logErrorHelper(callingMethod, msg, logFileNameTemp, error, FolderName, false)
    QuickLogWriter.appendContents("$logFileNameTemp.txt", msg, false, FolderName, false)
 ```
 
## **[QuickInternetCheckService]** ## 

service used to check for a valid internet connection when the application is in foreground:

Example use:

 ```
        fun unRegisterConnectionCheckerServiceReceiver(bManager: LocalBroadcastManager) {
            bManager.unregisterReceiver(mConnectionBroadcastReceiver)
        }
        
        fun registerConnectionCheckerServiceReceiver(bManager: LocalBroadcastManager) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(QuickInternetCheckService.KEY)
            bManager.registerReceiver(mConnectionBroadcastReceiver, intentFilter)
        }
        private var mConnectionCheckerServiceConnection: ServiceConnection? = null
        fun bindToConnectionCheckerService() {

            mConnectionCheckerServiceConnection = QuickInternetCheckService.bindToConnectionCheckerService(mConnectionCheckerServiceConnection, mContext)
        }

        fun unBindToConnectionCheckerService(){

            QuickInternetCheckService.unBindToConnectionCheckerService(mConnectionCheckerServiceConnection, mActivity)
        }
        
        private val mConnectionBroadcastReceiver = object : BroadcastReceiver() {
         override fun onReceive(context: Context, intent: Intent) {
         when {
                    intent.action == QuickInternetCheckService.KEY -> when {
                        intent.getBooleanExtra(QuickInternetCheckService.IS_ONLINE_KEY, true) -> {
                           view?.setConnectionMenuItem(R.drawable.ic_wifi)
                        else -> view?.setConnectionMenuItem(R.drawable.ic_offline)
                    }
               }}}
 ```
 
 **Other Tools:**
 ```
 QuickInternetCheckService.ONLINE_SINCE_KEY.rzPrefVal<String>()
 QuickInternetCheckService.OFFLINE_SINCE_KEY.rzPrefVal<String>()
 QuickInternetCheckService.IS_ONLINE_KEY.rzPrefVal<Boolean>()
 ```
 
 
## **[Base Classes]** ## 
 
 
 `Contains 8 different base classes that can be used accross the app`

**BaseModel**: base model class used as a supert type which helps in accessing the SQLITE database functions

**BasePresenter**: presenter class in MVP architecture used to as a super type defined with a BaseViewInterface class and a BaseModel Class 

**BaseViewInterface**: view class in MVP architecture used as a super type on an interface class

**BaseActivity**: activity class defined with a BasePresenter and implements a BaseViewInterface as per the MVP architecture

**BaseViewModel**: view-model class which implements the LifecycleObserver

**MVVMFragment**: fragment class defined with a BaseViewModel 

**MVPFragment**: fragment class defined with a BasePresenter class and implements a BaseViewInterface

**MVPFragmentDialog**: fragment class defined with a BasePresenter class and implements a BaseViewInterface


## **[Extensions]** ## 
 
**[QuickActivityExtensions]**: Contains extensions that can be used on an activity

**[QuickExtensions]**: Contains general extensions like shared preference saving and retreiving

**[QuickNotificationExtensions]**: Contains NotificationCompat.Builder extensions

**[QuickUIExtensions]**: Contains UI helper extensions


## **[Utils]** ## 

`Contains 6 different util object each containing a set of functions related to a certain aspect`

**[QuickAppUtils]**: Contains general functions related to the device Example: isNetworkAvailable, VersionName...

**[QuickDateUtils]**: Contains general functions related to fetching the current date

**[QuickDBUtils]**: Contains general functions related to initializing an SQLITE database file from the assets folder and performing select queries 

**[QuickFileUtils]**: Contains general functions related to working with files and directories

**[QuickUIUtils]**: Contains general functions related showing an alert dialog, notifications, animate background...

**[QuickUtils]**: Contains general functions


## **[All Types]** ## 
   
[QuickInjectable]: https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/QuickInjectable.kt

[QuickAppUtils]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/utils/QuickAppUtils.kt

[QuickDateUtils]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/utils/QuickDateUtils.kt

[QuickDBUtils]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/utils/QuickDBUtils.kt

[QuickFileUtils]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/utils/QuickFileUtils.kt

[QuickUIUtils]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/utils/QuickUIUtils.kt

[QuickUtils]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/utils/QuickUtils.kt

[QuickExtensions]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/extensions/QuickExtensions.kt

[Base Classes]:https://htmlpreview.github.io/?https://raw.githubusercontent.com/RZahr/QuickTools/master/documentation/quicktools/com.rzahr.quicktools/-quick-base-class/index.html

[QuickNotificationExtensions]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/extensions/QuickNotificationExtensions.kt

[QuickUIExtensions]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/extensions/QuickUIExtentions.kt

[QuickActivityExtensions]:https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/extensions/QuickActivityExtensions.kt   

[com.rzahr.quicktools]: https://htmlpreview.github.io/?https://raw.githubusercontent.com/RZahr/QuickTools/master/documentation/quicktools/com.rzahr.quicktools/index.html

[Adaptor Tools]: https://htmlpreview.github.io/?https://raw.githubusercontent.com/RZahr/QuickTools/master/documentation/quicktools/com.rzahr.quicktools.adaptors/index.html

[Extensions]: https://htmlpreview.github.io/?https://raw.githubusercontent.com/RZahr/QuickTools/master/documentation/quicktools/com.rzahr.quicktools.extensions/index.html
  
[Models]: https://htmlpreview.github.io/?https://raw.githubusercontent.com/RZahr/QuickTools/master/documentation/quicktools/com.rzahr.quicktools.models/index.html
  
[Utils]: https://htmlpreview.github.io/?https://raw.githubusercontent.com/RZahr/QuickTools/master/documentation/quicktools/com.rzahr.quicktools.utils/index.html
  
[Views]: https://htmlpreview.github.io/?https://raw.githubusercontent.com/RZahr/QuickTools/master/documentation/quicktools/com.rzahr.quicktools.views/index.html
  
[All Types]: https://htmlpreview.github.io/?https://raw.githubusercontent.com/RZahr/QuickTools/master/documentation/quicktools/alltypes/index.html

[QuickAppModule]: https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/QuickAppModule.kt

[QuickNotificationUtils]: https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/QuickNotificationUtils.kt

[QuickLogWriter]: https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/QuickLogWriter.kt

[QuickInternetCheckService]: https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/QuickInternetCheckService.kt

[QuickTrafficCalculator]: https://github.com/RZahr/QuickTools/blob/master/quicktools/src/main/java/com/rzahr/quicktools/QuickTrafficCalculator.kt
