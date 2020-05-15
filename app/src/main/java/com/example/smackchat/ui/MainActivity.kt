package com.example.smackchat.ui

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smackchat.R
import com.example.smackchat.adapters.ChannelAdapter
import com.example.smackchat.adapters.MessageAdapter
import com.example.smackchat.model.Channel
import com.example.smackchat.model.Message
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
    private val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ChannelAdapter
    private lateinit var messageAdapter: MessageAdapter
    var selectedChannel: Channel? = null
    lateinit var channels:MutableList<Channel>
    val test: Boolean = true

    private fun setUpAdapter() {
        if(test) {
            channelAdapter =
                ChannelAdapter(this, MessageService.channels)
            channel_list.adapter = channelAdapter
            channel_list.adapter = channelAdapter

            messageAdapter = MessageAdapter(this, MessageService.messages)
            messageListView.adapter = messageAdapter

            val layoutManager = LinearLayoutManager(this)
            messageListView.layoutManager = layoutManager
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.OpenNavigation,
            R.string.CloseNavigation
        )
        drawerLayout.addDrawerListener(toggle)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
             hideKeyboard()
        }
        toggle.syncState()

        socket.connect()
        /**
         * we have crated the Socket.on - listening for specific event call ChannelCreated. And if we detected
         * then we are going to use this listener right here new channel
         */
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated",onNewMessage)
        setUpAdapter()
        channels = MessageService.channels

        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )

        channel_list.setOnItemClickListener { _, _, index, _ ->
            selectedChannel = MessageService.channels[index]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }
        if(App.prefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }

        layout_main.setOnTouchListener(View.OnTouchListener { _, _ ->
            hideKeyboard()
            return@OnTouchListener true
        })
        messageListView.setOnTouchListener(View.OnTouchListener{_,_->
            hideKeyboard()
            return@OnTouchListener true
        })

        messageTextView.setOnTouchListener(View.OnTouchListener{_,_->
            btnSendMessage.background = getDrawable(R.drawable.rounded_send_button)
            btnSendMessage.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
            return@OnTouchListener false
        })
    }

    override fun onResume() {
        super.onResume()
            if (MessageService.channels.isNotEmpty()) {
                this.channels.clear()
                this.channels.addAll(MessageService.channels)

            }
        channelAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
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
                Toast.makeText(this@MainActivity, "ValueColour"+ UserDataService.returnAvatarColor(
                    UserDataService.avatarColor
                ), Toast.LENGTH_LONG).show()
                nav_view.btnLoginNavDrawer.text = getString(R.string.text_logout)

                MessageService.getChannels{ channelSuccess ->
                    if (channelSuccess) {
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateWithChannel(){
        hideKeyboard()
        mainChannelName.text = "# ${selectedChannel?.name}"
        //Download Message
        if(selectedChannel!= null){
            MessageService.getMessages(selectedChannel!!.id){messageSuccess->
                if(messageSuccess){
                     messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount > 0){
                      messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }
        fun btnLoginNavClick(view: View) {
            if(view.id == R.id.btnLoginNavDrawer) {
                if (App.prefs.isLoggedIn) {
                    UserDataService.logout()
                    channelAdapter.notifyDataSetChanged()
                    this.messageAdapter.notifyDataSetChanged()
                    nav_view.userNameNavDrawer.text = getString(R.string.login)
                    nav_view.userEmailNavDrawer.text = ""
                    nav_view.userImageNavDrawer.setImageResource(R.drawable.profiledefault)
                    nav_view.userImageNavDrawer.setBackgroundColor(Color.TRANSPARENT)
                    nav_view.btnLoginNavDrawer.text = getString(R.string.login)
                    mainChannelName.text = getString(R.string.please_log_in)

                } else {
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    startActivity(loginIntent)
                }
            }
        }

        @SuppressLint("InflateParams")
        fun addChannelClick(view: View) {
            if(view.id == R.id.btnAddChannelNav) {
                if (App.prefs.isLoggedIn) {
                    val builder = AlertDialog.Builder(this)
                    val dialogView = layoutInflater.inflate(R.layout.dialog_add_channels, null)

                    builder.setView(dialogView)
                        .setPositiveButton("Add") { _, _ ->
                            val channelName =
                                (dialogView.findViewById<EditText>(R.id.addChannelNameText)).text.toString()
                            val channelDesc =
                                (dialogView.findViewById<EditText>(R.id.addChannelDescText)).text.toString()


                            //Create Channel
                            socket.emit("newChannel", channelName, channelDesc)
                        }
                        .setNegativeButton("Cancel") { _, _ ->

                        }
                        .show()
                }
            }
        }

        //Listener for Emitter
        private val onNewChannel = Emitter.Listener { arguments ->
            if(App.prefs.isLoggedIn) {
                runOnUiThread {
                    val channelName = arguments[0] as String
                    val channelDescription = arguments[1] as String
                    val channelId = arguments[2] as String

                    val newChannel = Channel(channelName, channelDescription, channelId)
                    MessageService.channels.add(newChannel)
                    channelAdapter.notifyDataSetChanged()
                }
            }
        }

        private val onNewMessage = Emitter.Listener { arguments ->
            if(App.prefs.isLoggedIn) {
                runOnUiThread {
                    val channelId = arguments[2] as String
                    if (channelId == selectedChannel?.id) {
                        val msgBody = arguments[0] as String
                        val userName = arguments[3] as String
                        val userAvatar = arguments[4] as String
                        val userAvatarColor = arguments[5] as String
                        val id = arguments[6] as String
                        val timeStamp = arguments[7] as String

                        val newMessage = Message(
                            msgBody,
                            userName,
                            channelId,
                            userAvatar,
                            userAvatarColor,
                            id,
                            timeStamp
                        )
                        MessageService.messages.add(newMessage)
                        messageAdapter.notifyDataSetChanged()
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }

        fun sendMessageBtnClick(view: View) {
            if(view.id == R.id.btnSendMessage) {
                if (App.prefs.isLoggedIn && messageTextView.text.isNotEmpty() && selectedChannel != null) {
                    val userId = UserDataService.id
                    val channelId = selectedChannel!!.id
                    socket.emit(
                        "newMessage",
                        messageTextView.text.toString(),
                        userId,
                        channelId,
                        UserDataService.name,
                        UserDataService.avatarName,
                        UserDataService.avatarColor
                    )
                    messageTextView.text.clear()
                    hideKeyboard()
                }
            }
        }

        override fun onBackPressed() {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }

        private fun hideKeyboard() {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputManager.isAcceptingText) {
                inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
                btnSendMessage.setBackgroundColor(getColor(android.R.color.transparent))
                btnSendMessage.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
        }
    }

