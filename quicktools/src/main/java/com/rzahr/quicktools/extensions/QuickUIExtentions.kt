@file:Suppress("unused")

package com.rzahr.quicktools.extensions

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.google.android.material.button.MaterialButton
import com.rzahr.quicktools.QuickClickGuard
import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject

/**
 * @author Rashad Zahr
 */

/**
 * used to add animation to a constraint layout
 * @param layoutId: the layout id that the animation will change to
 * @param activity: the activity
 */
fun ConstraintLayout.addAnimationOnLayout(layoutId: Int, activity: Activity?) {

    try {

        android.os.Handler().postDelayed({

            val constraintSet = ConstraintSet()

            activity?.let { constraintSet.clone(it, layoutId) }

            activity?.let { TransitionManager.beginDelayedTransition(it.findViewById(android.R.id.content), null) }

            constraintSet.applyTo(this)
        }, 250)
    }
    catch(e: Exception){}
}

/**
 * tool used to hide the keyboard from a fragment
 */
fun Fragment.hideKeyboard() {

    view?.let { activity!!.hideKeyboard(it) }
}

/**
 * quick initializer for a recycler view
 * @param linearLayoutManager: the linear layout manager for the recycler view
 * @param context: the context
 */
fun RecyclerView.initializeLinear(linearLayoutManager: LinearLayoutManager, context: Context) {

    this.layoutManager = linearLayoutManager
    this.setHasFixedSize(true)
    this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}

/**
 * show the alert dialog
 * @return an alert dialog
 */
fun AlertDialog.Builder.showQuickAlert(): AlertDialog? {

    val dialog: AlertDialog = this.create()
    dialog.show()
    return dialog
}

/**
 * hiding keyboard anywhere
 * @param view: the view
 */
fun Context.hideKeyboard(view: View) {

    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * sets a background color on a material button (backward compatible)
 */
fun MaterialButton.rzBackgroundColor(color: Int) {

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) this.backgroundTintList = android.content.res.ColorStateList.valueOf(color)

    else  this.setBackgroundColor(color)
}

/**
 * show or hide a view
 */
fun View.rzHideShow() {

    if (this.visibility == View.VISIBLE) this.visibility = View.GONE

    else this.visibility = View.VISIBLE
}

/**
 * sets view visibility to gone
 */
fun View.rzSetVisibilityGone() {

    this.visibility = View.GONE
}

/**
 * sets view visibility to invisible
 */
fun View.rzSetVisibilityInvisible() {

    this.visibility = View.INVISIBLE
}

/**
 * returns true if the view is visible
 * @return boolean value representing if the view is visible or not
 */
fun View.rzVisible(): Boolean {

    return this.visibility == View.VISIBLE
}

/**
 * sets view visibility to visible
 */
fun View.rzSetVisible() {

    this.visibility = View.VISIBLE
}

fun View.rzFadeIn(duration: Long): Completable {
    this.alpha = 0f
    val animationSubject = CompletableSubject.create()
    return animationSubject.doOnSubscribe {
        ViewCompat.animate(this)
            .setDuration(duration)
            .alpha(1f)
            .withEndAction {
                animationSubject.onComplete()
            }
    }
}

fun View.rzOfFloat(durationn: Long): Completable {
    val animationSubject = CompletableSubject.create()
    return animationSubject.doOnSubscribe {
        ObjectAnimator.ofFloat(this, "translationX", 1500f,1000f,0f).apply {
            duration = durationn
            start()
            animationSubject.onComplete()
        }
    }
}

/**
 * click listener with a guard to prevent rapid clicks
 */
fun View.rzClickListener(clickGuard: QuickClickGuard, action: () -> Unit) {

    this.setOnClickListener {

        clickGuard.guard { action() }
    }
}

/**
 * used to guard from multiple rapid clicks on a view
 * @param clickGuard: the class that prevents the rapid clicks
 * @param action: passed function upon successful passed clicked
 */
@Suppress("unused")
fun View.rzClickGuard(clickGuard: QuickClickGuard, action: () -> Unit) {

    clickGuard.guard {
        action()
    }
}

/**
 * used to show a bubble like view as a hint on a view
 * @param title: the title displayed
 * @param id: the id of this bubble to be displayed only once
 * @param activity: the activity of this view
 * @param description: optional description
 * @param backgroundColorId: the background color of the bubble
 * @return BubbleShowCaseBuilder object
 */
fun View.addShowCase(title: String, id: String, activity: Activity, description: String = "", backgroundColorId: Int): BubbleShowCaseBuilder {

    val aas:List<BubbleShowCase.ArrowPosition> = emptyList()
    return BubbleShowCaseBuilder(activity) //Activity instance
        .title(title) //Any title for the bubble view
        .description(description)
        .arrowPosition(aas)
        .showOnce(id) //Id to show only once the BubbleShowCase
        .listener(object: BubbleShowCaseListener {

            override fun onBackgroundDimClick(bubbleShowCase: BubbleShowCase) {
                bubbleShowCase.dismiss()
            }

            override fun onBubbleClick(bubbleShowCase: BubbleShowCase) {
                bubbleShowCase.dismiss()
            }

            override fun onCloseActionImageClick(bubbleShowCase: BubbleShowCase) {
                bubbleShowCase.dismiss()
            }

            override fun onTargetClick(bubbleShowCase: BubbleShowCase) {
                bubbleShowCase.dismiss()
            }
        })
        .targetView(this) //View to point out
        .backgroundColor(ContextCompat.getColor(activity, backgroundColorId))
}
