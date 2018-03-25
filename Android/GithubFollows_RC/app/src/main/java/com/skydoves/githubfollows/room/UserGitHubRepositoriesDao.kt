package com.skydoves.githubfollows.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

import com.skydoves.githubfollows.models.UserGitHubRepositories

/**
 * Created by Arjan on 3/25/2018.
 */
@Dao
interface UserGitHubRepositoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserGitHubRepositories(userGitHubRepositories: List<UserGitHubRepositories>)

    @Query("SELECT * FROM UserGitHubRepositories where login=:user")
    fun getUserGitHubRepositories(user:String):LiveData<List<UserGitHubRepositories>>

    @Query("DELETE FROM UserGithubRepositories")
    fun truncateUserGithubRepositories()
}