package com.hpinc.voter

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_vote.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import com.hpinc.voter.Retrofit.ImageCallback
import com.hpinc.voter.Retrofit.RetrofitCallBuilder
import org.jetbrains.annotations.NotNull
import org.json.JSONArray


class VoteActivity : Activity() {
    private var db = DatabaseHelper(this@VoteActivity)
    private lateinit var sb: SQLiteDatabase

    private var firstCheck : Boolean = false
    private var secondCheck : Boolean = false

    val faceProperties: Array<String> = arrayOf("skin", "nose", "head", "girl", "eye", "mouth", "child", "ear", "face")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote)
        sb = db.readableDatabase
        val byteArray = intent.getByteArrayExtra("image")
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
        initState()
        initListenerForRadioGroup()
        initListenerForButton()
        checkForProperImage(encodedImage)
    }

    override fun onBackPressed() {
        Log.d("CDA", "onBackPressed Called")
        val setIntent = Intent(Intent.ACTION_MAIN)
        setIntent.addCategory(Intent.CATEGORY_HOME)
        setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(setIntent)
    }

    private fun checkForProperImage(encodedImage: String)
    {
        RetrofitCallBuilder.initRetroBuilder()
        RetrofitCallBuilder.getData("https://vision.googleapis.com/v1/images:annotate?key=".plus(resources.getString(R.string.key)), encodedImage,  object : ImageCallback {
            override fun updateImage(@NotNull jsonArray: JSONArray) {
                //println("Image JSONArray "+ jsonArray)
                if(jsonArray.length() > 0) {
                    val singleObject = jsonArray.getJSONObject(0)
                    if (singleObject.has("labelAnnotations")) {
                        val labels = singleObject.getJSONArray("labelAnnotations")
                        val desc: ArrayList<String> = ArrayList()
                        val score: ArrayList<Double> = ArrayList()
                        for (i in 0 until labels.length()) {
                            desc.add(labels.getJSONObject(i).getString("description"))
                            score.add(labels.getJSONObject(i).getDouble("score"))
                        }
                        Log.d("desc", desc.toString())
                        Log.d("score", score.toString())
                        if (desc.contains("face") && score[desc.indexOf("face")] > 0.9) {
                            Log.d("First condition", "passed")
                            firstCheck = true
                        }
                        for (i in 0 until desc.size) {
                            if (faceProperties.contains(desc[i])) {
                                Log.d("Second condition", "passed")
                                secondCheck = true
                                break
                            }
                        }
                        if (firstCheck && secondCheck) {
                            statusText.text = resources.getString(R.string.capture_successful)
                            voteButton.visibility = View.VISIBLE
                            val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            val matrix = Matrix()
                            matrix.postRotate(270.0f)
                            val rotatedBitmap = Bitmap.createBitmap(decodedImage, 0, 0, decodedImage.width, decodedImage.height, matrix, true)
                            imageView.setImageBitmap(rotatedBitmap)
                        } else {
                            statusText.text = resources.getString(R.string.retake)
                            Toast.makeText(this@VoteActivity,  resources.getString(R.string.retake), Toast.LENGTH_SHORT ).show()
                            val i = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(i)
                            overridePendingTransition(R.anim.open_scale, R.anim.close_translate)
                        }
                    } else {
                        statusText.text = resources.getString(R.string.retake)
                        Toast.makeText(this@VoteActivity,  resources.getString(R.string.retake), Toast.LENGTH_SHORT ).show()
                        val i = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(i)
                        overridePendingTransition(R.anim.open_scale, R.anim.close_translate)
                    }
                }
               /* else
                {
                    statusText.text = resources.getString(R.string.capture_error)
                }*/
            }
        })
    }

    private fun initState()
    {
        voteButton.visibility = View.GONE
        firstCheck = false
        secondCheck = false
    }

    private fun initListenerForRadioGroup()
    {
        radioGroup1!!.setOnCheckedChangeListener { _, checkedId ->
            // find which radio button is selected
            when (checkedId) {
                R.id.candidateN1 -> Toast.makeText(applicationContext, "choice: ASSA", Toast.LENGTH_SHORT).show()
                R.id.candidateN2 -> Toast.makeText(applicationContext, "choice: PHPRY", Toast.LENGTH_SHORT).show()
                R.id.nota -> Toast.makeText(applicationContext, "choice: NOTA", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(applicationContext, "choose any one option",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initListenerForButton()
    {
        voteButton!!.setOnClickListener {
            val selectedId = radioGroup1!!.checkedRadioButtonId
            // find which radioButton is checked by id
            when (selectedId) {
                candidateN1!!.id -> {
                    Toast.makeText(applicationContext, "You chose 'ASSA'", Toast.LENGTH_SHORT).show()
                    var query = "UPDATE LOGGER SET voted='true' WHERE first_name='"+ LoginActivity.user +"' and password='"+LoginActivity.pass+"'"
                    sb.execSQL(query)
                    query = "UPDATE LOGGER SET castedTo='ASSA' WHERE first_name='"+ LoginActivity.user +"' and password='"+LoginActivity.pass+"'"
                    sb.execSQL(query)
                }
                candidateN2!!.id -> {
                    Toast.makeText(applicationContext, "You chose 'PHPRY'", Toast.LENGTH_SHORT).show()
                    var query = "UPDATE LOGGER SET voted='true' WHERE first_name='"+ LoginActivity.user +"' and password='"+LoginActivity.pass+"'"
                    sb.execSQL(query)
                    query = "UPDATE LOGGER SET castedTo='PHPRY' WHERE first_name='"+ LoginActivity.user +"' and password='"+LoginActivity.pass+"'"
                    sb.execSQL(query)
                }
                nota!!.id -> {
                    Toast.makeText(applicationContext, "You chose 'NOTA'", Toast.LENGTH_SHORT).show()
                    var query = "UPDATE LOGGER SET voted='true' WHERE first_name='"+ LoginActivity.user +"' and password='"+LoginActivity.pass+"'"
                    sb.execSQL(query)
                    query = "UPDATE LOGGER SET castedTo='NOTA' WHERE first_name='"+ LoginActivity.user +"' and password='"+LoginActivity.pass+"'"
                    sb.execSQL(query)
                }
                else -> Toast.makeText(applicationContext, "you did not choose any option", Toast.LENGTH_SHORT).show()
            }

            closeDB()
            val i = Intent(applicationContext, ResultActivity::class.java)
            startActivity(i)
        }
    }

    private fun closeDB()
    {
        sb.close()
        db.close()
    }
}


