class AppMonitorService : Service() {
    private var timeLeft = 3600L // 1小時 (秒)
    private var targetPackage = "com.example.targetapp" // 使用者選定的App
    private val handler = Handler(Looper.getMainLooper())

    private val monitorRunnable = object : Runnable {
        override fun run() {
            if (isTargetAppInForeground(targetPackage)) {
                // 如果正在用目標App，開始倒數
                if (timeLeft > 0) {
                    timeLeft--
                } else {
                    showLockScreen() // 時間到，彈出鎖定
                }
            } else {
                // 離開了，計時器自然暫停（不執行 timeLeft--）
            }
            handler.postDelayed(this, 1000) // 每秒檢查一次
        }
    }

    // 檢查目前畫面最上層的 App
    private fun isTargetAppInForeground(packageName: String): Boolean {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000, time)
        val topApp = stats?.maxByOrNull { it.lastTimeUsed }?.packageName
        return topApp == packageName
    }

    private fun showLockScreen() {
        val intent = Intent(this, LockActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
