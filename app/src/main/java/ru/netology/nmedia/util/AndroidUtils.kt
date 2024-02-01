package ru.netology.nmedia.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object AndroidUtils {
    fun hideKeyboard(view: View) {
        // HINT: Этот код пока работает
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // HINT: Требуется для получения объекта Window
    // См. коммантарии в View.showTheKeyboardNow()
    fun View.getActivity(ctx: Context?) : Activity? {
        if (context == null) return null;
        if (context is Activity) return context as Activity;
        if (context is ContextWrapper) return getActivity((context as ContextWrapper).getBaseContext());
        return null;
    }

    fun View.showTheKeyboardNow() {
        if (hasWindowFocus()) {
            post {
                // HINT: Не работает отображение клавиатуры
                // В логе проскакивает: Ignoring showSoftInput as ... is not served
                /*
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                */

                // HINT: Решение найдено тут:
                // https://stackoverflow.com/questions/75477112/android-12-ignoring-showsoftinput-as-view-is-not-served
                getActivity(this.context)?.let {
                    WindowInsetsControllerCompat(it.window, this)
                        .show(WindowInsetsCompat.Type.ime())
                }
            }
        }
    }

    fun View.focusAndShowKeyboard() {
        // HINT: прямой вызов "requestFocus()" не передает фокус в инпут (нет курсора)
        post { requestFocus() }

        if (hasWindowFocus()) {
            showTheKeyboardNow()
        } else {
            viewTreeObserver.addOnWindowFocusChangeListener(
                object : ViewTreeObserver.OnWindowFocusChangeListener {
                    override fun onWindowFocusChanged(hasFocus: Boolean) {
                        if (hasFocus) {
                            this@focusAndShowKeyboard.showTheKeyboardNow()
                            viewTreeObserver.addOnWindowFocusChangeListener(this)
                        }
                    }
                }
            )
        }
    }
}