package com.example.polybluetoothmap.ui


import androidx.drawerlayout.widget.DrawerLayout
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.ImageButton

class SideMenuHelper(
    private val drawerLayout: DrawerLayout,
    private val menuDrawer: ConstraintLayout,
    private val btnToggleList: ImageButton
) {

    init {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        btnToggleList.setOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(menuDrawer)) {
            drawerLayout.closeDrawer(menuDrawer)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            drawerLayout.openDrawer(menuDrawer)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }
}
