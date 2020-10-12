@file:Suppress("unused")

package com.rzahr.quicktools

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.rzahr.quicktools.extensions.get
import com.rzahr.quicktools.utils.QuickDBUtils
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject

class QuickBaseClassN {

    /**
     * @author Rashad Zahr
     *
     * new base model class helper for performing SQLITE queries
     */
    abstract class BaseModelN<D: QuickDatabase> @Inject constructor() {

        @Inject
        lateinit var mDatabase: D

        fun simpleSelect(columns: String, table: String, whereClause: String = "", groupByClause: String = "", orderByClause: String = ""): String {

            return QuickDBUtils.simpleSelect(columns, table, whereClause, groupByClause, orderByClause)
        }

        fun distinctSelect(columns: String, table: String, whereClause: String = ""): String {

            return QuickDBUtils.distinctSelect(columns, table, whereClause)
        }

        fun delete(table: String, where: String = ""): String {

            return if (where.isNotEmpty()) "DELETE FROM $table WHERE $where"
            else "DELETE FROM $table"
        }

        fun execSQL(query: String, beforeAction:() -> Unit = {}, afterAction:() -> Unit = {}, closedAction:() -> Unit = {}) {

            beforeAction()

            mDatabase.getDatabase()

            if (mDatabase.myDataBase?.isOpen!!) mDatabase.myDataBase?.execSQL(query)

            else closedAction()

            afterAction()
        }

        fun singleSelect(query: String, increment: Boolean, delimiter: String, defaultReturn: String, args: Array<String> = emptyArray()): String {

            return mDatabase.singleSelect(query, increment, delimiter, defaultReturn, args)
        }

        fun multiSelect(query: String): ArrayList<Array<String>> {

            return mDatabase.multiSelect(query)
        }

        fun multiHashSelect(query: String): ArrayList<HashMap<String, String>> {

            return mDatabase.multiSelect(query, "")
        }

        fun createSimpleSelect(columns: String, table: String, whereClause: String = "", groupByClause: String = "", orderByClause: String = ""): String {

            return QuickDBUtils.simpleSelect(columns, table, whereClause, groupByClause, orderByClause)
        }

        fun multiSelect(query: String, onResult: (cursor: Cursor) -> Unit) {

            mDatabase.multiSelect(query, onResult)
        }

        fun createDistinctSelect(columns: String, table: String, whereClause: String = ""): String {

            return QuickDBUtils.distinctSelect(columns, table, whereClause)
        }
    }


    /**
     * @author Rashad Zahr
     *
     * base presenter class
     */
    open class BasePresenterN<V : BaseViewInterfaceN, M: BaseModelN<QuickDatabase>> @Inject constructor(): BasePresenterInterfaceN<V>, LifecycleObserver {

        @Inject lateinit var model: M
        @Inject lateinit var mContext: Context
        @Inject lateinit var mActivity: Activity

        private var stateBundle: Bundle? = null

        override fun getStateBundle(): Bundle? {
            if (stateBundle == null)
                stateBundle = Bundle()
            return stateBundle
        }

        override fun onPresenterDestroy() {
            if (stateBundle != null && !stateBundle!!.isEmpty) stateBundle?.clear()
        }

        override fun detachLifecycle(lifecycle: Lifecycle) {
            lifecycle.removeObserver(this)
        }

        override fun attachLifecycle(lifecycle: Lifecycle) {
            lifecycle.addObserver(this)
        }

        override fun onPresenterCreated() {
        }

        private var weakReference: WeakReference<V>? = null

        fun getString(id: Int) =  id.get(mActivity)

        override fun attachView(view: V) {
            if (!isViewAttached) {
                weakReference = WeakReference(view)
                view.setPresenter(this)
            }
        }

        override fun detachView() {
            weakReference?.clear()

            weakReference = null
        }

        val view: V?
            get() = weakReference?.get()

        private val isViewAttached: Boolean
            get() = weakReference != null && weakReference!!.get() != null
    }

    /**
     * @author Rashad Zahr
     *
     * base view interface
     */
    interface BaseViewInterfaceN {

        fun setPresenter(presenter: BasePresenterN<*, *>)
    }


    /**
     * @author Rashad Zahr
     *
     * base presenter interface
     */
    interface BasePresenterInterfaceN<V : BaseViewInterfaceN> {

        fun attachView(view: V)
        fun detachView()
        fun attachLifecycle(lifecycle: Lifecycle)
        fun onPresenterCreated()
        fun detachLifecycle(lifecycle: Lifecycle)
        fun onPresenterDestroy()
        fun getStateBundle(): Bundle?
    }


    /**
     * @author Rashad Zahr
     *
     * base activity
     */
    abstract class BaseActivityN<P : BasePresenterInterfaceN<*>>: AppCompatActivity(), BaseViewInterfaceN {

        @Inject lateinit var mPresenter: P
        @Inject lateinit var mClickGuard: QuickClickGuard

        private var presenter: BasePresenterN<*, *>? = null

        protected abstract fun onActivityInject()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            onActivityInject()
        }

        override fun setPresenter(presenter: BasePresenterN<*, *>) {

            this.presenter = presenter
        }

        override fun onDestroy() {
            super.onDestroy()

            presenter?.detachView()
            presenter = null
        }
    }
}