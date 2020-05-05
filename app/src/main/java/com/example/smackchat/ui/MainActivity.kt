package com.example.smackchat.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smackchat.R
import com.example.smackchat.model.Channel
import com.example.smackchat.services.AuthService
import com.example.smackchat.services.MessageService
import com.example.smackchat.services.UserDataService
import com.example.smackchat.utilities.BROADCAST_USER_DATA_CHANGE
import com.example.smackchat.utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {
    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapeter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    private fun setUpAdapter() {
        channelAdapeter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapeter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.OpenNavigation,
            R.string.CloseNavigation
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        socket.connect()
        /**
         * we have crated the Socket.on - listening for specific event call ChannelCreated. And if we detected
         * then we are going to use this listener right here new channel
         */
        socket.on("channelCreated", onNewChannel)
        setUpAdapter()
        channel_list.setOnItemClickListener { _, _, index, _ ->
            selectedChannel = MessageService.channels[index]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }
        if(App.prefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )

        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggedIn) {
                nav_view.userNameNavDrawer.text = UserDataService.name
                nav_view.userEmailNavDrawer.text = UserDataService.email
                val resourcesId =
                    resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                nav_view.userImageNavDrawer.setImageResource(resourcesId)
                nav_view.userImageNavDrawer.setBackgroundColor(
                    UserDataService.returnAvatarColor(
                        UserDataService.avatarColor
                    )
                )
                nav_view.btnLoginNavDrawer.text = getString(R.string.text_logout)

                MessageService.getChannels{ channelSuccess ->
                    if (channelSuccess) {
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapeter.notifyDataSetChanged()
                            updateWithChannel()
                        }

                    }
                }
            }
        }
    }

    fun updateWithChannel(){
        mainChannelName.text = "#${selectedChannel?.name}"

    }
        fun btnLoginNavClick(view: View) {
            if (App.prefs.isLoggedIn) {
                UserDataService.logout()
                nav_view.userNameNavDrawer.text = getString(R.string.login)
                nav_view.userEmailNavDrawer.text = ""
                nav_view.userImageNavDrawer.setImageResource(R.drawable.profiledefault)
                nav_view.userImageNavDrawer.setBackgroundColor(Color.TRANSPARENT)
                nav_view.btnLoginNavDrawer.text = getString(R.string.login)
            } else {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }

        fun addChannelClick(view: View) {
            if (App.prefs.isLoggedIn) {
                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.dialog_add_channels, null)

                builder.setView(dialogView)
                    .setPositiveButton("Add") { dialogInterface, i ->
                        // val nameText = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                        // val descText = dialogView.findViewById<EditText>(R.id.addChannelDescText)
                        val channelName =
                            (dialogView.findViewById<EditText>(R.id.addChannelNameText)).text.toString()
                        val channelDesc =
                            (dialogView.findViewById<EditText>(R.id.addChannelDescText)).text.toString()


                        //Create Channel
                        socket.emit("newChannel", channelName, channelDesc)
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i ->

                    }
                    .show()
            }
        }

        //Listener for Emitter
        private val onNewChannel = Emitter.Listener { arguments ->
            runOnUiThread {
                val channelName = arguments[0] as String
                val channelDescription = arguments[1] as String
                val channelId = arguments[2] as String

                val newChannel = Channel(channelName, channelDescription, channelId)
                MessageService.channels.add(newChannel)
                channelAdapeter.notifyDataSetChanged()
            }
        }

        fun sendMessageBtnClick(view: View) {
            hideKeyboard()
        }

        override fun onBackPressed() {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }

        fun hideKeyboard() {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputManager.isAcceptingText) {
                inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
        }
    }

