package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CricketRepository
import com.example.data.repository.LiveEventAlert
import com.example.worker.MatchReminderScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Main : ScreenDestination()
    data class MatchDetail(val matchId: String) : ScreenDestination()
    object Search : ScreenDestination()
    object Notifications : ScreenDestination()
    object PointsTable : ScreenDestination()
    object Teams : ScreenDestination()
    object Players : ScreenDestination()
}

enum class BottomNavTab(val label: String) {
    HOME("Home"),
    LIVE("Live"),
    MATCHES("Matches"),
    SERIES("Series"),
    MORE("More")
}

class CricketViewModel(
    private val repository: CricketRepository = CricketRepository()
) : ViewModel() {

    val matches = repository.matches
    val seriesList = repository.seriesList
    val pointsTables = repository.pointsTables
    val players = repository.players
    val teams = repository.teams
    val favoriteTeamIds = repository.favoriteTeamIds
    val notificationPreference = repository.notificationPreference
    val matchReminders = repository.matchReminders
    val isSimulationRunning = repository.isLiveSimulationRunning
    val granularStatsMap = repository.granularStatsMap
    val isStatsLoading = repository.isStatsLoading

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Main)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(BottomNavTab.HOME)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    private val _activeAlert = MutableStateFlow<LiveEventAlert?>(null)
    val activeAlert: StateFlow<LiveEventAlert?> = _activeAlert.asStateFlow()

    init {
        viewModelScope.launch {
            repository.liveAlerts.collect { alert ->
                _activeAlert.value = alert
                delay(3500)
                if (_activeAlert.value == alert) {
                    _activeAlert.value = null
                }
            }
        }
    }

    fun selectTab(tab: BottomNavTab) {
        _currentTab.value = tab
        _currentScreen.value = ScreenDestination.Main
    }

    fun navigateTo(dest: ScreenDestination) {
        _currentScreen.value = dest
    }

    fun navigateBack() {
        _currentScreen.value = ScreenDestination.Main
    }

    fun toggleLiveSimulation() {
        repository.toggleLiveSimulation()
    }

    fun triggerNextBall() {
        repository.triggerNextBallManually()
    }

    fun toggleFavorite(teamId: String) {
        repository.toggleFavorite(teamId)
    }

    fun toggleMatchFavorite(matchId: String) {
        repository.toggleMatchFavorite(matchId)
    }

    fun updateNotificationPreferences(pref: NotificationPreference) {
        repository.updateNotificationPreferences(pref)
    }

    fun attachContext(context: Context) {
        repository.attachContext(context)
    }

    fun triggerTestWicketNotification(context: Context) {
        repository.triggerTestWicketNotification(context)
    }

    fun triggerTestMatchStartNotification(context: Context, delaySeconds: Long = 0L) {
        repository.triggerTestMatchStartNotification(context, delaySeconds)
    }

    fun startMatchAsLive(context: Context, matchId: String) {
        repository.startMatchAsLive(context, matchId)
    }

    fun scheduleMatchReminder(
        context: Context,
        match: CricketMatch,
        leadMinutes: Int,
        customDelaySeconds: Long? = null
    ) {
        val reminder = MatchReminderScheduler.scheduleMatchReminder(
            context = context,
            match = match,
            leadMinutes = leadMinutes,
            customDelaySeconds = customDelaySeconds
        )
        repository.setMatchReminder(reminder)
    }

    fun cancelMatchReminder(context: Context, matchId: String) {
        MatchReminderScheduler.cancelMatchReminder(context, matchId)
        repository.removeMatchReminder(matchId)
    }

    fun resetMatch(matchId: String) {
        repository.resetMatch(matchId)
    }

    fun loadGranularStats(matchId: String) {
        viewModelScope.launch {
            repository.fetchGranularStats(matchId, forceRefresh = false)
        }
    }

    fun refreshGranularStats(matchId: String) {
        viewModelScope.launch {
            repository.fetchGranularStats(matchId, forceRefresh = true)
        }
    }
}
