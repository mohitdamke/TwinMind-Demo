package mohit.voice.twinmind.navigation

object Routes {
    var SplashScreen = "splash_screen"
    const val RecordScreen = "record_screen/{meetingId}"
    fun recordScreen(meetingId: Long) = "record_screen/$meetingId"
    var HomeScreen = "home_screen"


}