package com.example.smackchat.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smackchat.R
import com.example.smackchat.services.AuthService
import com.example.smackchat.services.UserDataService
import com.example.smackchat.utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(this,drawer_layout,toolbar,
            R.string.OpenNavigation,
            R.string.CloseNavigation
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )
    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLoggedIn){
                nav_view.userNameNavDrawer.text = UserDataService.name
                nav_view.userEmailNavDrawer.text = UserDataService.email
                val resourcesId = resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
                nav_view.userImageNavDrawer.setImageResource(resourcesId)
                nav_view.userImageNavDrawer.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                nav_view.btnLoginNavDrawer.text = getString(R.string.text_logout)
            }
        }
    }
    fun btnLoginNavClick(view:View){
        if(AuthService.isLoggedIn){
            UserDataService.logout()
            nav_view.userNameNavDrawer.text = getString(R.string.login)
            nav_view.userEmailNavDrawer.text = ""
            nav_view.userImageNavDrawer.setImageResource(R.drawable.profiledefault)
            nav_view.userImageNavDrawer.setBackgroundColor(Color.TRANSPARENT)
            nav_view.btnLoginNavDrawer.text = getString(R.string.login)


        }else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClick(view: View){

    }

    fun sendMessageBtnClick(view:View){

    }
    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }
}
