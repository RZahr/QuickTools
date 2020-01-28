package com.rzahr.quicktools

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
class QuickContextWrapper(base: Context?): ContextWrapper(base) {

    companion object {

        @Suppress("DEPRECATION")
        fun wrap(context: Context?, language: String): ContextWrapper {

            var contextTemp = context
            val config = contextTemp?.resources?.configuration
            val sysLocale =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) config?.let { getSystemLocale(it) } else config?.let {
                    getSystemLocaleLegacy(it)
                }

            if (sysLocale != null) {

                if (language != "" && !sysLocale.language.contains(language)) {

                    val locale = Locale(language)
                    Locale.setDefault(locale)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) config?.let {
                        setSystemLocale(
                            it,
                            locale
                        )
                    } else config?.let { setSystemLocaleLegacy(it, locale) }
                }
            }

            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) contextTemp =
                config?.let { contextTemp?.createConfigurationContext(it) }
            else contextTemp?.resources?.updateConfiguration(config, contextTemp.resources.displayMetrics)

            return QuickContextWrapper(contextTemp)
        }

        @Suppress("DEPRECATION")
        private fun getSystemLocaleLegacy(config: Configuration): Locale {
            return config.locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun getSystemLocale(config: Configuration): Locale {
            return config.locales.get(0)
        }

        @Suppress("DEPRECATION")
        private fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }
    }
}