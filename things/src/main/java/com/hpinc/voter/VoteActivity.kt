package com.hpinc.voter

import android.Manifest
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.View
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_vote.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.support.v4.app.ActivityCompat
import android.util.Base64
import android.widget.TextView
import com.hpinc.voter.Retrofit.ImageCallback
import com.hpinc.voter.Retrofit.RetrofitCallBuilder
import com.hpinc.voter.Util.CameraHandler
import com.hpinc.voter.Util.ImagePreprocessor
import org.jetbrains.annotations.NotNull
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList


class VoteActivity : Activity(), ImageReader.OnImageAvailableListener {

    private var db = DatabaseHelper(this@VoteActivity)
    private lateinit var sb: SQLiteDatabase

    private var firstCheck : Boolean = false
    private var secondCheck : Boolean = false

    private var mImagePreprocessor: ImagePreprocessor? = null
    private var mCameraHandler: CameraHandler? = null

    private var mTtsEngine: TextToSpeech? = null

    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null

    private var mResultViews: Array<TextView>? = null

    private val mReady = AtomicBoolean(false)
    private val PERMISSION_REQUEST_CODE = 100

    private val RANDOM = Random()

    private val UTTERANCE_ID = "com.hpinc.voter.UTTERANCE_ID"


    val faceProperties: Array<String> = arrayOf("skin", "nose", "head", "girl", "eye", "mouth", "child", "ear", "face")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote)
        sb = db.readableDatabase
//        val byteArray = intent.getByteArrayExtra("image")
//        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
        initState()
        initListenerForRadioGroup()
        initListenerForButton()
       /* checkForProperImage(encodedImage)*/
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
                println("Image JSONArray "+ jsonArray)
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
                            voteButton.text = "Vote"
                            statusText.text = resources.getString(R.string.capture_successful)
                            voteButton.visibility = View.VISIBLE
                        } else {
                            statusText.text = resources.getString(R.string.retake)
                            Toast.makeText(this@VoteActivity,  resources.getString(R.string.retake), Toast.LENGTH_SHORT ).show()
                            voteButton.text = "Back"
                 /*           val i = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(i)
                            overridePendingTransition(R.anim.open_scale, R.anim.close_translate)*/
                        }
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
        voteButton.text = "Take picture"
        voteButton.visibility = View.VISIBLE
        firstCheck = false
        secondCheck = false
        mBackgroundThread = HandlerThread("BackgroundThread")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
        mBackgroundHandler!!.post(mInitializeOnBackground)
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

            if(voteButton.text == "Take picture")
            {
                if (mReady.get()) {
                    Log.i("Logging", "Taking photo")
                    mReady.set(true)
                    mBackgroundHandler?.post(mBackgroundClickHandler)
                } else {
                    Toast.makeText(this, "Photo capture in progress", Toast.LENGTH_SHORT).show()
                    Log.i("Logging", "Sorry, processing hasn't finished. Try again in a few seconds")
                }
            }
            else if(voteButton.text == "Back")
            {
                val i = Intent(applicationContext, LoginActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.open_scale, R.anim.close_translate)
            }
            else {
                val selectedId = radioGroup1!!.checkedRadioButtonId
                // find which radioButton is checked by id
                when (selectedId) {
                    candidateN1!!.id -> {
                        Toast.makeText(applicationContext, "You chose 'ASSA'", Toast.LENGTH_SHORT).show()
                        var query = "UPDATE LOGGER SET voted='true' WHERE first_name='" + LoginActivity.user + "' and password='" + LoginActivity.pass + "'"
                        sb.execSQL(query)
                        query = "UPDATE LOGGER SET castedTo='ASSA' WHERE first_name='" + LoginActivity.user + "' and password='" + LoginActivity.pass + "'"
                        sb.execSQL(query)
                    }
                    candidateN2!!.id -> {
                        Toast.makeText(applicationContext, "You chose 'PHPRY'", Toast.LENGTH_SHORT).show()
                        var query = "UPDATE LOGGER SET voted='true' WHERE first_name='" + LoginActivity.user + "' and password='" + LoginActivity.pass + "'"
                        sb.execSQL(query)
                        query = "UPDATE LOGGER SET castedTo='PHPRY' WHERE first_name='" + LoginActivity.user + "' and password='" + LoginActivity.pass + "'"
                        sb.execSQL(query)
                    }
                    nota!!.id -> {
                        Toast.makeText(applicationContext, "You chose 'NOTA'", Toast.LENGTH_SHORT).show()
                        var query = "UPDATE LOGGER SET voted='true' WHERE first_name='" + LoginActivity.user + "' and password='" + LoginActivity.pass + "'"
                        sb.execSQL(query)
                        query = "UPDATE LOGGER SET castedTo='NOTA' WHERE first_name='" + LoginActivity.user + "' and password='" + LoginActivity.pass + "'"
                        sb.execSQL(query)
                    }
                    else -> Toast.makeText(applicationContext, "you did not choose any option", Toast.LENGTH_SHORT).show()
                }

                closeDB()
                val i = Intent(applicationContext, ResultActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun closeDB()
    {
        sb.close()
        db.close()
    }

    private val mInitializeOnBackground = Runnable {
        mImagePreprocessor = ImagePreprocessor()

        mTtsEngine = TextToSpeech(this@VoteActivity,
                TextToSpeech.OnInitListener { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        mTtsEngine.setLanguage(Locale.US)
                        mTtsEngine.setOnUtteranceProgressListener(utteranceListener)
                        mTtsEngine.speak("I'm ready!", TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID)
                    } else {
                        Log.w("Error: ", "Could not open TTS Engine (onInit status=" + status
                                + "). Ignoring text to speech")
                        mTtsEngine = null
                    }
                })

        mCameraHandler = CameraHandler.getInstance() as CameraHandler
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraHandler!!.initializeCamera(
                    this@VoteActivity, mBackgroundHandler,
                    this@VoteActivity)
            mReady.set(true)
        }
        else
        {
            ActivityCompat.requestPermissions(this@VoteActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CODE)
        }

    }

    private val mBackgroundClickHandler = Runnable {
        mCameraHandler?.takePicture()
    }


    private val utteranceListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String) {
            mReady.set(false)
        }

        override fun onDone(utteranceId: String) {
            mReady.set(true)
        }

        override fun onError(utteranceId: String) {
            mReady.set(true)
        }
    }


    override fun onImageAvailable(reader: ImageReader?) {
        lateinit var bitmap: Bitmap
        reader?.acquireNextImage().use { image -> bitmap = mImagePreprocessor?.preprocessImage(image)!! }

        runOnUiThread { imageView.setImageBitmap(bitmap) }

        mReady.set(true)

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream .toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
        checkForProperImage(encoded)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                mCameraHandler!!.initializeCamera(
                        this@VoteActivity, mBackgroundHandler,
                        this@VoteActivity)
                mReady.set(true)
            }
        }
        else
        {
            finish()
        }
    }


    private fun <T> getRandomElement(list: List<T>): T {
        return list[RANDOM.nextInt(list.size)]
    }

    fun speakShutterSound(tts: TextToSpeech) {

    }

}


