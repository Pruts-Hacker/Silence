package me.lucky.silence

import android.content.SharedPreferences
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class TileService : TileService() {
    private lateinit var prefs: Preferences

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == Preferences.SERVICE_ENABLED) update()
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        prefs = Preferences(this)
    }

    override fun onClick() {
        super.onClick()
        prefs.isServiceEnabled = qsTile.state == Tile.STATE_INACTIVE
        Utils.setSmsReceiverState(this, prefs.isServiceEnabled && prefs.isMessagesChecked)
    }

    override fun onStartListening() {
        super.onStartListening()
        update()
        prefs.registerListener(prefsListener)
    }

    override fun onStopListening() {
        super.onStopListening()
        prefs.unregisterListener(prefsListener)
    }

    private fun update() {
        qsTile.state = when {
            !Utils.hasCallScreeningRole(this) -> Tile.STATE_UNAVAILABLE
            prefs.isServiceEnabled -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }
}