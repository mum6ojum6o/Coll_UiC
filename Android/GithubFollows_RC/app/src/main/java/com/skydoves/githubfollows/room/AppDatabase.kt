package com.skydoves.githubfollows.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.skydoves.githubfollows.models.Follower
import com.skydoves.githubfollows.models.GithubUser
import com.skydoves.githubfollows.models.History
import com.skydoves.githubfollows.models.UserGitHubRepositories

/**
 * Developed by skydoves on 2018-01-27.
 * Copyright (c) 2018 skydoves rights reserved.
 */

@Database(entities = [(History::class), (Follower::class), (GithubUser::class), (UserGitHubRepositories::class)], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun githubUserDao(): GithubUserDao
    abstract fun followersDao(): FollowersDao
    abstract fun userGitHubRepositoriesDao():UserGitHubRepositoriesDao
}
