package com.rzahr.quicktools.models

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Rashad Zahr
 *
 * This class is used with retrofit to return an object response from the web-service of this form
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "unused")
class QuickServiceResponse(parcel: Parcel) : Parcelable {

    private var responseDetails = ""
    private var errorDetails = ""
    private var responseCode = 500

    init {
        responseDetails = parcel.readString().toString()
        errorDetails = parcel.readString().toString()
        responseCode = parcel.readInt()
    }

    fun getResponseDetails(): String? {
        return responseDetails
    }

    fun getErrorDetails(): String? {
        return errorDetails
    }

    fun getResponseCode(): Int {
        return responseCode
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(responseDetails)
        dest.writeString(errorDetails)
        dest.writeInt(responseCode)
    }

    companion object CREATOR : Parcelable.Creator<QuickServiceResponse> {
        override fun createFromParcel(parcel: Parcel): QuickServiceResponse {
            return QuickServiceResponse(parcel)
        }

        override fun newArray(size: Int): Array<QuickServiceResponse?> {
            return arrayOfNulls(size)
        }
    }
}
