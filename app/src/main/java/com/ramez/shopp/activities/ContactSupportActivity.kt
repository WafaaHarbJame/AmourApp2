package com.ramez.shopp.activities


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Dialogs.PickImageDialog
import com.ramez.shopp.Models.ChatModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.MemberModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.PathUtil
import com.ramez.shopp.adapter.ChatAdapter
import com.ramez.shopp.databinding.ActivityContactSupportBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class ContactSupportActivity : ActivityBase() {
    lateinit var binding: ActivityContactSupportBinding
    var list: ArrayList<ChatModel>? = null
    var databaseReference: DatabaseReference? = null
    var TAG = "ContactSupportActivity"
    var message: String? = null
    var userID = ""
    var imageUrl: String? = ""
    var userName = ""
    var userImage = ""
    var providerID = ""
    var providerName: String? = ""
    var orderID = ""
    var chatId = ""
    var user: MemberModel? = null
    var pickImageDialog: PickImageDialog? = null
    var REQUEST_PICK_IMAGE = 11
    var REQUEST_CAPTURE_IMAGE = 12
    var selectedPhotoFil: File? = null
    private var chatAdapter: ChatAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var chatsUpdates: MutableMap<String?, Any?>? = null
    private var chatMessages: ArrayList<ChatModel?>? = null
    private var tempChatList: ArrayList<ChatModel>? = null
    private var chatMessage: ChatModel? = null
    private var inputType = ""
    private var imageBitmap: Bitmap? = null
    var localModel: LocalModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactSupportBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        user = UtilityApp.getUserData()
        userID = if (user != null && user?.id != null) user?.id.toString() else "0"
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        userName = if (user != null && user?.name != null) user?.name?:"" else getString(R.string.customer)
        userImage = user?.profilePicture ?:""
        chatMessages = ArrayList()
        tempChatList = ArrayList()
        chatsUpdates = HashMap()
        inputType = Constants.inputType_text
        databaseReference = FirebaseDatabase.getInstance().reference.child("Chats").child("Rooms")
        setTitle(R.string.customer_service)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.rv.layoutManager = linearLayoutManager
        chatAdapter = ChatAdapter(activity, chatMessages)
        binding.rv.adapter = chatAdapter
        extraData
        binding.sendMessageBtn.setOnClickListener { view1 ->
            message = binding.messageTxt.text.toString() + ""
            if (inputType == Constants.inputType_text && message!!.trim { it <= ' ' }.isEmpty()) {
                binding.messageTxt.requestFocus()
                binding.messageTxt.error = getString(R.string.enter_message)
            } else {
                if (inputType == Constants.inputType_text) {
                    sendChatMessage()
                } else {
                    uploadPhoto(userID.toInt(), selectedPhotoFil)
                }
            }
        }
        binding.addBut.setOnClickListener { openPicker() }
        binding.ivRemoveImage.setOnClickListener { resetInputs() }
    }

    fun initAdapter() {
        chatAdapter = ChatAdapter(activity, list)
        binding.rv.adapter = chatAdapter
    }

    private val roomChats: Unit
        get() {
            binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
            binding.dataLY.visibility = View.GONE
            databaseReference?.child(chatId)?.addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        chatMessages?.clear()
                        chatsUpdates?.clear()
                        for (snapshot in dataSnapshot.children) {
                            Log.d(TAG, "onDataChange: " + snapshot.value)
                            chatMessage = ChatModel()
                            chatMessage = snapshot.getValue(ChatModel::class.java)
                            if (chatMessage?.senderID == userID) {
                                chatMessage?.messageType = Constants.Sender
                                chatMessages?.add(chatMessage)
                            } else {
                                chatMessage?.messageType = Constants.Receiver
                                chatMessages?.add(chatMessage)
                                if (!chatMessage?.isIs_read!!) {
                                    chatMessage?.isIs_read = true
                                    chatsUpdates!![snapshot.key] = chatMessage
                                }
                            }
                        }
                        chatAdapter?.notifyDataSetChanged()
                        databaseReference!!.child(chatId).updateChildren(chatsUpdates!!)
                        binding.rv.scrollToPosition(chatMessages!!.size - 1)
                    }
                    binding.dataLY.visibility = View.VISIBLE
                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                    binding.dataLY.visibility = View.VISIBLE
                }
            })
            binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
            binding.dataLY.visibility = View.VISIBLE
        }
    private val extraData: Unit
        get() {
            orderID = "0"
            if (providerName == null || providerName?.isEmpty() == true) {
                providerName = getString(R.string.customer_service)
                orderID = "0"
                providerID = "0"
            }
            chatId = "room: $orderID - $userID - $providerID"
            roomChats
        }

    private fun sendChatMessage() {
        chatMessage = ChatModel()
        val key = databaseReference?.child(chatId)?.push()?.key
        chatMessage?.senderID = userID
        chatMessage?.senderName = userName
        chatMessage?.senderImage = userImage
        chatMessage?.receiverID = providerID
        chatMessage?.receiverName = providerName
        chatMessage?.receiverImage = "null"
        chatMessage?.messageBody = message?.trim { it <= ' ' }
        chatMessage?.isIs_read = false
        chatMessage?.messageTime = System.currentTimeMillis()
        chatMessage?.messageType = Constants.Sender
        chatMessage?.inputType = inputType
        chatMessage?.imageUrl = imageUrl
        chatMessage?.lng = ""
        chatMessage?.lat = ""
        binding.messageTxt.setText("")
        if (key != null);
        databaseReference?.child(chatId)?.child(key!!)?.setValue(chatMessage)?.addOnCompleteListener(
            this
        ) { _: Task<Void?>? ->
            binding.rv.scrollToPosition(
                chatMessages?.size?:0 - 1
            )
            binding.ImageContainer.visibility = View.GONE
            chatAdapter?.notifyDataSetChanged()
        }
    }

    private fun openPicker() {
        try {
            val builder = PermissionCompat.Builder(activity)
            builder.addPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            builder.addPermissionRationale(getString(R.string.should_allow_permission))
            builder.addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {
                    pickImage()
                }

                override fun onDenied(permission: String) {
                    Toast(R.string.some_permission_denied)
                }
            })
            builder.build().request()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }

    private fun pickImage() {
        pickImageDialog = PickImageDialog(
            activity,
            object :DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (func == Constants.CAPTURE) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE)
                    } else if (func == Constants.PICK) {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        startActivityForResult(
                            Intent.createChooser(intent, getString(R.string.selectImage)),
                            REQUEST_PICK_IMAGE
                        )
                    }                }

            })
        pickImageDialog?.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
                Glide.with(activity).load(imageBitmap).into(
                    binding.ivChatImage
                )
                val imagePath = PathUtil.getPath(activity, data?.data)
                selectedPhotoFil = File(imagePath)
                binding.ImageContainer.visibility = View.VISIBLE
                inputType = Constants.inputType_image
            } catch (e: Exception) {
                e.printStackTrace()
                GlobalData.errorDialog(activity, R.string.upload_photo, getString(R.string.textTryAgain))
            }
        }
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            try {
                imageBitmap = data?.extras?.get("data") as Bitmap?
                Glide.with(activity).load(imageBitmap).into(
                    binding.ivChatImage
                )
                val imagePath = PathUtil.getPath(activity, data?.data)
                selectedPhotoFil = File(imagePath)
                binding.ImageContainer.visibility = View.VISIBLE
                inputType = Constants.inputType_image
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TAG", "onActivityResult: Exception GALLERY_CONSTANT: " + e.message)
                GlobalData.errorDialog(activity, R.string.upload_photo, getString(R.string.textTryAgain))
            }
        }
    }

    private fun uploadPhoto(userId: Int, photo: File?) {
        Log.i("tag", "Log  userId $userId")
        Log.i("tag", "Log  photo " + photo?.name)
        Log.i("tag", "Log  uploadPhoto " + photo?.name)
        GlobalData.progressDialog(activity, R.string.upload_photo, R.string.please_wait_to_upload_photo)
        val country: String = if (localModel?.shortname != null) {
            localModel?.shortname?:Constants.default_short_name
        } else {
            GlobalData.COUNTRY
        }
        val token = if (UtilityApp.getToken() != null) UtilityApp.getToken() else "token"
        AndroidNetworking.upload(UtilityApp.getUrl() + country + GlobalData.grocery + GlobalData.Api + " v8/Account/UploadPhoto" + "?user_id=" + userId)
            .addMultipartFile("file", photo)
            .addHeaders("ApiKey", Constants.api_key)
            .addHeaders("device_type", Constants.deviceType)
            .addHeaders("app_version", UtilityApp.getAppVersionStr())
            .addHeaders("token", token)
            .build().setUploadProgressListener { _: Long, _: Long -> }
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    GlobalData.hideProgressDialog()
                    Log.i("tag", "Log data response $response")
                    var message = getString(R.string.fail_to_load)
                    if (response.toString() == Constants.ERROR) {
                        GlobalData.errorDialog(activity, R.string.fail_to_load, message)
                    } else {
                        var data: String? = null
                        try {
                            val status = response.getInt("status")
                            if (status == 200) {
                                data = response.getString("data")
                                Log.i("tag", "Log data result $data")
                                imageUrl = data
                                sendChatMessage()
                                GlobalData.successDialog(
                                    activity,
                                    getString(R.string.upload_photo),
                                    getString(R.string.success_upload)
                                )
                            } else {
                                message = response.getString("message")
                                GlobalData.errorDialog(activity, R.string.fail_to_load, message)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(error: ANError) {
                    // handle error
                }
            })
    }

    private fun resetInputs() {
        binding.ImageContainer.visibility = View.GONE
        inputType = Constants.inputType_text
        imageUrl = ""
    }
}