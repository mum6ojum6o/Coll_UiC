package com.skydoves.githubfollows.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Arjan on 3/24/2018.
 */
@Entity
data class UserGitHubRepositories(
        @PrimaryKey(autoGenerate = true) val number: Int,
        val id:Int,
        val name:String,
        val full_name:String,
        val html_url: String,
        val description:String?,
        var login:String
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(number)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(full_name)
        parcel.writeString(html_url)
        parcel.writeString(description)
        parcel.writeString(login)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserGitHubRepositories> = object : Parcelable.Creator<UserGitHubRepositories> {
            override fun createFromParcel(source: Parcel): UserGitHubRepositories = UserGitHubRepositories(source)
            override fun newArray(size: Int): Array<UserGitHubRepositories?> = arrayOfNulls(size)
        }
    }
}

