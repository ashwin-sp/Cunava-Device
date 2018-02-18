package com.hpinc.voter


import android.Manifest
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.Toast

import android.provider.MediaStore
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream



class LoginActivity : Activity() {
    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var entercode: EditText

    private lateinit var db: DatabaseHelper
    private lateinit var sb: SQLiteDatabase
    private val PERMISSION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userName = findViewById<View>(R.id.editText1) as EditText
        password = findViewById<View>(R.id.editText2) as EditText
        entercode = findViewById<View>(R.id.editText3) as EditText

    }

    fun Login(v: View) {

        db = DatabaseHelper(this@LoginActivity)
        sb = db.readableDatabase

        user = userName.text.toString()

        pass = password.text.toString()

        c = entercode.text.toString()

        if (user == "" || pass == "" || c == "") {
            Toast.makeText(applicationContext, "Please enter Name or Password or code", Toast.LENGTH_SHORT).show()
        } else {
            var args = arrayOf(user, pass)
            val crs = sb.rawQuery("SELECT * FROM LOGGER WHERE first_name = ? and password = ?", args)
            args = arrayOf(user, pass, c)
            val crCode = sb.rawQuery("SELECT * FROM LOGGER WHERE first_name = ? and password = ? and Register = ?", args)
            if (crs.count == 0) {

                Toast.makeText(applicationContext, "Username or Password Invalid", Toast.LENGTH_SHORT).show()

            } else {
                if (crCode.count != 0) {

                       dbClose()

                    if (db.getVotedStatus(user, pass) == "true") {
                        Toast.makeText(applicationContext, "Already voted...", Toast.LENGTH_SHORT).show()
                    } else {

                    /*    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())*/
                        Toast.makeText(this,  resources.getString(R.string.instruct_capture), Toast.LENGTH_SHORT ).show()
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (cameraIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(cameraIntent, 1000)
                        }
                        overridePendingTransition(R.anim.open_translate, R.anim.close_scale)
                       /* val home = Intent(this@LoginActivity, VoteActivity::class.java)

                        startActivity(home)
                        overridePendingTransition(R.anim.open_translate, R.anim.close_scale)*/
                    }
                } else {
                    Toast.makeText(applicationContext, "Invalid Registration code", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }


    fun Register(v: View) {
        val register1 = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(register1)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_login, menu)
        return true
    }

    override fun onBackPressed() {
        Log.d("CDA", "onBackPressed Called")
        val setIntent = Intent(Intent.ACTION_MAIN)
        setIntent.addCategory(Intent.CATEGORY_HOME)
        setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(setIntent)
    }

    companion object {
        lateinit var user: String
        lateinit var pass: String
        lateinit var c: String
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Please grant permission for sending sms to Verification client", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
       // Log.d("Entered", "outside")
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
               val extras = data.extras
               val imageBitmap =  extras.get("data") as Bitmap

                //Convert to byte array
                val stream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()

               //imageView.setImageBitmap(imageBitmap)
/*

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                val tempUri = getImageUri(applicationContext, photo)

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                val finalFile = File(getRealPathFromURI(tempUri))

                Glide.with(this)
                        .load()
                        .apply(RequestOptions()
                                .centerCrop())
                        .into(imageView)*/
                val home = Intent(this@LoginActivity, VoteActivity::class.java)
                home.putExtra("image", byteArray)
                startActivity(home)
                overridePendingTransition(R.anim.open_translate, R.anim.close_scale)
          }
        }
    }
    private fun dbClose()
    {
        sb.close()
        db.close()
    }
}
