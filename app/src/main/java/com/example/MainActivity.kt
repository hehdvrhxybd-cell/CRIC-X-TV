package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.CricketMatch
import com.example.data.model.MatchStatus
import com.example.ui.BottomNavTab
import com.example.ui.CricketViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.CricTopAppBar
import com.example.ui.components.LiveNotificationBanner
import com.example.ui.components.MatchReminderDialog
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private var initialMatchId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialMatchId = intent?.getStringExtra("OPEN_MATCH_ID")
        enableEdgeToEdge()
        setContent {
            CricXTheme {
                val viewModel: CricketViewModel = viewModel()
                LaunchedEffect(initialMatchId) {
                    initialMatchId?.let { matchId ->
                        viewModel.navigateTo(ScreenDestination.MatchDetail(matchId))
                        initialMatchId = null
                    }
                }
                CricXApp(viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra("OPEN_MATCH_ID")?.let { matchId ->
            // Let the Compose layer navigate if already running
            initialMatchId = matchId
        }
    }
}

@Composable
fun CricXApp(viewModel: CricketViewModel) {
    val context = LocalContext.current
    LaunchedEffect(context) {
        viewModel.attachContext(context.applicationContext)
    }
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val seriesList by viewModel.seriesList.collectAsStateWithLifecycle()
    val pointsTables by viewModel.pointsTables.collectAsStateWithLifecycle()
    val players by viewModel.players.collectAsStateWithLifecycle()
    val teams by viewModel.teams.collectAsStateWithLifecycle()
    val favoriteTeamIds by viewModel.favoriteTeamIds.collectAsStateWithLifecycle()
    val notificationPreference by viewModel.notificationPreference.collectAsStateWithLifecycle()
    val matchReminders by viewModel.matchReminders.collectAsStateWithLifecycle()
    val isSimulationRunning by viewModel.isSimulationRunning.collectAsStateWithLifecycle()
    val activeAlert by viewModel.activeAlert.collectAsStateWithLifecycle()
    val granularStatsMap by viewModel.granularStatsMap.collectAsStateWithLifecycle()
    val isStatsLoading by viewModel.isStatsLoading.collectAsStateWithLifecycle()

    var matchForReminderDialog by remember { mutableStateOf<CricketMatch?>(null) }
    val upcomingMatches = remember(matches) { matches.filter { it.status == MatchStatus.UPCOMING } }

    val onMatchClick: (CricketMatch) -> Unit = { match ->
        viewModel.navigateTo(ScreenDestination.MatchDetail(match.id))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        bottomBar = {
            if (currentScreen == ScreenDestination.Main) {
                CricBottomNavigationBar(
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is ScreenDestination.Main -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top App Bar
                        CricTopAppBar(
                            onSearchClick = { viewModel.navigateTo(ScreenDestination.Search) },
                            onNotificationsClick = { viewModel.navigateTo(ScreenDestination.Notifications) },
                            isSimulationRunning = isSimulationRunning,
                            onToggleSimulation = { viewModel.toggleLiveSimulation() }
                        )

                        // Live Event Banner overlay
                        LiveNotificationBanner(alert = activeAlert)

                        // Screen Content by Bottom Tab
                        when (currentTab) {
                            BottomNavTab.HOME -> {
                                HomeScreen(
                                    matches = matches,
                                    favoriteTeamIds = favoriteTeamIds,
                                    matchReminders = matchReminders,
                                    onMatchClick = onMatchClick,
                                    onToggleFavorite = { viewModel.toggleMatchFavorite(it) },
                                    onReminderClick = { matchForReminderDialog = it },
                                    onNavigateToSeries = { viewModel.selectTab(BottomNavTab.SERIES) }
                                )
                            }
                            BottomNavTab.LIVE -> {
                                LiveTabScreen(
                                    matches = matches,
                                    isSimulationRunning = isSimulationRunning,
                                    onToggleSimulation = { viewModel.toggleLiveSimulation() },
                                    onTriggerNextBall = { viewModel.triggerNextBall() },
                                    onMatchClick = onMatchClick,
                                    onToggleFavorite = { viewModel.toggleMatchFavorite(it) }
                                )
                            }
                            BottomNavTab.MATCHES -> {
                                MatchesTabScreen(
                                    matches = matches,
                                    matchReminders = matchReminders,
                                    onMatchClick = onMatchClick,
                                    onToggleFavorite = { viewModel.toggleMatchFavorite(it) },
                                    onReminderClick = { matchForReminderDialog = it }
                                )
                            }
                            BottomNavTab.SERIES -> {
                                SeriesScreen(
                                    seriesList = seriesList,
                                    onSeriesClick = {
                                        viewModel.navigateTo(ScreenDestination.PointsTable)
                                    }
                                )
                            }
                            BottomNavTab.MORE -> {
                                MoreScreen(
                                    onNavigateToPointsTable = { viewModel.navigateTo(ScreenDestination.PointsTable) },
                                    onNavigateToTeams = { viewModel.navigateTo(ScreenDestination.Teams) },
                                    onNavigateToPlayers = { viewModel.navigateTo(ScreenDestination.Players) },
                                    onNavigateToNotifications = { viewModel.navigateTo(ScreenDestination.Notifications) }
                                )
                            }
                        }
                    }
                }

                is ScreenDestination.MatchDetail -> {
                    val match = matches.find { it.id == screen.matchId } ?: matches.first()
                    LaunchedEffect(screen.matchId) {
                        viewModel.loadGranularStats(screen.matchId)
                    }
                    LiveMatchDetailScreen(
                        match = match,
                        isSimulationRunning = isSimulationRunning,
                        onToggleSimulation = { viewModel.toggleLiveSimulation() },
                        onTriggerNextBall = { viewModel.triggerNextBall() },
                        onResetMatch = { viewModel.resetMatch(it) },
                        onToggleFavorite = { viewModel.toggleMatchFavorite(it) },
                        onBack = { viewModel.navigateBack() },
                        reminder = matchReminders[match.id],
                        onReminderClick = { matchForReminderDialog = it },
                        granularStats = granularStatsMap[match.id],
                        isLoadingStats = isStatsLoading[match.id] == true,
                        onRefreshStats = { viewModel.refreshGranularStats(match.id) }
                    )
                }

                is ScreenDestination.Search -> {
                    SearchScreen(
                        matches = matches,
                        teams = teams,
                        seriesList = seriesList,
                        players = players,
                        onMatchClick = onMatchClick,
                        onTeamClick = { viewModel.navigateTo(ScreenDestination.Teams) },
                        onSeriesClick = { viewModel.navigateTo(ScreenDestination.PointsTable) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.Notifications -> {
                    NotificationsScreen(
                        currentPreferences = notificationPreference,
                        matchReminders = matchReminders,
                        upcomingMatches = upcomingMatches,
                        onUpdatePreferences = { viewModel.updateNotificationPreferences(it) },
                        onCancelReminder = { matchId ->
                            viewModel.cancelMatchReminder(context, matchId)
                        },
                        onSetReminderClick = { match ->
                            matchForReminderDialog = match
                        },
                        onTestWicketAlert = {
                            viewModel.triggerTestWicketNotification(context)
                        },
                        onTestMatchStartAlert = {
                            viewModel.triggerTestMatchStartNotification(context, delaySeconds = 0L)
                        },
                        onScheduleMatchStartDemo = {
                            viewModel.triggerTestMatchStartNotification(context, delaySeconds = 5L)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.PointsTable -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubScreenHeader(title = "Points Table", onBack = { viewModel.navigateBack() })
                        PointsTableScreen(pointsTables = pointsTables)
                    }
                }

                is ScreenDestination.Teams -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubScreenHeader(title = "Teams & Squads", onBack = { viewModel.navigateBack() })
                        TeamsScreen(
                            teams = teams,
                            favoriteTeamIds = favoriteTeamIds,
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onTeamClick = { /* noop */ }
                        )
                    }
                }

                is ScreenDestination.Players -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubScreenHeader(title = "Players & Stats", onBack = { viewModel.navigateBack() })
                        PlayersScreen(players = players)
                    }
                }
            }

            // Match Reminder Dialog
            matchForReminderDialog?.let { match ->
                MatchReminderDialog(
                    match = match,
                    existingReminder = matchReminders[match.id],
                    onSetReminder = { leadMinutes, customDelaySeconds ->
                        viewModel.scheduleMatchReminder(
                            context = context,
                            match = match,
                            leadMinutes = leadMinutes,
                            customDelaySeconds = customDelaySeconds
                        )
                    },
                    onCancelReminder = {
                        viewModel.cancelMatchReminder(context, match.id)
                    },
                    onDismiss = {
                        matchForReminderDialog = null
                    }
                )
            }
        }
    }
}

@Composable
fun SubScreenHeader(
    title: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )
    }
}

@Composable
fun CricBottomNavigationBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .border(width = 0.5.dp, color = CardBorder)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            val icon = when (tab) {
                BottomNavTab.HOME -> Icons.Default.Home
                BottomNavTab.LIVE -> Icons.Default.Sensors
                BottomNavTab.MATCHES -> Icons.Default.SportsCricket
                BottomNavTab.SERIES -> Icons.Default.EmojiEvents
                BottomNavTab.MORE -> Icons.Default.Menu
            }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("nav_tab_${tab.name.lowercase()}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) CricketNeonGreen else TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                    if (tab == BottomNavTab.LIVE) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LiveRed)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tab.label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    color = if (isSelected) CricketNeonGreen else TextTertiary
                )
            }
        }
    }
}
